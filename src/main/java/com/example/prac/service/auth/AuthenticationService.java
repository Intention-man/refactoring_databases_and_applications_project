package com.example.prac.service.auth;

import com.example.prac.data.DTO.auth.AuthenticationRequest;
import com.example.prac.data.DTO.auth.AuthenticationResponse;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.authEntity.Role;
import com.example.prac.repository.auth.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ActorRepository actorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(Actor actor) {
        boolean userExists = actorRepository.existsByUsername(actor.getUsername());
        if (userExists){
            return null;
        }

        actor.setPassword(passwordEncoder.encode(actor.getPassword()));
        actorRepository.save(actor);

        var token = jwtService.generateToken(actor);
        Role role = actor.getRole();
        return AuthenticationResponse.builder()
                .token(token)
                .role(role)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        var actor = actorRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Actor not found"));

        var token = jwtService.generateToken(actor);
        Role role = actor.getRole();

        return AuthenticationResponse.builder()
                .token(token)
                .role(role)
                .build();
    }

    public boolean isTokenValid(String token) {
        String jwt = token.substring(7);
        String username = jwtService.extractUsername(jwt);

        if (username != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            return jwtService.isTokenValid(jwt, userDetails);
        }
        return false;
    }
}