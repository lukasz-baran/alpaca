package com.evolve.importing.importDbf.deducers;

import lombok.extern.slf4j.Slf4j;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO add corrections:
 *  Kusz Marta: 08.11.1958
 *  Artur Mazur 27.04.1966
 *
 */
@Slf4j
public class PersonDateOfBirthDeducer extends AbstractSmartDeducer<LocalDate> {
    //30,11.67
    static final String DATE_SEPARATOR = "[,-.]";
    static final String DAY_PATTERN = "(\\d{1,2})";
    static final String MONTH_PATTERN = "(\\d{1,2})";
    static final String YEAR_PATTERN = "(\\d{2,4})";

    static final Pattern DOB = Pattern.compile(DAY_PATTERN + DATE_SEPARATOR + MONTH_PATTERN + DATE_SEPARATOR + YEAR_PATTERN);

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

        final Matcher matcher = DOB.matcher(split[0]);

        if (matcher.matches()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = sanitizeYear(matcher.group(3));

            try {
                return Optional.of(LocalDate.of(year, month, day));
            } catch (DateTimeException dateTimeException) {
                issues.store(String.format("Unable to create LocalDate for %s", input));
            }
        }
        return Optional.empty();
    }


    int sanitizeYear(String year) {
        if (year.length() == 2) {
            return Integer.parseInt("19" + year);
        }
        return Integer.parseInt(year);
    }

}
