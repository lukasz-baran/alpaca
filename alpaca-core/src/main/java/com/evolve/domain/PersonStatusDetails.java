package com.evolve.domain;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embeddable;
import java.util.List;

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
    public static final String NO_DATA = null;

    private PersonStatus status;
    private String deathDate;
    private String resignationDate;
    private String removedDate;
    private String comment;

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

    public static PersonStatusDetails unknown() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.UNKNOWN)
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

    /**
     * User was removed from the members list
     */
    public static PersonStatusDetails removed() {
        return PersonStatusDetails.builder()
                .status(PersonStatus.REMOVED)
                .build();
    }

    public static PersonStatusDetails removed(String removedDate) {
        return PersonStatusDetails.builder()
                .status(PersonStatus.REMOVED)
                .removedDate(removedDate)
                .build();
    }

    public static PersonStatusDetails basedOnStatusChange(List<PersonStatusChange> statusChanges) {
        if (!statusChanges.isEmpty()) {
            final int lastIndex = statusChanges.size() - 1;

            return basedOnStatusChange(statusChanges.get(lastIndex));
        }
        return unknown();
    }

    private static PersonStatusDetails basedOnStatusChange(PersonStatusChange change) {
        return switch (change.getEventType()) {
            case DIED ->  PersonStatusDetails.dead(change.getOriginalValue());
            case RESIGNED -> PersonStatusDetails.resigned(change.getOriginalValue());
            case REMOVED -> PersonStatusDetails.removed(change.getOriginalValue());
            default -> PersonStatusDetails.active();
        };
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
