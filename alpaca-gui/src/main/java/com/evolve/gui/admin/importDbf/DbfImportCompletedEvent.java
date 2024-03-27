package com.evolve.gui.admin.importDbf;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DbfImportCompletedEvent extends ApplicationEvent {
    private final Integer importedCount;

    public DbfImportCompletedEvent(Object source, Integer importedCount) {
        super(source);
        this.importedCount = importedCount;
    }

}
