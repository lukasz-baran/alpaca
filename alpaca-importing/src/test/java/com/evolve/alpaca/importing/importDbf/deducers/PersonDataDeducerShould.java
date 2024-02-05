package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.RegistryNumbers;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.domain.PersonId;
import com.evolve.domain.PersonStatusChange;
import org.junit.jupiter.api.Test;

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
                .INFO("skr zw skł 12.04.2005r.")
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
                        .build())
                .hasStatusHistory(
                        PersonStatusChange.born(LocalDate.of(1964, 12, 17)),
                        PersonStatusChange.joined(LocalDate.of(1992, 11, 5)),
                        PersonStatusChange.removed(LocalDate.of(2005, 4, 12), "12.04.2005r."));
    }

    @Test
    void handleDoubleLastName() {
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("12067")
                .NAZ_ODB1("ALFA-BETA ANNA")
                .NAZ_ODB2("ALFA-BETA ANNA")
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
                .hasLastName("Alfa-Beta")
                .wasBornOn(LocalDate.of(1969, Month.AUGUST, 25))
                .hasAddress(new Person.PersonAddress(Address.of("A. Mickiewicza 13", "35-211", "Rzeszów"), Person.AddressType.HOME))
                .hasAuthorizedPerson(Person.AuthorizedPerson.builder()
                        .firstName("Zygmunt")
                        .lastName("Madera")
                        .relation("mąż")
                        .build())
                .hasStatusHistory(PersonStatusChange.born(LocalDate.of(1969, 8, 25)));
    }

    @Test
    void decodeCorrectDeathDate() {
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("11022")
                .NAZ_ODB1("ALFA-BETA ANNA")
                .NAZ_ODB2("ALFA-BETA ANNA")
                .NAZ_ODB3("25,07 40")
                .NAZ_ODB4("")
                .NAZ_ODB5("")
                .NAZ_ODB6("")
                .NAZ_ODB7("ZMARŁA 25-08-2016")
                .build();
        final Person person = new PersonDataDeducer(PERSON_DBF, new IssuesLogger(), registryNumbers).deduce().orElseThrow();

        assertPerson(person)
                .hasPersonId(new PersonId("11", "022"))
                .hasFirstName("Anna")
                .hasLastName("Alfa-Beta")
                .hasNoBirthDate()
                .hasStatusHistory(PersonStatusChange.died(LocalDate.of(2016, 8, 25), "25-08-2016"));
    }

    @Test
    void decodeResignationDate() {
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("01003")
                .NAZ_ODB1("ALFA ADAM")
                .NAZ_ODB2("ALFA ADAM")
                .NAZ_ODB3("10.04.43 20.10.82")
                .NAZ_ODB4("ż.Beta Gamma-Delta")
                .NAZ_ODB5("Bazaltowa 9/12")
                .NAZ_ODB6("22-333 Nigdzie")
                .NAZ_ODB7("")
                .INFO("rez. 09.11.2020") //skr zw skł 12.04.2005r.
                .build();
        final Person person = new PersonDataDeducer(PERSON_DBF, new IssuesLogger(), registryNumbers).deduce().orElseThrow();

        assertPerson(person)
            .hasPersonId(new PersonId("01", "003"))
            .hasFirstName("Adam")
            .hasLastName("Alfa")
            .wasBornOn(LocalDate.of(1943, 4, 10))
            .hasStatusHistory(
                PersonStatusChange.born(LocalDate.of(1943, 4, 10)),
                PersonStatusChange.joined(LocalDate.of(1982, 10, 20)),
                PersonStatusChange.resigned(LocalDate.of(2020, 11, 9), "09.11.2020"));
    }

    @Test
    void decodeRemovalDate() {
        final LocalDate expectedDob = LocalDate.of(1955, 7, 2);
        final DbfPerson PERSON_DBF = DbfPerson.builder()
                .SYM_ODB("01003")
                .NAZ_ODB1("ALFA ADAM")
                .NAZ_ODB2("ALFA ADAM")
                .NAZ_ODB3("2.07.55 13,03,84")
                .NAZ_ODB4("s.Piotr Serwus")
                .NAZ_ODB5("Świadka 7/119")
                .NAZ_ODB6("35-310 Rzeszów")
                .NAZ_ODB7("Skreślenie 2012")
                .INFO("")
                .build();
        final Person person = new PersonDataDeducer(PERSON_DBF, new IssuesLogger(), registryNumbers).deduce().orElseThrow();

        assertPerson(person)
                .hasPersonId(new PersonId("01", "003"))
                .hasFirstName("Adam")
                .hasLastName("Alfa")
                .wasBornOn(expectedDob)
                .hasStatusHistory(
                        PersonStatusChange.born(expectedDob),
                        PersonStatusChange.joined(LocalDate.of(1984, 3, 13)),
                        PersonStatusChange.removed("2012"));
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