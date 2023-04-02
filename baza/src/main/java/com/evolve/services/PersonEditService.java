package com.evolve.services;

import com.evolve.EditPersonDataCommand;
import com.evolve.domain.Person;
import com.evolve.importing.importDbf.deducers.PersonGenderDeducer;
import com.evolve.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonEditService {
    private final Nitrite nitrite;

    /*
        * TODO maybe it should handle adding new person to the database
     */
    public Person editPerson(EditPersonDataCommand command) {
        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);

        final Person person = personRepo.getById(command.id());
        person.setFirstName(command.firstName());
        person.setLastName(command.lastName());
        person.setSecondName(command.secondName());
        person.updatePersonDob(command.dob());
        person.setEmail(command.email());
        person.setAddresses(command.addresses());
        person.setAuthorizedPersons(command.authorizedPersons());

        final Person.Gender gender = PersonGenderDeducer.getGender(command.firstName());
        person.setGender(gender);

        new PersonValidator()
                .validate(person)
                .throwException(ValidationException::new);

        final WriteResult result = personRepo.update(person);
        if (result.getAffectedCount() != 1) {
            throw new RuntimeException("Person not updated");
        }
        return person;
    }
}
