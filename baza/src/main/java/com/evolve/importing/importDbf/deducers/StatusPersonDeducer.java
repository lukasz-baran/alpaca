package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.PersonStatusDetails;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class StatusPersonDeducer implements SmartDeducer<PersonStatusDetails> {

    //NOTE: the order in the following lists is important:
    public static final List<String> DECEASED = List.of("ZMARŁA", "ZMARŁ", "ZM.", "ZM ");
    public static final List<String> RESIGNED = List.of("REZ ", "REZ.", "rezygnacja", "Rezyg.");
    public static final List<String> REMOVED = List.of("skreśl.", "Skreśl", "skre", "SKR");

    @Override
    public Optional<PersonStatusDetails> deduceFrom(List<String> guesses) {
        final Optional<PersonStatusDetails> maybeDead = guesses.stream()
                .filter(guess -> containsIgnoreCase(DECEASED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(DECEASED, guess))
                .map(PersonStatusDetails::dead);
        if (maybeDead.isPresent()) {
            return maybeDead;
        }

        final Optional<PersonStatusDetails> maybeResigned = guesses.stream()
                .filter(guess -> containsIgnoreCase(RESIGNED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(RESIGNED, guess))
                .map(PersonStatusDetails::resigned);

        if (maybeResigned.isPresent()) {
            return maybeResigned;
        }

        return guesses.stream()
                .filter(guess -> containsIgnoreCase(REMOVED, guess))
                .findFirst()
                .map(guess -> removeMatchingString(REMOVED, guess))
                .map(PersonStatusDetails::removed);
    }

    boolean containsIgnoreCase(List<String> list, String guess) {
        return list.stream().anyMatch(element -> StringUtils.containsIgnoreCase(guess, element));
    }

    String removeMatchingString(List<String> list, String guess) {
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
