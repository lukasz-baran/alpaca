package com.evolve.gui.person.list.search;

import com.evolve.domain.PersonStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class PersonStatusSearchItem {
    public static final PersonStatusSearchItem ALL = new PersonStatusSearchItem(null);

    @Getter
    private final PersonStatus personStatus;

    @Override
    public String toString() {
        return Optional.ofNullable(personStatus)
                .map(PersonStatus::getName)
                .orElse("(wszystkie statusy)");
    }
}
