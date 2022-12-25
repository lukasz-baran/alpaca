package com.evolve.importDbf;

import com.evolve.domain.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmartPersonDeducerShould {

    @Test
    void deduceAddress() {
        //		"naz_ODB1": "CZEKAŃSKI PI0TR",
        //		"naz_ODB2": "CZEKAŃSKI PI0TR",
        //		"naz_ODB3": "17.12.64 5.11.92",
        //		"naz_ODB4": "35-020 Rzeszów",
        // "naz_ODB5": "Słoneczna 1 / 7",
        //"naz_ODB6": "35-020 Rzeszów",
        //		"naz_ODB7": "rez.",

        var person = DbfPerson.builder()
                .NAZ_ODB1("CZEKAŃSKI PI0TR")
                .NAZ_ODB2("CZEKAŃSKI PI0TR")
                .NAZ_ODB3("17.12.64 5.11.92")
                .NAZ_ODB4("35-020 Rzeszów")
                .NAZ_ODB5("Słoneczna 1 / 7")
                .NAZ_ODB6("35-020 Rzeszów")
                .NAZ_ODB7("rez.")
                .build();

        var address = SmartPersonDeducer.decuceAddress(person);

        assertThat(address)
                .hasValue(Address.of("Słoneczna 1 / 7", "35-020 Rzeszów"));


    }

}