package com.evolve;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;

import java.time.LocalDate;
import java.util.List;

public record EditPersonDataCommand(String id,
                                    String firstName,
                                    String lastName,
                                    String secondName,
                                    String email,
                                    List<String> phoneNumbers,
                                    LocalDate dob,
                                    List<Person.PersonAddress> addresses,
                                    List<Person.AuthorizedPerson> authorizedPersons,
                                    List<PersonStatusChange> personStatusChanges) {

}
