package com.evolve.importing.importDbf.deducers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PersonDateOfBirthDeducer implements SmartDeducer<LocalDate> {

    @Override
    public Optional<LocalDate> deduceFrom(List<String> guesses) {
        return guesses.stream().findFirst().flatMap(this::deduceDob);

    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<LocalDate> deduceDob(String input) {
        return Optional.empty();
    }
}
