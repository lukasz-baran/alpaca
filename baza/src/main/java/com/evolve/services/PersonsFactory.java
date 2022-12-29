package com.evolve.services;

import com.evolve.domain.Person;
import com.evolve.importDbf.DbfPerson;
import com.evolve.importDbf.deducers.PersonDataDeducer;

import java.util.List;
import java.util.stream.Collectors;

public class PersonsFactory {

    public List<Person> from(List<DbfPerson> dbfPeople) {
        return dbfPeople.stream()
                .map(this::from)
                .collect(Collectors.toList());
    }

    Person from(DbfPerson dbfPerson) {
        return new PersonDataDeducer(dbfPerson).deduce();
    }

}
