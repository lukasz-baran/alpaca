package com.evolve.alpaca.export;

import com.evolve.services.PersonsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonExportServiceShould {

    @Mock
    PersonsService personsService;

    @Test
    void properlyOrderColumns() throws Exception {
        // given
        var exportService = new PersonExportService(personsService);
        final StringWriter writer = new StringWriter();

        var person = new PersonExportView("12312", "John", "Rambo", null, null, null, null, null, null, null);


        // when
        exportService.writeToFile(writer, List.of(person));

        String output = writer.getBuffer().toString();

        // then
        assertThat(output)
                .contains("\"ID\",\"Imię\",\"Nazwisko\",\"Płeć\",\"Status\",\"Data urodzenia\",\"Wiek\"")
                .contains("\"12312\",\"John\",\"Rambo\"");


    }

}