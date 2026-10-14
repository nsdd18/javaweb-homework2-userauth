package com.yangxin.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 退出登录：销毁 Session 后回到登录页面。
 *
 * <p>虽然作业只要求三个页面，但登录后能退出才算完整，
 * 顺便演示 {@code session.invalidate()} 的用法。</p>
 *
 * <p>访问地址：http://localhost:8080/homework2/logout</p>
 *
 * @author 杨鑫（学号：25040500）
 */
@WebServlet(name = "logoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * GET 请求：销毁 Session 并重定向到登录页。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // 重定向（而不是转发），让浏览器地址栏变回登录页
        response.sendRedirect(request.getContextPath() + "/login");
    }

    /**
     * POST 请求与 GET 处理方式一致。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
