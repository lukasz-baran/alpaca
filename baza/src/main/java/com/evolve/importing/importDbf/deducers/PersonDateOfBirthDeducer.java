package com.evolve.importing.importDbf.deducers;

import com.evolve.importing.DateParser;
import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.evolve.importing.DateParser.DOB;

@Slf4j
public class PersonDateOfBirthDeducer extends AbstractSmartDeducer<LocalDate> {
    public PersonDateOfBirthDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<LocalDate> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(guess -> guess.matches("^" + DOB.pattern() + ".*"))
                .findFirst().flatMap(this::deduceDob);
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<LocalDate> deduceDob(String input) {
        final String[] split = input.split(" ");
        if (split.length < 1) {
            return Optional.empty();
        }

        try {
            return DateParser.parse(split[0]);
        } catch (DateTimeException dateTimeException) {
            issues.store(String.format("Unable to create LocalDate for %s", input));
        }
        return Optional.empty();
    }

}
