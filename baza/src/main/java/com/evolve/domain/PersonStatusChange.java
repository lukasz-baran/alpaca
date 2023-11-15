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

    // sometimes it is impossible to get exact date, so we keep here original value
    private String originalValue;

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
