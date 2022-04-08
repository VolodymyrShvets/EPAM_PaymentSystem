<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.accBlockPageTitle"/></title>
    <style>
        div {
            margin-top: 10%;
            font-size: larger;
        }

        table {
            font-size: larger;
            padding: 0 15px 0 15px;
        }

        body {
            background-color: lightgrey;
        }
    </style>
</head>
<body>
<div align="center">
    <p>
    <h2><fmt:message key="label.accBlockPageH2"/></h2>
    <h3><fmt:message key="label.accBlockPage1"/> <%=request.getParameter("id")%><br><fmt:message
            key="label.accBlockPage2"/>
        <br><fmt:message key="label.accBlockPage3"/></h3></p>
    <form action="${pageContext.request.contextPath}/accstatuschanging" method="post">
        <c:set var="accountID" scope="session" value="${param.id}"/>
        <p>
            <button type="submit"><fmt:message key="label.blockAcc"/> <c:out value="${accountID}"/>
            </button>
        </p>
    </form>
</div>
</body>
</html>
