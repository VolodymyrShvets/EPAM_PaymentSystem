<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    </style>
</head>
<body>
<div align="center">
    <p>
    <h2><fmt:message key="label.fundingPageH2"/></h2>
    <h3><fmt:message key="label.fundingPage1"/><br><fmt:message key="label.fundingPage2"/></h3></p>
    <form action="<%= request.getContextPath()%>/fund" method="post">
        <%
            HttpSession session1 = request.getSession();
            session1.setAttribute("accountID", request.getParameter("id"));
        %>
        <label for="amount"><fmt:message key="label.amount"/></label>
        <input id="amount" type="number" min="0" step="0.01" name="amount">
        <p>
            <button type="submit"><fmt:message key="label.funding"/></button>
        </p>
    </form>
</div>
</body>
</html>
