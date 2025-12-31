package com.example.prac.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resourceName, identifier));
    }
}

