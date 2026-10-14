package com.yangxin.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库中的 {@code user} 表。
 *
 * <p>字段与表字段一一对应；密码字段存放的是加盐哈希，绝不保存明文。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户编号（主键，自增） */
    private Integer id;

    /** 登录用户名（唯一） */
    private String username;

    /** 密码：SHA-256(盐值 + 明文密码) 的十六进制字符串 */
    private String password;

    /** 每个用户独立的随机盐值（16 字节的十六进制） */
    private String salt;

    /** 注册邮箱（找回密码时用来核对身份） */
    private String email;

    /** 手机号（选填） */
    private String phone;

    /** 注册时间 */
    private LocalDateTime createTime;

    /** 最后更新时间 */
    private LocalDateTime updateTime;

    /** 无参构造方法（JavaBean 规范要求） */
    public User() {
    }

    /**
     * 注册时使用的构造方法。
     *
     * @param username 用户名
     * @param password 已经加盐哈希后的密码
     * @param salt     盐值
     * @param email    邮箱
     * @param phone    手机号，可为 null
     */
    public User(String username, String password, String salt, String email, String phone) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 为了日志安全，不输出密码与盐值。
     */
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email
                + "', phone='" + phone + "', createTime=" + createTime + '}';
    }
}
