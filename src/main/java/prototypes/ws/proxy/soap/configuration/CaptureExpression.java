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
import prototypes.ws.proxy.soap.constants.Messages;

/**
 *
 * @author jlamande
 */
public class CaptureExpression extends Expression {

    private static final Pattern CAPTURE_REGEX_FORMAT = Pattern.compile(".*\\([^\\(]+\\).*");
    private static final String MUST_PROVIDE_AT_LEAST_ONE_GROUP = "' is not correct. Must provide at least one capture group.";
    private static final String FORMAT_OF_CAPTURE_EXPRESSION = "Format of capture expression '";
    private static final String CANT_ACCESS_FIELD_OF_CLASS_2P = "Cant access field {} on object of class {}";
    private static final String UNKNOWN_FIELD_OF_CLASS_2P = "Unknown field {} on object of class {}";

    protected Pattern regexCompiled;

    protected String objectField;

    protected CaptureExpression() {
        super();
    }

    public CaptureExpression(String name, String body) {
        super(name, body);
        validate();
    }

    public CaptureExpression(String name, String objectField, String regex) {
        this(name, regex);
        this.objectField = objectField;
    }

    @Override
    public final void validate() {
        checkRegexFormat(body);
        try {
            this.regexCompiled = Pattern.compile(body);
        } catch (PatternSyntaxException ex) {
            logger.warn(Messages.MSG_ERROR_DETAILS, ex);
            throw new IllegalArgumentException("Format of capture expression '" + body + "' is not correct. Must provide a correct regular expression.");
        }
    }

    public String getObjectField() {
        return objectField;
    }

    protected final void checkRegexFormat(String regex) {
        Matcher m = CAPTURE_REGEX_FORMAT.matcher(regex);
        if (!m.find()) {
            throw new IllegalArgumentException(FORMAT_OF_CAPTURE_EXPRESSION + regex + MUST_PROVIDE_AT_LEAST_ONE_GROUP);
        }
        if (m.groupCount() > 1) {
            throw new IllegalArgumentException(FORMAT_OF_CAPTURE_EXPRESSION + regex + MUST_PROVIDE_AT_LEAST_ONE_GROUP);
        }
    }

    public String capture(String content) {
        String captured = "";
        Matcher m = this.regexCompiled.matcher(content);
        if (m.find()) {
            try {
                captured = m.group(1);
            } catch (IllegalStateException ex) {
                logger.warn("Matching error on {} : {}", content, ex);
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
            logger.warn(UNKNOWN_FIELD_OF_CLASS_2P, this.objectField, object.getClass().getName());
            logger.debug(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IllegalAccessException ex) {
            logger.warn(CANT_ACCESS_FIELD_OF_CLASS_2P, this.objectField, object.getClass().getName());
            logger.debug(Messages.MSG_ERROR_DETAILS, ex);
        }
        return "";
    }

    public boolean match(String content) {
        Matcher m = this.regexCompiled.matcher(content);
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
            logger.warn(UNKNOWN_FIELD_OF_CLASS_2P, this.objectField, object.getClass().getName());
            logger.debug(Messages.MSG_ERROR_DETAILS, ex);
        } catch (IllegalAccessException ex) {
            logger.warn(CANT_ACCESS_FIELD_OF_CLASS_2P, this.objectField, object.getClass().getName());
            logger.debug(Messages.MSG_ERROR_DETAILS, ex);
        }
        return false;
    }

}
