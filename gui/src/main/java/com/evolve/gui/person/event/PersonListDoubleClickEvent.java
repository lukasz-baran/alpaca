package com.evolve.gui.person.event;

import com.evolve.gui.person.list.PersonModel;
import org.springframework.context.ApplicationEvent;

/**
 * User requested edition of the person details
 */
public class PersonListDoubleClickEvent extends ApplicationEvent {
    public PersonListDoubleClickEvent(PersonModel person) {
        super(person);
    }
}
