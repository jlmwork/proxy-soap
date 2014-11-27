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
package prototypes.ws.proxy.soap.constants;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 *
 * @author JL06436S
 */
public class Messages {

    private Messages() {
    }

    public static final String MSG_ERROR = "Error";

    public static final String MSG_ERROR_DETAILS = MSG_ERROR + " Details : {}";

    public static final String MSG_ERROR_ON = "Error on {} {}";

    public static final String MSG_ERROR_CAUSE = "Cause : {}";

    public static String format(final String message, final Object[] argArray) {
        FormattingTuple ft = MessageFormatter.arrayFormat(message, argArray);
        return ft.getMessage();
    }
}
