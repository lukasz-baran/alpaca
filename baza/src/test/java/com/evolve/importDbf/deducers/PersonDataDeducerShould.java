package com.evolve.importDbf.deducers;

import com.evolve.domain.Person;
import com.evolve.importDbf.DbfPerson;
import org.junit.jupiter.api.Test;

import static com.evolve.domain.PersonAssertion.assertPerson;

class PersonDataDeducerShould {
    private static final DbfPerson PERSON_DBF = DbfPerson.builder()
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
        final Person person = new PersonDataDeducer(PERSON_DBF).deduce();

        // then
        assertPerson(person)
                .hasFirstName("Albert")
                .hasLastName("Einstein")
                .hasAuthorizedPerson(Person.AuthorizedPerson.builder()
                        .firstName("Alicja")
                        .lastName("Einstein")
                        .relation("żona")
                        .build());
    }



}