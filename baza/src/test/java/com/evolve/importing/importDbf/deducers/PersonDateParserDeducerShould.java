package com.evolve.importing.importDbf.deducers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonDateParserDeducerShould {
    @Mock
    IssuesLogger.ImportIssues importIssues;

    @InjectMocks
    PersonDateOfBirthDeducer deducer;

    @Test
    void guessBirthDay() {
        assertThat(deducer.deduceDob("30,11.67 30-12-1996"))
                .hasValue(LocalDate.of(1967, Month.NOVEMBER, 30));
        assertThat(deducer.deduceDob("22.12.1979  VIII-2015"))
                .hasValue(LocalDate.of(1979, Month.DECEMBER, 22));
        assertThat(deducer.deduceDob("7.02.55 2.02.87"))
                .hasValue(LocalDate.of(1955, Month.FEBRUARY, 7));
        assertThat(deducer.deduceDob("28.12.58"))
                .hasValue(LocalDate.of(1958, Month.DECEMBER, 28));
        assertThat(deducer.deduceDob("20,10,71"))
                .hasValue(LocalDate.of(1971, Month.OCTOBER, 20));
    }

    @Test
    void findCorrectGuess() {
        List<String> guesses = List.of("rubbish", "30,11.67 30-12-1996");

        Optional<LocalDate> result = deducer.deduceFrom(guesses);

        assertThat(result)
                .hasValue(LocalDate.of(1967, Month.NOVEMBER, 30));
    }

}