package com.evolve.alpaca.importing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DateParserShould {

    @Test
    void detectRomanLiterals() {
        final String input = "15-VIII-1998";

        assertTrue(DateParser.parse(input).isPresent());
    }

}