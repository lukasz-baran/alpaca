package com.evolve.gui.person.list.search;

import com.evolve.domain.PersonStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.stream.Stream;

public record PersonStatusSearchItem(PersonStatus personStatus) {
    public static final PersonStatusSearchItem ALL = new PersonStatusSearchItem(null);

    @Override
    public String toString() {
        return Optional.ofNullable(personStatus)
                .map(PersonStatus::getName)
                .orElse("(wszystkie statusy)");
    }

    public static ObservableList<PersonStatusSearchItem> getSearchItems() {
        final ObservableList<PersonStatusSearchItem> statuses = FXCollections.observableArrayList();
        statuses.add(PersonStatusSearchItem.ALL);
        statuses.addAll(Stream.of(PersonStatus.values())
                .map(PersonStatusSearchItem::new)
                .toList());
        return statuses;
    }
}
