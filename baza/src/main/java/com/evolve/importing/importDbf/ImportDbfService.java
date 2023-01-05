package com.evolve.importing.importDbf;

import com.evolve.importing.event.DbfImportCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportDbfService {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void startImport(String filePath) {
        final List<DbfPerson> osobyDbf = new ImportDbfFile()
                .performImport(filePath)
                .getOsoby();

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

}
