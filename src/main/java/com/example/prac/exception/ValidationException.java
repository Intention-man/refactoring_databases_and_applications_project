package com.example.prac.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = Collections.unmodifiableList(List.of(message));
    }

    public ValidationException(List<String> validationErrors) {
        super("Validation failed");
        // Создаем защитную копию списка для предотвращения exposure of internal representation
        this.validationErrors = Collections.unmodifiableList(
                new ArrayList<>(validationErrors != null ? validationErrors : List.of()));
    }

    public List<String> getValidationErrors() {
        // Возвращаем unmodifiable list для предотвращения изменения внутреннего состояния
        return validationErrors;
    }
}

