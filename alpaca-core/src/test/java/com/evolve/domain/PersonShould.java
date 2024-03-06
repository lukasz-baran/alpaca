package com.evolve.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

class PersonShould {

    final LocalDate dob = LocalDate.of(1990, 10, 10);
    final LocalDate death = LocalDate.of(2020, 4, 10);
    final LocalDate joined = LocalDate.of(2001, 7, 1);
    final LocalDate resignedDate = LocalDate.of(2021, 12, 2);

    @Test
    void alwaysPutDeathDateAsTheLastStatus() {
        // given
        Person person = new Person();

        // when
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.BORN, dob);
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.DIED, death);
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.JOINED, joined);

        // then
        assertPerson(person)
                .hasStatusHistory(PersonStatusChange.born(dob),
                        PersonStatusChange.joined(joined),
                        PersonStatusChange.died(death));
    }

    @Test
    void allowToResignAndJoinAgain() {
        // given
        Person person = new Person();

        // when
        var newJoinedDate = LocalDate.of(2022, 3, 20);
        var joinedAgainDate = List.of(PersonStatusChange.born(dob),
                PersonStatusChange.joined(joined),
                PersonStatusChange.resigned(resignedDate),
                PersonStatusChange.joined(newJoinedDate));
        person.updateStatusChanges(joinedAgainDate);

        // then
        assertPerson(person)
                .hasStatusHistory(PersonStatusChange.born(dob),
                        PersonStatusChange.joined(joined),
                        PersonStatusChange.resigned(resignedDate),
                        PersonStatusChange.joined(newJoinedDate));
    }

    @Test
    void setRetirement() {
        assertThat(new Person().updateRetirement(true).getRetired())
                .isTrue();
        assertThat(new Person().updateRetirement(false).getRetired())
                .isNull();

        assertThat(Person.builder().retired(true).build().updateRetirement(true).getRetired())
                .isTrue();
        assertThat(Person.builder().retired(false).build().updateRetirement(true).getRetired())
                .isTrue();

        assertThat(Person.builder().retired(true).build().updateRetirement(false).getRetired())
                .isNull();

        assertThat(Person.builder().retired(true).build().updateRetirement(null).getRetired())
                .isTrue();
    }

    @Test
    void updatePeselWithNewValue() {
        final String pesel = "123";

        assertPerson(new Person().updatePesel(pesel))
                .hasPesel(pesel);

        assertPerson(Person.builder().pesel("").build().updatePesel(pesel))
                .hasPesel(pesel);

        assertPerson(Person.builder().pesel("456").build().updatePesel(pesel))
                .hasPesel(pesel);
    }

    @Test
    void notUpdatePeselWhenNull() {
        assertPerson(new Person().updatePesel(null))
                .hasNoPesel();

        assertPerson(Person.builder().pesel("").build().updatePesel(null))
                .hasPesel("");

        assertPerson(Person.builder().pesel("456").build().updatePesel(null))
                .hasPesel("456");
    }

    @Test
    void clearPeselWhenEmpty() {
        final String clearingValue = "";

        assertPerson(new Person().updatePesel(clearingValue))
                .hasNoPesel();

        assertPerson(Person.builder().pesel("").build().updatePesel(clearingValue))
                .hasNoPesel();

        assertPerson(Person.builder().pesel("456").build().updatePesel(clearingValue))
                .hasNoPesel();
    }

    @Test
    void updateIdNumberWithNewValue() {
        final String idnumber = "123";

        assertPerson(new Person().updateIdNumber(idnumber))
                .hasIdNumber(idnumber);

        assertPerson(Person.builder().idNumber("").build().updateIdNumber(idnumber))
                .hasIdNumber(idnumber);

        assertPerson(Person.builder().idNumber("456").build().updateIdNumber(idnumber))
                .hasIdNumber(idnumber);
    }

    @Test
    void notUpdateIdNumberWhenNull() {
        assertPerson(new Person().updateIdNumber(null))
                .hasNoIdNumber();

        assertPerson(Person.builder().idNumber("").build().updateIdNumber(null))
                .hasIdNumber("");

        assertPerson(Person.builder().idNumber("456").build().updateIdNumber(null))
                .hasIdNumber("456");
    }

    @Test
    void clearIdNumberWhenEmpty() {
        final String clearingValue = "";

        assertPerson(new Person().updateIdNumber(clearingValue))
                .hasNoIdNumber();

        assertPerson(Person.builder().idNumber("").build().updateIdNumber(clearingValue))
                .hasNoIdNumber();

        assertPerson(Person.builder().idNumber("456").build().updateIdNumber(clearingValue))
                .hasNoIdNumber();
    }


}