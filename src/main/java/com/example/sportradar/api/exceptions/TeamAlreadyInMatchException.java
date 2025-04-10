package com.example.sportradar.api.exceptions;

public class TeamAlreadyInMatchException extends RuntimeException {
    public TeamAlreadyInMatchException(String message) {
        super(message);
    }
}
