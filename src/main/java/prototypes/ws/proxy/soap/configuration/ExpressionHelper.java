/*
 * Copyright 2014 JL06436S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package prototypes.ws.proxy.soap.configuration;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
public class ExpressionHelper {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExpressionHelper.class);

    protected String marshallExpressions(List expressions) {
        try {
            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
            // for use of Moxy
            JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{Expression.class}, properties);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(expressions, baos);
            return baos.toString();
        } catch (JAXBException e) {
            LOGGER.warn("JAXB marshalling error {}", e);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Cause : {}", e.getCause());
            }
        }
        return null;
    }

    protected List<BooleanExecutableExpression> parseBooleanExecutableExpressions(String expressions) {
        List<BooleanExecutableExpression> expressionsList = (List<BooleanExecutableExpression>) parseJsonExpressions(expressions, BooleanExecutableExpression.class);
        if (validateExpressions(expressionsList)) {
            LOGGER.debug("BooleanExecutable expressions : {}", expressions);
            return expressionsList;
        }
        return new ArrayList<BooleanExecutableExpression>(0);
    }

    protected List<CaptureExpression> parseCaptureExpressions(String expressions) {
        List<CaptureExpression> expressionsList = (List<CaptureExpression>) parseJsonExpressions(expressions, CaptureExpression.class);
        if (validateExpressions(expressionsList)) {
            LOGGER.debug("Capture expressions : {}", expressions);
            return expressionsList;
        }
        return new ArrayList<CaptureExpression>(0);
    }

    protected List<Expression> parseExpressions(String expressions) {
        List<Expression> expressionsList = (List<Expression>) parseJsonExpressions(expressions, Expression.class);
        if (validateExpressions(expressionsList)) {
            LOGGER.debug("Capture expressions : {}", expressions);
            return expressionsList;
        }
        return new ArrayList<Expression>(0);
    }

    private boolean validateExpressions(List expressionsList) {
        if (expressionsList != null) {
            try {
                // need to validate fields of expression after the jaxb mapping
                for (Object expression : expressionsList) {
                    ((Expression) expression).validate();

                }
            } catch (IllegalArgumentException ex) {
                LOGGER.warn("Bad expression found : {}", ex);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private Object parseJsonExpressions(String expressions, Class<?> clazz) {
        try {
            Map<String, Object> properties = new HashMap<String, Object>(1);
            properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
            // for use of Moxy
            JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{clazz}, properties);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
            JAXBElement o = unmarshaller.unmarshal(new StreamSource(new StringReader(expressions)), clazz);
            return o.getValue();
        } catch (JAXBException e) {
            LOGGER.warn("JAXB unmarshalling error {}", e);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("Cause : {}", e.getCause());
            }
        }
        return null;
    }
}
