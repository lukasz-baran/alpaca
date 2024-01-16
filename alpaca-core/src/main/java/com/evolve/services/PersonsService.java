package com.evolve.services;

import com.evolve.FindPerson;
import com.evolve.content.ContentFile;
import com.evolve.content.FileRepository;
import com.evolve.domain.*;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PersonsService implements FindPerson {
    private final PersonRepository personRepository;
    private final FileRepository fileRepository;

    @Override
    public List<PersonListView> fetchList(PersonLookupCriteria criteria) {
        return fetch(criteria)
                .stream()
                .map(PersonListView::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<Person> fetch(PersonLookupCriteria criteria) {
        final List<Person> filteredByUnitAndStatus = findPersons(criteria);

        if (criteria.getHasDocuments() != null) {
            var listOfIds = fileRepository.findAll().stream().map(ContentFile::getPersonId).collect(Collectors.toSet());

            if (criteria.getHasDocuments()) {
                return filteredByUnitAndStatus.stream().filter(p -> listOfIds.contains(p.getPersonId())).collect(Collectors.toList());
            } else {
                return filteredByUnitAndStatus.stream().filter(p -> !listOfIds.contains(p.getPersonId())).collect(Collectors.toList());
            }
        }

        return filteredByUnitAndStatus;
    }

    private List<Person> findPersons(PersonLookupCriteria criteria) {
        var personBuilder = Person.builder();
        boolean hasCriteria = false;

        if (StringUtils.isNotEmpty(criteria.getUnitNumber())) {
            personBuilder.unitNumber(criteria.getUnitNumber());
            hasCriteria = true;
        }

        if (criteria.getStatus() != null) {
            personBuilder.status(PersonStatusDetails.builder().status(criteria.getStatus()).build());
            hasCriteria = true;
        }

        if (hasCriteria) {
            return personRepository.findAll(
                    Example.of(personBuilder.build()),
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
