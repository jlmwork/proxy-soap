<%@page import="prototypes.ws.proxy.soap.constantes.ApplicationConfig"%>
<%@page import="prototypes.ws.proxy.soap.configuration.ProxyConfiguration"%>
<%@page import="prototypes.ws.proxy.soap.monitor.SoapRequestMonitor"%>
<%@page import="prototypes.ws.proxy.soap.monitor.MonitorManager"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<%
    ProxyConfiguration proxy = (ProxyConfiguration) application.getAttribute(ProxyConfiguration.UID);
    pageContext.setAttribute("proxy", proxy);
    MonitorManager monitor = (MonitorManager) application.getAttribute(MonitorManager.UID);
    request.setAttribute("requestList", monitor.getRequests());
%>

<style>.white,.white:hover,.white:visited{color:white;}</style>
<h2><fmt:message key="logs.pagetitle"/> <small class="autorefresh"><fmt:message key="logs.autorefresh"/> [<span>off</span>]</small>
    <c:if test="${!empty requestList}">
        <small class="goright">
            <a href="ui/action/clearRequests" class="goright" title="<fmt:message key="requests.clear"/>"><span class="glyphicon glyphicon-trash"></span></a>
            <a href="requests?accept=text/csv" id="export" class="goright glyphicon glyphicon-cloud-download" title="<fmt:message key="requests.export"/>"></a>
        </small>
    </c:if>
</h2>
<c:choose>
    <c:when test="${!proxy.validationActive}">
        <c:set var="messageType" value="panel-danger" />
        <c:set var="message"><fmt:message key="config.proxy.soap.validate"/> <fmt:message key="config.proxy.soap.validate.false"/></c:set>
    </c:when>
    <c:when test="${proxy.blockingMode}">
        <c:set var="messageType" value="panel-warning" />
        <c:set var="message"><fmt:message key="config.proxy.soap.blockingmode"/> <fmt:message key="config.proxy.soap.blockingmode.true"/></c:set>
    </c:when>
