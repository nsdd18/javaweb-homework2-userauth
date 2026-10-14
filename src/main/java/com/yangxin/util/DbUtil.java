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
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("找不到配置文件：" + CONFIG_FILE);
            }
            Properties props = new Properties();
            // ★ 必须用 UTF-8 读取，否则中文注释类配置会乱码
            props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));

            String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            // JDBC 4.0 以后驱动可以自动注册，这里显式加载，保证在任何容器里都能工作
            Class.forName(driver);

            initialized = true;
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("数据库初始化失败：" + e.getMessage());
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
