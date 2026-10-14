package com.yangxin.util;

import java.util.regex.Pattern;

/**
 * 表单校验工具类：服务端对用户提交的数据做二次校验。
 *
 * <p>页面上的 HTML5 校验（required、pattern、minlength）可以被绕过，
 * 所以服务端必须再校验一次，这是 Web 开发的基本安全原则。</p>
 *
 * @author 杨鑫（学号：25040500）
 */
public final class Validator {

    /** 用户名：4~20 位，只能用字母、数字、下划线，且必须以字母开头 */
    private static final Pattern USERNAME =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]{3,19}$");

    /** 邮箱：常规的邮箱格式 */
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** 手机号：11 位，1 开头，第二位 3~9 */
    private static final Pattern PHONE =
            Pattern.compile("^1[3-9]\\d{9}$");

    /** 密码最短长度 */
    private static final int PASSWORD_MIN = 6;

    /** 密码最长长度 */
    private static final int PASSWORD_MAX = 20;

    /** 工具类不允许被实例化 */
    private Validator() {
    }

    /**
     * 判断字符串是否为空（null 或去掉空白后为空串）。
     *
     * @param s 待判断的字符串
     * @return 为空返回 true
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * 校验用户名。
     *
     * @param username 用户名
     * @return 合法返回 null，不合法返回错误提示文字
     */
    public static String checkUsername(String username) {
        if (isBlank(username)) {
            return "用户名不能为空";
        }
        if (!USERNAME.matcher(username).matches()) {
            return "用户名需为 4~20 位，以字母开头，只能包含字母、数字和下划线";
        }
        return null;
    }

    /**
     * 校验密码强度。
     *
     * @param password 明文密码
     * @return 合法返回 null，不合法返回错误提示文字
     */
    public static String checkPassword(String password) {
        if (isBlank(password)) {
            return "密码不能为空";
        }
        if (password.length() < PASSWORD_MIN || password.length() > PASSWORD_MAX) {
            return "密码长度需在 " + PASSWORD_MIN + "~" + PASSWORD_MAX + " 位之间";
        }
        if (password.contains(" ")) {
            return "密码中不能包含空格";
        }
        return null;
    }

    /**
     * 校验两次输入的密码是否一致。
     *
     * @param password 密码
     * @param confirm  确认密码
     * @return 一致返回 null，不一致返回错误提示文字
     */
    public static String checkPasswordConfirm(String password, String confirm) {
        if (isBlank(confirm)) {
            return "请再次输入密码";
        }
        if (!password.equals(confirm)) {
            return "两次输入的密码不一致";
        }
        return null;
    }

    /**
     * 校验邮箱。
     *
     * @param email 邮箱
     * @return 合法返回 null，不合法返回错误提示文字
     */
    public static String checkEmail(String email) {
        if (isBlank(email)) {
            return "邮箱不能为空";
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            return "邮箱格式不正确，例如：yangxin@example.com";
        }
        return null;
    }

    /**
     * 校验手机号（选填项，为空时视为合法）。
     *
     * @param phone 手机号
     * @return 合法返回 null，不合法返回错误提示文字
     */
    public static String checkPhone(String phone) {
        if (isBlank(phone)) {
            return null;
        }
        if (!PHONE.matcher(phone).matches()) {
            return "手机号格式不正确，应为 11 位数字";
        }
        return null;
    }

    /**
     * 对用户输入做 HTML 转义，防止 XSS 攻击
     * （把用户提交的用户名回显到页面上时使用）。
     *
     * @param text 原始文本
     * @return 转义后的安全文本
     */
    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
