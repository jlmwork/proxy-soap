/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools.saml.asserter;

import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
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
        }
        LOGGER.debug("Parameters validated.");
    }
    
}
