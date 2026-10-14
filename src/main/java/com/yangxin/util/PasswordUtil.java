package com.yangxin.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 密码工具类：负责生成盐值、把密码加盐后做 SHA-256 哈希、校验密码。
 *
 * <p>数据库中绝不保存明文密码。每个用户注册时都会生成一个独立的随机盐值，
 * 最终存入数据库的是 {@code SHA-256(盐值 + 明文密码)} 的十六进制字符串，
 * 这样即使数据库泄露，也无法直接还原出原始密码。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
public final class PasswordUtil {

    /** 盐值字节数：16 字节 → 32 位十六进制字符 */
    private static final int SALT_BYTES = 16;

    /** 随机数生成器（用于生成盐值） */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 十六进制字符表 */
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /** 工具类不允许被实例化 */
    private PasswordUtil() {
    }

    /**
     * 生成一个随机的盐值。
     *
     * @return 32 位十六进制字符串
     */
    public static String generateSalt() {
        byte[] bytes = new byte[SALT_BYTES];
        RANDOM.nextBytes(bytes);
        return toHex(bytes);
    }

    /**
     * 把明文密码加盐后做 SHA-256 哈希。
     *
     * @param plainPassword 明文密码
     * @param salt          该用户对应的盐值
     * @return 64 位十六进制哈希值
     */
    public static String hash(String plainPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 盐值放在前面，一起参与哈希
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 是 JDK 必备算法，正常不会走到这里
            throw new IllegalStateException("当前 JDK 不支持 SHA-256 算法", e);
        }
    }

    /**
     * 校验明文密码是否正确。
     *
     * <p>使用逐字符异或比较（时间恒定），避免因比较耗时不同而泄露信息。</p>
     *
     * @param plainPassword 用户输入的明文密码
     * @param salt          数据库中保存的盐值
     * @param expectedHash  数据库中保存的密码哈希
     * @return 密码正确返回 true
     */
    public static boolean verify(String plainPassword, String salt, String expectedHash) {
        if (plainPassword == null || salt == null || expectedHash == null) {
            return false;
        }
        String actualHash = hash(plainPassword, salt);
        return constantTimeEquals(actualHash, expectedHash);
    }

    /**
     * 时间恒定的字符串比较。
     *
     * @param a 字符串一
     * @param b 字符串二
     * @return 内容相同返回 true
     */
    private static boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(StandardCharsets.UTF_8);
        byte[] y = b.getBytes(StandardCharsets.UTF_8);
        if (x.length != y.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < x.length; i++) {
            result |= x[i] ^ y[i];
        }
        return result == 0;
    }

    /**
     * 字节数组转十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String toHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }
}
