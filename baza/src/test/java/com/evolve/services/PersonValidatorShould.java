package com.evolve.services;

import com.evolve.domain.Person;
import com.evolve.validation.ValidationResult;
import org.junit.jupiter.api.Test;

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

        // when
        person.setFirstName("");
        person.setLastName("");
        result = validator.validate(person);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors())
                .contains(PersonValidator.LAST_NAME_CANNOT_BE_EMPTY,
                        PersonValidator.FIRST_NAME_CANNOT_BE_EMPTY);

    }
}