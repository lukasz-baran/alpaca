package com.evolve;

import com.evolve.alpaca.ddd.Command;
import com.evolve.domain.BankAccount;
import com.evolve.domain.Person;
import com.evolve.domain.PersonContactData;
import com.evolve.domain.PersonStatusChange;

import java.util.List;

/**
 *
 * @param retired {@code null} will keep the value untouched
 * @param exemptFromFees {@code null} will keep the value untouched
 */
public record EditPersonDataCommand(String id,
                                    String firstName,
                                    String lastName,
                                    String secondName,
                                    List<PersonContactData> contactData,
                                    List<Person.PersonAddress> addresses,
                                    List<Person.AuthorizedPerson> authorizedPersons,
                                    List<PersonStatusChange> personStatusChanges,
                                    String unitNumber,
                                    String registryNumber,
                                    String oldRegistryNumber,
                                    List<BankAccount> bankAccounts,
                                    Boolean retired,
                                    Boolean exemptFromFees,
                                    String pesel,
                                    String idNumber,
                                    List<String> previousNames) implements Command {

}
