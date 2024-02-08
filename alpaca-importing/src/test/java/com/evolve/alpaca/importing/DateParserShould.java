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

    @Test
    void removeTrailingPolishYearSuffix() {
        assertThat(DateParser.parse("12.04.2005r."))
                .hasValue(LocalDate.of(2005, Month.APRIL, 12));

        assertThat(DateParser.parse("4.07.2006 r."))
                .hasValue(LocalDate.of(2006, Month.JULY, 4));

        assertThat(DateParser.parse("4.07.2006 R."))
                .hasValue(LocalDate.of(2006, Month.JULY, 4));
    }

    @Test
    void parseShortDateWithRomanNumbers() {
        assertThat(DateParser.parse("IV / 2005"))
                .hasValue(LocalDate.of(2005, Month.APRIL, 1));
        assertThat(DateParser.parse("IV/2005"))
                .hasValue(LocalDate.of(2005, Month.APRIL, 1));
        assertThat(DateParser.parse("IV-2005"))
                .hasValue(LocalDate.of(2005, Month.APRIL, 1));
        assertThat(DateParser.parse("IV-05"))
                .hasValue(LocalDate.of(1905, Month.APRIL, 1)); // 1905 will be corrected later
    }

}