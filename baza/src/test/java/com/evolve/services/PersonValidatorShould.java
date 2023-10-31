package com.evolve.services;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.alpaca.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonValidatorShould {
    final PersonValidator validator = new PersonValidator();

    @Test
    void validatePerson() {
        // given
        final Person person = new Person();

        person.setFirstName("John");
        person.setLastName("Doe");

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
        //assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors())
                .hasSize(3)
                .contains(Person.LAST_NAME_CANNOT_BE_EMPTY,
                        Person.FIRST_NAME_CANNOT_BE_EMPTY,
                        Address.STREET_IS_NOT_VALID);

    }
}