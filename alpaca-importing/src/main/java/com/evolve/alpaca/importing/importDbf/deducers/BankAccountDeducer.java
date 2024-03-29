package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.BankAccount;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class BankAccountDeducer extends AbstractSmartDeducer<BankAccount> {
    public BankAccountDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<BankAccount> deduceFrom(List<String> guesses) {
        final String result = guesses.stream()
                .filter(this::containsOnlyCiphersAndLegalChars)
                .map(this::removeNonNumericCharacters)
                .collect(Collectors.joining());

        final String notes = guesses.stream()
                .filter(not(this::containsOnlyCiphersAndLegalChars))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
        if (StringUtils.isNotBlank(result) || StringUtils.isNotBlank(notes)) {
            return Optional.of(BankAccount.of(result, StringUtils.stripToNull(notes)));
        }
        return Optional.empty();
    }

    boolean containsOnlyCiphersAndLegalChars(String item) {
        return StringUtils.containsOnly(item, "0123456789 -");
    }

    String removeNonNumericCharacters(String item) {
        String removed = StringUtils.removeAll(item, " ");
        return StringUtils.removeAll(removed, "-");
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }
}
