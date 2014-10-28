<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<style>.white,.white:hover,.white:visited{color:white;}</style>
<h2><fmt:message key="exchanges.pagetitle"/>
    <%--<small class="autorefresh"><fmt:message key="exchanges.autorefresh"/> [<span>off</span>]</small>--%>
    <small>
        <a href="exchanges" id="refresh" class="goright" title="<fmt:message key="exchanges.reload"/>"><span class="glyphicon glyphicon-refresh"></span></a>
    </small>
    <c:if test="${empty requestList}">
        <c:set var="displayActionButtons">style="display: none;"</c:set>
    </c:if>
    <small id="actionButtons" ${displayActionButtons}>
        <a href="ui/action/clearRequests" id="clear" class="goright" title="<fmt:message key="exchanges.clear"/>"><span class="glyphicon glyphicon-trash"></span></a>
        <a href="exchanges?accept=application/zip" id="export" class="goright glyphicon glyphicon-cloud-download" title="<fmt:message key="exchanges.export"/>"></a>
    </small>
</h2>
<c:choose>
    <c:when test="${!proxy.validationActive}">
        <c:set var="panelMessageType" value="panel-danger" />
        <c:set var="panelMessage"><fmt:message key="config.proxy.soap.validate"/> <fmt:message key="config.proxy.soap.validate.false"/></c:set>
    </c:when>
    <c:when test="${proxy.blockingMode}">
        <c:set var="panelMessageType" value="panel-warning" />
        <c:set var="panelMessage"><fmt:message key="config.proxy.soap.blockingmode"/> <fmt:message key="config.proxy.soap.blockingmode.true"/></c:set>
    </c:when>
