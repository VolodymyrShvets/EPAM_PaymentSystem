<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

        body {
            background-color: lightgrey;
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
    <a href="${pageContext.request.contextPath}/self-admin">ENG</a>
    <a href="${pageContext.request.contextPath}/self-admin?lang=ua">UA</a>
</p>

<h3><fmt:message key="label.requests"/></h3>
<c:set var="requestList" scope="page" value="${sessionScope.requests}"/>
<c:if test="${requestList.size() == 0}">
    <p><fmt:message key="label.noRequests"/></p>
</c:if>
<c:if test="${requestList.size() > 0}">
    <table class="requests">
        <tr>
            <th><fmt:message key="label.reqID"/></th>
            <th><fmt:message key="label.reqType"/></th>
            <th><fmt:message key="label.userID"/></th>
            <th><fmt:message key="label.reqAccountID"/></th>
            <th><fmt:message key="label.reqOperation"/></th>
        </tr>
        <c:forEach var="req" items="${requestList}">
            <tr>
                <td id="requests">
                    <c:out value="${req.getRequestID()}"/>
                </td>
                <td id="requests">
                    <c:out value="${req.getType()}"/>
                </td>
                <td id="requests">
                    <c:out value="${req.getUserID()}"/>
                </td>
                <c:if test="${req.getAccountID() == -1}">
                    <td id="requests">---</td>
                </c:if>
                <c:if test="${req.getAccountID() != -1}">
                    <td id="requests">
                        <c:out value="${req.getAccountID()}"/>
                    </td>
                </c:if>
                <c:if test="${req.getType() == RequestType.ACCOUNT}">
                    <td id="unblocked">
                        <form action="${pageContext.request.contextPath}/change-account-status?id=${req.getAccountID()}&amp;operation=<%=false%>"
                              method="get">
                            <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
                        </form>
                    </td>
                </c:if>
                <c:if test="${req.getType() != RequestType.ACCOUNT}">
                    <td id="unblocked">
                        <form action="${pageContext.request.contextPath}/change-user-status?id=${req.getUserID()}&amp;operation=<%=false%>"
                              method="get">
                            <button type="submit"><fmt:message key="label.reqUnblockUser"/></button>
                        </form>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
</c:if>
<h3><fmt:message key="label.users"/></h3>
<c:set var="userList" scope="page" value="${sessionScope.users}"/>
<c:if test="${userList.size() == 0}">
    <p><fmt:message key="label.noUsers"/></p>
</c:if>
<c:if test="${userList.size() != 0}">
    <table>
        <tr>
            <th><fmt:message key="label.userID"/></th>
            <th><fmt:message key="label.status"/></th>
            <th><fmt:message key="label.userFullName"/></th>
            <th><fmt:message key="label.userAccountID"/></th>
            <th><fmt:message key="label.userAccStatus"/></th>
            <th><fmt:message key="label.accTableCNumb"/></th>
            <th><fmt:message key="label.accTableExpDate"/></th>
            <th><fmt:message key="label.reqOperation"/></th>
        </tr>
        <c:forEach var="user" items="${userList}">
            <tr>
                <c:if test="${user.isBlocked()}">
                    <td id="blocked">
                        <c:out value="${user.getUserID()}"/>
                    </td>
                    <td id="blocked">
                        <c:out value="${user.getStatus()}"/>
                    </td>
                    <td id="blocked">
                        <c:out value="${user.getFirstName()}${' '}${user.getLastName()}"/>
                    </td>
                </c:if>
                <c:if test="${!user.isBlocked()}">
                    <td>
                        <c:out value="${user.getUserID()}"/>
                    </td>
                    <td>
                        <c:out value="${user.getStatus()}"/>
                    </td>
                    <td>
                        <c:out value="${user.getFirstName()}${' '}${user.getLastName()}"/>
                    </td>
                </c:if>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <c:if test="${user.isBlocked()}">
                    <td id="unblocked">
                        <form action="${pageContext.request.contextPath}/change-user-status?id=${user.getUserID()}&amp;operation=<%=false%>"
                              method="post">
                            <button type="submit"><fmt:message key="label.reqUnblockUser"/></button>
                        </form>
                    </td>
                </c:if>
                <c:if test="${!user.isBlocked()}">
                    <td>
                        <form action="${pageContext.request.contextPath}/change-user-status?id=${user.getUserID()}&amp;operation=<%=true%>"
                              method="post">
                            <button type="submit"><fmt:message key="label.blockUser"/></button>
                        </form>
                    </td>
                </c:if>
            </tr>
            <c:forEach var="account" items="${user.getAccounts()}">
                <tr>
                <td></td>
                <td></td>
                <td></td>
                <c:if test="${account.isBlocked()}">
                    <td id="blocked"><c:out value="${account.getAccountID()}"/>
                    </td>
                    <td id="blocked"><c:out value="${account.getStatus()}"/>
                    </td>
                    <td id="blocked"><c:out value="${account.getCard().getCardNumber()}"/>
                    </td>
                    <td id="blocked"><c:out value="${account.getCard().getExpirationDate()}"/>
                    </td>
                    <c:if test="${account.isBlocked()}">
                        <td id="unblocked">
                            <form action="${pageContext.request.contextPath}/change-account-status?id=${account.getAccountID()}&amp;operation=<%=false%>"
                                  method="post">
                                <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
                            </form>
                        </td>
                    </c:if>
                    <c:if test="${!account.isBlocked()}">
                        <td>
                        <form action="${pageContext.request.contextPath}/change-account-status?id=${account.getAccountID()}&amp;operation=<%=true%>"
                              method="post">
                            <button type="submit"><fmt:message key="label.blockAcc"/></button>
                        </form>
                    </c:if>
                </c:if>
                <c:if test="${!account.isBlocked()}">
                    <td><c:out value="${account.getAccountID()}"/>
                    </td>
                    <td><c:out value="${account.getStatus()}"/>
                    </td>
                    <td><c:out value="${account.getCard().getCardNumber()}"/>
                    </td>
                    <td><c:out value="${account.getCard().getExpirationDate()}"/>
                    </td>
                    <c:if test="${account.isBlocked()}">
                        <td id="unblocked">
                            <form action="${pageContext.request.contextPath}/change-account-status?id=${account.getAccountID()}&amp;operation=<%=false%>"
                                  method="post">
                                <button type="submit"><fmt:message key="label.reqUnblockAcc"/></button>
                            </form>
                        </td>
                    </c:if>
                    <c:if test="${!account.isBlocked()}">
                        <td>
                            <form action="${pageContext.request.contextPath}/change-account-status?id=${account.getAccountID()}&amp;operation=<%=true%>"
                                  method="post">
                                <button type="submit"><fmt:message key="label.blockAcc"/></button>
                            </form>
                        </td>
                    </c:if>
                    </tr>
                </c:if>
            </c:forEach>
        </c:forEach>
    </table>
</c:if>
<div class="logout"><a href="${pageContext.request.contextPath}/logout"><fmt:message key="label.logout"/></a></div>
</body>
</html>
