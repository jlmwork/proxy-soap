<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<h4><fmt:message key="exchanges.pagetitle"/></h4>

<%--
<div id="custom-toolbar">
    <div class="form-inline" role="form">
        <button id="export" data-href="exchanges?accept=application/zip" type="button" class="btn btn-default" title="<fmt:message key="exchanges.export"/>"><span class="glyphicon glyphicon-cloud-download"></span></button>
    </div>
</div>--%>
<%--
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
<div class="panel ${panelMessageType}">Panel header
<div class="panel-heading">${panelMessage}</div>--%>
<table id="exchangestable" data-toggle="table"
       data-sort-order="desc"
       data-url="exchanges?accept=application/json" data-cache="false"
       data-clear-url="exchanges"
       data-height="500"
       data-toolbar="#custom-toolbar"
       data-show-refresh="true"
       data-show-toggle=true"
       data-show-export="true"
       data-show-clear="true"
       data-pagination="true"
       data-search="true"
       data-show-columns="true"
       data-row-style="rowStyle">
    <thead>
        <tr class="text-center">
            <th data-field="id" data-visible="false">#</th>
            <th data-field="date" data-sortable="true"><fmt:message key="exchanges.request.date"/></th>
            <th data-field="from"><fmt:message key="exchanges.request.from"/></th>
            <th data-field="to"><fmt:message key="exchanges.request.to"/></th>
            <th data-field="validator" data-sortable="true"><fmt:message key="exchanges.request.validator"/></th>
            <th data-field="operation"><fmt:message key="exchanges.request.operation"/></th>
            <th data-field="resp_time" data-sortable="true"><fmt:message key="exchanges.request.response.time"/></th>
            <th data-field="request_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> Valid</th>
            <th data-field="request_xml_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> XML Valid</th>
            <th data-field="request_soap_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> SOAP Valid</th>
            <th data-field="response_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> Valid</th>
            <th data-field="response_xml_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> XML Valid</th>
            <th data-field="response_soap_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> SOAP Valid</th>
        </tr>
    </thead><%--
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
                        <a class="viewvalidator" href="#${exchange.validatorId}">${exchange.validatorId}</a>
                    </small>
                </td>
                <td><small>${exchange.operation}</small></td>

                <!--  ================================ -->
                <!--  ========== Request  ============ -->
                <td><c:choose>
                        <c:when test="${exchange.requestXmlValid}">
                            <span class="label label-success" title="XML Valid">XML</span>
                        </c:when>
                        <c:when test="${empty exchange.requestXmlValid}">
                            <span class="label label-default" title="No XML validation">XML</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger" title="XML Invalid">XML</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${exchange.requestSoapValid}">
                            <span class="label label-success" title="SOAP Valid">SOAP</span>
                        </c:when>
                        <c:when test="${empty exchange.requestSoapValid}">
                            <span class="label label-default" title="No SOAP Validation">SOAP</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger" title="SOAP Invalid">SOAP</span>
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

                <!--  ================================ -->
                <!--  ========== Response ============ -->
                <td class="text-nowrap">
                    <c:choose>
                        <c:when test="${exchange.responseXmlValid}">
                            <span class="label label-success" title="XML Valid">XML</span>
                        </c:when>
                        <c:when test="${empty exchange.responseXmlValid}">
                            <span class="label label-default" title="No XML Validation">XML</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger" title="XML Invalid">XML</span>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${exchange.responseSoapValid}">
                            <span class="label label-success" title="SOAP Valid">SOAP</span>
                        </c:when>
                        <c:when test="${empty exchange.responseSoapValid}">
                            <span class="label label-default" title="No SOAP Validation">SOAP</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-danger" title="SOAP Invalid">SOAP</span>
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
    --%>

</table>
<%--</div>--%>

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
