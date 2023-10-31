package com.evolve.alpaca.validation;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ValidationException extends RuntimeException{

    private final Set<String> errorMessages = new HashSet<>();

    public ValidationException(Set<String> errorMessages) {
        this.errorMessages.addAll(errorMessages);
    }

}
