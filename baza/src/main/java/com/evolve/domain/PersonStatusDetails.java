package com.evolve.domain;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * Describes Person status: dead, resigned, etc
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PersonStatusDetails {
    public static final String NO_DATA = null;

    private PersonStatus status;
    private String deathDate;
    private String resignationDate;
    private String comment;

    /**
     * resigned without date
     */
    public static PersonStatusDetails resigned() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.RESIGNED)
                .build();
    }

    /**
     * still active
     */
    public static PersonStatusDetails active() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.ACTIVE)
                .build();
    }

    public static PersonStatusDetails dead(String deathDate) {
        return PersonStatusDetails.builder()
                .status(PersonStatus.DEAD)
                .deathDate(deathDate)
                .build();
    }

    public static PersonStatusDetails resigned(String resignationDate) {
        return PersonStatusDetails.builder()
                .status(PersonStatus.RESIGNED)
                .resignationDate(resignationDate)
                .build();
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
