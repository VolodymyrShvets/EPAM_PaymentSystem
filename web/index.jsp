<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.title"/></title>
    <style>
        div {
            margin-top: 10%;
            font-size: larger;
        }

        sysName {
            color: crimson;
            font-family: Tahoma;
        }

        table {
            font-size: larger;
        }

        input:invalid:required {
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
    <h1 align="center"><fmt:message key="label.welcome1"/>
        <sysName>MazeBank</sysName>
        <fmt:message key="label.welcome2"/></h1>
    <p>
    <h2><fmt:message key="label.login1"/></h2></p>
    <form action="${pageContext.request.contextPath}/login" method="get">
        <table>
            <tr>
                <td><fmt:message key="label.login"/></td>
                <td><label>
                    <input type="text" name="username" required minlength="6" maxlength="45">
                </label></td>
            </tr>
            <tr>
                <td><fmt:message key="label.password"/></td>
                <td><label>
                    <input type="password" name="password" required minlength="8" maxlength="45">
                </label></td>
            </tr>
        </table>
        <p class="loginResult">${sessionScope.loginResult}</p>
        <p><input type="submit" value="<fmt:message key="label.login2" />"></p>
    </form>
    <p><fmt:message key="label.firstTime"/><br>
        <a href="${pageContext.request.contextPath}/registration"><fmt:message key="label.newProfile"/></a></p>
    <p>
        <fmt:message key="label.lang"/>
        <a href="${pageContext.request.contextPath}/self-index?sessionLocale=en">ENG</a>
        <a href="${pageContext.request.contextPath}/self-index?sessionLocale=ua">UA</a>
    </p>
</div>
</body>
</html>
