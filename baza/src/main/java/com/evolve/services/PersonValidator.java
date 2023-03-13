package com.evolve.services;

import com.evolve.domain.Person;
import com.evolve.validation.ValidationAssertion;
import com.evolve.validation.ValidationResult;
import com.evolve.validation.Validator;

import java.util.HashSet;
import java.util.Set;

public class PersonValidator implements Validator<Person> {

    @Override
    public ValidationResult validate(Person toValidate) {
        final Set<String> violations = new HashSet<>();

        final ValidationAssertion<Person> validationAssertion = new ValidationAssertion<>(toValidate);
        validationAssertion
                .getViolations()
                .forEach(violation -> violations.add(violation.getMessage()));

        return new ValidationResult(violations);
    }

}
