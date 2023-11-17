package com.evolve.services;

import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.evolve.domain.Person;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonEditionValidator implements Validator<Person> {
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = "First name cannot be empty";
    public static final String LAST_NAME_CANNOT_BE_EMPTY = "Last name cannot be empty";
    public static final String EMAIL_IS_NOT_VALID = "Email address is not valid";

    private final AddressValidator addressValidator = new AddressValidator();

    @Override
    public ValidationResult validate(Person personToValidate) {
        final Set<String> violations = new HashSet<>();

        if (StringUtils.isBlank(personToValidate.getFirstName())) {
            violations.add(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (StringUtils.isBlank(personToValidate.getLastName())) {
            violations.add(LAST_NAME_CANNOT_BE_EMPTY);
        }

        if (StringUtils.isNotBlank(personToValidate.getEmail())) {
            if (!EmailValidator.getInstance().isValid(personToValidate.getEmail())) {
                violations.add(EMAIL_IS_NOT_VALID);
            }
        }

        validateAddresses(violations, personToValidate.getAddresses());

        return new ValidationResult(violations);
    }

    private void validateAddresses(Set<String> validations, List<Person.PersonAddress> addresses) {
        ListUtils.emptyIfNull(addresses).forEach(address -> {
            final ValidationResult addressValid = addressValidator.validate(address);
            if (!addressValid.isValid()) {
                validations.addAll(addressValid.getErrors());
            }
        });
    }

}
