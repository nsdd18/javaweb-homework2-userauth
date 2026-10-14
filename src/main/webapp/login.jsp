<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    ============================================================
    前端页面一：登录页面
    对应 Servlet：com.yangxin.servlet.LoginServlet（/login）
    姓名：杨鑫 25040500　冉铭 25040566
    ============================================================
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>用户登录 - 软件2501班第二次作业</title>
    <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>

<body>

<!-- 顶部标题 -->
<div class="container page-header text-center">
    <h1>用户登录</h1>
    <p>软件2501班 第二次作业　杨鑫 25040500　冉铭 25040566</p>
</div>

<!-- 登录表单 -->
<div class="container pb-4" style="max-width: 460px;">
    <div class="card auth-card">
        <div class="card-header">
            <ul class="nav nav-pills justify-content-center gap-2">
                <li class="nav-item"><a class="nav-link active" href="${ctx}/login">登录</a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/register">注册</a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/forgot">忘记密码</a></li>
            </ul>
        </div>

        <div class="card-body">

            <%-- 登录失败时的错误提示 --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <%-- 注册成功后跳转过来时的成功提示 --%>
            <c:if test="${param.registered eq '1'}">
                <div class="alert alert-success py-2" role="alert">
                    注册成功，请使用新账号登录。
                </div>
            </c:if>

            <%-- 提交给 LoginServlet 处理，method 必须是 post --%>
            <form action="${ctx}/login" method="post" autocomplete="off">

                <div class="mb-3">
                    <label for="username" class="form-label">用户名</label>
                    <input type="text" class="form-control" id="username" name="username"
                           value="<c:out value='${username}'/>"
                           placeholder="请输入用户名" required autofocus>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">密码</label>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="请输入密码" required minlength="6">
                </div>

                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="remember" name="remember"
                               value="1">
                        <label class="form-check-label small" for="remember">记住用户名</label>
                    </div>
                    <a href="${ctx}/forgot" class="small link-primary text-decoration-none">忘记密码？</a>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-submit">登 录</button>
            </form>

            <p class="text-center small text-secondary mt-3 mb-0">
                还没有账号？<a href="${ctx}/register" class="text-decoration-none">立即注册</a>
            </p>
        </div>
    </div>
</div>

<div class="page-footer">
    <p class="mb-1">软件2501班　杨鑫 25040500　冉铭 25040566</p>
    <p class="mb-0">JavaWeb 登录 / 注册 / 忘记密码 —— Servlet + MySQL 实现</p>
</div>

<script src="${ctx}/js/bootstrap.bundle.min.js"></script>
</body>

</html>
