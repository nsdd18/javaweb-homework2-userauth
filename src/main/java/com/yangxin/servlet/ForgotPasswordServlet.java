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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 【前端页面三 → 对应 Servlet 三】忘记密码（重置密码）处理。
 *
 * <p>分两步完成，用 {@code step} 参数区分：</p>
 * <ol>
 *   <li><b>第一步</b>（无 step 或 step=1）：提交用户名 + 注册邮箱，
 *       两者与数据库匹配后，在 Session 里记下 {@code resetUser}，才允许进入第二步；</li>
 *   <li><b>第二步</b>（step=2）：提交新密码，
 *       服务端会再次确认 Session 里确实通过过身份验证，然后更新密码。</li>
 * </ol>
 *
 * <p>这里特意用 Session 而不是隐藏域来保存“已通过验证”的状态，
 * 否则别人只要手动构造一个 step=2 的请求就能改掉任意用户的密码。</p>
 *
 * <p>访问地址：http://localhost:8080/homework2/forgot</p>
 *
 * @author 杨鑫（学号：25040500）
 */
@WebServlet(name = "forgotPasswordServlet", urlPatterns = "/forgot")
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Session 中保存“已通过身份验证、待重置密码”的用户名 */
    private static final String SESSION_RESET_USER = "resetUser";

    /** 用户数据访问对象 */
    private final UserDao userDao = new UserDao();

    /**
     * GET 请求：打开忘记密码页面（第一步）。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot.jsp").forward(request, response);
    }

    /**
     * POST 请求：根据 step 参数决定处理第一步还是第二步。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = trim(request.getParameter("step"));
        if ("2".equals(step)) {
            handleReset(request, response);
        } else {
            handleVerify(request, response);
        }
    }

    /**
     * 第一步：核对用户名 + 邮箱。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    private void handleVerify(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));

        if (Validator.isBlank(username) || Validator.isBlank(email)) {
            backToStep1(request, response, username, email, "用户名和注册邮箱都不能为空");
            return;
        }

        try {
            // ★ 核心校验：用户名与邮箱必须同时匹配同一条记录
            User user = userDao.findByUsernameAndEmail(username, email);
            if (user == null) {
                backToStep1(request, response, username, email,
                        "用户名与注册邮箱不匹配，请检查后重新输入");
                return;
            }

            // 验证通过，记进 Session，第二步会再次检查
            HttpSession session = request.getSession();
            session.setAttribute(SESSION_RESET_USER, user.getUsername());

            // 进入第二步：填写新密码
            request.setAttribute("username", Validator.escapeHtml(user.getUsername()));
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("【找回密码】数据库访问异常：" + e.getMessage());
            backToStep1(request, response, username, email,
                    "数据库连接异常，请联系管理员检查 MySQL 是否已启动");
        }
    }

    /**
     * 第二步：保存新密码。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    private void handleReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String resetUser = (session == null) ? null : (String) session.getAttribute(SESSION_RESET_USER);

        // ★ 没有通过第一步验证，直接拒绝，防止越权改密码
        if (Validator.isBlank(resetUser)) {
            backToStep1(request, response, null, null, "请先验证用户名与注册邮箱，再设置新密码");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        String error = Validator.checkPassword(newPassword);
        if (error == null) {
            error = Validator.checkPasswordConfirm(newPassword, confirmPassword);
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("username", Validator.escapeHtml(resetUser));
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
            return;
        }

        try {
            // 重置密码时重新生成盐值，安全性更好
            String newSalt = PasswordUtil.generateSalt();
            String newHash = PasswordUtil.hash(newPassword, newSalt);

            int rows = userDao.updatePassword(resetUser, newHash, newSalt);
            if (rows != 1) {
                request.setAttribute("error", "密码重置失败，请重新验证身份后再试");
                request.setAttribute("username", Validator.escapeHtml(resetUser));
                request.getRequestDispatcher("/forgot.jsp").forward(request, response);
                return;
            }

            // 用完就把 Session 里的标记清掉，避免被重复利用
            session.removeAttribute(SESSION_RESET_USER);

            request.setAttribute("resultType", "success");
            request.setAttribute("resultTitle", "密码重置成功");
            request.setAttribute("resultMessage",
                    "账号「" + Validator.escapeHtml(resetUser) + "」的密码已更新，请用新密码登录。");
            request.setAttribute("resultDetails", new String[]{
                    "重置账号：" + Validator.escapeHtml(resetUser),
                    "新密码已重新加盐哈希后写入数据库"
            });
            request.setAttribute("backUrl", request.getContextPath() + "/login");
            request.setAttribute("backText", "去登录");
            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("【重置密码】数据库访问异常：" + e.getMessage());
            request.setAttribute("error", "数据库连接异常，请稍后再试");
            request.setAttribute("username", Validator.escapeHtml(resetUser));
            request.getRequestDispatcher("/forgot.jsp").forward(request, response);
        }
    }

    /**
     * 回到忘记密码页面的第一步，并带上错误提示。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param username 用户名（回填）
     * @param email    邮箱（回填）
     * @param message  错误提示
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    private void backToStep1(HttpServletRequest request, HttpServletResponse response,
                             String username, String email, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("username", Validator.escapeHtml(username));
        request.setAttribute("email", Validator.escapeHtml(email));
        request.getRequestDispatcher("/forgot.jsp").forward(request, response);
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
