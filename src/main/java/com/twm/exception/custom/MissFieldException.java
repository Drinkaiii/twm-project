package com.twm.exception.custom;

public class MissFieldException extends RuntimeException {
    public MissFieldException(String message) {
        super(message);
    }
}
