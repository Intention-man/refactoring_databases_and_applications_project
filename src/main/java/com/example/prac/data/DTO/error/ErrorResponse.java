package com.example.prac.data.DTO.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Стандартизированный ответ об ошибке")
public class ErrorResponse {
    @Schema(description = "Время возникновения ошибки", example = "2025-12-31T15:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "HTTP статус код", example = "404")
    private int status;
    
    @Schema(description = "Тип ошибки", example = "Resource Not Found")
    private String error;
    
    @Schema(description = "Сообщение об ошибке", example = "Project with id 1 not found")
    private String message;
    
    @Schema(description = "Путь запроса, вызвавшего ошибку", example = "/api/projects/1")
    private String path;
    
    @Schema(description = "Список ошибок валидации (только для ошибок валидации)")
    private List<ValidationError> validationErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Ошибка валидации поля")
    public static class ValidationError {
        @Schema(description = "Название поля с ошибкой", example = "name")
        private String field;
        
        @Schema(description = "Сообщение об ошибке", example = "Name is required")
        private String message;
        
        @Schema(description = "Отклоненное значение", example = "null")
        private Object rejectedValue;
    }
}

