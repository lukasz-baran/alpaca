package com.evolve.services;

import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.evolve.domain.Address;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class AddressValidator implements Validator<Address> {
    public static final String CITY_IS_NOT_VALID = "City cannot be empty";
    public static final String POSTAL_CODE_IS_NOT_VALID = "Postal code cannot be empty";
    public static final String STREET_IS_NOT_VALID = "Street cannot be empty";

    @Override
    public ValidationResult validate(Address toValidate) {
        final Set<String> violations = new HashSet<>();

        if (StringUtils.isBlank(toValidate.getStreet())) {
            violations.add(STREET_IS_NOT_VALID);
        }

        if (StringUtils.isBlank(toValidate.getCity())) {
            violations.add(CITY_IS_NOT_VALID);
        }

        if (StringUtils.isBlank(toValidate.getPostalCode())) {
            violations.add(POSTAL_CODE_IS_NOT_VALID);
        }

        return new ValidationResult(violations);
    }
}
