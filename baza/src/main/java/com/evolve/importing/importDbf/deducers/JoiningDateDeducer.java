package com.evolve.importing.importDbf.deducers;

import com.evolve.importing.DateParser;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.evolve.importing.DateParser.DATE_PATTERN;

public class JoiningDateDeducer extends AbstractSmartDeducer<LocalDate> {

    public static final String DATE_JOINED = ".*" + DATE_PATTERN.pattern() + "$";

    public JoiningDateDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<LocalDate> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(this::isDateJoined)
                .findFirst().flatMap(this::deduceJoiningDate);
    }

    boolean isDateJoined(String input) {
        if (StatusPersonDeducer.RESIGNED.stream().anyMatch(input::startsWith)) {
            return false;
        }

        return input.matches(DATE_JOINED);
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<LocalDate> deduceJoiningDate(String input) {
        final String[] split = input.split(" ");
        if (split.length < 2) {
            return Optional.empty();
        }

        final int index = split.length - 1;

        try {
            return DateParser.parse(split[index]);
        } catch (DateTimeException dateTimeException) {
            issues.store(String.format("Unable to create LocalDate for %s", input));
        }
        return Optional.empty();
    }
}
