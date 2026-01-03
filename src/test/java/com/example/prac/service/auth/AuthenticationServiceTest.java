package com.example.prac.service.auth;

import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.data.DTO.auth.AuthenticationRequest;
import com.example.prac.data.DTO.auth.AuthenticationResponse;
import com.example.prac.exception.ResourceAlreadyExistsException;
import com.example.prac.repository.auth.ActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Actor testActor;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        testActor = Actor.builder()
                .actorId(1)
                .username("testuser")
                .password("encodedPassword")
                .role(Role.MANAGER)
                .contactInformation("test@example.com")
                .build();

        authRequest = AuthenticationRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenUserDoesNotExist() {
        // Arrange
        Actor newActor = Actor.builder()
                .username("newuser")
                .password("password123")
                .role(Role.MANAGER)
                .build();

        when(actorRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(actorRepository.save(any(Actor.class))).thenAnswer(invocation -> {
            Actor saved = invocation.getArgument(0);
            saved.setActorId(1);
            return saved;
        });
        when(jwtService.generateToken(any(Actor.class))).thenReturn("testToken");

        // Act
        AuthenticationResponse response = authenticationService.register(newActor);

        // Assert
        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        assertEquals(Role.MANAGER, response.getRole());
        verify(actorRepository).existsByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(actorRepository).save(any(Actor.class));
        verify(jwtService).generateToken(any(Actor.class));
    }

    @Test
    void register_ShouldThrowResourceAlreadyExistsException_WhenUserExists() {
        // Arrange
        when(actorRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> authenticationService.register(testActor)
        );

        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains("testuser"));
        verify(actorRepository).existsByUsername("testuser");
        verify(actorRepository, never()).save(any(Actor.class));
    }

    @Test
    void authenticate_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(actorRepository.findByUsername("testuser")).thenReturn(Optional.of(testActor));
        when(jwtService.generateToken(any(Actor.class))).thenReturn("testToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        assertEquals(Role.MANAGER, response.getRole());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(actorRepository).findByUsername("testuser");
        verify(jwtService).generateToken(any(Actor.class));
    }

    @Test
    void authenticate_ShouldThrowBadCredentialsException_WhenAuthenticationFails() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // Act & Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(authRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(actorRepository, never()).findByUsername(anyString());
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        // Arrange
        String token = "Bearer validToken";
        when(jwtService.extractUsername("validToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testActor);
        when(jwtService.isTokenValid("validToken", testActor)).thenReturn(true);

        // Act
        boolean isValid = authenticationService.isTokenValid(token);

        // Assert
        assertTrue(isValid);
        verify(jwtService).extractUsername("validToken");
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtService).isTokenValid("validToken", testActor);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Arrange
        String token = "Bearer invalidToken";
        when(jwtService.extractUsername("invalidToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testActor);
        when(jwtService.isTokenValid("invalidToken", testActor)).thenReturn(false);

        // Act
        boolean isValid = authenticationService.isTokenValid(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameIsNull() {
        // Arrange
        String token = "Bearer token";
        when(jwtService.extractUsername("token")).thenReturn(null);

        // Act
        boolean isValid = authenticationService.isTokenValid(token);

        // Assert
        assertFalse(isValid);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}

