package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.Address;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmartAddressPersonDeducer extends AbstractSmartDeducer<Address>{

    // \p{IsAlphabetic} - includes also Polish characters ąćż..
    static final Pattern CITY_CODE_PATTERN = Pattern.compile("\\d{2}-\\d{3} [\\p{IsAlphabetic} ]+");

    //ul. Wyspiañskiego 63/2
    static final String STREET_NUMBER_PATTERN = "( ?((/ ?\\d+)|[AaBbCcDdEeFfGg]))?";
    static final Pattern STREET_PATTERN = Pattern.compile("([uU]l\\. )?[\\p{IsAlphabetic} \\.]+\\d+[AaBbCcDd]?" + STREET_NUMBER_PATTERN);

    // contains elements that were removed during deduction process
    private final Set<String> usedLines;

    public SmartAddressPersonDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
        this.usedLines = new HashSet<>();
    }

    @Override
    public Optional<Address> deduceFrom(List<String> guesses) {
        List<String> streetCandidates = new ArrayList<>();
        Set<String> cityCodes = new HashSet<>();

        for (int i = 0; i < guesses.size(); i++ ) {
            final String maybeCityCode = guesses.get(i);

            if (isCityCode(maybeCityCode)) {
                cityCodes.add(maybeCityCode);
                if (i > 0) {
                    final String maybeStreet = guesses.get(i - 1);
                    if (isStreet(maybeStreet)) {
                        streetCandidates.add(maybeStreet);
                    }
                }
            }
        }

        if (!cityCodes.isEmpty()) {
            final String cityWithPostalCode = cityCodes.stream().findFirst().get();
            usedLines.add(cityWithPostalCode);
            final Optional<String> street = streetCandidates.stream().findFirst();
            street.ifPresent(usedLines::add);
            return Optional.of(postalCode(cityWithPostalCode, street.orElse(null)));
        }

        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses.stream()
                .filter(guess -> !usedLines.contains(guess))
//                .filter(guess -> !isCityCode(guess))
//                .filter(guess -> !isStreet(guess))
                .collect(Collectors.toList());
    }

    static Address postalCode(String candidate, String street) {
        String[] afterSplit = candidate.split(" ", 2);
        return new Address(street, afterSplit[0], afterSplit[1]);
    }

    static boolean isStreet(String street) {
        if (StringUtils.isBlank(street)) {
            return false;
        }
        return STREET_PATTERN.matcher(street).matches();
    }

    static boolean isCityCode(String candidate) {
        if (StringUtils.isBlank(candidate)) {
            return false;
        }
        return CITY_CODE_PATTERN.matcher(candidate).matches();
    }


}
