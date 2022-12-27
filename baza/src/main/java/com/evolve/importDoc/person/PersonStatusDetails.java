package com.evolve.importDoc.person;

import com.evolve.domain.PersonStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Describes Person status: dead, resigned, etc
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PersonStatusDetails {
    public static final String NO_DATA = null;

    private final PersonStatus status;
    private final String deathDate;
    private final String resignationDate;
    private final String comment;

    /**
     * resigned without date
     */
    public static PersonStatusDetails resigned() {
        return new PersonStatusDetails(PersonStatus.RESIGNED, NO_DATA, NO_DATA, NO_DATA);
    }

    /**
     * still active
     */
    public static PersonStatusDetails active() {
        return new PersonStatusDetails(PersonStatus.ACTIVE, NO_DATA, NO_DATA, NO_DATA);
    }

    public static PersonStatusDetails dead(String deathDate) {
        return new PersonStatusDetails(PersonStatus.DEAD, deathDate, NO_DATA, NO_DATA);
    }

    public static PersonStatusDetails resigned(String resignationDate) {
        return new PersonStatusDetails(PersonStatus.RESIGNED, NO_DATA, resignationDate, NO_DATA);
    }

    @Override
    public String toString() {
        if (status == PersonStatus.RESIGNED && StringUtils.isNotBlank(resignationDate)) {
            return status + ": " + resignationDate;
        }
        if (status == PersonStatus.DEAD && StringUtils.isNotBlank(deathDate)) {
            return status + ": " + deathDate;
        }
        return status.toString();
    }
}
