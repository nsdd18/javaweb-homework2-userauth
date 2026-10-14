<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    ============================================================
    前端页面三：忘记密码页面（分两步：① 验证身份 ② 设置新密码）
    对应 Servlet：com.yangxin.servlet.ForgotPasswordServlet（/forgot）
    姓名：杨鑫    学号：25040500
    ============================================================
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- 判断是否已经通过第一步的身份验证（该标记由 Servlet 放入 Session） --%>
<c:set var="verified" value="${not empty sessionScope.resetUser}"/>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>忘记密码 - 软件2501班第二次作业</title>
    <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>

<body>

<!-- 顶部标题 -->
<div class="container page-header text-center">
    <h1>忘记密码</h1>
    <p>软件2501班 第二次作业　姓名：杨鑫　学号：25040500</p>
</div>

<!-- 忘记密码表单 -->
<div class="container pb-4" style="max-width: 520px;">
    <div class="card auth-card">
        <div class="card-header">
            <ul class="nav nav-pills justify-content-center gap-2">
                <li class="nav-item"><a class="nav-link" href="${ctx}/login">登录</a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/register">注册</a></li>
                <li class="nav-item"><a class="nav-link active" href="${ctx}/forgot">忘记密码</a></li>
            </ul>
        </div>

        <div class="card-body">

            <%-- 错误提示 --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <%-- 步骤指示条 --%>
            <div class="d-flex align-items-center justify-content-center mb-3 small">
                <span class="badge ${verified ? 'bg-secondary' : 'bg-primary'} rounded-pill">1</span>
                <span class="mx-2 ${verified ? 'text-secondary' : 'fw-bold text-primary'}">验证身份</span>
                <span class="text-secondary">――――</span>
                <span class="badge ${verified ? 'bg-primary' : 'bg-secondary'} rounded-pill ms-2">2</span>
                <span class="mx-2 ${verified ? 'fw-bold text-primary' : 'text-secondary'}">设置新密码</span>
            </div>

            <c:choose>

                <%-- ================= 第一步：验证用户名与注册邮箱 ================= --%>
                <c:when test="${not verified}">
                    <p class="small text-secondary">
                        请输入你的用户名和注册时填写的邮箱，两者匹配才能重置密码。
                    </p>

                    <form action="${ctx}/forgot" method="post" autocomplete="off">
                        <%-- 隐藏域：告诉 Servlet 当前是第一步 --%>
                        <input type="hidden" name="step" value="1">

                        <div class="mb-3">
                            <label for="username" class="form-label">用户名</label>
                            <input type="text" class="form-control" id="username" name="username"
                                   value="<c:out value='${username}'/>"
                                   placeholder="请输入用户名" required autofocus>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">注册邮箱</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<c:out value='${email}'/>"
                                   placeholder="请输入注册时填写的邮箱" required maxlength="100">
                        </div>

                        <button type="submit" class="btn btn-warning w-100 btn-submit text-white">
                            下一步：验证身份
                        </button>
                    </form>
                </c:when>

                <%-- ================= 第二步：设置新密码 ================= --%>
                <c:otherwise>
                    <p class="small text-secondary">
                        身份验证通过：<strong class="text-success">
                        <c:out value="${sessionScope.resetUser}"/></strong>，
                        请设置新密码。
                    </p>

                    <form action="${ctx}/forgot" method="post" autocomplete="off">
                        <%-- 隐藏域：告诉 Servlet 当前是第二步 --%>
                        <input type="hidden" name="step" value="2">

                        <div class="mb-3">
                            <label for="newPassword" class="form-label">新密码</label>
                            <input type="password" class="form-control" id="newPassword"
                                   name="newPassword" placeholder="6~20 位"
                                   required minlength="6" maxlength="20" autofocus>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">确认新密码</label>
                            <input type="password" class="form-control" id="confirmPassword"
                                   name="confirmPassword" placeholder="请再次输入新密码"
                                   required minlength="6" maxlength="20">
                        </div>

                        <button type="submit" class="btn btn-primary w-100 btn-submit">
                            确认重置密码
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>

            <p class="text-center small text-secondary mt-3 mb-0">
                想起密码了？<a href="${ctx}/login" class="text-decoration-none">返回登录</a>
            </p>
        </div>
    </div>
</div>

<div class="page-footer">
    <p class="mb-1">JavaWeb 登录 / 注册 / 忘记密码 —— Servlet + MySQL 实现</p>
    <p class="mb-0">软件2501班　杨鑫　25040500</p>
</div>

<script src="${ctx}/js/bootstrap.bundle.min.js"></script>
</body>

</html>
