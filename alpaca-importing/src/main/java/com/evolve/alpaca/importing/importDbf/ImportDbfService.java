package com.evolve.alpaca.importing.importDbf;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.ddd.CommandsApplier;
import com.evolve.alpaca.ddd.PersistedCommand;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.person.PersonsWrapper;
import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Account;
import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import com.evolve.services.AccountsService;
import com.evolve.services.PersonEditService;
import com.evolve.services.PersonsService;
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
    private final PersonsService personsService;
    private final PersonFixer personFixer;
    private final AccountsService accountsService;
    private final CommandsApplier commandsApplier;
    private final CommandCollector commandCollector;
    private final PersonEditService personEditService;

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

        personsService.insertPersons(persons);

        if (StringUtils.isNotBlank(accountsFilePath)) {
            importAccounts(accountsFilePath);
        }

        // post import handles re-setting person status
        postImportStep();

        processCommands();

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);

        return persons;
    }

    private void postImportStep() {
        postImportStepService.processAll();
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

                    personEditService.editPerson(editPersonDataCommand);
                }
            } catch (ClassNotFoundException e) {
                log.error("Cannot find class - ", e);
            } finally {
                commandCollector.startRecording();
            }
        });
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
