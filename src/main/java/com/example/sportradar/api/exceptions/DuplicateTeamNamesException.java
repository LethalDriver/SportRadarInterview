package com.example.sportradar.api.exceptions;

public class DuplicateTeamNamesException extends RuntimeException {
    public DuplicateTeamNamesException(String message) {
        super(message);
    }
}
