package com.evolve.importing.importDbf.deducers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class RegistryNumbersDeducer extends AbstractSmartDeducer<String> {

    public RegistryNumbersDeducer(IssuesLogger.ImportIssues issues) {
        super(issues);
    }

    @Override
    public Optional<String> deduceFrom(List<String> guesses) {
        // TODO

        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }


    @Getter
    @AllArgsConstructor
    public static class RegistryNumber {
        private Integer registryNum; // numer kartoteki
        private Integer oldRegistryNum; // numer starej kartoteki
    }
}
