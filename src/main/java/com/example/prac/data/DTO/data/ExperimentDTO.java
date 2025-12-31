package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class ExperimentDTO {

    private Integer experimentId;
    private String name;
    private String description;
    private String status;
    private String deadline;
    private Integer projectId;
}