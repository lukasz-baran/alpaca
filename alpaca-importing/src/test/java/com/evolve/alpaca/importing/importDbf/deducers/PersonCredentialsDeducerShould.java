package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.domain.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PersonCredentialsDeducerShould {

    @Test
    void deduceSecondName() {
        DbfPerson dbfPerson = DbfPerson.builder()
                .NAZ_ODB1("KOWALSKA ZOFIA GRAŻYNA")
                .NAZ_ODB2("KOWALSKA ZOFIA GRAŻYNA")
                .build();

        PersonCredentialsDeducer namePersonDeducer = new PersonCredentialsDeducer(dbfPerson, mock(IssuesLogger.ImportIssues.class));

        var result = namePersonDeducer.deduceFrom(List.of()); // result is ignored

        assertThat(result)
                .hasValue(new PersonCredentialsDeducer.DeducedCredentials(
                        "Zofia",
                        "Grażyna",
                        "Kowalska"));
        assertThat(result.get().getGender())
                .isEqualTo(Person.Gender.FEMALE);

    }

}