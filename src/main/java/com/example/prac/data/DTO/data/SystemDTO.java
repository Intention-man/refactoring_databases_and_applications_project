package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class SystemDTO {

    private Integer systemId;
    private String type;
    private String description;
    private String status;
    private Integer projectId;
}