package com.evolve.gui.person.list;

import org.springframework.context.ApplicationEvent;

public class PersonListDoubleClickEvent extends ApplicationEvent {
    public PersonListDoubleClickEvent(PersonModel person) {
        super(person);
    }
}
