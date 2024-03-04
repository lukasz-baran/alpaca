package com.evolve.services;

import com.evolve.FindPerson;
import com.evolve.FindProblems;
import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.RegistryNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
class ProblemsQueryService implements FindProblems {
    private final FindPerson findPerson;

    @Override
    public List<String> findRegistryNumbersIssues() {
        final List<Person> personList = findPerson.fetch(PersonLookupCriteria.ALL);

        final List<String> results = checkDuplicatedRegistryNumbers(personList);
        results.addAll(checkMissingRegistryNumbers(personList));
        return results;
    }

    private List<String> checkDuplicatedRegistryNumbers(List<Person> personList) {
        final List<String> problems = new ArrayList<>();

        final int minimum = 1;
        final int maximum = findBiggestRegistryNumber(personList);

        IntStream.range(minimum, maximum)
                .boxed()
                .forEach(index -> {
                    final List<Person> duplicatedRegistryNumbers = personList.stream()
                            .filter(person -> person.hasRegistryNumber(index))
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

    private List<String> checkMissingRegistryNumbers(List<Person> personList) {
        final List<String> problems = new ArrayList<>();

        final int minimum = 1;
        final int maximum = findBiggestRegistryNumber(personList);

        IntStream.range(minimum, maximum)
                .boxed()
                .forEach(index -> {
                    final Optional<Person> missingRegistryNumber = personList.stream()
                            .filter(person -> person.hasRegistryNumber(index))
                            .findFirst();

                    if (missingRegistryNumber.isEmpty()) {
                        final StringBuilder message = new StringBuilder("Brakuje osoby z numerem kartoteki: " + index);

                        var candidates = findByOldRegistryNumber(personList, index);
                        if (!candidates.isEmpty()) {
                            message.append(" - może jest przypisany do: ")
                                .append(candidates.stream()
                                    .map(person -> person.getFirstName() + " " + person.getLastName())
                                    .collect(Collectors.joining(", ")));
                        }

                        problems.add(message.toString());
                    }
                });
        return problems;
    }

    private List<Person> findByOldRegistryNumber(List<Person> personList, int index) {
        return personList.stream()
                .filter(person -> person.hasOldRegistryNumber(index))
                .collect(Collectors.toList());
    }

    private int findBiggestRegistryNumber(List<Person> personList) {
        return personList.stream()
                .filter(person -> Objects.nonNull(person.getRegistryNumber()))
                .max(Comparator.comparing(person -> person.getRegistryNumber().getNumber().orElse(0)))
                .map(Person::getRegistryNumber)
                .map(RegistryNumber::getRegistryNum)
                .orElse(0);
    }
}
