package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.domain.Account;
import com.evolve.domain.Person;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.services.AccountsService;
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
    private final AccountsService accountsService;

    public void startImport(String personsFilePath, String accountsFilePath) {
        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(personsFilePath)
                .getItems();

        final List<Person> persons = new PersonsFactory().from(osobyDbf)
                .stream()
                .map(personFixer::fixData)
                .collect(Collectors.toList());

        personsService.insertPersons(persons);

        importAccounts(accountsFilePath);

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    void importAccounts(String accountsFilePath) {
        final List<DbfAccount> accountsDbf = new ImportAccountDbf()
                .performImport(accountsFilePath)
                .getItems();

        final List<Account> accounts = accountsDbf.stream()
                .map(AccountsFactory::from)
                .toList();

        accountsService.insertAccounts(accounts);
    }


}
