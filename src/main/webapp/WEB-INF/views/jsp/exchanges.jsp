<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>

<table id="exchangestable" data-toggle="table"
       data-sort-name="time" data-sort-order="desc"
       data-url="resources/exchange?accept=application/json" data-cache="false"
       data-clear-url="exchanges"
       data-height="500"
       data-toolbar="#custom-toolbar"
       data-show-refresh="true"
       data-show-toggle="true"
       data-show-export="true"
       data-show-clear="true"
       data-pagination="true"
       data-search="true"
       data-show-columns="true"
       data-row-style="rowStyle">
    <thead>
        <tr class="text-center">
            <th data-field="id" data-visible="false">ID</th>
            <th data-field="time" data-sortable="true"><fmt:message key="exchanges.exchange.date"/></th>
            <th data-field="from"><fmt:message key="exchanges.exchange.from"/></th>
            <th data-field="to"><fmt:message key="exchanges.exchange.to"/></th>
            <th data-field="validatorId" data-sortable="true" data-formatter="validatorFieldFormatter">
                <fmt:message key="exchanges.exchange.validator"/>
            </th>
            <th data-field="operation">
                <fmt:message key="exchanges.exchange.operation"/>
            </th>
            <th data-field="backEndResponseTime" data-sortable="true" data-formatter="responseTimeFieldFormatter">
                <fmt:message key="exchanges.exchange.back_end_response_time"/>
            </th>
            <th data-field="proxyInternalTime" data-sortable="true"  data-visible="false" data-formatter="responseTimeFieldFormatter">
                <fmt:message key="exchanges.exchange.proxy_internal_time"/>
            </th>
            <th data-field="backEndResponseCode"  data-visible="false">
                <fmt:message key="exchanges.exchange.response.code"/>
            </th>
            <th data-field="requestValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.request"/> Status
            </th>
            <th data-field="requestXmlValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.request"/> XML
            </th>
            <th data-field="requestSoapValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.request"/> SOAP
            </th>
            <th data-field="responseValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.response"/> Status
            </th>
            <th data-field="responseXmlValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.response"/> XML
            </th>
            <th data-field="responseSoapValid" data-visible="false" data-formatter="validationStatusFormatter">
                <fmt:message key="exchanges.exchange.response"/> SOAP
            </th>
        </tr>
    </thead>
</table>

<div id="exchangedetails" class="hidden panel panel-default">
    <div class="panel-heading" data-toggle="collapse" data-target="#exchangedetails .panel-body">
        <h5 class="panel-title"><fmt:message key="exchanges.exchange.details.label"/><span id="exchangeId"></span></h5>
        <span id="proxy_validation_status" class="text-info"></span>&nbsp;/
        <span id="proxy_blocking_status" class="text-info"></span> /
        <fmt:message key="exchanges.exchange.request"/> : <span id="request_status"></span> /
        <fmt:message key="exchanges.exchange.response"/> : <span id="response_status"></span>
    </div>
    <div class="panel-body panel-collapse collapse">
        <!-- Exchange Nav tabs -->
        <ul class="nav navbar-default nav-tabs" role="tablist">
            <li role="presentation" class="active">
                <a href="#reqheaders" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.headers_of"/>
                    <fmt:message key="exchanges.exchange.request"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#reqcontent" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.request"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#reqerrors" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.errors_of"/>
                    <fmt:message key="exchanges.exchange.request"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#respheaders" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.headers_of"/>
                    <fmt:message key="exchanges.exchange.response"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#respcontent" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.response"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#resperrors" role="tab" data-toggle="tab">
                    <fmt:message key="exchanges.exchange.errors_of"/>
                    <fmt:message key="exchanges.exchange.response"/>
                </a>
            </li>
            <li role="presentation">
                <a href="#proxyresponse" role="tab" data-toggle="tab">
                    Proxy -
                    <fmt:message key="exchanges.exchange.response"/>
                </a>
            </li>
        </ul>

        <!-- Exchange Tab panes -->
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane active" id="reqheaders"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane content" id="reqcontent"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="reqerrors"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="respheaders"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane content" id="respcontent"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="resperrors"><pre><code class="xml"></code></pre></div>
            <div role="tabpanel" class="tab-pane" id="proxyresponse"><pre><code class="xml"></code></pre></div>
        </div>
    </div>
</div>
