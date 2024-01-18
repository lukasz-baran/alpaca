package com.evolve.alpaca.importing.importDbf.deducers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JoiningDateDeducerShould {
    @Mock IssuesLogger.ImportIssues importIssues;
    @InjectMocks JoiningDateDeducer deducer;

    @Test
    void notDeduceJoinedDateFromDeceasedDate() {
        final List<String> guesses = List.of("ALEKSANDRA 10.05.33 V / 7",
                "c.Magdalena WesołowskaKie",
                 "Al.Niepodległości 11/7",
                 "39-300 Mielec",
                 "zm. 19.07.2023",
                "");

        var result = deducer.deduceFrom(guesses);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void adjustJoinedDateToCurrentCentury() {
        final List<String> guesses = List.of("19.10.70 17.02.04");

        var result = deducer.deduceFrom(guesses);

        assertThat(result)
                .hasValue(LocalDate.of(2004, 2, 17));
    }

}