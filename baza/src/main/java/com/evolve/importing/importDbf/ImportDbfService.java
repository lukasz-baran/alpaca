package com.evolve.importing.importDbf;

import com.evolve.domain.Person;
import com.evolve.importing.event.DbfImportCompletedEvent;
import com.evolve.services.PersonsFactory;
import com.evolve.services.PersonsService;
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
    private final PersonsService personsService;

    public void startImport(String filePath) {
        final List<DbfPerson> osobyDbf = new ImportDbfFile()
                .performImport(filePath)
                .getOsoby();

        personsService.insertPersons(new PersonsFactory()
                .from(osobyDbf));

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

}
