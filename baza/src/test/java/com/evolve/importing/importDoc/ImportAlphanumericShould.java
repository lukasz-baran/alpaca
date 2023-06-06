package com.evolve.importing.importDoc;

import com.evolve.domain.Group;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImportAlphanumericShould {

    @Test
    void verifyGroups() {
        assertThat(ImportAlphanumeric.START_SECTIONS)
                .isSubsetOf(Group.allowedCharacters()
                        .stream()
                        .map(String::valueOf)
                        .toList())
                .hasSize(25);
    }

}