package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.RegistryNumbers;
import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.domain.Address;
import com.evolve.domain.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SmartAddressPersonDeducerShould {

    final IssuesLogger issuesLogger = new IssuesLogger();
    final RegistryNumbers registryNumbers = new RegistryNumbers();

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
            new PersonDataDeducer(person, issuesLogger, registryNumbers).deduce().orElseThrow().getAddresses()
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
                new PersonDataDeducer(person, issuesLogger, registryNumbers).deduce().orElseThrow().getAddresses()
                        .stream().findFirst();

        assertThat(address)
                .hasValue(new Person.PersonAddress(Address.of("Monopolowa 1 / 7", "39-308", "Wadowice Górne"), Person.AddressType.HOME));

    }

    @ParameterizedTest
    @ValueSource(strings = {"Monopolowa 1 / 7", "Kochanowskiego 17",
            "Świadka 7/119", "M. C.Skłodowskiej 4/13", "Grunwaldzka 8 A",
            "Al. Niepodległości 14",
            "Kochanowskiego 3 B",
            "Paderewskiego 122a",
            "Obrońców poczty Gdań 1A/1",
            "Warszawska 1/3/219",
            "Chmielnik 476 G",
            "Seniora 2/IV/4",
            "Wiosenna 1 c / 3",
            "Obr. Pokoju 74d /15",
            "Sikorskiego 6a/3",
            "Dąbka 10 F",
            "8 Marca 2",
            "Słowackiego 15a",
            "6-go-Sierpnia 4",
            "Potockiego 5 a",
            "8-go Marca 22",
            "Nagawczyna 210 H",
            "16 Stycznia 22 / 7",
            "3 Maja 33/2",
            "11-ego Listopada 13/22",
            "1000-lecia 32 / 15",
            "29-ego Listopada 51/7"
    })
    void detectValidStreetNames(String input) {
        assertThat(SmartAddressPersonDeducer.isStreet(input))
                .isTrue();
    }
}