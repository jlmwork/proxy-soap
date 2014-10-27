<%@page import="prototypes.ws.proxy.soap.constantes.ApplicationConfig"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>

<h2><fmt:message key="help.pagetitle"/></h2>

<h3><fmt:message key="help.section.preconfig.title"/></h3>
<fmt:message key="help.section.preconfig.description"/>
<dl>
    <dt><%=ApplicationConfig.PROP_VALIDATION%></dt><dd><fmt:message key="help.section.preconfig.proxy.active"/></dd>
    <dt><%=ApplicationConfig.PROP_BLOCKING_MODE%></dt><dd><fmt:message key="help.section.preconfig.proxy.blocking"/></dd>
    <dt><%=ApplicationConfig.PROP_WSDL_DIRS%></dt><dd><fmt:message key="help.section.preconfig.proxy.wsdls"/></dd>
    <dt><%=ApplicationConfig.PROP_MAX_EXCHANGES%></dt><dd><fmt:message key="help.section.preconfig.proxy.maxexchanges"/></dd>
</dl>
<fmt:message key="Example" /> :
<code>
    -D<%=ApplicationConfig.PROP_VALIDATION%>=true
    -D<%=ApplicationConfig.PROP_BLOCKING_MODE%>=true
    -D<%=ApplicationConfig.PROP_WSDL_DIRS%>=path1<%=java.io.File.pathSeparator%>path2
    -D<%=ApplicationConfig.PROP_MAX_EXCHANGES%>=100
</code>
<br/>

<h3><fmt:message key="help.section.config.title"/></h3>
<p>

    <fmt:message key="help.section.config.intro" />
    <fmt:message key="help.section.config.cases" />
    <span class="glyphicon glyphicon-info-sign"></span>
    <fmt:message key="help.section.config.multiplecases">
        <fmt:param value="<%=java.io.File.pathSeparator%>"/>
    </fmt:message>
    <br /><fmt:message key="Example" /> :<code>-D<%=ApplicationConfig.PROP_WSDL_DIRS%>=path1<%=java.io.File.pathSeparator%>path2</code>
    <br/>
</p>

<h4><fmt:message key="help.section.load.title" /></h4>
<p>
    <fmt:message key="help.section.load.txt">
        <fmt:param value="<%=ApplicationConfig.PROP_VALIDATION%>"/>
        <fmt:param value="<%=ApplicationConfig.PROP_BLOCKING_MODE%>"/>
        <fmt:param value="<%=ApplicationConfig.PROP_MAX_EXCHANGES%>"/>
    </fmt:message>
<p>

    <fmt:message key="Examples" /> :<br/>
    <code><%=request.getContextPath()%>/ui/config?wsdls=http://remotehost/wsdl.jar</code><br/>
    <code><%=request.getContextPath()%>/ui/config?wsdls=E:/tmp/wsdl.jar</code>
</p>

<h4><fmt:message key="help.section.resolution.title" /></h4>
<fmt:message key="help.section.resolution.txt" />

</p>