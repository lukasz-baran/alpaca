package com.evolve.alpaca.importing.importDbf.deducers.status;

import com.evolve.alpaca.importing.PersonStatusDetails;
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

        assertThat(deducer.deduceFrom(List.of("zm. 19.07.2023")))
                .hasValue(PersonStatusDetails.dead("19.07.2023"));
    }

    @Test
    void deduceIfPersonResigned() {
        assertThat(deducer.deduceFrom(List.of("rez. 04.03.")).orElseThrow())
                .isEqualTo(PersonStatusDetails.resigned("04.03."));

        assertThat(deducer.deduceFrom(List.of("REZ VIII/07")).orElseThrow())
                .isEqualTo(PersonStatusDetails.resigned("VIII/07"));

        assertThat(deducer.deduceFrom(List.of("rez zw skł 12.04.2005")).orElseThrow())
                .isEqualTo(PersonStatusDetails.resigned("12.04.2005"));

        assertThat(deducer.deduceFrom(List.of("rez zwr skł 12.04.2005")).orElseThrow())
                .isEqualTo(PersonStatusDetails.resigned("12.04.2005"));

        assertThat(deducer.deduceFrom(List.of("REZYGNACJA 4.07.2006 r.")))
                .hasValue(PersonStatusDetails.resigned("4.07.2006 r."));

        assertThat(deducer.deduceFrom(List.of("R 11.06.02")))
                .hasValue(PersonStatusDetails.resigned("11.06.02"));
    }

    @Test
    void ignoreExceptionsForResignedStatuses() {
        assertThat(deducer.deduceFrom(List.of("rezyg z podwyższonej skła")))
                .isEmpty();
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
        assertThat(deducer.deduceFrom(List.of("skreśl XII 2009")))
                .hasValue(PersonStatusDetails.removed("XII 2009"));

        assertThat(deducer.deduceFrom(List.of("sk.02-10-2017")))
                .hasValue(PersonStatusDetails.removed("02-10-2017"));

        assertThat(deducer.deduceFrom(List.of("sk-02-10-2017")))
                .hasValue(PersonStatusDetails.removed("02-10-2017"));

        assertThat(deducer.deduceFrom(List.of("SKRŚL VIII-2010")))
                .hasValue(PersonStatusDetails.removed("VIII-2010"));

        assertThat(deducer.deduceFrom(List.of("skr. zw skł 12.04.2005")))
                .hasValue(PersonStatusDetails.removed("12.04.2005"));

        // note r. suffix will be removed later:
        assertThat(deducer.deduceFrom(List.of("skr zw skł 12.04.2005r.")))
                .hasValue(PersonStatusDetails.removed("12.04.2005r."));

        assertThat(deducer.deduceFrom(List.of("sker zw skł 12.04.2005r.")))
                .hasValue(PersonStatusDetails.removed("12.04.2005r."));

        assertThat(deducer.deduceFrom(List.of("skr. zwr skł 12,04.2005")))
                .hasValue(PersonStatusDetails.removed("12,04.2005"));

        assertThat(deducer.deduceFrom(List.of("skreślony XII 2002")))
                .hasValue(PersonStatusDetails.removed("XII 2002"));
    }

}