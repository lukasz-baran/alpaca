package com.evolve;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;

import java.util.List;
import java.util.Optional;

public interface FindPerson {

    List<PersonListView> fetchList(PersonLookupCriteria criteria);

    List<Person> fetch(PersonLookupCriteria criteria);

    default List<Person> byRegistryNumber(Integer registryNumber) {
        return fetch(PersonLookupCriteria.builder().registryNumber(registryNumber).build());
    }

    /**
     * Find next person id based on last name.
     *
     * @param lastName last name (can be null)
     * @return empty() if last name is null or invalid (does not start with allowed characters)
     */
    Optional<String> findNextPersonId(String lastName);

    Optional<Integer> findLastRegistryNumber();

    Person findById(String id);

    List<Person> findByUnitId(String unitId);

}
