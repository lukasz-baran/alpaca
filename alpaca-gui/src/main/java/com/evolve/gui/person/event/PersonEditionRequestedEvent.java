package com.evolve.gui.person.event;

import com.evolve.gui.person.list.PersonModel;
import org.springframework.context.ApplicationEvent;

/**
 * User requested edition of the person details
 */
public class PersonEditionRequestedEvent extends ApplicationEvent {
    public PersonEditionRequestedEvent(PersonModel person) {
        super(person);
    }
}
