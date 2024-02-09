package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.validation.ValidationException;
import com.evolve.domain.*;
import com.evolve.repo.jpa.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PersonEditService extends ApplicationService {
    private final PersonRepository personRepository;

    public PersonEditService(PersonRepository personRepository, CommandCollector commandCollector) {
        super(commandCollector);
        this.personRepository = personRepository;
    }

    /**
     * TODO maybe it should also handle adding new person to the database
     */
    public Person editPerson(EditPersonDataCommand command) {
        new PersonEditionValidator()
                .validate(command)
                .throwException(ValidationException::new);

        final Person person = personRepository.findByPersonId(command.id());

        person.setFirstName(command.firstName());
        person.setLastName(command.lastName());
        person.setSecondName(command.secondName());
        person.setContactData(command.contactData());
        person.setAddresses(command.addresses());
        person.setAuthorizedPersons(command.authorizedPersons());
        person.setStatusChanges(command.personStatusChanges());
        person.setStatus(PersonStatus.basedOnStatusChange(command.personStatusChanges()));
        person.setUnitNumber(command.unitNumber());
        person.setRegistryNumber(RegistryNumber.of(command.registryNumber()));
        person.setOldRegistryNumber(RegistryNumber.of(command.oldRegistryNumber()));
        person.setBankAccounts(command.bankAccounts());
        person.updateRetirement(command.retired());
        person.updateExemptionFromFees(command.exemptFromFees());

        final Person.Gender gender = PersonGenderDeducer.getGender(command.firstName());
        person.setGender(gender);

        final Person result = personRepository.save(person);

        persistCommand(command);

        return result;
    }
}
