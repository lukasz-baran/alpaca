package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

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

    public static PersonListView of(Person person) {
        return new PersonListView(person.getPersonId(), person.getFirstName(), person.getSecondName(),
                person.getLastName(), person.getEmail(),
                person.getDob());
    }

}
