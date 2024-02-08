package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.PersonStatusChange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonDateOfBirthDeducerShould {
    @Mock
    IssuesLogger.ImportIssues importIssues;

    @InjectMocks
    PersonDateOfBirthDeducer deducer;

    @Test
    void guessBirthDay() {
        assertThat(deducer.deduceDob("30,11.67 30-12-1996"))
                .hasValue(PersonStatusChange.born(LocalDate.of(1967, Month.NOVEMBER, 30)));
        assertThat(deducer.deduceDob("22.12.1979  VIII-2015"))
                .hasValue(PersonStatusChange.born(LocalDate.of(1979, Month.DECEMBER, 22)));
        assertThat(deducer.deduceDob("7.02.55 2.02.87"))
                .hasValue(PersonStatusChange.born(LocalDate.of(1955, Month.FEBRUARY, 7)));
        assertThat(deducer.deduceDob("28.12.58"))
                .hasValue(PersonStatusChange.born(LocalDate.of(1958, Month.DECEMBER, 28)));
        assertThat(deducer.deduceDob("20,10,71"))
                .hasValue(PersonStatusChange.born(LocalDate.of(1971, Month.OCTOBER, 20)));
    }

    @Test
    void findCorrectGuess() {
        // given
        List<String> guesses = List.of("rubbish", "30,11.67 30-12-1996");

        // when & then
        assertThat(deducer.deduceFrom(guesses))
                .hasValue(PersonStatusChange.born(LocalDate.of(1967, Month.NOVEMBER, 30)));
    }

}