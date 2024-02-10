package com.evolve.domain;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Shortened data about a person for showing in the main table
 * @param firstName      imię
 * @param secondName     drugie imię
 * @param lastName       nazwisko
 * @param dob            urodzony/urodzona
 * @param registryNumber numer ewidencyjny (kartoteki)
 */
public record PersonListView(String personId, String firstName, String secondName, String lastName,
                             LocalDate dob, PersonStatus status, Long registryNumber, Boolean retired,
                             Boolean exemptFromFees) {

    public static PersonListView of(Person person) {
        return new PersonListView(person.getPersonId(), person.getFirstName(), person.getSecondName(),
                person.getLastName(),
                person.getDob(),
                personStatus(person),
                registryNumber(person),
                person.getRetired(),
                person.getExemptFromFees());
    }

    public Optional<Long> getRegistryNumber() {
        return Optional.ofNullable(registryNumber);
    }

    static PersonStatus personStatus(Person person) {
        return Optional.ofNullable(person.getStatus())
                .orElse(PersonStatus.ACTIVE);
    }

    static Long registryNumber(Person person) {
        return Optional.ofNullable(person.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum)
                .map(Long::valueOf)
                .orElse(null);
    }

}
