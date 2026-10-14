package com.yangxin.dao;

import com.yangxin.entity.User;
import com.yangxin.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * 用户数据访问类（DAO）：只负责与 {@code user} 表打交道。
 *
 * <p>所有 SQL 都使用 {@link PreparedStatement} 预编译参数，
 * 从根上避免 SQL 注入。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
public class UserDao {

    /** 查询时统一的字段列表 */
    private static final String COLUMNS =
            "id, username, password, salt, email, phone, create_time, update_time";

    /**
     * 新增一个用户（注册）。
     *
     * @param user 待保存的用户（密码字段必须已经是加盐哈希）
     * @return 新增记录的主键 id；失败返回 null
     * @throws SQLException 数据库异常（用户名重复时违反唯一约束，也会抛异常）
     */
    public Integer insert(User user) throws SQLException {
        String sql = "INSERT INTO `user` (username, password, salt, email, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setId(id);
                    return id;
                }
            }
            return null;
        }
    }

    /**
     * 按用户名查询用户（登录、注册查重、找回密码都用它）。
     *
     * @param username 用户名
     * @return 查到的用户；不存在返回 null
     * @throws SQLException 数据库异常
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM `user` WHERE username = ?";
        return queryOne(sql, username);
    }

    /**
     * 按用户名 + 邮箱查询用户（找回密码时核对身份）。
     *
     * @param username 用户名
     * @param email    注册时填写的邮箱
     * @return 匹配的用户；不匹配返回 null
     * @throws SQLException 数据库异常
     */
    public User findByUsernameAndEmail(String username, String email) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM `user` WHERE username = ? AND email = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * 修改密码（忘记密码流程最后一步）。
     *
     * @param username    用户名
     * @param newPassword 新的密码哈希
     * @param newSalt     新的盐值（重置密码时重新生成盐，更安全）
     * @return 受影响行数，1 表示修改成功
     * @throws SQLException 数据库异常
     */
    public int updatePassword(String username, String newPassword, String newSalt) throws SQLException {
        String sql = "UPDATE `user` SET password = ?, salt = ? WHERE username = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, newSalt);
            ps.setString(3, username);
            return ps.executeUpdate();
        }
    }

    /**
     * 查询单条记录的通用方法。
     *
     * @param sql    带一个参数的查询语句
     * @param param  参数值
     * @return 查询结果，无记录返回 null
     * @throws SQLException 数据库异常
     */
    private User queryOne(String sql, String param) throws SQLException {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * 把结果集的一行映射成 {@link User} 对象。
     *
     * @param rs 已经指向有效行的结果集
     * @return 用户对象
     * @throws SQLException 读取异常
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setSalt(rs.getString("salt"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));

        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            user.setCreateTime(createTime.toLocalDateTime());
        }
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            user.setUpdateTime(updateTime.toLocalDateTime());
        }
        return user;
    }
}
