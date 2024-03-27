package com.evolve.alpaca.importing.importDbf.deducers.status;

import com.evolve.alpaca.importing.DateParser;
import com.evolve.alpaca.importing.importDbf.deducers.AbstractSmartDeducer;
import com.evolve.alpaca.importing.importDbf.deducers.IssuesLogger;
import com.evolve.alpaca.utils.DateUtils;
import com.evolve.domain.PersonStatusChange;
import org.apache.commons.lang.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.evolve.alpaca.importing.DateParser.DATE_PATTERN;

public class JoiningDateDeducer extends AbstractSmartDeducer<PersonStatusChange> {
    private static final String DATE_JOINED = ".*" + DATE_PATTERN.pattern() + "$";

    private static final Pattern SHORT_DATE_JOINED = Pattern.compile(".*" + DateParser.SHORT_DATE_ROMAN_LITERALS_PATTERN.pattern() + "$", Pattern.CASE_INSENSITIVE);

    public JoiningDateDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<PersonStatusChange> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(StringUtils::isNotBlank)
                .filter(this::isDateJoined)
                .findFirst().flatMap(this::deduceJoiningDate);
    }

    boolean isDateJoined(String input) {
        if (StatusPersonDeducer.RESIGNED.stream().anyMatch(
                prefix -> StringUtils.startsWithIgnoreCase(input, prefix)) ||

            StatusPersonDeducer.DECEASED.stream().anyMatch(
                prefix -> StringUtils.startsWithIgnoreCase(input, prefix))) {
            return false;
        }

        return input.matches(DATE_JOINED) || SHORT_DATE_JOINED.matcher(input).matches();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<PersonStatusChange> deduceJoiningDate(String input) {
        final String[] split = input.split("\\s+", 2);
        if (split.length < 2) {
            return Optional.empty();
        }

        final int index = split.length - 1;

        try {
            final String originalValue = split[index];

            return DateParser.parse(originalValue)
                    .map(DateUtils::adjustDateToCurrentCentury)
                    .filter(this::filterOutInvalidDates)
                    .map(joinedDate -> PersonStatusChange.joined(joinedDate, originalValue));
        } catch (DateTimeException dateTimeException) {
            issues.store(String.format("Unable to create LocalDate for %s", input));
        }
        return Optional.empty();
    }

    boolean filterOutInvalidDates(LocalDate joiningDate) {
        final int currentYear = LocalDate.now().getYear();
        return joiningDate.getYear() > 1960 && joiningDate.getYear() < currentYear + 1;
    }
}
