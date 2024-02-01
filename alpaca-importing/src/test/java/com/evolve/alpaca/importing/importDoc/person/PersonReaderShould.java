package com.evolve.alpaca.importing.importDoc.person;

import com.evolve.alpaca.importing.PersonStatusDetails;
import com.evolve.domain.RegistryNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonReaderShould {

    @Test
    void fetchExceptions() {
        final String line = "119	03	12	017		ŁOZIŃSKA	/MARIA/	MAGDALENA";
        assertThat(PersonReader.fromLine(line))
                .hasValue(PersonFromDoc.builder()
                        .numerKartoteki(RegistryNumber.of("119"))
                        .numerJednostki("03")
                        .numerGrupy("12")
                        .index("017")
                        .lastName("ŁOZIŃSKA")
                        .firstName("MARIA")
                        .secondName("MAGDALENA")
                        .line(line)
                        .statusDetails(PersonStatusDetails.active())
                        .build());
    }


}