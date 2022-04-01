<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.bank.BankAccount" %>
<%@ page import="java.util.List" %>
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
    </style>
</head>
<body>
<div align="center">
    <p>
    <h2><fmt:message key="label.paymentPageH2"/></h2>
    <h3><fmt:message key="label.paymentPage1"/><br><fmt:message key="label.paymentPage2"/><br><fmt:message
            key="label.paymentPage3"/></h3></p>
    <form action="<%=request.getContextPath()%>/newPayment" method="get">
        <%
            HttpSession session1 = request.getSession();
            session1.setAttribute("accountID", request.getParameter("id"));
        %>
        <p>
            <input type="radio" id="sentPayment"
                   name="paymentType" value="SENT" required>
            <label for="sentPayment"><fmt:message key="label.paymentSent"/></label>
            <input type="radio" id="preparedPayment"
                   name="paymentType" value="PREPARED">
            <label for="preparedPayment"><fmt:message key="label.paymentPrepared"/></label>
        </p>
        <table>
            <tr>
                <td>
                    <fmt:message key="label.selectAccID"/>
                </td>
                <td>
                    <select name="senderID">
                        <%
                            List<BankAccount> accounts = (List<BankAccount>) session.getAttribute("accountsList");
                            for (BankAccount account : accounts) {
                                if (!account.isBlocked()) {%>
                        <option value="<%=account.getAccountID()%>"><%=account.getAccountID() + " - " + account.getCard().getMoneyAmount()%>
                        </option>
                        <%
                                }
                            }
                        %>
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
                    <input id="payDate" type="date" name="paymentDate" value="<%=LocalDate.now()%>" min="<%=LocalDate.now()%>">
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
        <% session1.setAttribute("paymentError", "");%>
        <p>
            <button type="submit"><fmt:message key="label.pay"/></button>
        </p>
    </form>
</div>
</body>
</html>
