package com.evolve.services;

import com.evolve.domain.Person;
import com.evolve.validation.ValidationResult;
import com.evolve.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class PersonValidator implements Validator<Person> {

    @Override
    public ValidationResult validate(Person toValidate) {
        final Set<String> violations = new HashSet<>();

        if (StringUtils.isBlank(toValidate.getFirstName())) {
            violations.add("First name cannot be empty");
        }

        if (StringUtils.isBlank(toValidate.getLastName())) {
            violations.add("Last name cannot be empty");
        }

        return new ValidationResult(violations);
    }

}
