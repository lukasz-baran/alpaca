package com.evolve.alpaca.importing.importDoc;

import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.domain.Group;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@Slf4j
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

    @Test
    void importPeople() throws IOException {
        // given
        final Resource oldDoc = new DefaultResourceLoader().getResource("PLAN KONT.doc");
        assumeThat(oldDoc.exists())
                .isTrue();

        // when
        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric(true)
                .processDocFile(oldDoc.getFile().getPath());

        // then
        log.info("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych ");
    }

}