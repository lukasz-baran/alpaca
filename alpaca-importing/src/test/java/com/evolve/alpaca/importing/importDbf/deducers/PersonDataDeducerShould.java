package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.RegistryNumbers;
import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.domain.PersonId;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

class PersonDataDeducerShould {

    final RegistryNumbers registryNumbers = new RegistryNumbers();

    @Test
    void deducePersonData() {
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("22095")
                .NAZ_ODB1("EINSTEIN ALBERT")
                .NAZ_ODB2("EINSTEIN ALBERT")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Monopolowa 1 / 7")
                .NAZ_ODB6("35-020 Rzeszów")
                .NAZ_ODB7("ż.Alicja Einstein")
                .build();
        final Person person = new PersonDataDeducer(PERSON_DBF, new IssuesLogger(), registryNumbers).deduce().orElseThrow();

        assertPerson(person)
                .hasPersonId(new PersonId("22", "095"))
                .hasFirstName("Albert")
                .hasLastName("Einstein")
                .wasBornOn(LocalDate.of(1964, Month.DECEMBER, 17))
                .hasAddress(new Person.PersonAddress(Address.of("Monopolowa 1 / 7", "35-020", "Rzeszów"), Person.AddressType.HOME))
                .hasAuthorizedPerson(Person.AuthorizedPerson.builder()
                        .firstName("Alicja")
                        .lastName("Einstein")
                        .relation("żona")
                        .build());
    }

    @Test
    void handleDoubleLastName() {
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("12067")
                .NAZ_ODB1("KOWALSKA-MADERA ANNA")
                .NAZ_ODB2("KOWALSKA-MADERA ANNA")
                .NAZ_ODB3("A. Mickiewicza 13")
                .NAZ_ODB4("35-211 Rzeszów")
                .NAZ_ODB5("")
                .NAZ_ODB6("25.08.69 08.00")
                .NAZ_ODB7("m.Zygmunt Madera")
                .build();
        final Person person = new PersonDataDeducer(PERSON_DBF, new IssuesLogger(), registryNumbers).deduce().orElseThrow();

        assertPerson(person)
                .hasPersonId(new PersonId("12", "067"))
                .hasFirstName("Anna")
                .hasLastName("Kowalska-Madera")
                .wasBornOn(LocalDate.of(1969, Month.AUGUST, 25))
                //.hasAddress(new Person.PersonAddress(Address.of("A. Mickiewicza 13", "35-211", "Rzeszów"), Person.AddressType.HOME))
                .hasAuthorizedPerson(Person.AuthorizedPerson.builder()
                        .firstName("Zygmunt")
                        .lastName("Madera")
                        .relation("mąż")
                        .build());
    }

    @Test
    void ignoreIncorrectPersonData() {
        // given
        final DbfPerson invalidPersonData = DbfPerson.builder()
                .SYM_ODB("11")
                .build();

        // when
        final Optional<Person> person = new PersonDataDeducer(invalidPersonData, new IssuesLogger(), registryNumbers).deduce();

        // then
        assertThat(person).isEmpty();
    }


}