package com.evolve.gui.person.event;

import com.evolve.domain.Person;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PersonArchivedEvent extends ApplicationEvent {
    private final Person archivedPerson;

    public PersonArchivedEvent(Person person) {
        super(person);
        this.archivedPerson = person;
    }
}
