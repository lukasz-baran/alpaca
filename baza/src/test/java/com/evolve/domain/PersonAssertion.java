package com.evolve.domain;

import org.assertj.core.api.ObjectAssert;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonAssertion extends ObjectAssert<Person> {

    public PersonAssertion(Person person) {
        super(person);
    }

    public static PersonAssertion assertPerson(Person person) {
        return new PersonAssertion(person);
    }

    public PersonAssertion hasPersonId(PersonId expected) {
        assertThat(actual.getPersonId()).isEqualTo(expected.toString());
        return this;
    }

    public PersonAssertion hasFirstName(String expected) {
        assertThat(actual.getFirstName()).isEqualTo(expected);
        return this;
    }

    public PersonAssertion hasLastName(String expected) {
        assertThat(actual.getLastName()).isEqualTo(expected);
        return this;
    }

    public PersonAssertion wasBornOn(LocalDate expectedDob) {
        assertThat(actual.getDob()).isEqualTo(expectedDob);
        return this;
    }

    public PersonAssertion hasAddress(Person.PersonAddress expecteAddress) {
        assertThat(actual.getAddresses()).contains(expecteAddress);
        return this;
    }

    public PersonAssertion hasAuthorizedPerson(Person.AuthorizedPerson expected) {
        assertThat(actual.getAuthorizedPersons()).contains(expected);
        return this;
    }
}