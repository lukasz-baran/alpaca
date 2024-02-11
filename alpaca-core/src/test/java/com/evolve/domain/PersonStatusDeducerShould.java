package com.evolve.domain;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class PersonStatusDeducerShould {

    @Test
    void deduceProperStatus() {
        final LocalDate born = LocalDate.of(1944, 9, 23);
        final LocalDate resigned = LocalDate.of(2020,6, 29);
        final LocalDate joined = LocalDate.of(1993, 2, 14);

        final List<PersonStatusChange> changes = Lists.newArrayList(PersonStatusChange.born(born),
                PersonStatusChange.resigned(resigned));

        Person person = Person.builder()
                .dob(born)
                .statusChanges(changes)
                .build();

        PersonStatusDeducer personStatusDeducer = new PersonStatusDeducer(person);

        personStatusDeducer.addOrUpdateStatusChange(PersonStatusChange.EventType.JOINED, joined);



    }


}