package com.yangxin.servlet;

import com.yangxin.dao.UserDao;
import com.yangxin.entity.User;
import com.yangxin.util.PasswordUtil;
import com.yangxin.util.Validator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 【前端页面二 → 对应 Servlet 二】注册处理。
 *
 * <p>GET 请求：转发到注册页面 {@code /register.jsp}。<br>
 * POST 请求：校验表单 → 检查用户名是否已被占用 → 密码加盐哈希 → 写入数据库。</p>
 *
 * <p>访问地址：http://localhost:8080/homework2/register</p>
 *
 * @author 杨鑫（学号：25040500）
 */
@WebServlet(name = "registerServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** 用户数据访问对象 */
    private final UserDao userDao = new UserDao();

    /**
     * GET 请求：打开注册页面。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /**
     * POST 请求：处理注册表单。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① 读取表单参数
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));

        // ② 服务端逐项校验（HTML 上的校验可以被绕过，这里必须再验一遍）
        String error = Validator.checkUsername(username);
        if (error == null) {
            error = Validator.checkPassword(password);
        }
        if (error == null) {
            error = Validator.checkPasswordConfirm(password, confirmPassword);
        }
        if (error == null) {
            error = Validator.checkEmail(email);
        }
        if (error == null) {
            error = Validator.checkPhone(phone);
        }
        if (error != null) {
            backToPage(request, response, username, email, phone, error);
            return;
        }

        try {
            // ③ 查重：用户名必须唯一
            if (userDao.findByUsername(username) != null) {
                backToPage(request, response, username, email, phone,
                        "用户名「" + Validator.escapeHtml(username) + "」已被注册，请换一个");
                return;
            }

            // ④ 生成随机盐值，把密码加盐哈希后再入库（数据库里不放明文）
            String salt = PasswordUtil.generateSalt();
            String hashed = PasswordUtil.hash(password, salt);

            User user = new User(username, hashed, salt, email,
                    Validator.isBlank(phone) ? null : phone);

            Integer id = userDao.insert(user);
            if (id == null) {
                backToPage(request, response, username, email, phone, "注册失败，请稍后重试");
                return;
            }

            // ⑤ 注册成功，提示去登录
            request.setAttribute("resultType", "success");
            request.setAttribute("resultTitle", "注册成功");
            request.setAttribute("resultMessage",
                    "账号「" + Validator.escapeHtml(username) + "」注册成功，现在就可以登录了。");
            request.setAttribute("resultDetails", new String[]{
                    "用户编号：" + id,
                    "登录用户名：" + Validator.escapeHtml(username),
                    "注册邮箱：" + Validator.escapeHtml(email),
                    "密码已加盐加密后存入数据库（表中不保存明文）"
            });
            request.setAttribute("backUrl", request.getContextPath() + "/login");
            request.setAttribute("backText", "去登录");
            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("【注册失败】数据库访问异常：" + e.getMessage());
            backToPage(request, response, username, email, phone,
                    "数据库连接异常，请联系管理员检查 MySQL 是否已启动");
        }
    }

    /**
     * 注册校验失败时回到注册页面，并回填用户已经填过的内容。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param username 用户名
     * @param email    邮箱
     * @param phone    手机号
     * @param message  错误提示
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    private void backToPage(HttpServletRequest request, HttpServletResponse response,
                            String username, String email, String phone, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /**
     * 去掉字符串首尾空白，null 安全。
     *
     * @param s 原始字符串
     * @return 处理后的字符串
     */
    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
