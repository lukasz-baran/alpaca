package com.evolve.alpaca.importing.exception;

public class ImportFailedException extends RuntimeException {

    public ImportFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
