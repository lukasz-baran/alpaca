package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.RegistryNumber;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

import static java.util.function.Predicate.not;

public class RegistryNumbersDeducer extends AbstractSmartDeducer<RegistryNumber> {

    public static final int TWO_NUMBERS_EXPECTED = 6;

    public RegistryNumbersDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<RegistryNumber> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .flatMap(this::parseRegistryNumbers)
                .filter(not(RegistryNumber::isUseless));
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }

    Optional<RegistryNumber> parseRegistryNumbers(String value) {
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }

        if (StringUtils.contains(value, "-")) {
            issues.store("The following entry seems to have no valid registry numbers: " + value);
            return Optional.empty();
        }

        if (StringUtils.length(value) > TWO_NUMBERS_EXPECTED) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);

            if (stringTokenizer.countTokens() == 3) {

                final String oldRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();
                final String newRegistry = stringTokenizer.nextToken();

                return Optional.of(new RegistryNumber(oldRegistry, newRegistry));
            } else if (stringTokenizer.countTokens() == 2) {
                final String oldRegistry = stringTokenizer.nextToken();
                final String newRegistry = stringTokenizer.nextToken();
                return Optional.of(new RegistryNumber(oldRegistry, newRegistry));
            } else if (stringTokenizer.countTokens() == 1) {
                final String newRegistry = stringTokenizer.nextToken();
                return Optional.of(new RegistryNumber(null, newRegistry));

            }
        }

        if (StringUtils.length(value) > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);
            if (stringTokenizer.countTokens() == 2) {
                final String oldRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();
                return Optional.of(RegistryNumber.onlyOldRegistryNumber(oldRegistry));
            }
            return Optional.of(RegistryNumber.onlyOldRegistryNumber(stringTokenizer.nextToken()));
        }

        return Optional.empty();

    }



}
