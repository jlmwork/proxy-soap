<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<style type="text/css" scoped="scoped">.anchor{position:relative;top:-60px;}</style>
<h2><fmt:message key="validators.title"/></h2>
<p><em><fmt:message key="validators.description"/></em></p>
<table class="table table-bordered" id="logs" data-toggle="table" data-height="500">
    <thead>
        <tr>
            <th><fmt:message key="validators.name"/></th>
            <th><fmt:message key="validators.wsdl.path"/></th>
            <th><fmt:message key="validators.wsdl.operations"/></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="validator" items="${validators}">
            <tr>
                <td><span id="${validator.key}" class="anchor">&nbsp;</span>${validator.key}</td>
                <td>
                    <fmt:message key="validators.created.from"/> : ${validator.value.from}
                    <jsp:useBean id="dateValue" class="java.util.Date" scope="page" />
                    <jsp:setProperty name="dateValue" property="time" value="${validator.value.creationTime}" />
                    <br /><fmt:message key="validators.created.on"/> : <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss"  value="${dateValue}" />
                    <br /><a target="_blank" href="ui/action/viewWSDL?validator=${validator.key}" title="${validator.value.url}"><fmt:message key="validators.wsdl.view"/></a>
                </td>
                <td>
                    <ul>
                        <c:if test="${!empty validator.value.operations}">
                            <c:forEach var="operation" items="${validator.value.operations}">
                                <li>${operation}</li>
                                </c:forEach>
                            </c:if>
                    </ul>
                </td>
            </c:forEach>
    </tbody>
</table>
