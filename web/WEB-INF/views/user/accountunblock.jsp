<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.unblockPageTitle"/></title>
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
    <h2><fmt:message key="label.accUnblockPageH2"/></h2>
    <h3><fmt:message key="label.accUnblockPage1"/><br><fmt:message key="label.accUnblockPage2"/>
        <br><fmt:message key="label.accUnblockPage3"/></h3>
    </p>
    <form action="${pageContext.request.contextPath}/unblock" method="post">
        <c:set var="accountID" scope="session" value="${param.id}"/>
        <select name="accountID">
            <c:forEach var="account" items="${accountsList}">
                <c:if test="${account.isBlocked()}">
                    <option><c:out value="${account.getAccountID()}"/>
                    </option>
                </c:if>
            </c:forEach>
        </select>
        <p>
            <button type="submit"><fmt:message key="label.sendRequest"/></button>
        </p>
    </form>
</div>
</body>
</html>
