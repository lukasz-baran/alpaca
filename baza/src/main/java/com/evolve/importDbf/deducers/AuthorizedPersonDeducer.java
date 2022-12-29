package com.evolve.importDbf.deducers;

import com.evolve.domain.Person;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class AuthorizedPersonDeducer implements SmartDeducer<Person.AuthorizedPerson> {

    private static final Predicate<String> IS_WIFE = s -> s.startsWith("ż.");

    @Override
    public Optional<Person.AuthorizedPerson> deduceFrom(List<String> guesses) {
        Optional<String> maybeWife = guesses.stream()
                .filter(IS_WIFE)
                .findFirst();
        return maybeWife.map(goodGuess -> {
            final String trimmed = StringUtils.removeStart(goodGuess, "ż.");
            String[] afterSplit = trimmed.split(" ");
            return new Person.AuthorizedPerson(afterSplit[0], afterSplit[1], "żona", null, null, null);
        });
        // TODO deduce other relations

    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses.stream()
                .filter(not(IS_WIFE))
                .collect(Collectors.toList());
    }



}
