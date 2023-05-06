package com.evolve.importing.importDoc.person;

import com.evolve.domain.PersonStatusDetails;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonReaderShould {

    @Test
    void fetchExceptions() {
        final String line = "119	03	12	017		ŁOZIŃSKA	/MARIA/	MAGDALENA";
        assertThat(PersonReader.fromLine(line))
                .hasValue(Person.builder()
                        .numerKartoteki(KartotekaId.of("119"))
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