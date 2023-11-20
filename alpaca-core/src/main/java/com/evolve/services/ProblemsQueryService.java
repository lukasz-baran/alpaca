package com.evolve.services;

import com.evolve.FindProblems;
import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.RegistryNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProblemsQueryService implements FindProblems {
    private final PersonsService personsService;

    @Override
    public List<String> findProblems() {
        final List<Person> personList = personsService.fetch(PersonLookupCriteria.ALL);


        return checkRegistryNumbers(personList);
    }

    List<String> checkRegistryNumbers(List<Person> personList) {
        final List<String> problems = new ArrayList<>();

        final int minimum = 1;
        final int maximum = personList.stream()
                .filter(person -> Objects.nonNull(person.getRegistryNumber()))
                .max(Comparator.comparing(person -> person.getRegistryNumber().getNumber().orElse(0)))
                .map(Person::getRegistryNumber)
                .map(RegistryNumber::getRegistryNum)
                .orElse(0);

        IntStream.range(minimum, maximum)
                .boxed()
                .forEach(index -> {
                    final List<Person> duplicatedRegistryNumbers = personList.stream()
                            .filter(person -> Objects.nonNull(person.getRegistryNumber()))
                            .filter(person -> index.equals(person.getRegistryNumber().getRegistryNum()))
                            .toList();
                    if (duplicatedRegistryNumbers.size() > 1) {
                        final List<String> personIds = duplicatedRegistryNumbers.stream().map(Person::getPersonId)
                                    .distinct().toList();

                        problems.add("Poniższe osoby posiadają ten sam numer kartoteki: " +
                            personIds + " numer: " + index);
                    }
                });

        return problems;
    }
}
