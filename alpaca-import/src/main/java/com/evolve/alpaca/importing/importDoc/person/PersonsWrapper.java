package com.evolve.alpaca.importing.importDoc.person;

import com.evolve.domain.RegistryNumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PersonsWrapper {
    public static final RegistryNumber MISSING_REGISTRY_NUMBER = RegistryNumber.of("");

    private final List<PersonFromDoc> personList;

    public RegistryNumber findByPersonId(String personId) {
        return personList.stream()
                .filter(person -> StringUtils.equals(personId, person.getPersonId()))
                .map(PersonFromDoc::getNumerKartoteki)
                .findFirst()
                .orElse(MISSING_REGISTRY_NUMBER);
    }
}
