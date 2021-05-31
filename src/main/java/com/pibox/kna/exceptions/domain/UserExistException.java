package com.pibox.kna.exceptions.domain;

public class UserExistException extends Exception{
    public UserExistException(String message) {
        super(message);
    }
}
