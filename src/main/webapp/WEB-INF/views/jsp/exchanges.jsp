<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>

<table id="exchangestable" data-toggle="table"
       data-sort-name="date" data-sort-order="desc"
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
            <th data-field="id" data-visible="false">ID</th>
            <th data-field="date" data-sortable="true"><fmt:message key="exchanges.request.date"/></th>
            <th data-field="from"><fmt:message key="exchanges.request.from"/></th>
            <th data-field="to"><fmt:message key="exchanges.request.to"/></th>
            <th data-field="validator" data-sortable="true" data-formatter="validatorFieldFormatter"><fmt:message key="exchanges.request.validator"/></th>
            <th data-field="operation"><fmt:message key="exchanges.request.operation"/></th>
            <th data-field="backend_response_time" data-sortable="true" data-formatter="responseTimeFieldFormatter"><fmt:message key="exchanges.request.response.time"/></th>
            <th data-field="backend_response_code"  data-visible="false"><fmt:message key="exchanges.request.response.time"/></th>
            <th data-field="request_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> Valid</th>
            <th data-field="request_xml_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> XML Valid</th>
            <th data-field="request_soap_valid" data-visible="false"><fmt:message key="exchanges.request.request"/> SOAP Valid</th>
            <th data-field="response_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> Valid</th>
            <th data-field="response_xml_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> XML Valid</th>
            <th data-field="response_soap_valid" data-visible="false"><fmt:message key="exchanges.request.response"/> SOAP Valid</th>
        </tr>
    </thead>
</table>

<div id="exchangedetails" class="hidden panel panel-default">
    <div class="panel-heading" data-toggle="collapse" data-target="#exchangedetails .panel-body">
        <h5 class="panel-title"><fmt:message key="exchanges.exchange.details.label"/><span id="exchangeId"></span></h5>
    </div>
    <div class="panel-body panel-collapse collapse">
        <!-- Exchange Nav tabs -->
        <ul class="nav navbar-default nav-tabs" role="tablist">
            <li role="presentation" class="active"><a href="#reqheaders" role="tab" data-toggle="tab">Request Headers</a></li>
            <li role="presentation"><a href="#reqcontent" role="tab" data-toggle="tab">Request Content</a></li>
            <li role="presentation"><a href="#reqerrors" role="tab" data-toggle="tab">Request Errors</a></li>
            <li role="presentation"><a href="#respheaders" role="tab" data-toggle="tab">Response Headers</a></li>
            <li role="presentation"><a href="#respcontent" role="tab" data-toggle="tab">Response Content</a></li>
            <li role="presentation"><a href="#resperrors" role="tab" data-toggle="tab">Response Errors</a></li>
        </ul>

        <!-- Exchange Tab panes -->
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane active" id="reqheaders"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane content" id="reqcontent"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="reqerrors"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="respheaders"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane content" id="respcontent"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="resperrors"><pre><code class="xml"></code></pre></div>
        </div>
    </div>
</div>
