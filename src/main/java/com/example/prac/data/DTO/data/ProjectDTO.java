package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class ProjectDTO {

    private Integer projectId;
    private String name;
    private String status;
    private String startDate;
    private String endDate;
    private Long budget;
    private Integer spaceStationId;
}