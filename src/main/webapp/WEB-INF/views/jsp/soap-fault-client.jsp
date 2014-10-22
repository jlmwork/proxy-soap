<%@ page language="java" contentType="text/xml; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="true" isErrorPage="true" %><%@ page session="false" %><?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <soapenv:Fault>
      <faultcode>soapenv:Client</faultcode>
      <faultstring xml:lang="en"><%=request.getAttribute("javax.servlet.error.message")%></faultstring>
    </soapenv:Fault>
  </soapenv:Body>
</soapenv:Envelope>
