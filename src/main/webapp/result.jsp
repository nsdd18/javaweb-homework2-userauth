<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    ============================================================
    统一结果页面：三个 Servlet 处理成功后都转发到这里
    通过 request 属性控制显示内容：
      resultType    success / error
      resultTitle   标题
      resultMessage 主要内容
      resultDetails 细节列表（String[]）
      backUrl       按钮跳转地址
      backText      按钮文字
    姓名：杨鑫    学号：25040500
    ============================================================
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="ok" value="${resultType ne 'error'}"/>
<!DOCTYPE html>
<html lang="zh-CN">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${resultTitle}"/> - 软件2501班第二次作业</title>
    <link rel="stylesheet" href="${ctx}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>

<body>

<div class="container page-header text-center">
    <h1>操作结果</h1>
    <p>软件2501班 第二次作业　姓名：杨鑫　学号：25040500</p>
</div>

<div class="container pb-4" style="max-width: 520px;">
    <div class="card auth-card">
        <div class="card-body text-center">

            <%-- 结果图标与标题 --%>
            <div class="display-4 ${ok ? 'text-success' : 'text-danger'}">
                <c:choose>
                    <c:when test="${ok}">&#10004;</c:when>
                    <c:otherwise>&#10006;</c:otherwise>
                </c:choose>
            </div>
            <h4 class="mt-2 mb-3 ${ok ? 'text-success' : 'text-danger'}">
                <c:out value="${resultTitle}"/>
            </h4>

            <%-- 主要信息 --%>
            <p class="mb-3"><c:out value="${resultMessage}"/></p>

            <%-- 细节信息列表 --%>
            <c:if test="${not empty resultDetails}">
                <ul class="list-group list-group-flush text-start small mb-3">
                    <c:forEach var="detail" items="${resultDetails}">
                        <li class="list-group-item bg-transparent">
                            <c:out value="${detail}"/>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>

            <%-- 操作按钮 --%>
            <div class="d-grid gap-2">
                <a href="${empty backUrl ? ctx : backUrl}"
                   class="btn btn-primary btn-submit">
                    <c:out value="${empty backText ? '返回首页' : backText}"/>
                </a>
                <a href="${ctx}/login" class="btn btn-outline-secondary btn-sm">返回登录页</a>
            </div>
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
