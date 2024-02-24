package com.evolve.repo.jpa;

import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;

import java.util.List;

public interface PersonRepositoryCustom {

    List<Person> findByCriteria(PersonLookupCriteria criteria);
}
