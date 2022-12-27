package com.evolve.exception;

public class ImportFailedException extends RuntimeException {

    public ImportFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
