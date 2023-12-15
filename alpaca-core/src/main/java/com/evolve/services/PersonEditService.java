package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.validation.ValidationException;
import com.evolve.domain.Person;
import com.evolve.domain.PersonGenderDeducer;
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
     * TODO maybe it should handle adding new person to the database
     */
    public Person editPerson(EditPersonDataCommand command) {
        final Person person = personRepository.findByPersonId(command.id());

        person.setFirstName(command.firstName());
        person.setLastName(command.lastName());
        person.setSecondName(command.secondName());
        person.setEmail(command.email());
        person.setPhoneNumbers(command.phoneNumbers());
        person.setAddresses(command.addresses());
        person.setAuthorizedPersons(command.authorizedPersons());
        person.setStatusChanges(command.personStatusChanges());
        person.setUnitNumber(command.unitNumber());

        final Person.Gender gender = PersonGenderDeducer.getGender(command.firstName());
        person.setGender(gender);

        new PersonEditionValidator()
                .validate(person)
                .throwException(ValidationException::new);

        final Person result = personRepository.save(person);

        persistCommand(command);

        return result;
    }
}
