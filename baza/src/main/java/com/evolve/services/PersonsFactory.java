package com.evolve.services;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.importDbf.DbfPerson;
import com.evolve.importDbf.SmartAddressPersonDeducer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PersonsFactory {

    public List<Person> from(List<DbfPerson> dbfPeople) {
        return dbfPeople.stream()
                .map(this::from)
                .collect(Collectors.toList());
    }

    Person from(DbfPerson dbfPerson) {
        final Optional<Address> address = SmartAddressPersonDeducer.deduceAddress(dbfPerson);

        final List<Person.PersonAddress> personAddresses =
            address.map(address1 -> new Person.PersonAddress(address1, Person.AddressType.HOME))
                .map(List::of)
                    .orElse(List.of());
        return Person.builder()
                .addresses(personAddresses)
                .build();
    }

}
