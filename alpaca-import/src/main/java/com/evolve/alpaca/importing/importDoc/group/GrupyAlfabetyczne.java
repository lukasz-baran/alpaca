package com.evolve.alpaca.importing.importDoc.group;

import com.evolve.domain.Group;
import com.evolve.alpaca.importing.importDoc.person.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 *  Grupy:
01 - A
02 - B
03 - C, Ć
04 - D
05 - E
06 - F
07 - G
08 - H
09 - I
10 - J
11 - K
12 - L, Ł
13 - M
14 - N
15 - O, Ó
16 - P
17 - R
18 - S
19 - Ś
20 - T
21 - U
22 - W
23 - Z
24 - Ż, Ź
 */
@AllArgsConstructor
@Builder
@Getter
@Slf4j
public class GrupyAlfabetyczne {

    private final Map<Group, List<Person>> grupyLudzie = new HashMap<>();

    public void addNewPerson(Group grupa, Person person) {
        log.info("add new person {}", grupa);
        final List<Person> osoby = grupyLudzie.getOrDefault(grupa, new ArrayList<>());
        osoby.add(person);
        grupyLudzie.put(grupa, osoby);
    }

    public long getSize() {
        return grupyLudzie.values().stream().mapToLong(Collection::size)
                .sum();
    }

    public void validateContinuity() {
        log.info("VALIDATE continuity - start");
        grupyLudzie.forEach(this::validateContinuity);
        log.info("VALIDATE continuity - end");
    }

    private void validateContinuity(Group grupa, List<Person> personList) {
        final Optional<Person> firstPerson = personList.stream().findFirst();
        firstPerson.ifPresent(person -> {
            String index = "000";
            final Iterator<Person> iterator = personList.iterator();

            while (iterator.hasNext()) {
                final Person personToCheck = iterator.next();
                int previous = Integer.parseInt(index);
                int next = Integer.parseInt(personToCheck.getIndex());
                if (previous + 1 != next) {
                    throw new RuntimeException("No continuity for " + personToCheck);
                }

                index = personToCheck.getIndex();
            }
        });

    }

}
