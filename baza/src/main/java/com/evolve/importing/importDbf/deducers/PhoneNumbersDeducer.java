package com.evolve.importing.importDbf.deducers;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class PhoneNumbersDeducer extends AbstractSmartDeducer<List<String>> {

    private static final char MEANINGLESS_ENTRY = '.';

    public PhoneNumbersDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<List<String>> deduceFrom(List<String> guesses) {
        return Optional.of(guesses.stream()
                .filter(StringUtils::isNotBlank)
                .filter(not(PhoneNumbersDeducer::containsOnlyDots))
                .map(String::trim)
                .collect(Collectors.toList()));
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    static boolean containsOnlyDots(String entry) {
        return entry.chars()
                .mapToObj(c -> (char) c)
                .allMatch(c -> c == MEANINGLESS_ENTRY);
    }
}
