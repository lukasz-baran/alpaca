package com.evolve.importing.importDbf.deducers;

import java.util.List;
import java.util.Optional;

public interface SmartDeducer<T> {

    Optional<T> deduceFrom(List<String> guesses);

    List<String> removeGuesses(List<String> guesses);

}
