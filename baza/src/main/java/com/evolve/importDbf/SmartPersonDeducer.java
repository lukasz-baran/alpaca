package com.evolve.importDbf;

import com.evolve.domain.Address;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class SmartPersonDeducer {

    static final Pattern CITY_CODE_PATTERN = Pattern.compile("\\d{2}-\\d{3} [a-zA-Z]+");

    public static Optional<Address> decuceAddress(DbfPerson person) {
        final List<String> guesses = List.of(person.getNAZ_ODB3(), person.getNAZ_ODB4(), person.getNAZ_ODB5(),
                person.getNAZ_ODB6());
//        guesses.stream()
//                .map(String::trim)
//                .filter()

        return Optional.empty();
    }

}
