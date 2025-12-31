package com.example.prac.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    public ValidationException(List<String> validationErrors) {
        super("Validation failed");
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}

