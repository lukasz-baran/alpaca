package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.RegistryNumber;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

public class RegistryNumbersDeducer extends AbstractSmartDeducer<RegistryNumber> {

    public static final int TWO_NUMBERS_EXPECTED = 6;

    private final RegistryNumberType registryNumberType;

    public RegistryNumbersDeducer(IssuesLogger.ImportIssues issues, RegistryNumberType registryNumberType) {
        super(issues);
        this.registryNumberType = registryNumberType;
    }

    @Override
    public Optional<RegistryNumber> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .flatMap(this::parseRegistryNumbers);
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
            final StringTokenizer stringTokenizer = new StringTokenizer(value);

            if (stringTokenizer.countTokens() == 4) {
                final String oldRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();
                final String newRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();

                return properRegistryType(oldRegistry, newRegistry);

            } else if (stringTokenizer.countTokens() == 3) {
                final String oldRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();
                final String newRegistry = stringTokenizer.nextToken();

                return properRegistryType(oldRegistry, newRegistry);

            } else if (stringTokenizer.countTokens() == 2) {
                final String oldRegistry = stringTokenizer.nextToken();
                final String newRegistry = stringTokenizer.nextToken();

                return properRegistryType(oldRegistry, newRegistry);

            } else if (stringTokenizer.countTokens() == 1) {
                final String newRegistry = stringTokenizer.nextToken();
                return Optional.of(RegistryNumber.fromText(newRegistry));
            }
        }

        if (StringUtils.length(value) > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(value);
            if (stringTokenizer.countTokens() == 2) {
                final String oldRegistry = stringTokenizer.nextToken() + stringTokenizer.nextToken();
                return Optional.of(RegistryNumber.fromText(oldRegistry));
            }
            return Optional.of(RegistryNumber.fromText(stringTokenizer.nextToken()));
        }

        return Optional.empty();

    }

    private Optional<RegistryNumber> properRegistryType(String oldRegistry, String newRegistry) {
        return switch (registryNumberType) {
            case NEW -> Optional.of(RegistryNumber.fromText(newRegistry));
            case OLD -> Optional.of(RegistryNumber.fromText(oldRegistry));
        };
    }

    // used by deducer logic which type we're interested in:
    public enum RegistryNumberType {
        OLD,
        NEW
    }

}
