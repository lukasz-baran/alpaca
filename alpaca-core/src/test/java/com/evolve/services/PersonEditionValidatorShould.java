package com.evolve.services;

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
        final Person person = Person.builder().firstName("John").lastName("Doe").build();

        // when
        ValidationResult result = validator.validate(person);

        // then
        assertThat(result.isValid()).isTrue();

        // when -- put some invalid data into the pojo
        person.setFirstName("");
        person.setLastName("");
        person.setAddresses(List.of(new Person.PersonAddress("", "city", "zip", null))) ;

        result = validator.validate(person);

        // then
        assertThat(result.getErrors())
                .hasSize(3)
                .contains(PersonEditionValidator.LAST_NAME_CANNOT_BE_EMPTY,
                        PersonEditionValidator.FIRST_NAME_CANNOT_BE_EMPTY,
                        AddressValidator.STREET_IS_NOT_VALID);

    }
}