package com.evolve.importing.importDbf.deducers;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class EmailPersonDeducer extends AbstractSmartDeducer<String> {

    public EmailPersonDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<String> deduceFrom(List<String> guesses) {
        return guesses.stream()
                .filter(StringUtils::isNotBlank)
                .filter(guess -> guess.contains("@"))
                .findFirst();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        final Optional<String> maybeEmail = deduceFrom(guesses);
        maybeEmail.ifPresent(guesses::remove);
        return guesses;
    }

}
