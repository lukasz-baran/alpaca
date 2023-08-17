package com.evolve.gui.person.list;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Getter
public class PersonListModel {
    private final ObservableList<PersonModel> data = FXCollections.observableArrayList();
    private final ObjectProperty<PersonModel> currentPersonProperty = new SimpleObjectProperty<>(null);


    public FilteredList<PersonModel>  feed(List<PersonListView> persons) {
        data.clear();
        persons.stream()
                .map(PersonModel::new)
                .forEach(data::add);
        return getFilteredList();
    }

    public FilteredList<PersonModel> getFilteredList() {
        return new FilteredList<>(data, p -> true);
    }

    public void updatePerson(Person updatedPerson) {
        final PersonModel personModel = data.stream()
                .filter(p -> p.getId().equals(updatedPerson.getPersonId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Person not found"));
        personModel.update(updatedPerson);

        currentPersonProperty.setValue(personModel);
    }

    public PersonModel insertPerson(Person newPerson) {
        final PersonModel addedPerson = new PersonModel(PersonListView.of(newPerson));

        data.add(addedPerson);
        data.sort(Comparator.comparing(PersonModel::getId));

        currentPersonProperty.setValue(addedPerson);
        return addedPerson;
    }
}
