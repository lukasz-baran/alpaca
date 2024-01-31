package com.evolve.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class PersonStatusDeducer {
    private final List<PersonStatusChange> statusChanges;
    private PersonStatusDetails status;
    private LocalDate dob;

    public Optional<LocalDate> getDob() {
        return Optional.ofNullable(dob);
    }

    public PersonStatusDeducer(Person person) {
        this.statusChanges = person.getStatusChanges() == null ? new ArrayList<>() : person.getStatusChanges();
    }

    public void addOrUpdateStatusChange(PersonStatusChange.EventType eventType, LocalDate when) {
        this.statusChanges
                .stream()
                .filter(personStatusChange -> personStatusChange.getEventType() == eventType)
                .findFirst()
                .ifPresentOrElse(statusChange -> {
                            statusChange.setWhen(when);
                            statusChange.setEventType(eventType);
                        },
                        () -> addNewStatusChange(eventType, when));

        this.status = PersonStatusDetails.basedOnStatusChange(this.statusChanges);
    }

    private void addNewStatusChange(PersonStatusChange.EventType eventType, LocalDate when) {
        PersonStatusChange newPersonStatusChange = PersonStatusChange.builder()
                .eventType(eventType)
                .when(when)
                .build();

        if (eventType == PersonStatusChange.EventType.BORN) {
            this.dob = when;
            this.statusChanges.add(0, newPersonStatusChange);
        } else {
            this.statusChanges.stream()
                    .filter(PersonStatusChange::isDeathDate)
                    .findFirst()
                    .ifPresentOrElse(element -> statusChanges.add(statusChanges.indexOf(element), newPersonStatusChange),
                            () -> this.statusChanges.add(newPersonStatusChange));
        }
    }



}
