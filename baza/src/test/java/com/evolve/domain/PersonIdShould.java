package com.evolve.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PersonIdShould {

    @Test
    void getNextId() {
        assertThat(PersonId.nextId(PersonId.of("01009")))
                .isEqualTo(PersonId.of("01010"));

        assertThatCode(() -> PersonId.nextId(PersonId.of("01999")))
                .hasMessage("Maximum number of people in group reached: 999");
    }

}