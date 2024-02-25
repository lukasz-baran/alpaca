package com.evolve.gui.person.event;

import com.evolve.domain.Person;
import org.springframework.context.ApplicationEvent;

import java.util.Optional;

public class PersonEditionFinishedEvent extends ApplicationEvent {
    private final Person editedPerson;

    public PersonEditionFinishedEvent(Object source, Person newPerson) {
        super(source);
        this.editedPerson = newPerson;
    }

    public Optional<Person> getEditedPerson() {
        return Optional.ofNullable(editedPerson);
    }
}
