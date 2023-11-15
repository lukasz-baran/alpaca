package com.evolve.services;

import com.evolve.FindPerson;
import com.evolve.domain.*;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonsService implements InitializingBean, FindPerson {
    private final PersonRepository personRepository;

    @Override
    public List<PersonListView> fetchList(PersonLookupCriteria criteria) {
        return fetch(criteria)
                .stream()
                .map(PersonListView::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> fetch(PersonLookupCriteria criteria) {
        if (StringUtils.isNotEmpty(criteria.getUnitNumber())) {
            return personRepository.findAll(
                    Example.of(Person.builder().unitNumber(criteria.getUnitNumber()).build()),
                    criteria.getSort());
        }

        return personRepository.findAll(criteria.getSort());
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

        final Optional<String> maybeGroupName = Group.groupFor(firstLetter)
                .map(Group::getNumer);

        final List<Person> personFromDb = maybeGroupName
                .map(personRepository::findByGroupName)
                .orElse(Collections.emptyList());

        if (personFromDb.isEmpty()) {
            return PersonId.firstId(maybeGroupName);
        }

        return personFromDb.stream()
                .max(Comparator.comparing(Person::getPersonId))
                .map(Person::getPersonId)
                .map(PersonId::of)
                .map(PersonId::nextId)
                .map(PersonId::toString);
    }

    @Override
    public Person findById(String personId) {
        return personRepository.findByPersonId(personId);
    }

    @Override
    public List<Person> findByUnitId(String unitNumber) {
        return personRepository.findAll(
                Example.of(Person.builder().unitNumber(unitNumber).build()));
    }

    public boolean insertPerson(Person person) {
        log.info("Adding person {}", person);
        final Person insertedPerson = personRepository.save(person);
        return true;
    }

    public void insertPersons(List<Person> personList) {
        validatePerson(personList);

        personRepository.deleteAll();
        personRepository.saveAll(personList);
    }

    @Override
    public void afterPropertiesSet() {
        log.info("LIST persons: ");
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
