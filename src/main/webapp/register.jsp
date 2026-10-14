<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    ============================================================
    前端页面二：注册页面
    对应 Servlet：com.yangxin.servlet.RegisterServlet（/register）
    姓名：杨鑫 25040500　冉铭 25040566
    ============================================================
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>用户注册 - 软件2501班第二次作业</title>
    <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>

<body>

<!-- 顶部标题 -->
<div class="container page-header text-center">
    <h1>用户注册</h1>
    <p>软件2501班 第二次作业　杨鑫 25040500　冉铭 25040566</p>
</div>

<!-- 注册表单 -->
<div class="container pb-4" style="max-width: 520px;">
    <div class="card auth-card">
        <div class="card-header">
            <ul class="nav nav-pills justify-content-center gap-2">
                <li class="nav-item"><a class="nav-link" href="${ctx}/login">登录</a></li>
                <li class="nav-item"><a class="nav-link active" href="${ctx}/register">注册</a></li>
                <li class="nav-item"><a class="nav-link" href="${ctx}/forgot">忘记密码</a></li>
            </ul>
        </div>

        <div class="card-body">

            <%-- 校验失败时的错误提示 --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger py-2" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <%-- 提交给 RegisterServlet 处理 --%>
            <form action="${ctx}/register" method="post" autocomplete="off">

                <div class="mb-3">
                    <label for="username" class="form-label">
                        用户名 <span class="text-danger">*</span>
                    </label>
                    <input type="text" class="form-control" id="username" name="username"
                           value="<c:out value='${username}'/>"
                           placeholder="4~20 位，字母开头，可含数字和下划线"
                           required minlength="4" maxlength="20"
                           pattern="[A-Za-z][A-Za-z0-9_]{3,19}" autofocus>
                    <div class="form-text">例如：yangxin、yang_2025</div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="password" class="form-label">
                            密码 <span class="text-danger">*</span>
                        </label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="6~20 位" required minlength="6" maxlength="20">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="confirmPassword" class="form-label">
                            确认密码 <span class="text-danger">*</span>
                        </label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" placeholder="请再次输入密码"
                               required minlength="6" maxlength="20">
                    </div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">
                        邮箱 <span class="text-danger">*</span>
                    </label>
                    <input type="email" class="form-control" id="email" name="email"
                           value="<c:out value='${email}'/>"
                           placeholder="用于找回密码，例如 yangxin@example.com"
                           required maxlength="100">
                </div>

                <div class="mb-3">
                    <label for="phone" class="form-label">手机号（选填）</label>
                    <input type="tel" class="form-control" id="phone" name="phone"
                           value="<c:out value='${phone}'/>"
                           placeholder="11 位手机号" maxlength="11"
                           pattern="1[3-9][0-9]{9}">
                </div>

                <div class="form-check mb-3">
                    <input class="form-check-input" type="checkbox" id="agree" required>
                    <label class="form-check-label small" for="agree">
                        我已阅读并同意《用户服务协议》
                    </label>
                </div>

                <button type="submit" class="btn btn-success w-100 btn-submit">立即注册</button>
            </form>

            <p class="text-center small text-secondary mt-3 mb-0">
                已有账号？<a href="${ctx}/login" class="text-decoration-none">返回登录</a>
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
