package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.Person;
import com.evolve.domain.PersonId;
import com.evolve.importing.importDbf.DbfPerson;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

class PersonDataDeducerShould {
    private static final DbfPerson PERSON_DBF = DbfPerson.builder()
            .SYM_ODB("22095")
            .NAZ_ODB1("EINSTEIN ALBERT")
            .NAZ_ODB2("EINSTEIN ALBERT")
            .NAZ_ODB3("17.12.64 5.11.92")
            .NAZ_ODB4("35-020 Rzeszów")
            .NAZ_ODB5("Monopolowa 1 / 7")
            .NAZ_ODB6("35-020 Rzeszów")
            .NAZ_ODB7("ż.Alicja Einstein")
            .build();

    @Test
    void deducePersonData() {
        // when
        final Person person = new PersonDataDeducer(PERSON_DBF).deduce().orElseThrow();

        // then
        assertPerson(person)
                .hasPersonId(new PersonId("22", "095"))
                .hasFirstName("Albert")
                .hasLastName("Einstein")
                .hasAuthorizedPerson(Person.AuthorizedPerson.builder()
                        .firstName("Alicja")
                        .lastName("Einstein")
                        .relation("żona")
                        .build());
    }

    @Test
    void ignoreIncorrectPersonData() {
        // given
        final DbfPerson invalidPersonData = DbfPerson.builder()
                .SYM_ODB("11")
                .build();

        // when
        final Optional<Person> person = new PersonDataDeducer(invalidPersonData).deduce();

        // then
        assertThat(person).isEmpty();
    }


}