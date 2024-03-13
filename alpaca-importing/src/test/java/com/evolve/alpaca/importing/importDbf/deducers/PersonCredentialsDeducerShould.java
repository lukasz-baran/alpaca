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
        // given
        final DbfPerson dbfPerson = DbfPerson.builder()
                .NAZ_ODB1("KOWALSKA ZOFIA GRAŻYNA")
                .NAZ_ODB2("KOWALSKA ZOFIA GRAŻYNA")
                .build();

        final PersonCredentialsDeducer namePersonDeducer = new PersonCredentialsDeducer(dbfPerson, mock(IssuesLogger.ImportIssues.class));

        // when
        var result = namePersonDeducer.deduceFrom(List.of());

        // then
        assertThat(result)
                .hasValue(new PersonCredentialsDeducer.DeducedCredentials(
                        "Zofia",
                        "Grażyna",
                        "Kowalska"));
        assertThat(result.get().getGender())
                .isEqualTo(Person.Gender.FEMALE);

    }

    @Test
    void dontTreatPreviousNameAsSecondName() {
        DbfPerson dbfPerson = DbfPerson.builder()
                .NAZ_ODB1("ALFA (BETA) GAMMA")
                .NAZ_ODB2("ALFA (BETA) GAMMA")
                .build();

        final PersonCredentialsDeducer namePersonDeducer = new PersonCredentialsDeducer(dbfPerson, mock(IssuesLogger.ImportIssues.class));

        // when
        var result = namePersonDeducer.deduceFrom(List.of());

        // then
        assertThat(result)
                .hasValue(new PersonCredentialsDeducer.DeducedCredentials(
                        "Gamma",
                        null,
                        "Alfa"));

    }

}