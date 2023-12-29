package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.domain.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonEditionValidatorShould {
    final PersonEditionValidator validator = new PersonEditionValidator();

    @Test
    void validatePerson() {
        // given
        final EditPersonDataCommand person = new EditPersonDataCommand("id", "John", "Doe", "secondName", null,
                List.of("123456789"), List.of(new Person.PersonAddress("street", "city", "zip", null)),
                List.of(),
                List.of(),
                "unitNumber",
                "123",
                null,
                List.of());

        // when
        ValidationResult result = validator.validate(person);

        // then
        assertThat(result.isValid()).isTrue();

        // when -- put some invalid data into the pojo
        final EditPersonDataCommand invalidCommand = new EditPersonDataCommand("id", "", "", "secondName", "invalid email address",
                List.of("123456789"), List.of(new Person.PersonAddress("", "city", "zip", null)),
                List.of(),
                List.of(),
                "unitNumber",
                "registryNumber",
                "oldRegistryNumber",
                List.of());

        result = validator.validate(invalidCommand);

        // then
        assertThat(result.getErrors())
                .hasSize(5)
                .contains(PersonEditionValidator.LAST_NAME_CANNOT_BE_EMPTY,
                        PersonEditionValidator.FIRST_NAME_CANNOT_BE_EMPTY,
                        AddressValidator.STREET_IS_NOT_VALID,
                        PersonEditionValidator.EMAIL_IS_NOT_VALID,
                        "Numer kartoteki musi być liczbą: registryNumber");

    }
}