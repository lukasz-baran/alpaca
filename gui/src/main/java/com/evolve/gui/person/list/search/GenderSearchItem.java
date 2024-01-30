package com.evolve.gui.person.list.search;

import com.evolve.domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.stream.Stream;

public record GenderSearchItem(Person.Gender gender) {
    public static final GenderSearchItem ALL = new GenderSearchItem(null);

    @Override
    public String toString() {
        return Optional.ofNullable(gender)
                .map(Person.Gender::getName)
                .orElse("(wszyscy)");
    }

    public static ObservableList<GenderSearchItem> getSearchItems() {
        final ObservableList<GenderSearchItem> statuses = FXCollections.observableArrayList();
        statuses.add(GenderSearchItem.ALL);
        statuses.addAll(Stream.of(Person.Gender.values())
                .map(GenderSearchItem::new)
                .toList());
        return statuses;
    }
}
