package com.evolve;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;

import java.util.List;

public interface FindPerson {

    List<PersonListView> fetchList(PersonLookupCriteria criteria);

    List<Person> fetch(PersonLookupCriteria criteria);


}
