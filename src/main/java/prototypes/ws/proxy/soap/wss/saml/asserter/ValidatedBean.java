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
package prototypes.ws.proxy.soap.wss.saml.asserter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jlamande
 */
public abstract class ValidatedBean {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(ValidatedBean.class);

    protected void validate() {
        LOGGER.debug("Validating Parameters...");
        /*ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         Validator validator = factory.getValidator();
         Set<ConstraintViolation<ValidatedBean>> constraintViolations = validator.validate(this);
         if (constraintViolations.size() > 0) {
         StringBuilder errors = new StringBuilder();
         Iterator<ConstraintViolation<ValidatedBean>> it = constraintViolations.iterator();
         while (it.hasNext()) {
         String errorMsg = it.next().getMessage();
         LOGGER.error(errorMsg);
         errors.append(errorMsg).append("\n");
         }
         throw new IllegalArgumentException(errors.toString());
         }*/
        LOGGER.debug("Parameters validated.");
    }

}
