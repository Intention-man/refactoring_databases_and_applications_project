package com.example.prac.exception;

import com.example.prac.data.DTO.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Project", 1L);
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceNotFoundException(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource Not Found", response.getBody().getError());
        assertEquals("Project with id 1 not found", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleResourceAlreadyExistsException() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("User", "admin");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceAlreadyExistsException(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Resource Already Exists", response.getBody().getError());
        assertEquals("User with identifier 'admin' already exists", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationException() {
        ValidationException ex = new ValidationException("Field is required");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals(1, response.getBody().getValidationErrors().size());
    }

    @Test
    void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadCredentialsException(ex, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void testHandleUsernameNotFoundException() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUsernameNotFoundException(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("User Not Found", response.getBody().getError());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(ex, request);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void testHandleBusinessLogicException() {
        BusinessLogicException ex = new BusinessLogicException("Business rule violation");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessLogicException(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Business Logic Error", response.getBody().getError());
        assertEquals("Business rule violation", response.getBody().getMessage());
    }
}

