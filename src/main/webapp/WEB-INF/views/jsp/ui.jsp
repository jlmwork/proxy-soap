<%@page import="java.util.Locale"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %><%@ page session="false" %>
<fmt:setBundle basename="messages"/>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>Proxy Soap - UI</title>
        <base href="<%=request.getContextPath()%>/" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="" />
        <meta name="author" content="" />

        <!-- Le styles -->
        <link href="res/lib/twitter-bootstrap/css/bootstrap.css" rel="stylesheet" />
        <link href="res/lib/twitter-bootstrap/css/bootstrap-theme.css" rel="stylesheet" />
        <link rel="stylesheet" href="res/css/bootstrap-table.css">
        <link rel="stylesheet" href="res/css/style.css">
        <link rel="stylesheet" href="res/lib/highlight.js/styles/default.min.css">

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="<%=request.getContextPath()%>/res/js/html5shiv.js"></script>
        <![endif]-->
    </head>
    <c:choose>
        <c:when test="${action == 'config'}">
            <c:set var="exchangesActive" value="" />
            <c:set var="configActive" value="active in" />
        </c:when>
        <c:otherwise>
            <c:set var="exchangesActive" value="active in" />
            <c:set var="configActive" value="" />
        </c:otherwise>
    </c:choose>
    <body>
        <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="ui">Proxy SOAP</a></a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav nav-pills" role="tablist">
                        <!-- nav -->
                        <li class="${exchangesActive}"><a href="#exchanges" role="tab" data-toggle="tab"><fmt:message key="exchanges.pagetitle"/></a></li>
                        <li class="${configActive}"><a href="#config" role="tab" data-toggle="tab"><fmt:message key="config.pagetitle"/></a></li>
                        <li><a href="#validators" role="tab" data-toggle="tab"><fmt:message key="validators.title"/></a></li>
                        <li><a href="#help" role="tab" data-toggle="tab"><fmt:message key="help.pagetitle"/></a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </div>
        <div class="container">
            <div class="tab-content">
                <div class="tab-pane ${exchangesActive} fade" id="exchanges">
                    <jsp:include page="./exchanges.jsp"/>
                </div>
                <div class="tab-pane ${configActive} fade" id="config">
                    <jsp:include page="./config.jsp"/>
                </div>
                <div class="tab-pane fade" id="validators">
                    <jsp:include page="./validators.jsp"/>
                </div>
                <div class="tab-pane fade" id="help">
                    <jsp:include page="./help.jsp"/>
                </div>
            </div> <!-- /container -->
        </div>
        <%@include file="footer.jspf" %>
        <!-- JS
        ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->
        <script src="res/lib/jquery/jquery.js"></script>
        <script src="res/lib/twitter-bootstrap/js/bootstrap.js"></script>
        <script src="res/js/bootstrap-table.js"></script>
        <script src="res/js/tableExport.js"></script>
        <script src="res/js/jquery.base64.js"></script>
        <script src="res/js/bootstrap-table-export.js"></script>
        <script src="res/js/bootstrap-table-clear.js"></script>
        <%
            String userLocale = request.getHeader("Accept-Language");
            if (userLocale != null && userLocale.startsWith("fr")) {
        %><script src="res/js/bootstrap-table-fr-FR.min.js"></script><%
            }
        %>
        <script src="res/lib/highlight.js/highlight.pack.js"></script>
        <script src="res/js/jquery.fileDownload.js"></script>
        <script>
            hljs.initHighlightingOnLoad();
            $(document).ready(function() {
                // popover demo
                $("a[data-toggle=popover]")
                        .popover()
                        .click(function(e) {
                            e.preventDefault()
                        })
            });</script>
        <script src="res/js/exchanges.js"></script>
        <script src="res/js/config.js"></script>
        <script src="res/js/validators.js"></script>
        <script src="res/js/help.js"></script>

    </body>
</html>