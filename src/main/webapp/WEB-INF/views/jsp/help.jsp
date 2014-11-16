<%@page import="prototypes.ws.proxy.soap.constantes.ApplicationConfig"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<c:set var="propValidation"><%=ApplicationConfig.PROP_VALIDATION%></c:set>
<c:set var="propBlocking"><%=ApplicationConfig.PROP_BLOCKING_MODE%></c:set>
<c:set var="propWsdl"><%=ApplicationConfig.PROP_WSDL_DIRS%></c:set>
<c:set var="propMax"><%=ApplicationConfig.PROP_MAX_EXCHANGES%></c:set>

    <h2><fmt:message key="help.pagetitle"/></h2>

<h3><fmt:message key="help.section.preconfig.title"/></h3>
<fmt:message key="help.section.preconfig.description"/>
<dl>
    <dt>${propValidation}</dt><dd><fmt:message key="config.${propValidation}.help"/></dd>
    <dt>${propBlocking}</dt><dd><fmt:message key="config.${propBlocking}.help"/></dd>
    <dt>${propWsdl}</dt><dd><fmt:message key="config.${propWsdl}.help"/></dd>
    <dt>${propMax}</dt><dd><fmt:message key="config.${propMax}.help"/></dd>
</dl>
<fmt:message key="Example" /> :
<code>
    -D${propValidation}=true
    -D${propBlocking}=true
    -D${propWsdl}=path1<%=java.io.File.pathSeparator%>path2
    -D${propMax}=100
</code>
<br/>

<h3><fmt:message key="help.section.config.title"/></h3>
<div>
    <fmt:message key="help.section.config.intro" />
    <fmt:message key="help.section.config.cases" />
    <span class="glyphicon glyphicon-info-sign"></span>
    <fmt:message key="help.section.config.multiplecases">
        <fmt:param value="<%=java.io.File.pathSeparator%>"/>
    </fmt:message>
    <br /><fmt:message key="Example" /> :<code>-D${propWsdl}=path1<%=java.io.File.pathSeparator%>path2</code>
    <br/>
</div>

<h4><fmt:message key="help.section.load.title" /></h4>
<p>
    <fmt:message key="help.section.load.txt">
        <fmt:param value="${propValidation}"/>
        <fmt:param value="${propBlocking}"/>
        <fmt:param value="${propMax}"/>
    </fmt:message>
</p>
<p>
    <fmt:message key="Examples" /> :<br/>
    <code><%=request.getContextPath()%>/ui/config?wsdls=http://remotehost/wsdl.jar</code><br/>
    <code><%=request.getContextPath()%>/ui/config?wsdls=E:/tmp/wsdl.jar</code>
</p>

<h4><fmt:message key="help.section.resolution.title" /></h4>
<p>
    <fmt:message key="help.section.resolution.txt" />
</p>