package com.evolve.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Embeddable
public class PersonStatusChange {

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

    public static PersonStatusChange resigned(String originalValue) {
        return new PersonStatusChange(EventType.RESIGNED, null, originalValue);
    }

    public static PersonStatusChange died(LocalDate when, String originalValue) {
        return new PersonStatusChange(EventType.DIED, when, originalValue);
    }

    @Getter
    @RequiredArgsConstructor
    public enum EventType {
        BORN("Data urodzenia"),
        JOINED("Dołączenie"),
        RESIGNED("Rezygnacja"),
        DIED("Zmarł(a)"),
        REMOVED("Skreślenie"),
        ACCOUNT_CREATED("Data założenia");

        private final String name;

        @Override
        public String toString() {
            return this.name;
        }
    }



}
