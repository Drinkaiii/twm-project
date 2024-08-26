package com.twm.exception.custom;

public class InvalidProviderException extends RuntimeException {
    public InvalidProviderException(String message) {
        super(message);
    }
}
