package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.importing.importDbf.DbfPerson;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SmartAddressPersonDeducerShould {

    @Test
    void deduceAddress() {

        var person = DbfPerson.builder()
                .SYM_ODB("02543")
                .NAZ_ODB1("EINSTEIN ALBERT")
                .NAZ_ODB2("EINSTEIN ALBERT")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Monopolowa 1 / 7")
                .NAZ_ODB6("35-020 Rzeszów")
                .NAZ_ODB7("rez.")
                .build();

        Optional<Person.PersonAddress> address =
            new PersonDataDeducer(person).deduce().orElseThrow().getAddresses()
                    .stream().findFirst();

        assertThat(address)
                .hasValue(new Person.PersonAddress(Address.of("Monopolowa 1 / 7", "35-020", "Rzeszów"), Person.AddressType.HOME));

    }

    @Test
    void deduceCityEvenIfCityNameHasWhitespace() {
        var person = DbfPerson.builder()
                .SYM_ODB("02543")
                .NAZ_ODB1("EINSTEIN ALBERT")
                .NAZ_ODB2("EINSTEIN ALBERT")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Monopolowa 1 / 7")
                .NAZ_ODB6("39-308 Wadowice Górne")
                .NAZ_ODB7("rez.")
                .build();

        Optional<Person.PersonAddress> address =
                new PersonDataDeducer(person).deduce().orElseThrow().getAddresses()
                        .stream().findFirst();

        assertThat(address)
                .hasValue(new Person.PersonAddress(Address.of("Monopolowa 1 / 7", "39-308", "Wadowice Górne"), Person.AddressType.HOME));

    }

}