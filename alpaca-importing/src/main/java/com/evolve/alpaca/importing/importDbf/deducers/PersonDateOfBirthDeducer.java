package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.DateParser;
import com.evolve.domain.PersonStatusChange;
import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.evolve.alpaca.importing.DateParser.DATE_PATTERN;

@Slf4j
public class PersonDateOfBirthDeducer extends AbstractSmartDeducer<PersonStatusChange> {
    public PersonDateOfBirthDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<PersonStatusChange> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(Objects::nonNull)
                .filter(guess -> guess.matches("^" + DATE_PATTERN.pattern() + ".*"))
                .findFirst().flatMap(this::deduceDob);
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<PersonStatusChange> deduceDob(String input) {
        final String[] split = input.split(" ");
        if (split.length < 1) {
            return Optional.empty();
        }

        try {
            return DateParser.parse(split[0]).map(PersonStatusChange::born);
        } catch (DateTimeException dateTimeException) {
            issues.store(String.format("Unable to create LocalDate for %s", input));
        }
        return Optional.empty();
    }

}
