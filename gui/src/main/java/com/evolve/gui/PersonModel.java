package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.ToString;

import java.time.LocalDate;

@SuppressWarnings("unused")
@ToString
public class PersonModel {

    private final SimpleStringProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;
    private final ObjectProperty<LocalDate> dob;
    private final SimpleStringProperty status;

    PersonModel(String id, String fName, String lName, String email, LocalDate dob, String status) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
        this.dob = new SimpleObjectProperty<>(dob);
        this.status = new SimpleStringProperty(status);
    }

    PersonModel(PersonListView person) {
        this(person.getPersonId(), person.getFirstName(), person.getLastName(), person.getEmail(),
                person.getDob(), person.getStatus().name());
    }

    public String getId() {
        return this.id.get();
    }
    public void setId(String id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getEmail() {
        return email.get();
    }
    public void setEmail(String fName) {
        email.set(fName);
    }

    public LocalDate getDob() {
        return dob.get();
    }
    public void setDob(LocalDate dob) {
        this.dob.set(dob);
    }

    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    public void update(Person updatedPerson) {
        this.firstName.set(updatedPerson.getFirstName());
        this.lastName.set(updatedPerson.getLastName());
        this.email.set(updatedPerson.getEmail());
        this.dob.set(updatedPerson.getDob());

        if (updatedPerson.getStatus() != null && updatedPerson.getStatus().getStatus() != null) {
            this.status.set(updatedPerson.getStatus().getStatus().name());
        }

    }
}
