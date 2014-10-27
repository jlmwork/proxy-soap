/*
 * Copyright 2014 jlamande.
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
package prototypes.ws.proxy.soap.utils;

import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.reflect.Classes;

public class ClassesTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ClassesTest.class);

    public static void main(String... args) {
        WsdlContext context = new WsdlContext(
                "src/test/resources/wsdl/affaire/AffaireServiceWrite.wsdl");
        Classes.setStaticField(WsdlContext.class.getSuperclass(),
                "definitionCache", null);
        LOGGER.debug("done");
    }

}
