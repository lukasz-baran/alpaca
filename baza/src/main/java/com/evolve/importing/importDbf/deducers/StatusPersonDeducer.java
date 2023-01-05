package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.PersonStatus;

import java.util.List;
import java.util.Optional;

public class StatusPersonDeducer implements SmartDeducer<PersonStatus> {

    @Override
    public Optional<PersonStatus> deduceFrom(List<String> guesses) {
        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return null;
    }
}
