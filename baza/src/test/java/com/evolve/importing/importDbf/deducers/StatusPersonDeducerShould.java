package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.PersonStatus;
import com.evolve.domain.PersonStatusDetails;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatusPersonDeducerShould {

    final StatusPersonDeducer deducer = new StatusPersonDeducer();

    @Test
    void deduceIfPersonIsDead() {
        assertThat(deducer.deduceFrom(List.of("        ZMARŁ 20-VII-2009")))
                .hasValue(PersonStatusDetails.dead("20-VII-2009"));

        assertThat(deducer.deduceFrom(List.of("ZMARŁA 09-12-2013")))
                .hasValue(PersonStatusDetails.dead("09-12-2013"));

        assertThat(deducer.deduceFrom(List.of("ZM. 24.04.2005 r.")))
                .hasValue(PersonStatusDetails.dead("24.04.2005 r."));

        assertThat(deducer.deduceFrom(List.of("ZM 01.08.2014")))
                .hasValue(PersonStatusDetails.dead("01.08.2014"));
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

    @Test
    void deduceIfPersonIsRemoved() {
        assertThat(deducer.deduceFrom(List.of("        Skreśl VIII-2007")))
                .hasValue(PersonStatusDetails.removed("VIII-2007"));
        assertThat(deducer.deduceFrom(List.of("skre")))
                .hasValue(PersonStatusDetails.removed(""));
        assertThat(deducer.deduceFrom(List.of("SKR XII/04")))
                .hasValue(PersonStatusDetails.removed("XII/04"));
        assertThat(deducer.deduceFrom(List.of("Skreślenie 2012")))
                .hasValue(PersonStatusDetails.removed("2012"));

    }

}