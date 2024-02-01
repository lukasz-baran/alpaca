package com.evolve.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonStatusShould {

    @Test
    void establishProperPersonStatusBasedOnHistory() {
        assertThat(PersonStatus.basedOnStatusChange(List.of(
                PersonStatusChange.born(LocalDate.of(1933, 5, 10)),
                PersonStatusChange.died(LocalDate.of(2023, 7, 19), "19.07.2023"))))
                .isEqualTo(PersonStatus.DEAD);

        assertThat(PersonStatus.basedOnStatusChange(List.of(
                PersonStatusChange.born(LocalDate.of(1961, 3, 27)),
                PersonStatusChange.joined(LocalDate.of(1989, 3, 21)),
                PersonStatusChange.resigned("XII/00"))))
                .isEqualTo(PersonStatus.RESIGNED);
    }

}