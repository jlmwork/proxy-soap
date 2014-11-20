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
package prototypes.ws.proxy.soap.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JL06436S
 */
public class NameGenerator implements org.eclipse.persistence.oxm.XMLNameTransformer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(NameGenerator.class);

    //Use the unqualified class name as our root element name.
    @Override
    public String transformRootElementName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    //The same algorithm as root element name plus "Type" appended to the end.
    @Override
    public String transformTypeName(String name) {
        LOGGER.debug("Transform type name {}", name);
        return transformRootElementName(name) + "Type";
    }

    // The name will be lower case with word breaks represented by '-'.
    // Note:  A capital letter in the original name represents the start of a new word.
    @Override
    public String transformElementName(String name) {
        StringBuilder strBldr = new StringBuilder();
        for (char character : name.toCharArray()) {
            if (Character.isUpperCase(character)) {
                strBldr.append('_');
                strBldr.append(Character.toLowerCase(character));
            } else {
                strBldr.append(character);
            }
        }
        LOGGER.trace("Transform element name : {} to {}", name, strBldr.toString());
        return strBldr.toString();
    }

    //The original name converted to upper case.
    @Override
    public String transformAttributeName(String name) {
        LOGGER.debug("Transform attribute name {}", name);
        return transformElementName(name);
    }

}
