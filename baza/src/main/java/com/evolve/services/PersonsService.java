package com.evolve.services;

import com.evolve.FindPerson;
import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.common.SortOrder;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Person findById(String id) {
        final ObjectRepository<Person> personRepo = nitrite.getRepository(Person.class);

        return personRepo.getById(id);
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
    public void afterPropertiesSet() throws Exception {
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
