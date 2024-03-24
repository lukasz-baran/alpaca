package com.evolve.alpaca.importing.importDbf.person;

import com.evolve.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class ImportPersonDbfShould {

    @Test
    void importPeople() throws IOException {
        // given
        final Resource personsFile = new DefaultResourceLoader().getResource("Z_B_KO.DBF");
        assumeThat(personsFile.exists())
                .isTrue();

        // when
        final List<Person> persons = importPeople(personsFile.getURL());

        // then
        assertThat(persons)
                .isNotEmpty();
    }

    static List<Person> importPeople(URL url) {
        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(url.getPath())
                .getItems();
        return new PersonsFactory().from(osobyDbf);
    }

}