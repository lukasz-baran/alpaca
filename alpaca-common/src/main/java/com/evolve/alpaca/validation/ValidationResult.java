package com.evolve.alpaca.validation;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
@ToString
public class ValidationResult {

    public static ValidationResult empty() {
        return new ValidationResult();
    }

    public static ValidationResult of(String message) {
        return new ValidationResult(Set.of(message));
    }

    private final Set<String> validationMessages;

    public ValidationResult() {
        this(new HashSet<>());
    }

    public void throwException(Function<Set<String>, RuntimeException> toThrow) {
        if(!isEmpty()) {
            throw toThrow.apply(validationMessages);
        }
    }

    private boolean isEmpty() {
        return validationMessages.isEmpty();
    }

    public boolean isValid() {
        return isEmpty();
    }

    public Set<String> getErrors() {
        return validationMessages;
    }
}
