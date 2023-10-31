package com.evolve.alpaca.validation;

public interface Validator<T> {

    ValidationResult validate(T toValidate);

    default boolean accept(T test) {
        return true;
    }
}
