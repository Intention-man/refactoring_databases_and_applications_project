package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class TaskDTO {

    private Integer taskId;
    private String name;
    private String description;
    private String status;
    private String deadline;
    private Integer projectId;
}