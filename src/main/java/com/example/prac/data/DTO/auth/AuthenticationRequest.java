package com.example.prac.data.DTO.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на аутентификацию пользователя")
public class AuthenticationRequest {
    @Schema(description = "Имя пользователя", example = "manager", required = true)
    private String username;
    
    @Schema(description = "Пароль пользователя", example = "password123", required = true)
    private String password;
}
