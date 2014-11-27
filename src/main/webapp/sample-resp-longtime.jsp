<%@page contentType="text/xml; charset=UTF-8"
        pageEncoding="UTF-8" %><?xml version='1.0' encoding='UTF-8'?>
<%
    try {
        Thread.sleep(15000);
    } catch (InterruptedException ex) {

    }

%>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws.prototypes/">
    <soapenv:Header/>
    <soapenv:Body>
        <ws:helloResponse/>
    </soapenv:Body>
</soapenv:Envelope>
