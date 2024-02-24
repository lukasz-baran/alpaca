package com.evolve.alpaca.importing.importDbf;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.account.services.AccountsService;
import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.ddd.CommandsApplier;
import com.evolve.alpaca.ddd.PersistedCommand;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.importing.importDbf.account.AccountsFactory;
import com.evolve.alpaca.importing.importDbf.account.DbfAccount;
import com.evolve.alpaca.importing.importDbf.account.ImportAccountDbf;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.alpaca.importing.importDbf.person.ImportPersonDbf;
import com.evolve.alpaca.importing.importDbf.person.PersonsFactory;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.person.PersonsWrapper;
import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import com.evolve.services.PersonApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PersonFixer personFixer;
    private final AccountsService accountsService;
    private final CommandsApplier commandsApplier;
    private final CommandCollector commandCollector;
    private final PersonApplicationService personApplicationService;

    private final PostImportStepService postImportStepService;

    public List<Person> startImport(String personsFilePath, String accountsFilePath) {
        final List<DbfPerson> osobyDbf = new ImportPersonDbf()
                .performImport(personsFilePath)
                .getItems();

        final List<Person> persons = new PersonsFactory().from(osobyDbf)
                .stream()
                .map(personFixer::fixData)
                .collect(Collectors.toList());

        final PersonsWrapper wrapper = new PersonsWrapper(new ImportPeople(false).processFile());

        persons.forEach(person -> {
                final RegistryNumber kartotekaId = wrapper.findByPersonId(person.getPersonId());

                RegistryNumberFixer.fixPersonRegistryNumbers(person, kartotekaId);
        });

        personApplicationService.insertPersons(persons);

        final List<Account> importedAccounts = importAccounts(accountsFilePath);

        // post import handles re-setting person status
        postImportStep(importedAccounts);

        processCommands();

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);

        return persons;
    }

    private void postImportStep(List<Account> importedAccounts) {
        postImportStepService.processAll(importedAccounts);
    }

    void processCommands() {
        final List<PersistedCommand> commands = commandsApplier.loadCommands();
        commands.forEach(command -> {
            log.info("processing command: " + command);
            try {
                commandCollector.stopRecording();
                final Class<?> clazz = Class.forName(command.clazz());
                if (clazz == EditPersonDataCommand.class) {
                    final EditPersonDataCommand editPersonDataCommand =
                            objectMapper.convertValue(command.command(), EditPersonDataCommand.class);

                    log.info("applying command: {}", LogUtil.printJson(editPersonDataCommand));

                    personApplicationService.editPerson(editPersonDataCommand);
                }
            } catch (ClassNotFoundException e) {
                log.error("Cannot find class - ", e);
            } finally {
                commandCollector.startRecording();
            }
        });
    }

    List<Account> importAccounts(String accountsFilePath) {
        if (StringUtils.isBlank(accountsFilePath)) {
            return List.of();
        }

        final List<DbfAccount> accountsDbf = new ImportAccountDbf()
                .performImport(accountsFilePath)
                .getItems();

        final List<Account> accounts = accountsDbf.stream()
                .map(AccountsFactory::from)
                .toList();

        accountsService.insertAccounts(accounts);
        return accounts;
    }


}
