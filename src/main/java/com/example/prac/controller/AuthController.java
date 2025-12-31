package com.example.prac.controller;

import com.example.prac.data.DTO.auth.AuthenticationRequest;
import com.example.prac.data.DTO.auth.AuthenticationResponse;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе и возвращает JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким именем уже существует",
                    content = @Content(schema = @Schema(implementation = com.example.prac.data.DTO.error.ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(schema = @Schema(implementation = com.example.prac.data.DTO.error.ErrorResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody Actor request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        return ResponseEntity.ok(authenticationResponse);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Выполняет вход в систему и возвращает JWT токен"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content(schema = @Schema(implementation = com.example.prac.data.DTO.error.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = com.example.prac.data.DTO.error.ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(
            summary = "Проверка валидности токена",
            description = "Проверяет, является ли JWT токен валидным"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен валиден"),
            @ApiResponse(responseCode = "403", description = "Токен невалиден или истек")
    })
    @io.swagger.v3.oas.annotations.Parameter(
            name = "Authorization",
            description = "JWT токен в формате 'Bearer {token}'",
            required = true,
            schema = @Schema(type = "string")
    )
    @GetMapping("/verify-token")
    public ResponseEntity<?> checkToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authenticationService.isTokenValid(authorizationHeader))
            return ResponseEntity.ok("Token is valid");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
