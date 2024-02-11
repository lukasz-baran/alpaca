package com.evolve.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class PersonStatusChangeShould {

    LocalDate born = LocalDate.of(1933, 5, 10);
    LocalDate joined = LocalDate.of(1961, 3, 27);

    @Test
    void preserveProperOrderOfStatusChanges() {
        List<PersonStatusChange> input = List.of(PersonStatusChange.joined(joined), PersonStatusChange.born(born));

        Set<PersonStatusChange> personStatusChanges = new TreeSet<>(input);

        List.copyOf(personStatusChanges);

        System.out.println(personStatusChanges);
    }

}