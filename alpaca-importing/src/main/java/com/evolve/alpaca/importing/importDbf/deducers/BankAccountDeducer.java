package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.BankAccount;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (!result.isBlank()) {
            return Optional.of(BankAccount.of(result));
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
