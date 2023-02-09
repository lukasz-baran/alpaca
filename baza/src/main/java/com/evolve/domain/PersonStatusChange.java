package com.evolve.domain;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class PersonStatusChange {

    private EventType eventType;
    private LocalDate when;

    // sometimes it is impossible to get exact date, so we keep here original value
    private String originalValue;

    @Getter
    @RequiredArgsConstructor
    public enum EventType {
        BORN("Narodziny"),
        JOINED("Dołączenie"),
        RESIGNED("Rezygnacja"),
        DIED("Śmierć"),
        REMOVED("Usunięcie"),
        ACCOUNT_CREATED("Data założenia");

        private final String name;

        @Override
        public String toString() {
            return this.name;
        }
    }



}
