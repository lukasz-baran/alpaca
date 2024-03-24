package com.evolve.alpaca.importing.importDoc;

import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class ImportPeopleShould {

    @Test
    void importTextFile() throws IOException {
        final Resource oldDoc = new DefaultResourceLoader().getResource("PLAN KONT.doc");
        assumeThat(oldDoc.exists())
                .isTrue();

        final List<PersonFromDoc> people = new ImportPeople(true).processDocFile(oldDoc.getFile().getPath());
        assertThat(people.size())
                .isEqualTo(2369);
    }

}