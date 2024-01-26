package com.evolve.domain;

import org.assertj.core.api.AbstractAssert;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonAssertion extends AbstractAssert<PersonAssertion, Person> {

    public PersonAssertion(Person person) {
        super(person, PersonAssertion.class);
    }

    public static PersonAssertion assertPerson(Person person) {
        return new PersonAssertion(person);
    }

    public PersonAssertion hasPersonId(PersonId expected) {
        assertThat(actual.getPersonId()).isEqualTo(expected.toString());
        return this;
    }

    public PersonAssertion hasPersonId(String expected) {
        assertThat(actual.getPersonId()).isEqualTo(expected);
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

    public PersonAssertion hasUnitNumber(String expectedUnitNumber) {
        assertThat(actual.getUnitNumber()).isEqualTo(expectedUnitNumber);
        return this;
    }

    public PersonAssertion hasEmail(String expectedEmail) {
        assertThat(actual.getEmail()).hasValue(expectedEmail);
        return this;
    }

    public PersonAssertion hasNoBirthDate() {
        assertThat(actual.getDob()).isNull();
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

    public PersonAssertion hasRegistryNumber(RegistryNumber registryNumber) {
        assertThat(actual.getRegistryNumber()).isEqualTo(registryNumber);
        return this;
    }

    public PersonAssertion hasNoOldRegistryNumber() {
        assertThat(actual.getOldRegistryNumber()).isNull();
        return this;
    }

    public PersonAssertion hasOldRegistryNumber(RegistryNumber oldRegistryNumber) {
        assertThat(actual.getOldRegistryNumber()).isEqualTo(oldRegistryNumber);
        return this;
    }

    public PersonAssertion hasBankAccount(BankAccount... bankAccounts) {
        assertThat(actual.getBankAccounts()).containsExactlyInAnyOrder(bankAccounts);
        return this;
    }

    public PersonAssertion hasStatusHistory(PersonStatusChange... statusChanges) {
        assertThat(actual.getStatusChanges()).containsExactly(statusChanges);
        return this;
    }

    public PersonAssertion hasStatus(PersonStatus expectedStatus) {
        assertThat(actual.getStatus().getStatus()).isEqualTo(expectedStatus);
        return this;
    }

}