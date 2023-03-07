package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class PersonListModel {
    private final ObservableList<PersonModel> data = FXCollections.observableArrayList();
    private final ObjectProperty<PersonModel> currentPersonProperty = new SimpleObjectProperty<>(null);


    public void feed(List<PersonListView> persons) {
        data.clear();
        persons.stream()
                .map(PersonModel::new)
                .forEach(data::add);
    }

    public FilteredList<PersonModel> getFilteredList() {
        return new FilteredList<>(data, p -> true);
    }

    public void updatePerson(Person updatedPerson) {
        PersonModel personModel = data.stream()
                .filter(p -> p.getId().equals(updatedPerson.getPersonId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Person not found"));
        personModel.update(updatedPerson);

        currentPersonProperty.setValue(personModel);
    }
}
