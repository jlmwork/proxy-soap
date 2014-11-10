<%@page contentType="text/xml; charset=UTF-8"
        pageEncoding="UTF-8" %><?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v6="http://nsge.erdf.fr/sgel/aff/echange/service/v6_1">
    <soapenv:Header/>
    <soapenv:Body>
        <v6:arbitrerPublicationResponse>
            <pointPublie>12345678901234</pointPublie>
            <validationPublication>true</validationPublication>
            <!--Optional:-->
            <motifNonPublication>true</motifNonPublication>
            <!--Optional:-->
            <modificationsEnCours>true</modificationsEnCours>
            <!--Optional:-->
            <interventionsEnCours>true</interventionsEnCours>
            <!--Optional:-->
            <contratEnCours>true</contratEnCours>
        </v6:arbitrerPublicationResponse>
    </soapenv:Body>
</soapenv:Envelope>