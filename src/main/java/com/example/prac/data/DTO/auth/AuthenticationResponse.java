package com.example.prac.data.DTO.auth;

import com.example.prac.data.model.authEntity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на запрос аутентификации, содержащий JWT токен и роль пользователя")
public class AuthenticationResponse {
    @Schema(description = "JWT токен для доступа к защищенным эндпоинтам", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    
    @Schema(description = "Роль пользователя в системе", example = "MANAGER")
    private Role role;
}
