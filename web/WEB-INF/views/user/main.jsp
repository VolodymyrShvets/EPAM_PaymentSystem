<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.util.List" %>
<%@ page import="model.bank.BankAccount" %>
<%@ page import="model.bank.Payment" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.title"/></title>
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
            padding: 0 15px;
        }

        #blocked {
            background-color: lightsalmon;
        }
    </style>
</head>
<body>
<div>
    <h1 align="center"><fmt:message key="label.welcome1"/>
        <sysName>MazeBank</sysName>
        <fmt:message key="label.welcome2"/><br><fmt:message key="label.greet"/> ${sessionScope.userName}!
    </h1>
</div>

<p>
    <fmt:message key="label.lang"/>
    <a href="<%= request.getContextPath()%>/self-main?sessionLocale=en">ENG</a>
    <a href="<%= request.getContextPath()%>/self-main?sessionLocale=ua">UA</a>
</p>

<h3><fmt:message key="label.accounts"/></h3>
<form action="<%=request.getContextPath()%>/sorting-servlet" method="post">
    <p><b><fmt:message key="label.sorting"/></b>
        <select name="accountSortMethod">
            <option value="" disabled selected hidden><fmt:message key="label.disabledOption"/></option>
            <option value="1"><fmt:message key="label.accSortVal1"/></option>
            <option value="2"><fmt:message key="label.accSortVal2"/></option>
            <option value="3"><fmt:message key="label.accSortVal3"/></option>
            <option value="4"><fmt:message key="label.accSortVal4"/></option>
        </select>
        <button type="submit"><fmt:message key="label.SortShow"/></button>
    </p>
</form>
<%
    List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accountsList");
    if (accounts.size() == 0) {
%>
<p><fmt:message key="label.zeroAccounts1"/><br><fmt:message key="label.zeroAccounts2"/></p>
<%} else {%>
<table class="accounts">
    <tr>
        <th><fmt:message key="label.tableID"/></th>
        <th><fmt:message key="label.status"/></th>
        <th><fmt:message key="label.accTableCNumb"/></th>
        <th><fmt:message key="label.accTableCVV2"/></th>
        <th><fmt:message key="label.accTableExpDate"/></th>
        <th><fmt:message key="label.accTableMoneyAmount"/></th>
        <th><fmt:message key="label.accTableOperations"/></th>
        <th></th>
    </tr>
    <%
        for (BankAccount account : accounts) {
    %>
    <tr>
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
        <td id="blocked">
            <a role="button"><fmt:message key="label.accBlockFund"/></a>
        </td>
        <td id="blocked">
            <a role="button"
               href="${pageContext.request.contextPath}/contact-admin?id=<%=account.getAccountID()%>"><fmt:message
                    key="label.accBlockAdmin"/></a>
        </td>
        <% } else {%>
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
        <td>
            <a role="button"
               href="${pageContext.request.contextPath}/funding-page?id=<%=account.getAccountID()%>"><fmt:message
                    key="label.accFund"/></a>
        </td>
        <td>
            <a role="button"
               href="${pageContext.request.contextPath}/block-account?id=<%=account.getAccountID()%>"><fmt:message
                    key="label.accBlock"/></a>
        </td>
        <%
                }
            }
        %>
    </tr>
    <%
        }
    %>
</table>

<form action="<%= request.getContextPath()%>/newAccount" method="get">
    <p><input type="submit" value="<fmt:message key="label.newAccount" />"></p>
</form>

<h3><fmt:message key="label.payments"/></h3>

<form action="<%=request.getContextPath()%>/sorting-servlet" method="post">
    <p><b><fmt:message key="label.sorting"/></b>
        <select name="paymentSortMethod">
            <option value="" disabled selected hidden><fmt:message key="label.disabledOption"/></option>
            <option value="1"><fmt:message key="label.paySortVal1"/></option>
            <option value="2"><fmt:message key="label.paySortVal2"/></option>
        </select>
        <button type="submit"><fmt:message key="label.SortShow"/></button>
    </p>
</form>
<%
    List<Payment> payments = (List<Payment>) session.getAttribute("paymentsList");
    if (payments.size() == 0) {
%>
<p><fmt:message key="label.zeroPayments1"/><br><fmt:message key="label.zeroPayments2"/></p>
<%} else {%>
<table class="payments">
    <tr>
        <th><fmt:message key="label.tableID"/></th>
        <th><fmt:message key="label.status"/></th>
        <th><fmt:message key="label.payDate"/></th>
        <th><fmt:message key="label.payRecName"/></th>
        <th><fmt:message key="label.payRecID"/></th>
        <th><fmt:message key="label.paySendName"/></th>
        <th><fmt:message key="label.paySendID"/></th>
        <th><fmt:message key="label.paySum"/></th>
    </tr>
    <%
        for (Payment payment : payments) {
    %>
    <tr>
        <td><%=payment.getPaymentID()%>
        </td>
        <td><%=payment.getStatus()%>
        </td>
        <td><%=payment.getPaymentDate()%>
        </td>
        <td><%=payment.getRecipientName()%>
        </td>
        <td><%=payment.getRecipient()%>
        </td>
        <td><%=payment.getSenderName()%>
        </td>
        <td><%=payment.getSender()%>
        </td>
        <td><%=payment.getPaymentSum()%>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
<form action="<%= request.getContextPath()%>/payment-page" method="get">
    <p><input type="submit" value="<fmt:message key="label.newPayment" />"></p>
</form>

<div class="logout"><a href="${pageContext.request.contextPath}/logout"><fmt:message key="label.logout"/></a></div>
</body>
</html>
