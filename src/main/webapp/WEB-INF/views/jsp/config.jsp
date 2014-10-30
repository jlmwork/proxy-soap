<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<fmt:setBundle basename="messages"/>
<h2><fmt:message key="config.pagetitle"/></h2>

<form method="post" action="ui/action/config">
    <div class="panel ${success}">
        <c:if test="${!empty message}">
            <div class="panel-heading">
                <fmt:message key="${message}">
                    <fmt:param>${proxy.persistedConfPath}</fmt:param>
                </fmt:message>
            </div>
        </c:if>
        <table class="table table-bordered" id="settings">
            <thead>
                <tr>
                    <th><fmt:message key="config.key"/></th>
                    <th><fmt:message key="config.value"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="setting" items="${settings}" varStatus="loop">
                    <tr>
                        <td>
                            <fmt:message key="config.${setting}"/>
                            <a href="ui#" data-toggle="tooltip" title="<fmt:message key="config.${setting}.help"/>" data-placement="right">
                                <span class="glyphicon glyphicon-question-sign"></span>
                            </a>
                        </td>
                        <td>
                            <%-- this temp variable avoids boolean cast exception under jasper compilation (jetty, tomcat, ...) --%>
                            <c:set var="settingval">${proxy[setting]}</c:set>
                            <c:choose>
                                <c:when test="${settingval == 'true' || settingval == 'false' }">
                                    <label for="${setting}.true"><fmt:message key="config.${setting}.true"/></label>
                                    <input type="radio" name="${setting}" id="${setting}.true" value="true" <c:if test="${settingval == 'true'}">checked</c:if>>
                                    <label for="${setting}.false"><fmt:message key="config.${setting}.false"/></label>
                                    <input type="radio" name="${setting}" id="${setting}.false" value="false" <c:if test="${settingval == 'false'}">checked</c:if>>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="${setting}" value="${settingval}" style="width: 100%;" />
                                </c:otherwise>
                            </c:choose>

                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="panel-footer">
            <div class="btn-group">
                <button type="submit" class="btn btn-default">
                    <fmt:message key="config.validate"/>
                </button>
                <button type="submit" name="persist" class="btn btn-default">
                    <span class="glyphicon icon-download-alt"></span>
                    <fmt:message key="config.persist"/>
                </button>
            </div>
        </div>
    </div>

</form>