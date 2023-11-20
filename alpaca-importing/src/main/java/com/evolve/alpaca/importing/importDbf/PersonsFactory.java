package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.deducers.IssuesLogger;
import com.evolve.alpaca.importing.importDbf.deducers.PersonDataDeducer;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.evolve.domain.Person;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class PersonsFactory {

    @Getter
    private final IssuesLogger issuesLogger = new IssuesLogger();
    private final RegistryNumbers registryNumbers = new RegistryNumbers();

    public List<Person> from(List<DbfPerson> dbfPeople) {

        final List<Person> result = dbfPeople.stream()
                .map(this::from)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        log.warn("issues {}", issuesLogger);
        return result;
    }

    Optional<Person> from(DbfPerson dbfPerson) {
        return new PersonDataDeducer(dbfPerson, issuesLogger, registryNumbers).deduce();
    }

}
