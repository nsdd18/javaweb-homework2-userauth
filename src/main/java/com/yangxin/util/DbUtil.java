package com.yangxin.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库工具类：负责读取 db.properties、加载驱动、获取连接、关闭资源。
 *
 * <p>配置文件 {@code db.properties} 位于 {@code src/main/resources} 下，
 * 打包后会被放进 {@code WEB-INF/classes}，所以用类加载器即可读取。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
public final class DbUtil {

    /** 配置文件里的键名 */
    private static final String CONFIG_FILE = "db.properties";

    /** 可选的本地覆盖配置文件（已被 .gitignore 排除，不进版本库） */
    private static final String LOCAL_CONFIG_FILE = "db.local.properties";

    /** JDBC 连接串 */
    private static String url;
    /** 数据库用户名 */
    private static String username;
    /** 数据库密码 */
    private static String password;

    /** 是否已经初始化过（驱动只需注册一次） */
    private static boolean initialized = false;

    /** 工具类不允许被实例化 */
    private DbUtil() {
    }

    /**
     * 读取类路径下的 db.properties，并注册 JDBC 驱动。
     *
     * <p>如果同时存在 {@code db.local.properties}（本机专用配置，不进 git），
     * 会在 db.properties 的基础上覆盖同名键，方便每个同学使用自己的数据库密码，
     * 而不必修改要走版本控制的 db.properties。</p>
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }
        Properties props = loadProperties(CONFIG_FILE, true);

        // 可选的本地覆盖配置，存在才加载
        Properties local = loadProperties(LOCAL_CONFIG_FILE, false);
        if (local != null) {
            props.putAll(local);
        }

        try {
            String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            // JDBC 4.0 以后驱动可以自动注册，这里显式加载，保证在任何容器里都能工作
            Class.forName(driver);

            initialized = true;
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("数据库驱动加载失败：" + e.getMessage());
        }
    }

    /**
     * 从类路径读取一个 properties 文件。
     *
     * @param fileName 文件名（位于 src/main/resources 下）
     * @param required 为 true 时文件不存在会抛异常；为 false 时返回 null
     * @return 配置对象；可选文件不存在时返回 null
     */
    private static Properties loadProperties(String fileName, boolean required) {
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                if (required) {
                    throw new IllegalStateException("找不到配置文件：" + fileName);
                }
                return null;
            }
            Properties props = new Properties();
            // ★ 必须用 UTF-8 读取，否则中文注释类配置会乱码
            props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            return props;
        } catch (IOException e) {
            throw new ExceptionInInitializerError("读取配置文件失败：" + fileName + "，" + e.getMessage());
        }
    }

    /**
     * 获取一个数据库连接。
     *
     * @return 新的 {@link Connection}（由调用方负责关闭，推荐用 try-with-resources）
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        init();
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 关闭结果集（安静关闭，不抛异常）。
     *
     * @param rs 结果集，可为 null
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
                // 关闭失败无需处理
            }
        }
    }

    /**
     * 关闭语句对象（安静关闭，不抛异常）。
     *
     * @param stmt 语句对象，可为 null
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
                // 关闭失败无需处理
            }
        }
    }

    /**
     * 关闭连接（安静关闭，不抛异常）。
     *
     * @param conn 连接，可为 null
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
                // 关闭失败无需处理
            }
        }
    }
}
