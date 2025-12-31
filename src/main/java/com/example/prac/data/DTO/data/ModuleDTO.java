package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class ModuleDTO {

    private Integer moduleId;
    private String type;
    private String description;
    private String status;
    private Integer projectId;
}