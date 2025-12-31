package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class ResourceDTO {

    private Integer resourceId;
    private String type;
    private Long quantity;
    private String unit;
    private Integer projectId;
}