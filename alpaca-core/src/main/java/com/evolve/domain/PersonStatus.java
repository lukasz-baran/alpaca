package com.evolve.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PersonStatus {

    ACTIVE("Aktywny"),
    DEAD("Nie żyje"),
    RESIGNED("Rezygnacja"),
    REMOVED("Skreślenie"),
    UNKNOWN("Nieznany"),
    ARCHIVED("Usunięcie");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

    public static PersonStatus basedOnStatusChange(List<PersonStatusChange> statusChanges) {
        if (!statusChanges.isEmpty()) {
            final int lastIndex = statusChanges.size() - 1;

            return basedOnStatusChange(statusChanges.get(lastIndex));
        }
        return UNKNOWN;
    }

    private static PersonStatus basedOnStatusChange(PersonStatusChange change) {
        return switch (change.getEventType()) {
            case DIED ->  PersonStatus.DEAD;
            case RESIGNED -> PersonStatus.RESIGNED;
            case REMOVED -> PersonStatus.REMOVED;
            case ARCHIVED -> PersonStatus.ARCHIVED;
            default -> PersonStatus.ACTIVE;
        };
    }

}
