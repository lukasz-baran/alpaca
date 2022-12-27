package com.evolve.importDbf;

import com.evolve.domain.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmartPersonDeducerShould {

    @Test
    void deduceAddress() {

        var person = DbfPerson.builder()
                .NAZ_ODB1("EINSTEIN ALBERT")
                .NAZ_ODB2("EINSTEIN ALBERT")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Monopolowa 1 / 7")
                .NAZ_ODB6("35-020 Rzeszów")
                .NAZ_ODB7("rez.")
                .build();

        var address = SmartPersonDeducer.deduceAddress(person);

        assertThat(address)
                .hasValue(Address.of("Monopolowa 1 / 7", "35-020", "Rzeszów"));

    }

    @Test
    void deduceCityEvenIfCityNameHasWhitespace() {
        var person = DbfPerson.builder()
                .NAZ_ODB1("EINSTEIN ALBERT")
                .NAZ_ODB2("EINSTEIN ALBERT")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Monopolowa 1 / 7")
                .NAZ_ODB6("39-308 Wadowice Górne")
                .NAZ_ODB7("rez.")
                .build();
        var address = SmartPersonDeducer.deduceAddress(person);

        assertThat(address)
                .hasValue(Address.of("Monopolowa 1 / 7", "39-308", "Wadowice Górne"));
    }

}