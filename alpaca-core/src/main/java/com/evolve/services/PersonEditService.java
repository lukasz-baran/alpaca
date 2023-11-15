package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.validation.ValidationException;
import com.evolve.domain.Person;
import com.evolve.domain.PersonGenderDeducer;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonEditService {
    private final PersonRepository personRepository;

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

        final Person.Gender gender = PersonGenderDeducer.getGender(command.firstName());
        person.setGender(gender);

        new PersonValidator()
                .validate(person)
                .throwException(ValidationException::new);

        return personRepository.save(person);
    }
}
