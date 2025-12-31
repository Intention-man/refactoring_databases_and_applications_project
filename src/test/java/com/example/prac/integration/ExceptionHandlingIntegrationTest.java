package com.example.prac.integration;

import com.example.prac.exception.ResourceAlreadyExistsException;
import com.example.prac.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlingIntegrationTest {

    @Test
    void testResourceNotFoundException_MessageFormat() {
        // Given
        ResourceNotFoundException ex1 = new ResourceNotFoundException("Project", 123L);
        ResourceNotFoundException ex2 = new ResourceNotFoundException("User", "admin");
        ResourceNotFoundException ex3 = new ResourceNotFoundException("Custom message");

        // Then
        assertEquals("Project with id 123 not found", ex1.getMessage());
        assertEquals("User with identifier 'admin' not found", ex2.getMessage());
        assertEquals("Custom message", ex3.getMessage());
    }

    @Test
    void testResourceAlreadyExistsException_MessageFormat() {
        // Given
        ResourceAlreadyExistsException ex1 = new ResourceAlreadyExistsException("User", "admin");
        ResourceAlreadyExistsException ex2 = new ResourceAlreadyExistsException("Custom message");

        // Then
        assertEquals("User with identifier 'admin' already exists", ex1.getMessage());
        assertEquals("Custom message", ex2.getMessage());
    }
}

