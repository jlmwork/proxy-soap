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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prototypes.ws.proxy.soap.io.Strings;

/**
 *
 * @author jlamande
 */
public class Expression {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(Expression.class);

    protected String name;

    protected Pattern regex;

    protected String objectField;

    protected Expression() {
    }

    /**
     * wont use pattern fluent interface builder as this class contains only two
     * fields
     *
     * @param name
     * @param regex
     */
    public Expression(String name, String regex) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Capture expression has no name.");
        }
        this.name = name;
        this.objectField = null;
        checkRegexFormat(regex);
        try {
            this.regex = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Format of capture expression '" + regex + "' is not correct. Must provide a correct regular expression.");
        }
    }

    /**
     * wont use pattern fluent interface builder as this class contains only two
     * fields
     *
     * @param name
     * @param objectField
     * @param regex
     */
    public Expression(String name, String objectField, String regex) {
        this(name, regex);
        this.objectField = objectField;
    }

    public void validate() {
        this.checkRegexFormat(this.regex.pattern());
    }

    protected void checkRegexFormat(String regex) {
        // default expression dont control regex format
    }

    public boolean match(String content) {
        Matcher m = this.regex.matcher(content);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public boolean match(Object object) {
        try {
            Object targetField = FieldUtils.readField(object, this.objectField, true);
            if (targetField != null) {
                if (targetField instanceof byte[]) {
                    return match(new String((byte[]) targetField));
                }
                return match(targetField.toString());
            }
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Unknown field {} on object of class {}", this.objectField, object.getClass().getName());
        } catch (IllegalAccessException ex) {
            LOGGER.warn("Cant access field {} on object of class {}", this.objectField, object.getClass().getName());
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Pattern getRegex() {
        return regex;
    }

    public String getObjectField() {
        return objectField;
    }

}
