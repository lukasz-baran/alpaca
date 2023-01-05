package com.evolve.importing.importDoc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImportAlphanumericShould {


    @Test
    void verifyGroups() {
        assertThat(ImportAlphanumeric.START_SECTIONS)
                .hasSize(25);
    }
}