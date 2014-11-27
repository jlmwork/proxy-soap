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
package prototypes.ws.proxy.soap.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author jlamande
 */
public class Expression {

    protected Logger logger;

    protected String name;

    protected String body;

    protected Expression() {
        logger = LoggerFactory
                .getLogger(this.getClass());
    }

    /**
     * wont use pattern fluent interface builder as this class contains only two
     * fields
     *
     * @param name
     * @param regex
     */
    public Expression(String name, String regex) {
        this();
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Expression has no name.");
        }
        this.name = name;
    }

    public void validate() {
    }

    public String getName() {
        return name;
    }

    public String getBody() {
        return body;
    }

}
