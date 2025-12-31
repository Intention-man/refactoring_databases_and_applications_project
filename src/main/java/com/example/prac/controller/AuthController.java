package com.example.prac.controller;

import com.example.prac.data.DTO.auth.AuthenticationRequest;
import com.example.prac.data.DTO.auth.AuthenticationResponse;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody Actor request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> checkToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authenticationService.isTokenValid(authorizationHeader))
            return ResponseEntity.ok("Token is valid");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
