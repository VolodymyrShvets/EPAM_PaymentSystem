<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <h1 align="center"><fmt:message key="label.welcome1"/>
        <sysName>MazeBank</sysName>
        <fmt:message key="label.welcome2"/></h1>
    <p>
    <h3><fmt:message key="label.newHere1"/><br><fmt:message key="label.newHere2"/></h3>
    </p>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <table>
            <tr>
                <td><fmt:message key="label.firstName"/></td>
                <td><label>
                    <input type="text" name="firstName" required maxlength="20">
                </label></td>
            </tr>
            <tr>
                <td><fmt:message key="label.LastName"/></td>
                <td><label>
                    <input type="text" name="lastName" required maxlength="20">
                </label></td>
            </tr>
            <tr>
                <td><fmt:message key="label.login"/></td>
                <td><label>
                    <input type="text" name="userLogin" required minlength="6" maxlength="45">
                </label></td>
            </tr>
            <tr>
                <td><fmt:message key="label.password"/></td>
                <td><label>
                    <input type="password" name="userPassword" required minlength="8" maxlength="45">
                </label></td>
            </tr>
        </table>
        <p><input type="submit" value="<fmt:message key="label.register" />"></p>
    </form>
    <p>
        <fmt:message key="label.lang"/>
        <a href="${pageContext.request.contextPath}/registration?sessionLocale=en">ENG</a>
        <a href="${pageContext.request.contextPath}/registration?sessionLocale=ua">UA</a>
    </p>
</div>
</body>
</html>
