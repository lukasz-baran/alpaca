package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.PersonStatusDetails;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class StatusPersonDeducer implements SmartDeducer<PersonStatusDetails> {

    //NOTE: the order in the following lists is important:
    public static final List<String> DECEASED = List.of("ZMARŁA", "ZMARŁ", "ZM.", "ZM ");
    public static final List<String> RESIGNED = List.of("rezygnacja", "REZ.", "Rezyg.", "rezy", "REZ");
    public static final List<String> REMOVED = List.of("Skreślenie", "skreśl.", "Skreśl", "skre", "SKR");

    @Override
    public Optional<PersonStatusDetails> deduceFrom(List<String> guesses) {
        final Optional<PersonStatusDetails> maybeDead = detectDeceased(guesses);
        if (maybeDead.isPresent()) {
            return maybeDead;
        }

        final Optional<PersonStatusDetails> maybeResigned = detectResigned(guesses);
        if (maybeResigned.isPresent()) {
            return maybeResigned;
        }

        return detectRemoved(guesses);
    }

    private Optional<PersonStatusDetails> detectDeceased(List<String> guesses) {
        return guesses.stream()
                .filter(guess -> containsIgnoreCase(DECEASED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(DECEASED, guess))
                .map(PersonStatusDetails::dead);
    }

    private Optional<PersonStatusDetails> detectResigned(List<String> guesses) {
        final List<String> resignation = List.of("rez", "rezy", "rez.");

        if (guesses.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .anyMatch(guess -> anyEqualsIgnoreCase(resignation, guess))) {
            return Optional.of(PersonStatusDetails.resigned());
        }

        return guesses.stream()
                .filter(guess -> containsIgnoreCase(RESIGNED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(RESIGNED, guess))
                .map(PersonStatusDetails::resigned);
    }

    private Optional<PersonStatusDetails> detectRemoved(List<String> guesses) {
        return guesses.stream()
                .filter(guess -> containsIgnoreCase(REMOVED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(REMOVED, guess))
                .map(PersonStatusDetails::removed);
    }


    private boolean anyEqualsIgnoreCase(List<String> list, String guess) {
        return list.stream().anyMatch(element -> StringUtils.equalsIgnoreCase(guess, element));
    }

    private boolean containsIgnoreCase(List<String> list, String guess) {
        return list.stream().anyMatch(element -> StringUtils.containsIgnoreCase(guess, element));
    }

    private String removeMatchingString(List<String> list, String guess) {
        return list.stream()
                .filter(element -> StringUtils.containsIgnoreCase(guess, element))
                .map(element -> StringUtils.removeIgnoreCase(guess, element))
                .findFirst()
                .map(String::trim)
                .orElseThrow();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return null;
    }
}
