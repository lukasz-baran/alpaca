package com.evolve.validation;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Getter
public class ValidationAssertion<T> {

    private final Set<ConstraintViolation<T>> violations;

    public ValidationAssertion(T toValidate) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final Validator validator = validatorFactory.getValidator();
            violations = validator.validate(toValidate);
        }
    }


}
