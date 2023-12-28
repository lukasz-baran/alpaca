package com.evolve;

import com.evolve.alpaca.ddd.Command;
import com.evolve.domain.BankAccount;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;

import java.util.List;

public record EditPersonDataCommand(String id,
                                    String firstName,
                                    String lastName,
                                    String secondName,
                                    String email,
                                    List<String> phoneNumbers,
                                    List<Person.PersonAddress> addresses,
                                    List<Person.AuthorizedPerson> authorizedPersons,
                                    List<PersonStatusChange> personStatusChanges,
                                    String unitNumber,
                                    String registryNumber,
                                    String oldRegistryNumber,
                                    List<BankAccount> bankAccounts) implements Command {

}
