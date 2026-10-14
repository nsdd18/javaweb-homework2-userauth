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
 * 【前端页面二 → 对应 Servlet 二】登录处理。
 *
 * <p>GET 请求：转发到登录页面 {@code /login.jsp}。<br>
 * POST 请求：读取表单里的用户名和密码，去数据库校验；</p>
 * <ul>
 *   <li>校验通过 → 把用户信息放进 Session（表示已登录），跳到成功页面；</li>
 *   <li>校验失败 → 带着错误提示回到登录页面，并保留已输入的用户名。</li>
 * </ul>
 *
 * <p>访问地址：http://localhost:8080/homework2/login</p>
 *
 * @author 杨鑫（学号：25040500）
 */
@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** 用户数据访问对象 */
    private final UserDao userDao = new UserDao();

    /**
     * GET 请求：打开登录页面。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * POST 请求：处理登录表单。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ① 读取表单参数（编码已由 EncodingFilter 统一处理）
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        // ② 服务端非空校验
        if (Validator.isBlank(username) || Validator.isBlank(password)) {
            backToPage(request, response, username, "用户名和密码都不能为空");
            return;
        }

        try {
            // ③ 按用户名查库
            User user = userDao.findByUsername(username);

            // ④ 用户不存在 或 密码不匹配，都给出同一句提示
            //    （不提示“用户名不存在”，避免被用来枚举系统里有哪些账号）
            if (user == null || !PasswordUtil.verify(password, user.getSalt(), user.getPassword())) {
                backToPage(request, response, username, "用户名或密码错误，请重新输入");
                return;
            }

            // ⑤ 登录成功：把用户放进 Session
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user);

            request.setAttribute("resultType", "success");
            request.setAttribute("resultTitle", "登录成功");
            request.setAttribute("resultMessage",
                    "欢迎回来，" + Validator.escapeHtml(user.getUsername()) + "！");
            request.setAttribute("resultDetails", new String[]{
                    "用户编号：" + user.getId(),
                    "登录用户名：" + Validator.escapeHtml(user.getUsername()),
                    "注册邮箱：" + Validator.escapeHtml(user.getEmail()),
                    "注册时间：" + user.getCreateTime()
            });
            request.setAttribute("backUrl", request.getContextPath() + "/index.jsp");
            request.setAttribute("backText", "返回首页");
            request.getRequestDispatcher("/result.jsp").forward(request, response);

        } catch (SQLException e) {
            // 数据库异常统一当作服务器错误处理，并把原因写进日志
            System.err.println("【登录失败】数据库访问异常：" + e.getMessage());
            backToPage(request, response, username, "数据库连接异常，请联系管理员检查 MySQL 是否已启动");
        }
    }

    /**
     * 登录失败时回到登录页面，并带上错误提示与已输入的用户名。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param username 用户刚才输入的用户名（用于回填）
     * @param message  错误提示
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    private void backToPage(HttpServletRequest request, HttpServletResponse response,
                            String username, String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("username", username);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
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
