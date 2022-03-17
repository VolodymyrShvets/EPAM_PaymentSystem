<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Bank.BankAccount" %>
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
    </style>
</head>
<body>
<div align="center">
    <p>
    <h2><fmt:message key="label.accUnblockPageH2"/></h2>
    <h3><fmt:message key="label.accUnblockPage1"/><br><fmt:message key="label.accUnblockPage2"/>
        <br><fmt:message key="label.accUnblockPage3"/></h3>
    </p>
    <form action="<%=request.getContextPath()%>/unblock" method="post">
        <%
            HttpSession session1 = request.getSession();
            session1.setAttribute("accountID", request.getParameter("id"));
        %>
        <select name="accountID">
            <% List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accountsList");
                for (BankAccount account : accounts) {
                    if (account.isBlocked()) { %>
            <option><%=account.getAccountID()%>
            </option>
            <%
                    }
                }
            %>
        </select>
        <p>
            <button type="submit"><fmt:message key="label.sendRequest"/></button>
        </p>
    </form>
</div>
</body>
</html>