</c:choose>
<div class="panel ${panelMessageType}">
    <%-- Panel header --%>
    <div class="panel-heading">${panelMessage}</div>
    <table class="table table-bordered table-striped table-hover table-condensed" id="exchangestable">
        <thead>
            <tr class="text-center">
                <th>#</th>
                <th><fmt:message key="exchanges.request.date"/></th>
                <th><fmt:message key="exchanges.request.from"/></th>
                <th><fmt:message key="exchanges.request.to"/></th>
                <th><fmt:message key="exchanges.request.validator"/></th>
                <th><fmt:message key="exchanges.request.operation"/></th>
                <th><fmt:message key="exchanges.request.request"/></th>
                <th><fmt:message key="exchanges.request.response"/></th>
                <th><fmt:message key="exchanges.request.response.time"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="exchange" items="${requestList}" varStatus="loop">
                <c:choose>
                    <c:when test="${ ! empty exchange.request && ! empty exchange.validatorId }">
                        <c:set var="viewWsdl"><a target="_blank" href="ui/action/viewWSDL?validator=${exchange.validatorId}" title="<fmt:message key="exchanges.request.validated.by"/> ${exchange.validatorId} (<fmt:message key="exchanges.request.click.viewwsdl" />)" class="glyphicon glyphicon-file"></a></c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="viewWsdl"><span title="<fmt:message key="exchanges.request.error.nowsdl" />" class="glyphicon glyphicon-remove-sign"></span></c:set>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${ exchange.requestValid && exchange.responseValid }">
                    <c:set var="status" value="success" />
                </c:when>
                <c:when test="${ (not exchange.requestValid || not exchange.responseValid) && !empty exchange.validatorId }">
                    <c:set var="status" value="danger" />
                </c:when>
                <c:otherwise>
                    <c:set var="status" value="warning" />
                </c:otherwise>
            </c:choose>

            <tr class="${status}">
                <td><a href="#" title="${exchange.id}">#</a></td>
                <td><small>${exchange.date}</small></td>
                <td><small>${exchange.from}</small></td>
                <td><small>${exchange.uri}</small></td>
                <td>
                    <small>
                        <%-- TODO : activate validators tab on click --%>
                        <a class="viewvalidator" href="#${exchange.validatorId}">${exchange.validatorId}</a>
                    </small>
                </td>
                <td><small>${exchange.operation}</small></td>

                <%--  ================================ --%>
                <%--  ========== Request  ============ --%>
                <td><c:choose>
                        <c:when test="${exchange.requestXmlValid}">
                            <span class="label label-success">XML Valid</span>
                        </c:when>
                        <c:when test="${empty exchange.requestXmlValid}">
                            <span class="label label-default">No XML validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">XML Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${exchange.requestSoapValid}">
                            <span class="label label-success">SOAP Valid</span>
                        </c:when>
                        <c:when test="${empty exchange.requestSoapValid}">
                            <span class="label label-default">No SOAP Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">SOAP Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <div class="validation-actions">
                        <c:if test="${ ! empty exchange.request }">
                            <a class="glyphicon glyphicon-eye-open" data-toggle="modal"
                               href="#reqModal_${loop.index}" title="view Request content"></a>
                        </c:if>
                        <c:if test="${ ! empty exchange.request && ! empty exchange.validatorId }">
                            ${viewWsdl}
                        </c:if>

                        <c:if test="${ ! empty exchange.requestSoapErrors || ! empty exchange.requestXmlErrors}">
                            <a class="glyphicon error glyphicon-exclamation-sign"
                               data-toggle="modal" href="#reqErrors_${loop.index}"
                               title="<fmt:message key="exchanges.request.view.errors" />"></a>
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
                                        <pre><code class="xml"><c:out escapeXml="true" value="${exchange.requestAsXML}" /></code></pre>
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
                                        <c:if test="${ ! empty exchange.requestXmlErrors}">
                                            <h5 class="modal-title">XML errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ exchange.requestXmlErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                        <c:if test="${ ! empty exchange.requestSoapErrors}">
                                            <h5 class="modal-title">SOAP errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ exchange.requestSoapErrors }">
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
                        <c:when test="${exchange.responseXmlValid}">
                            <span class="label label-success">XML Valid</span>
                        </c:when>
                        <c:when test="${empty exchange.responseXmlValid}">
                            <span class="label label-default">No XML Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">XML Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${exchange.responseSoapValid}">
                            <span class="label label-success">SOAP Valid</span>
                        </c:when>
                        <c:when test="${empty exchange.responseSoapValid}">
                            <span class="label label-default">No SOAP Validation</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger">SOAP Invalid</span>
                        </c:otherwise>
                    </c:choose>
                    <div class="validation-actions">
                        <c:if test="${ ! empty exchange.response }">
                            <a class="glyphicon glyphicon-eye-open" data-toggle="modal"
                               href="#respModal_${loop.index}" title="View response content"></a>
                        </c:if>
                        <c:if test="${ ! empty exchange.response && ! empty exchange.validatorId }">
                            ${viewWsdl}
                        </c:if>

                        <c:if test="${ ! empty exchange.responseSoapErrors || ! empty exchange.responseXmlErrors}">
                            <a class="glyphicon error glyphicon-exclamation-sign"
                               data-toggle="modal" href="#respErrors_${loop.index}"
                               title="<fmt:message key="exchanges.request.view.errors" />"></a>
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
                                        <pre><code class="xml"><c:out escapeXml="true" value="${exchange.responseAsXML}" /></code></pre>
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
                                        <c:if test="${ ! empty exchange.responseXmlErrors}">
                                            <h5 class="modal-title">XML errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ exchange.responseXmlErrors }">
                                                    <li>${ error }</li>
                                                    </c:forEach>
                                            </ul>
                                        </c:if>
                                        <c:if test="${ ! empty exchange.responseSoapErrors}">
                                            <h5 class="modal-title">SOAP errors</h5>
                                            <ul>
                                                <c:forEach var="error" items="${ exchange.responseSoapErrors }">
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
                <td><small>${exchange.responseTime}</small></td>
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
