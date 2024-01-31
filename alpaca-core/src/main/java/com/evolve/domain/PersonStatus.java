package com.evolve.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
