package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

// shortened data about a person
// for showing in the main table
@AllArgsConstructor
@Getter
public class PersonListView {
    private final String personId;

    private final String firstName; // imię
    private final String secondName; // drugie imię
    private final String lastName; // nazwisko
    private final String email;

    private final LocalDate dob; // urodzony/urodzona

    private final PersonStatus status;

    private final Long registryNumber; // numer ewidencyjny (kartoteki)

    public static PersonListView of(Person person) {
        return new PersonListView(person.getPersonId(), person.getFirstName(), person.getSecondName(),
                person.getLastName(), person.getEmail(),
                person.getDob(),
                personStatus(person),
                registryNumber(person));
    }

    static PersonStatus personStatus(Person person) {
        return Optional.ofNullable(person.getStatus()).map(PersonStatusDetails::getStatus)
                .orElse(PersonStatus.ACTIVE);
    }

    static Long registryNumber(Person person) {
        return Optional.ofNullable(person.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum)
                .map(Long::valueOf)
                .orElse(0L);
    }

}
