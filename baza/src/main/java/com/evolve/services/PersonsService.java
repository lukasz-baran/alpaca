package com.evolve.services;

import com.evolve.FindPerson;
import com.evolve.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.events.EventType;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.common.WriteResult;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.FluentFilter.where;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonsService implements InitializingBean, FindPerson {
    private final Nitrite nitrite;

    @Override
    public List<PersonListView> fetchList(PersonLookupCriteria criteria) {
        return fetch(criteria)
                .stream()
                .map(PersonListView::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> fetch(PersonLookupCriteria criteria) {
        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);


        return personRepo.find()
                .sort("personId", criteria.getUpDown() ? SortOrder.Ascending : SortOrder.Descending)
                //.limit(criteria.getPageSize())
                .toList();
    }

    @Override
    public Optional<String> findNextPersonId(String lastName) {
        log.info("find next person id for last name: {}", lastName);
        if (StringUtils.isBlank(lastName)) {
            return Optional.empty();
        }

        final Character firstLetter = lastName.toUpperCase().charAt(0);
        if (!Group.isAllowedCharacter(firstLetter)) {
            log.info("Looks like we cannot deduce person ID for: {}", firstLetter);
            return Optional.empty();
        }

        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);

        final List<Person> persons = Group.groupFor(firstLetter)
                .map(Group::getNumer)
                .map(groupNumber -> personRepo.find(where("personId").regex("^" + groupNumber + ".*$"))
                        .sort("personId", SortOrder.Descending)
                        .toList())
                .orElse(List.of());

        return persons.stream().findFirst()
                .map(Person::getPersonId)
                .map(PersonId::of)
                .map(PersonId::nextId)
                .map(PersonId::toString);
    }

    @Override
    public Person findById(String id) {
        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);

        return personRepo.getById(id);
    }

    public boolean insertPerson(Person person, Consumer<Person> callback) {
        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);

        personRepo.subscribe(eventInfo -> {
            if (eventInfo.getEventType() == EventType.IndexEnd) {
                log.info("indexing finished, callback...");
                callback.accept(nitrite.getRepository(Person.class).getById(person.getPersonId()));
            }
        });

        final WriteResult writeResult = personRepo.insert(person);
        nitrite.commit();
        return writeResult.getAffectedCount() > 0;
    }

    public void insertPersons(List<Person> personList) {
        validatePerson(personList);

        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);
        personRepo.remove(Filter.ALL);
        personRepo.dropAllIndices();
        nitrite.commit();

        personList
                .stream()
                .peek(person -> log.info("inserting {}", person))
                .forEach(personRepo::insert);
    }

    @Override
    public void afterPropertiesSet() {
        log.info("LIST persons: ");
        final ObjectRepository<Person> unitRepo = nitrite.getRepository(Person.class);

        for (Person document : unitRepo.find()) {
            log.info("document: {}", document);
        }
    }

    void validatePerson(List<Person> personList) {
        Map<String, Long> counts =
                personList.stream().map(Person::getPersonId).collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> log.warn("duplicated id {} - {}", entry.getKey(), entry.getValue()));

        if (counts.entrySet().stream().anyMatch(entry -> entry.getValue() > 1)) {
            throw new RuntimeException("duplicated ids!");
        }

    }
}
