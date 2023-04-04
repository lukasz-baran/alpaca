package com.evolve.importing.importDbf.deducers;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PhoneNumbersDeducer extends AbstractSmartDeducer<List<String>> {

    public PhoneNumbersDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<List<String>> deduceFrom(List<String> guesses) {
        return Optional.of(guesses.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList()));
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }
}
