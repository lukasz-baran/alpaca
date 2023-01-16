package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.PersonStatus;
import com.evolve.domain.PersonStatusDetails;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatusPersonDeducerShould {

    StatusPersonDeducer deducer = new StatusPersonDeducer();

    @Test
    void deduceIfPersonIsDead() {
        PersonStatusDetails result = deducer.deduceFrom(List.of("        ZMARŁ 20-VII-2009"))
                .orElseThrow(AssertionError::new);

        assertThat(result.getStatus())
                .isEqualTo(PersonStatus.DEAD);
        assertThat(result.getDeathDate())
                .isEqualTo("20-VII-2009");

        result = deducer.deduceFrom(List.of("ZM. 24.04.2005 r."))
                .orElseThrow(AssertionError::new);
        assertThat(result.getStatus())
                .isEqualTo(PersonStatus.DEAD);
        assertThat(result.getDeathDate())
                .isEqualTo("24.04.2005 r.");
    }

    @Test
    void deduceIfPersonResigned() {
        PersonStatusDetails result = deducer.deduceFrom(List.of("rez. 04.03."))
                .orElseThrow(AssertionError::new);

        assertThat(result.getStatus())
                .isEqualTo(PersonStatus.RESIGNED);
        assertThat(result.getResignationDate())
                .isEqualTo("04.03.");

        result = deducer.deduceFrom(List.of("REZ VIII/07"))
                .orElseThrow(AssertionError::new);
        assertThat(result.getStatus())
                .isEqualTo(PersonStatus.RESIGNED);
        assertThat(result.getResignationDate())
                .isEqualTo("VIII/07");
    }


}