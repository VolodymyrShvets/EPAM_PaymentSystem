<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.fundingPageTitle"/></title>
    <style>
        div {
            margin-top: 10%;
            font-size: larger;
        }

        table {
            font-size: larger;
            padding: 0 15px 0 15px;
        }

        input:invalid {
            border: 2px solid red;
        }

        input:valid {
            border: 2px solid black;
        }

        body {
            background-color: lightgrey;
        }
    </style>
</head>
<body>
<div align="center">
    <p>
    <h2><fmt:message key="label.fundingPageH2"/></h2>
    <h3><fmt:message key="label.fundingPage1"/><br><fmt:message key="label.fundingPage2"/></h3></p>
    <form action="${pageContext.request.contextPath}/fund" method="post">
        <c:set var="accountID" scope="session" value="${param.id}"/>
        <label for="amount"><fmt:message key="label.amount"/></label>
        <input id="amount" type="number" min="0.01" max="100000" step="0.01" name="amount" required>
        <p>
            <button type="submit"><fmt:message key="label.funding"/></button>
        </p>
    </form>
</div>
</body>
</html>
