<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<fmt:setLocale value="${sessionScope.lang}"/>
<fmt:setBundle basename="messages"/>

<html lang="${sessionScope.lang}">
<head>
    <title><fmt:message key="label.accBlockPageTitle"/></title>
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
    <h2><fmt:message key="label.accBlockPageH2"/></h2>
    <h3><fmt:message key="label.accBlockPage1"/> <%=request.getParameter("id")%><br><fmt:message
            key="label.accBlockPage2"/>
        <br><fmt:message key="label.accBlockPage3"/></h3></p>
    <form action="<%= request.getContextPath()%>/accstatuschanging" method="get">
        <%
            HttpSession session1 = request.getSession();
            session1.setAttribute("accountID", request.getParameter("id"));
        %>
        <p>
            <button type="submit"><fmt:message key="label.blockAcc"/> <%=request.getParameter("id")%>
            </button>
        </p>
    </form>
</div>
</body>
</html>
