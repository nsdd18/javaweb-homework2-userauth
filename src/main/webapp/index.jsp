<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    ============================================================
    项目首页：三个页面的入口导航
    姓名：杨鑫 25040500　冉铭 25040566
    ============================================================
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>软件2501班第二次作业 - 首页</title>
    <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>

<body>

<div class="container page-header text-center">
    <h1>JavaWeb 作业二：登录 / 注册 / 忘记密码</h1>
    <p>软件2501班　杨鑫 25040500　冉铭 25040566</p>
</div>

<div class="container pb-4" style="max-width: 720px;">
    <div class="card auth-card">
        <div class="card-body">

            <%-- 如果已经登录过，显示当前登录用户 --%>
            <c:if test="${not empty sessionScope.loginUser}">
                <div class="alert alert-success py-2 d-flex justify-content-between align-items-center">
                    <span>当前登录用户：<strong>
                        <c:out value="${sessionScope.loginUser.username}"/></strong></span>
                    <a href="${ctx}/logout" class="btn btn-sm btn-outline-success">退出登录</a>
                </div>
            </c:if>

            <p class="text-secondary small">
                共三个前端页面，每个页面对应一个 Servlet，数据保存到 MySQL 数据库
                （库名 <code>javaweb_homework</code>，表 <code>user</code>）。
            </p>

            <div class="row g-3 mt-1">
                <div class="col-md-4">
                    <div class="border rounded-3 p-3 h-100 text-center">
                        <div class="fs-3 text-primary">&#128274;</div>
                        <h6 class="mt-2">页面一：登录</h6>
                        <p class="small text-secondary">login.jsp</p>
                        <p class="small text-secondary">Servlet：<code>/login</code></p>
                        <a href="${ctx}/login" class="btn btn-primary btn-sm w-100">进入登录页</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="border rounded-3 p-3 h-100 text-center">
                        <div class="fs-3 text-success">&#128221;</div>
                        <h6 class="mt-2">页面二：注册</h6>
                        <p class="small text-secondary">register.jsp</p>
                        <p class="small text-secondary">Servlet：<code>/register</code></p>
                        <a href="${ctx}/register" class="btn btn-success btn-sm w-100">进入注册页</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="border rounded-3 p-3 h-100 text-center">
                        <div class="fs-3 text-warning">&#128273;</div>
                        <h6 class="mt-2">页面三：忘记密码</h6>
                        <p class="small text-secondary">forgot.jsp</p>
                        <p class="small text-secondary">Servlet：<code>/forgot</code></p>
                        <a href="${ctx}/forgot" class="btn btn-warning btn-sm w-100 text-white">进入忘记密码页</a>
                    </div>
                </div>
            </div>

            <hr class="my-4">

            <h6 class="mb-2">使用流程</h6>
            <ol class="small text-secondary mb-0">
                <li>先在「注册」页创建一个账号（用户名 + 密码 + 邮箱），数据写入 MySQL。</li>
                <li>到「登录」页用刚才的账号登录，登录成功后用户信息保存在 Session 中。</li>
                <li>在「忘记密码」页输入用户名 + 注册邮箱验证身份，通过后设置新密码，
                    再用新密码登录验证是否生效。</li>
            </ol>
        </div>
    </div>
</div>

<div class="page-footer">
    <p class="mb-1">开发环境：IntelliJ IDEA + Maven 3.9.9 + JDK 17 + Tomcat 10.1.33 + MySQL 8.0</p>
    <p class="mb-0">软件2501班　杨鑫 25040500　冉铭 25040566</p>
</div>

<script src="${ctx}/js/bootstrap.bundle.min.js"></script>
</body>

</html>
