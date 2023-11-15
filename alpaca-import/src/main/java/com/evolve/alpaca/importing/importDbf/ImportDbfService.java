package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.person.PersonsWrapper;
import com.evolve.domain.Account;
import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import com.evolve.services.AccountsService;
import com.evolve.services.PersonsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                final String personId = person.getPersonId();

                final RegistryNumber kartotekaId = wrapper.findByPersonId(personId);
                if (kartotekaId.getNumber().isPresent()) {
                    final String registryNumber = Optional.ofNullable(person.getRegistryNumber())
                            .map(RegistryNumber::getRegistryNum)
                            .map(Objects::toString)
                            .orElse("");

                    final String oldRegistryNumber = Optional.ofNullable(person.getOldRegistryNumber())
                            .map(RegistryNumber::getRegistryNum)
                            .map(Objects::toString)
                            .orElse("");

                    if (StringUtils.equals(kartotekaId.getRegistryNum().toString(), registryNumber)) {
                        System.out.println("personId " + personId + " has correct registry number");
                    } else {
                        System.out.println("personId " + personId +
                                " has INVALID registry number: " + kartotekaId.getRegistryNum().toString() + " " +
                                registryNumber + " old: " + oldRegistryNumber);

                        if (StringUtils.equals(kartotekaId.getRegistryNum().toString(), oldRegistryNumber)) {
                            person.setRegistryNumber(RegistryNumber.fromText(oldRegistryNumber));
                            person.setOldRegistryNumber(RegistryNumber.fromText(registryNumber));
                        }

                    }
                }

        });

        personsService.insertPersons(persons);

        if (StringUtils.isNotBlank(accountsFilePath)) {
            importAccounts(accountsFilePath);
        }

        final DbfImportCompletedEvent customSpringEvent = new DbfImportCompletedEvent(this,
                "import completed for " + osobyDbf.size() + " entries");
        applicationEventPublisher.publishEvent(customSpringEvent);

        return persons;
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
