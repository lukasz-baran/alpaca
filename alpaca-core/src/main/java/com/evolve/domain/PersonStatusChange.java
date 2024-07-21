package com.evolve.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Embeddable
public class PersonStatusChange implements Serializable, Comparable<PersonStatusChange> {

    private EventType eventType;
    @Column(name = "whenAdded")
    private LocalDate when;

    // sometimes it is impossible to get exact date, so we keep here the original value
    private String originalValue;

    public PersonStatusChange(EventType eventType, LocalDate when) {
        this(eventType, when, null);
    }

    public static PersonStatusChange born(LocalDate when) {
        return new PersonStatusChange(EventType.BORN, when);
    }

    public static PersonStatusChange joined(LocalDate when) {
        return new PersonStatusChange(EventType.JOINED, when);
    }

    public static PersonStatusChange joined(LocalDate when, String originalValue) {
        return new PersonStatusChange(EventType.JOINED, when, originalValue);
    }

    // resignations:

    public static PersonStatusChange resigned(String originalValue) {
        return new PersonStatusChange(EventType.RESIGNED, null, originalValue);
    }

    public static PersonStatusChange resigned(LocalDate when, String originalValue) {
        return new PersonStatusChange(EventType.RESIGNED, when, originalValue);
    }

    public static PersonStatusChange resigned(LocalDate when) {
        return new PersonStatusChange(EventType.RESIGNED, when);
    }

    // deaths:

    public static PersonStatusChange died(LocalDate when, String originalValue) {
        return new PersonStatusChange(EventType.DIED, when, originalValue);
    }

    public static PersonStatusChange died(LocalDate when) {
        return new PersonStatusChange(EventType.DIED, when);
    }

    // removals:

    public static PersonStatusChange removed(LocalDate when, String originalValue) {
        return new PersonStatusChange(EventType.REMOVED, when, originalValue);
    }

    public static PersonStatusChange removed(String originalValue) {
        return new PersonStatusChange(EventType.REMOVED, null, originalValue);
    }

    // archiving:
    public static PersonStatusChange archived(LocalDate when) {
        return new PersonStatusChange(EventType.ARCHIVED, when);
    }

    @JsonIgnore
    public boolean isDeathDate() {
        return eventType == EventType.DIED;
    }

    @Override
    public int compareTo(PersonStatusChange o) {
        if (eventType == EventType.BORN) {
            return -1;
        }

        if (eventType == EventType.DIED) {
            return 1;
        }

        if (this.when != null && o.when != null) {
            return this.when.compareTo(o.when);
        }
        return this.eventType.compareTo(o.eventType);
    }

    @Getter
    @RequiredArgsConstructor
    public enum EventType {
        BORN("Data urodzenia"),
        JOINED("Dołączenie"),
        RESIGNED("Rezygnacja"),
        DIED("Zmarł(a)"),
        REMOVED("Skreślenie"),
        ARCHIVED("Usunięcie");

        private final String name;

        @Override
        public String toString() {
            return this.name;
        }
    }



}
