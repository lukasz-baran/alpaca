package com.evolve.importDbf.deducers;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.importDbf.DbfPerson;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PersonDataDeducer {

    private final DbfPerson person;

    public Person deduce() {
        List<String> guesses = List.of(
                person.getNAZ_ODB3().trim(),
                person.getNAZ_ODB4().trim(),
                person.getNAZ_ODB5().trim(),
                person.getNAZ_ODB6().trim(),
                person.getNAZ_ODB7().trim());

        NamePersonDeducer namePersonDeducer = new NamePersonDeducer(person);
        final Optional<NamePersonDeducer.DeducedCredentials> credentials = namePersonDeducer.deduceFrom(guesses);

        SmartAddressPersonDeducer addressDeducer = new SmartAddressPersonDeducer();
        Optional<Address> maybeAddress = addressDeducer.deduceFrom(guesses);
        if (maybeAddress.isPresent()) {
            guesses = addressDeducer.removeGuesses(guesses);
        }

        AuthorizedPersonDeducer authorizedPersonDeducer = new AuthorizedPersonDeducer();
        Optional<Person.AuthorizedPerson> maybeAuthorizedPerson = authorizedPersonDeducer.deduceFrom(guesses);
        if (maybeAuthorizedPerson.isPresent()) {
            guesses = authorizedPersonDeducer.removeGuesses(guesses);
        }


        final List<Person.PersonAddress> personAddresses =
                maybeAddress.map(address -> new Person.PersonAddress(address, Person.AddressType.HOME))
                        .map(List::of)
                        .orElse(List.of());

        final List<Person.AuthorizedPerson> authorizedPeople =
                maybeAuthorizedPerson.map(List::of).orElse(List.of());

        return Person.builder()
                .firstName(credentials.map(NamePersonDeducer.DeducedCredentials::getFirstName).orElse(null))
                .lastName(credentials.map(NamePersonDeducer.DeducedCredentials::getLastName).orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .build();
    }

}
