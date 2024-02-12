package com.evolve.alpaca.importing.importDbf.person;

import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DbfPersonShoud {

    @Test
    void happyPath() {
        final Map<String, Object> input = Map.of("FOO", "BAR");

        var result = DbfPerson.rawData(input);

        assertThat(result)
                .isEqualTo(Map.of("FOO", "BAR"));
    }

    @Test
    void handleNull() {
        final Map<String, Object> input = null;

        var result = DbfPerson.rawData(input);

        assertThat(result)
                .isEqualTo(Map.of());
    }

    @Test
    void handleNullAsValue() {
        final Map<String, Object> input = Maps.newHashMap("FOO", null);

        var result = DbfPerson.rawData(input);

        assertThat(result)
                .isEqualTo(Map.of("FOO", ""));
    }
}