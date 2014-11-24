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
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 *
 * @author jlamande
 */
public class CaptureExpression extends Expression {

    private static final Pattern CAPTURE_REGEX_FORMAT = Pattern.compile(".*\\([^\\(]+\\).*");

    protected CaptureExpression() {
        super();
    }

    public CaptureExpression(String name, String regex) {
        super(name, regex);
    }

    public CaptureExpression(String name, String objectField, String regex) {
        super(name, objectField, regex);
    }

    @Override
    protected final void checkRegexFormat(String regex) {
        Matcher m = CAPTURE_REGEX_FORMAT.matcher(regex);
        if (!m.find()) {
            throw new IllegalArgumentException("Format of capture expression '" + regex + "' is not correct. Must provide at least one capture group.");
        }
        if (m.groupCount() > 1) {
            throw new IllegalArgumentException("Format of capture expression '" + regex + "' is not correct. Must provide at least one capture group.");
        }
    }

    public String capture(String content) {
        String captured = "";
        Matcher m = this.regex.matcher(content);
        if (m.find()) {
            try {
                captured = m.group(1);
            } catch (IllegalStateException e) {
                LOGGER.warn("Matching error on {} : {}", content, e.getMessage());
            }
        }
        return captured;
    }

    public String capture(Object object) {
        try {
            Object targetField = FieldUtils.readField(object, this.objectField, true);
            if (targetField != null) {
                if (targetField instanceof byte[]) {
                    return capture(new String((byte[]) targetField));
                }
                return capture(targetField.toString());
            }
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Unknown field {} on object of class {}", this.objectField, object.getClass().getName());
        } catch (IllegalAccessException ex) {
            LOGGER.warn("Cant access field {} on object of class {}", this.objectField, object.getClass().getName());
        }
        return "";
    }

}
