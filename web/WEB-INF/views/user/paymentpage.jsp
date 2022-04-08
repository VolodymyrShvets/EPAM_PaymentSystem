<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.paymentPageTitle"/></title>
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
    <h2><fmt:message key="label.paymentPageH2"/></h2>
    <h3><fmt:message key="label.paymentPage1"/><br><fmt:message key="label.paymentPage2"/></h3></p>
    <form action="${pageContext.request.contextPath}/newPayment" method="post">
        <c:set var="accountID" scope="session" value="${param.id}"/>
        <table>
            <tr>
                <td>
                    <fmt:message key="label.selectAccID"/>
                </td>
                <td>
                    <select name="senderID">
                        <c:forEach var="account" items="${accountsList}">
                            <c:if test="${!account.isBlocked()}">
                                <option value="${account.getAccountID()}"><c:out
                                        value="${account.getAccountID()}${' - '}${account.getCard().getMoneyAmount()}"/>
                                </option>
                            </c:if>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <fmt:message key="label.recipientID"/>
                </td>
                <td>
                    <input id="recipient" type="number" pattern="\d{9}" step="1" name="recipientID" required
                           title="9-<fmt:message key="label.recipIDvalid"/>">
                </td>
            </tr>
            <tr>
                <td>
                    <fmt:message key="label.amount"/>
                </td>
                <td>
                    <input id="amount" type="number" min="0" max="100000" step="0.01" name="amount" required>
                </td>
            </tr>
            <tr>
                <td>
                    <fmt:message key="label.paymentDate"/>
                </td>
                <td>
                    <input id="payDate" type="date" name="paymentDate" value="${LocalDate.now()}"
                           min="${LocalDate.now()}" required>
                </td>
            </tr>
            <tr>
                <td>
                    <fmt:message key="label.confirmation"/>
                </td>
                <td>
                    <input id="cvv2" type="number" pattern="\d{3}" step="1" name="cvv2" required
                           title="3-<fmt:message key="label.cvv2valid"/>">
                </td>
            </tr>
        </table>
        <p class="paymentError">${sessionScope.paymentError}</p>
        <c:set var="paymentError" value="" scope="request"/>
        <p>
            <button type="submit"><fmt:message key="label.pay"/></button>
        </p>
    </form>
</div>
</body>
</html>
