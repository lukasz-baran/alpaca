package com.evolve.gui.person.details;

import com.evolve.domain.Person;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonDetailsChangeShould {

    final ObjectProperty<Person> personProperty = new SimpleObjectProperty<>();
    final PersonDetailsChange personDetailsChange = new PersonDetailsChange(personProperty);

    @Test
    void preparePesel() {


    }

}