</c:choose>
<div class="panel ${messageType}">
    <%-- Panel header --%>
    <div class="panel-heading">${message}</div>
    <table class="table table-bordered table-striped table-hover table-condensed" id="logs">
        <thead>
            <tr class="text-center">
                <th>#</th>
                <th><fmt:message key="logs.request.date"/></th>
                <th><fmt:message key="logs.request.from"/></th>
                <th><fmt:message key="logs.request.to"/></th>
                <th><fmt:message key="logs.request.validator"/></th>
                <th><fmt:message key="logs.request.operation"/></th>
                <th><fmt:message key="logs.request.request"/></th>
                <th><fmt:message key="logs.request.response"/></th>
                <th><fmt:message key="logs.request.response.time"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="call" items="${requestList}" varStatus="loop">
                <c:choose>
                    <c:when test="${ ! empty call.request && ! empty call.validatorId }">
                        <c:set var="viewWsdl"><a target="_blank" href="ui/action/viewWSDL?validator=${call.validatorId}" title="<fmt:message key="logs.request.validated.by"/> ${call.validatorId} (<fmt:message key="logs.request.click.viewwsdl" />)" class="glyphicon glyphicon-file"></a></c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="viewWsdl"><span title="<fmt:message key="logs.request.error.nowsdl" />" class="glyphicon glyphicon-remove-sign"></span></c:set>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${ call.requestValid && call.responseValid }">
                    <c:set var="status" value="success" />
                </c:when>
                <c:when test="${ (not call.requestValid || not call.responseValid) && !empty call.validatorId }">
                    <c:set var="status" value="danger" />
                </c:when>
                <c:otherwise>
                    <c:set var="status" value="warning" />
                </c:otherwise>
            </c:choose>

            <tr class="${status}">
                <td><a href="#" title="${call.id}">#</a></td>
                <td><small>${call.date}</small></td>
                <td><small>${call.from}</small></td>
                <td><small>${call.uri}</small></td>
                <td><small><a href="ui/validators#${call.validatorId}">${call.validatorId}</a></small></td>
                <td><small>${call.operation}</small></td>

                <%--  ================================ --%>
                <%--  ========== Request  ============ --%>
                <td><c:choose>
                        <c:when test="${call.requestXmlValid}">
                            <span class="label label-success">XML Valid</span>
                        </c:when>
                        <c:when test="${empty call.requestXmlValid}">
                            <span class="label label-default">No XML validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">XML Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${call.requestSoapValid}">
                            <span class="label label-success">SOAP Valid</span>
                        </c:when>
                        <c:when test="${empty call.requestSoapValid}">
                            <span class="label label-default">No SOAP Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">SOAP Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <div class="validation-actions">
                        <c:if test="${ ! empty call.request }">
                            <a class="glyphicon glyphicon-eye-open" data-toggle="modal"
                               href="#reqModal_${loop.index}" title="view Request content"></a>
                        </c:if>
                        <c:if test="${ ! empty call.request && ! empty call.validatorId }">
                            ${viewWsdl}
                        </c:if>

                        <c:if test="${ ! empty call.requestSoapErrors || ! empty call.requestXmlErrors}">
                            <a class="glyphicon error glyphicon-exclamation-sign"
                               data-toggle="modal" href="#reqErrors_${loop.index}"
                               title="<fmt:message key="logs.request.view.errors" />"></a>
                        </c:if>

                        <div class="modal fade" id="reqModal_${loop.index}">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal"
                                                aria-hidden="true">&times;</button>
                                        <h4 class="modal-title">Request content</h4>
                                    </div>
                                    <div class="modal-body">
                                        <pre><code class="xml"><c:out escapeXml="true" value="${call.requestAsXML}" /></code></pre>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default"
                                                data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <!-- /.modal -->

                        <div class="modal fade" id="reqErrors_${loop.index}">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal"
                                                aria-hidden="true">&times;</button>
                                        <h4 class="modal-title">Request errors</h4>
                                    </div>
                                    <div class="modal-body text-left">
                                        <c:if test="${ ! empty call.requestXmlErrors}">
                                            <h5 class="modal-title">XML errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ call.requestXmlErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                        <c:if test="${ ! empty call.requestSoapErrors}">
                                            <h5 class="modal-title">SOAP errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ call.requestSoapErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default"
                                                data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                    </div></td>

                <%--  ================================ --%>
                <%--  ========== Response ============ --%>
                <td>
                    <c:choose>
                        <c:when test="${call.responseXmlValid}">
                            <span class="label label-success">XML Valid</span>
                        </c:when>
                        <c:when test="${empty call.responseXmlValid}">
                            <span class="label label-default">No XML Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">XML Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${call.responseSoapValid}">
                            <span class="label label-success">SOAP Valid</span>
                        </c:when>
                        <c:when test="${empty call.responseSoapValid}">
                            <span class="label label-default">No SOAP Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">SOAP Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <div class="validation-actions">
                        <c:if test="${ ! empty call.response }">
                            <a class="glyphicon glyphicon-eye-open" data-toggle="modal"
                               href="#respModal_${loop.index}" title="View response content"></a>
                        </c:if>
                        <c:if test="${ ! empty call.response && ! empty call.validatorId }">
                            ${viewWsdl}
                        </c:if>

                        <c:if test="${ ! empty call.responseSoapErrors || ! empty call.responseXmlErrors}">
                            <a class="glyphicon error glyphicon-exclamation-sign"
                               data-toggle="modal" href="#respErrors_${loop.index}"
                               title="<fmt:message key="logs.request.view.errors" />"></a>
                        </c:if>

                        <div class="modal fade" id="respModal_${loop.index}">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal"
                                                aria-hidden="true">&times;</button>
                                        <h4 class="modal-title">Response content</h4>
                                    </div>
                                    <div class="modal-body">
                                        <pre><code class="xml"><c:out escapeXml="true" value="${call.responseAsXML}" /></code></pre>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default"
                                                data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>

                        <div class="modal fade" id="respErrors_${loop.index}">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal"
                                                aria-hidden="true">&times;</button>
                                        <h4 class="modal-title">Response errors</h4>
                                    </div>
                                    <div class="modal-body text-left">
                                        <c:if test="${ ! empty call.responseXmlErrors}">
                                            <h5 class="modal-title">XML errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ call.responseXmlErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                        <c:if test="${ ! empty call.responseSoapErrors}">
                                            <h5 class="modal-title">SOAP errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ call.responseSoapErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default"
                                                data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <!-- /.modal -->
                    </div></td>
                <td><small>${call.responseTime}</small></td>
            </tr>
        </c:forEach>
        </tbody>


    </table>
</div>

<div id="preparing-file-modal" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="exportInProgressLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h4 class="modal-title" id="exportInProgressLabel">Export en cours</h4>
          </div>
          <div class="modal-body">
              We are preparing your report, please wait...
          </div>
        </div>
    </div>
</div>


<div id="error-modal" class="modal fade bs-example-modal-sm" tabindex="-1" role="dialog" aria-labelledby="errorModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="errorModalLabel">Export en cours</h4>
            </div>
            <div class="modal-body">
                There was a problem generating your report, please try again.
            </div>
        </div>
    </div>
</div>
