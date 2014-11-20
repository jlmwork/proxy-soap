package prototypes.ws.proxy.soap.aop;

import javax.xml.bind.annotation.XmlRootElement;

import prototypes.ws.proxy.soap.model.SoapExchange;

public aspect JaxbApplyAspect {
    declare @type: prototypes.ws.proxy.soap.model..* : @XmlRootElement;
}