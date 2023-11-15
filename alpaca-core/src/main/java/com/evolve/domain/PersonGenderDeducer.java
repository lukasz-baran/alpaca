package com.evolve.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersonGenderDeducer {
    private static final List<String> NON_TYPICAL_FEMALE_NAMES = List.of("Miriam", "Beatrycze", "Nel",
            "Abigail", "Karmen", "Noemi", "Ivette");

    public static Person.Gender getGender(String firstName) {
        if (NON_TYPICAL_FEMALE_NAMES.stream().anyMatch(name -> StringUtils.equalsAnyIgnoreCase(name, firstName))) {
            return Person.Gender.FEMALE;
        }

        // this is very poor way of checking Gender
        return StringUtils.trimToEmpty(firstName).endsWith("a") ? Person.Gender.FEMALE : Person.Gender.MALE;
    }

}
