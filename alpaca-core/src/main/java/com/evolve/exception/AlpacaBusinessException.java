package com.evolve.exception;

public class AlpacaBusinessException extends RuntimeException {

    public AlpacaBusinessException(String message) {
        super(message);
    }

    public AlpacaBusinessException(Throwable cause) {
        super(cause);
    }
}
