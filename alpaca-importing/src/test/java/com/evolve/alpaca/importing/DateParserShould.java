package com.evolve.alpaca.importing;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DateParserShould {

    @Test
    void detectRomanLiterals() {
        // given
        final String input = "15-VIII-1998";
        // when
        final Optional<LocalDate> result = DateParser.parse(input);
        // then
        assertThat(result).hasValue(LocalDate.of(1998, Month.AUGUST, 15));
    }

    @Test
    void parseFourNumbersYear() {
        // given
        final String input = "28.02.2001";
        // when
        final Optional<LocalDate> result = DateParser.parse(input);
        // then
        assertThat(result).hasValue(LocalDate.of(2001, Month.FEBRUARY, 28));
    }

}