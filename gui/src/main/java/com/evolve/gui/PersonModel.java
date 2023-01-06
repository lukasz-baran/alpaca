package com.evolve.gui;

import com.evolve.domain.Person;
import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("unused")
public class PersonModel {

    private final SimpleStringProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty email;

    PersonModel(String id, String fName, String lName, String email) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
    }

    PersonModel(Person person) {
        this(person.getPersonId(), person.getFirstName(), person.getLastName(), person.getEmail());
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


}
