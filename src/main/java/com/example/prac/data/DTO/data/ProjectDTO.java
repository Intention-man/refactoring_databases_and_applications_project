package com.example.prac.data.DTO.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для проекта космической станции")
public class ProjectDTO {

    @Schema(description = "Уникальный идентификатор проекта", example = "1")
    private Integer projectId;
    
    @Schema(description = "Название проекта", example = "Mars Habitat Construction", required = true)
    private String name;
    
    @Schema(description = "Статус проекта", example = "ACTIVE",
            allowableValues = {"PLANNED", "ACTIVE", "COMPLETED", "OVERDUE"})
    private String status;
    
    @Schema(description = "Дата начала проекта в формате yyyy-MM-dd", example = "2024-01-10")
    private String startDate;
    
    @Schema(description = "Дата окончания проекта в формате yyyy-MM-dd", example = "2025-12-31")
    private String endDate;
    
    @Schema(description = "Бюджет проекта", example = "2000000000")
    private Long budget;
    
    @Schema(description = "Идентификатор космической станции", example = "1", required = true)
    private Integer spaceStationId;
}