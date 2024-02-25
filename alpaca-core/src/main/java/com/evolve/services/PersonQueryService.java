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
class PersonQueryService implements FindPerson {
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
        final List<Person> filteredByUnitAndStatus = personRepository.findByCriteria(criteria);

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
    public Optional<Integer> findLastRegistryNumber() {
        return Optional.ofNullable(personRepository.findMaxRegistryNumber());
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


}
