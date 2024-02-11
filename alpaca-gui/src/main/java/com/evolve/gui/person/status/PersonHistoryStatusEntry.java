package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PersonHistoryStatusEntry {
    @Setter
    private PersonStatusChange personStatusChange;

    public PersonStatusChange.EventType getEventType() {
        return personStatusChange.getEventType();
    }

    public LocalDate getWhen() {
        return personStatusChange.getWhen();
    }

    public String getOriginalValue() {
        return personStatusChange.getOriginalValue();
    }
}
