package com.evolve.alpaca.importing;

import com.evolve.domain.PersonStatus;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embeddable;

/**
 * Describes Person status: dead, resigned, etc
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Embeddable
public class PersonStatusDetails {

    private PersonStatus status;
    private String deathDate;
    private String resignationDate; // not needed by business code
    private String removedDate;

    /**
     * resigned without date
     */
    public static PersonStatusDetails resigned() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.RESIGNED)
                .build();
    }

    public static PersonStatusDetails resigned(String resignationDate) {
        return PersonStatusDetails.builder()
                .status(PersonStatus.RESIGNED)
                .resignationDate(resignationDate)
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

    public static PersonStatusDetails dead() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.DEAD)
                .build();
    }

    public static PersonStatusDetails removed(String removedDate) {
        return PersonStatusDetails.builder()
                .status(PersonStatus.REMOVED)
                .removedDate(removedDate)
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
