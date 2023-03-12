package com.evolve.services;

import com.evolve.domain.Person;
import com.evolve.validation.ValidationResult;
import com.evolve.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

public class PersonValidator implements Validator<Person> {
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = "First name cannot be empty";
    public static final String LAST_NAME_CANNOT_BE_EMPTY = "Last name cannot be empty";
    public static final String EMAIL_IS_NOT_VALID = "Email must contain @";
    public static final String CITY_IS_NOT_VALID = "City cannot be empty";
    public static final String POSTAL_CODE_IS_NOT_VALID = "Postal code cannot be empty";
    public static final String STREET_IS_NOT_VALID = "Street cannot be empty";

    @Override
    public ValidationResult validate(Person toValidate) {
        final Set<String> violations = new HashSet<>();

        if (StringUtils.isBlank(toValidate.getFirstName())) {
            violations.add(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (StringUtils.isBlank(toValidate.getLastName())) {
            violations.add(LAST_NAME_CANNOT_BE_EMPTY);
        }

        if (StringUtils.isNotEmpty(toValidate.getEmail())) {
            if (!toValidate.getEmail().contains("@")) {
                violations.add(EMAIL_IS_NOT_VALID);
            }
        }

        emptyIfNull(toValidate.getAddresses())
                .forEach(personAddress -> {
                    if (StringUtils.isBlank(personAddress.getCity())) {
                        violations.add(CITY_IS_NOT_VALID);
                    }

                    if (StringUtils.isBlank(personAddress.getStreet())) {
                        violations.add(STREET_IS_NOT_VALID);
                    }

                    if (StringUtils.isBlank(personAddress.getPostalCode())) {
                        violations.add(POSTAL_CODE_IS_NOT_VALID);
                    }

                });

        return new ValidationResult(violations);
    }

}
