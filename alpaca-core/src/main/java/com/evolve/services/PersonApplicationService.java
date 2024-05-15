package com.evolve.services;

import com.evolve.ArchivePersonCommand;
import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.auditlog.repo.AuditLogRepository;
import com.evolve.alpaca.ddd.ApplicationService;
import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.validation.ValidationException;
import com.evolve.domain.*;
import com.evolve.repo.jpa.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PersonApplicationService extends ApplicationService<Person> {
    private final PersonRepository personRepository;

    public PersonApplicationService(PersonRepository personRepository, CommandCollector commandCollector,
                                     AuditLogRepository auditLogRepository) {
        super(commandCollector, auditLogRepository);
        this.personRepository = personRepository;
    }

    /**
     * TODO maybe it should also handle adding new person to the database
     */
    public Person editPerson(EditPersonDataCommand command, LocalDateTime when) {
        new PersonEditionValidator()
                .validate(command)
                .throwException(ValidationException::new);

        final Person person = personRepository.findByPersonId(command.id());

        final Person originalPerson = SerializationUtils.clone(person);

        person.setFirstName(command.firstName());
        person.setLastName(command.lastName());
        person.setSecondName(command.secondName());
        person.setContactData(command.contactData());
        person.setAddresses(command.addresses());
        person.setAuthorizedPersons(command.authorizedPersons());
        person.updateStatusChanges(command.personStatusChanges());
        person.setUnitNumber(command.unitNumber());
        person.setRegistryNumber(RegistryNumber.of(command.registryNumber()));
        person.setOldRegistryNumber(RegistryNumber.of(command.oldRegistryNumber()));
        person.setBankAccounts(command.bankAccounts());
        person.updateRetirement(command.retired());
        person.updateExemptionFromFees(command.exemptFromFees());
        person.updatePesel(command.pesel());
        person.updateIdNumber(command.idNumber());
        person.setPreviousLastNames(command.previousNames());

        final Person.Gender gender = PersonGenderDeducer.getGender(command.firstName());
        person.setGender(gender);

        final Person result = personRepository.save(person);

        persistCommand(originalPerson, result, command, when);

        return result;
    }


    public Person archivePerson(ArchivePersonCommand command) {
        final Person person = personRepository.findByPersonId(command.personId());
        final Person originalPerson = ObjectUtils.clone(person);

        person.getStatusChanges().add(PersonStatusChange.archived(LocalDate.now()));
        person.setStatus(PersonStatus.ARCHIVED);
        final Person result = personRepository.save(person);

        persistCommand(originalPerson, result, command, LocalDateTime.now());
        return result;
    }


    public boolean insertPerson(Person person) {
        log.info("Adding person {}", person);
        final Person insertedPerson = personRepository.save(person);
        return true;
    }

    public void insertPersons(List<Person> personList) {
        validatePerson(personList);

        personRepository.deleteAll();
        personRepository.saveAll(personList);
    }

    void validatePerson(List<Person> personList) {
        Map<String, Long> counts =
                personList.stream().map(Person::getPersonId).collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> log.warn("duplicated id {} - {}", entry.getKey(), entry.getValue()));

        if (counts.entrySet().stream().anyMatch(entry -> entry.getValue() > 1)) {
            throw new RuntimeException("duplicated ids!");
        }
    }
}
