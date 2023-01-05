package com.evolve.importing.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DbfImportCompletedEvent extends ApplicationEvent {
    private final String message;

    public DbfImportCompletedEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

}
