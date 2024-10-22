package com.twm.exception.custom;

public class VerificationFailedException extends RuntimeException{
    public VerificationFailedException(String message) {
        super(message);
    }
}
