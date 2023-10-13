package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.domain.Person;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.services.PersonsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for importing different types of DBF files.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImportDbfService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PersonsService personsService;
    private final PersonFixer personFixer;

    public void startImport(String filePath) {
        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(filePath)
                .getItems();

        final List<Person> persons = new PersonsFactory().from(osobyDbf)
                .stream()
                .map(personFixer::fixData)
                .collect(Collectors.toList());

        personsService.insertPersons(persons);

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    public void importAccounts(String filePath) {
        final List<DbfAccount> osobyDbf = new ImportAccountDbf()
                .performImport(filePath)
                .getItems();

//        final List<Person> persons = new PersonsFactory().from(osobyDbf)
//                .stream()
//                .map(personFixer::fixData)
//                .collect(Collectors.toList());
//
//        personsService.insertPersons(persons);
//
//        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
//                "import completed for " + osobyDbf.size() + " entries");
//        applicationEventPublisher.publishEvent(customSpringEvent);
    }


}
