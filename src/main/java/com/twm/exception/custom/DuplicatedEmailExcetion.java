package com.twm.exception.custom;

public class DuplicatedEmailExcetion extends RuntimeException{
    public DuplicatedEmailExcetion(String message) {
        super(message);
    }
}
