package com.evolve.importing.importDbf.deducers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Optional;

public class UnitNumberDeducer extends AbstractSmartDeducer<String> {

    public UnitNumberDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<String> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(StringUtils::isNotBlank)
                .filter(NumberUtils::isCreatable)
                .map(guess -> guess.substring(3, 5))
                .findFirst();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }
}
