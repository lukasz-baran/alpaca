package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.Address;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class SmartAddressPersonDeducer extends AbstractSmartDeducer<Address>{

    // \p{IsAlphabetic} - includes also Polish characters ąćż..
    static final Pattern CITY_CODE_PATTERN = Pattern.compile("\\d{2}-\\d{3} [\\p{IsAlphabetic} ]+");

    //ul. Wyspiañskiego 63/2
    //8-go Marca
    static final String STREET_NAME_PATTERN = "[\\p{IsAlphabetic} \\.]+\\d+[AaBbCcDd]?";
    static final String STREET_NUMBER_PATTERN = "( ?((/ ?\\d+)|[AaBbCcDdEeFfGgHh]))?";
    static final Pattern STREET_PATTERN = Pattern.compile(STREET_NAME_PATTERN + STREET_NUMBER_PATTERN);

    /**
     * <b>Ugly way</b> of recognizing some street addresses.
     * We don't want to lose time on preparing super-advanced regular expressions.
     */
    static List<String> ARBITRARY_ACCEPTED_STREET_PREFIXES = List.of(
            "ul.",
            "1000-lecia",
            "11 Listopada",
            "11-ego Listopada",
            "15 Sierpnia",
            "16 Stycznia",
            "17 Stycznia",
            "22-go Lipca",
            "29 Listopada",
            "29-ego Listopada",
            "3 Maja",
            "3-go Maja",
            "6-go-Sierpnia",
            "8 Marca",
            "8-go Marca",
            "Cegielniana",
            "Chmielnik",
            "Chopina",
            "Dąbrowskiego",
            "Grunwaldzka",
            "Kielnarowa",
            "Kochanowskiego",
            "Kopisto",
            "Króla K. Wielkiego",
            "Kwiatkowskiego",
            "Miła",
            "Monte Cassino",
            "Obrońców Pokoju",
            "Paderewskiego",
            "Podwisłocze",
            "Prymasa 1000",
            "Rudna Mała",
            "Seniora",
            "Strażacka",
            "Warszawska",
            "Wieniawskiego",
            "Wiosenna",
            "Wyszyńskiego",
            "os. Dywizjonu 303");

    // contains elements that were removed during deduction process
    private final Set<String> usedLines;

    /**
     * {@code true} remove matched lines
     */
    private final boolean removeGuesses;

    public SmartAddressPersonDeducer(IssuesLogger.ImportIssues issues, boolean removeGuesses) {
        super(issues);
        this.usedLines = new HashSet<>();
        this.removeGuesses = removeGuesses;
    }

    @Override
    public Optional<Address> deduceFrom(List<String> guesses) {
        final Optional<Address> maybeAddress = deduce(guesses);
        maybeAddress.ifPresent(address -> removeGuesses(guesses));
        return maybeAddress;
    }

     Optional<Address> deduce(List<String> guesses) {
        final List<String> streetCandidates = new ArrayList<>();
        final Set<String> cityCodes = new HashSet<>();

        for (int i = 0; i < guesses.size(); i++ ) {
            final String maybeCityCode = guesses.get(i);

            if (isCityCode(maybeCityCode)) {
                cityCodes.add(maybeCityCode);
                if (i > 0) {
                    final String maybeStreet = guesses.get(i - 1);
                    if (isStreet(maybeStreet)) {
                        streetCandidates.add(maybeStreet);
                    } else {
                        final String message = String.format("Cannot identify a valid street address: '%s'", maybeStreet);
                        issues.store(message);
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
        if (this.removeGuesses) {
            guesses.removeAll(usedLines);
        }
        return guesses;
    }

    static Address postalCode(String candidate, String street) {
        String[] afterSplit = candidate.split(" ", 2);
        return new Address(street, afterSplit[0], afterSplit[1]);
    }

    static boolean isStreet(String street) {
        if (StringUtils.isBlank(street)) {
            return false;
        }
        if (ARBITRARY_ACCEPTED_STREET_PREFIXES.stream().anyMatch(prefix -> StringUtils.startsWithIgnoreCase(street, prefix))) {
            return true;
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
