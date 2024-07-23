package com.company.exeptions;

public class UserNotFountException extends RuntimeException{
    public UserNotFountException(String message) {
        super(message);
    }
}
