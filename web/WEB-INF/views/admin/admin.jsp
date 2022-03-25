<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.bank.UserRequest" %>
<%@ page import="java.util.List" %>
<%@ page import="model.bank.User" %>
<%@ page import="model.bank.BankAccount" %>
<%@ page import="model.enums.RequestType" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${param.lang}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title><fmt:message key="label.adminPageTitle"/></title>
    <style>
        div {
            font-size: larger;
            text-align: center;
        }

        sysName {
            color: crimson;
            font-family: Tahoma;
        }

        table {
            font-size: larger;
            border: 1px solid black;
            border-spacing: 7px 2px;
        }

        td {
            border: 1px solid black;
            padding: 0 15px 0 15px;
        }

        #blocked {
            background-color: lightsalmon;
        }

        #unblocked {
            background-color: lightgreen;
        }

        #requests {
            background-color: gold;
        }
    </style>
</head>
<body>
<h1 align="center"><fmt:message key="label.welcome1"/>
    <sysName>MazeBank</sysName>
    <fmt:message key="label.welcome2"/><br><fmt:message key="label.greet"/> ${sessionScope.userName}!
</h1>
<p>
    <fmt:message key="label.lang"/>
    <a href="<%=request.getContextPath()%>/self-admin">ENG</a>
    <a href="<%=request.getContextPath()%>/self-admin?lang=ua">UA</a>
</p>

<h3><fmt:message key="label.requests"/></h3>
<%
    List<UserRequest> requestList = (List<UserRequest>) session.getAttribute("requests");
    if (requestList.size() == 0) {
%>
<p><fmt:message key="label.noRequests"/></p>
<% } else { %>
<table class="requests">
    <tr>
        <th><fmt:message key="label.reqID"/></th>
        <th><fmt:message key="label.reqType"/></th>
        <th><fmt:message key="label.userID"/></th>
        <th><fmt:message key="label.reqAccountID"/></th>
        <th><fmt:message key="label.reqOperation"/></th>
    </tr>
    <%
        for (UserRequest req : requestList) {
    %>
    <tr>
        <td id="requests"><%=req.getRequestID()%>
        </td>
        <td id="requests"><%=req.getType()%>
        </td>
        <td id="requests"><%=req.getUserID()%>
        </td>
        <% if (req.getAccountID() == -1) {%>
        <td id="requests">---</td>
        <%} else {%>
        <td id="requests"><%=req.getAccountID()%>
        </td>
        <%}%>
        <%if (req.getType() == RequestType.ACCOUNT) {%>
        <td id="unblocked">
            <form action="<%=request.getContextPath()%>/change-account-status?id=<%=req.getAccountID()%>&amp;operation=<%=false%>"
                  method="post">
                <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
            </form>
        </td>
        <%} else {%>
        <td id="unblocked">
            <form action="<%=request.getContextPath()%>/change-user-status?id=<%=req.getUserID()%>&amp;operation=<%=false%>"
                  method="post">
                <button type="submit"><fmt:message key="label.reqUnblockUser"/></button>
            </form>
        </td>
        <%}%>
    </tr>
    <%
        }
    %>
</table>
<%
    }
%>

<h3><fmt:message key="label.users"/></h3>
<%
    List<User> userList = (List<User>) session.getAttribute("users");
    if (userList.size() == 0) {
%>
<p><fmt:message key="label.noUsers"/></p>
<% } else { %>
<table>
    <tr>
        <th><fmt:message key="label.userID"/></th>
        <th><fmt:message key="label.status"/></th>
        <th><fmt:message key="label.userFullName"/></th>
        <th><fmt:message key="label.userAccountID"/></th>
        <th><fmt:message key="label.userAccStatus"/></th>
        <th><fmt:message key="label.accTableCNumb"/></th>
        <th><fmt:message key="label.accTableCVV2"/></th>
        <th><fmt:message key="label.accTableExpDate"/></th>
        <th><fmt:message key="label.accTableMoneyAmount"/></th>
        <th><fmt:message key="label.reqOperation"/></th>
    </tr>
    <%
        for (User user : userList) {
    %>
    <tr>
        <%if (user.isBlocked()) {%>
        <td id="blocked"><%=user.getUserID()%>
        </td>
        <td id="blocked"><%=user.getStatus()%>
        </td>
        <td id="blocked"><%=user.getFirstName() + " " + user.getLastName()%>
        </td>
        <%} else {%>
        <td><%=user.getUserID()%>
        </td>
        <td><%=user.getStatus()%>
        </td>
        <td><%=user.getFirstName() + " " + user.getLastName()%>
        </td>
        <%}%>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <%if (user.isBlocked()) {%>
        <td id="unblocked">
            <form action="<%=request.getContextPath()%>/change-user-status?id=<%=user.getUserID()%>&amp;operation=<%=false%>"
                  method="post">
                <button type="submit"><fmt:message key="label.reqUnblockUser"/></button>
            </form>
        </td>
        <%} else {%>
        <td>
            <form action="<%=request.getContextPath()%>/change-user-status?id=<%=user.getUserID()%>&amp;operation=<%=true%>"
                  method="post">
                <button type="submit"><fmt:message key="label.blockUser"/></button>
            </form>
                <%}%>
    </tr>
    <%
        for (BankAccount account : user.getAccounts()) {%>
    <tr>
        <td></td>
        <td></td>
        <td></td>
        <%if (account.isBlocked()) {%>
        <td id="blocked"><%=account.getAccountID()%>
        </td>
        <td id="blocked"><%=account.getStatus()%>
        </td>
        <td id="blocked"><%=account.getCard().getCardNumber()%>
        </td>
        <td id="blocked"><%=account.getCard().getCvv2()%>
        </td>
        <td id="blocked"><%=account.getCard().getExpirationDate()%>
        </td>
        <td id="blocked"><%=account.getCard().getMoneyAmount()%>
        </td>
        <%if (account.isBlocked()) {%>
        <td id="unblocked">
            <form action="<%=request.getContextPath()%>/change-account-status?id=<%=account.getAccountID()%>&amp;operation=<%=false%>"
                  method="post">
                <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
            </form>
        </td>
        <%} else {%>
        <td>
            <form action="<%=request.getContextPath()%>/change-account-status?id=<%=account.getAccountID()%>&amp;operation=<%=true%>"
                  method="post">
                <button type="submit"><fmt:message key="label.blockAcc"/></button>
            </form>
                <%}%>
                <%} else {%>
        <td><%=account.getAccountID()%>
        </td>
        <td><%=account.getStatus()%>
        </td>
        <td><%=account.getCard().getCardNumber()%>
        </td>
        <td><%=account.getCard().getCvv2()%>
        </td>
        <td><%=account.getCard().getExpirationDate()%>
        </td>
        <td><%=account.getCard().getMoneyAmount()%>
        </td>
        <%if (account.isBlocked()) {%>
        <td id="unblocked">
            <form action="<%=request.getContextPath()%>/change-account-status?id=<%=account.getAccountID()%>&amp;operation=<%=false%>"
                  method="post">
                <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
            </form>
        </td>
        <%} else {%>
        <td>
            <form action="<%=request.getContextPath()%>/change-account-status?id=<%=account.getAccountID()%>&amp;operation=<%=true%>"
                  method="post">
                <button type="submit"><fmt:message key="label.blockAcc"/></button>
            </form>
                <%}%>
    </tr>
    <%
                }
            }
        }
    %>
</table>
<%
    }
%>
<div class="logout"><a href="${pageContext.request.contextPath}/logout"><fmt:message key="label.logout"/></a></div>
</body>
</html>
