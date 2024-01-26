package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.evolve.domain.Person;
import com.evolve.domain.PersonContactData;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonEditionValidator implements Validator<EditPersonDataCommand> {
    public static final String FIRST_NAME_CANNOT_BE_EMPTY = "First name cannot be empty";
    public static final String LAST_NAME_CANNOT_BE_EMPTY = "Last name cannot be empty";
    public static final String EMAIL_IS_NOT_VALID = "Email address is not valid";
    public static final String REGISTRY_NUMBER_MUST_BE_NUMERIC = "Numer kartoteki musi być liczbą: %s";

    private final AddressValidator addressValidator = new AddressValidator();

    @Override
    public ValidationResult validate(EditPersonDataCommand command) {
        final Set<String> violations = new HashSet<>();

        if (StringUtils.isBlank(command.firstName())) {
            violations.add(FIRST_NAME_CANNOT_BE_EMPTY);
        }

        if (StringUtils.isBlank(command.lastName())) {
            violations.add(LAST_NAME_CANNOT_BE_EMPTY);
        }

        validateEmails(violations, command.contactData());

        if (StringUtils.isNotBlank(command.registryNumber())) {
            if (!StringUtils.isNumeric(command.registryNumber())) {
                violations.add(REGISTRY_NUMBER_MUST_BE_NUMERIC.formatted(command.registryNumber()));
            }
        }

        validateAddresses(violations, command.addresses());

        return new ValidationResult(violations);
    }

    private void validateEmails(Set<String> validations, List<PersonContactData> contactData) {
        ListUtils.emptyIfNull(contactData).forEach(contact -> {
           if (contact.getType() == PersonContactData.ContactType.EMAIL) {
               if (!EmailValidator.getInstance().isValid(contact.getData())) {
                   validations.add(EMAIL_IS_NOT_VALID);
               }
           }
        });
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
