package com.evolve.gui.person.details;

import com.evolve.domain.Person;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonDetailsChangeShould {

    final ObjectProperty<Person> personProperty = new SimpleObjectProperty<>();
    final PersonDetailsChange personDetailsChange = new PersonDetailsChange(personProperty);

    @Test
    void preparePesel() {
        assertThat(personDetailsChange.newPesel(null)).isNull();

        personProperty.setValue(Person.builder().build());
        assertThat(personDetailsChange.newPesel(null)).isNull();

        personProperty.setValue(Person.builder().build());
        assertThat(personDetailsChange.newPesel("")).isNull();

        personProperty.setValue(Person.builder().build());
        assertThat(personDetailsChange.newPesel("123")).isEqualTo("123");

        personProperty.setValue(Person.builder().pesel("").build());
        assertThat(personDetailsChange.newPesel("123")).isEqualTo("123");

        personProperty.setValue(Person.builder().pesel("123").build());
        assertThat(personDetailsChange.newPesel("123")).isEqualTo(null);

        personProperty.setValue(Person.builder().pesel("123").build());
        assertThat(personDetailsChange.newPesel("456")).isEqualTo("456");

        personProperty.setValue(Person.builder().pesel("123").build());
        assertThat(personDetailsChange.newPesel("")).isEqualTo("");
    }

}