package com.evolve.importDoc.group;

import com.evolve.domain.PersonStatusDetails;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonGroupReaderShould {

    public static final String RECORD = "   01  01  001     ADAMEK          MIROSŁAW    ZM 15.08.98";
    public static final String RECORD_REZ = "\t03\t18\t077\t\tSOBUŚ\t\t\tWIESŁAW REZ III-98";

    @Test
    void recognizeDateOfDeath() {
        assertThat(PersonGroupReader.decodeStatus(RECORD))
                .isEqualTo(PersonStatusDetails.dead("15.08.98"));
    }

    @Test
    void recognizeDateOfResignation() {
        assertThat(PersonGroupReader.decodeStatus(RECORD_REZ))
                .isEqualTo(PersonStatusDetails.resigned("III-99"));
    }


}