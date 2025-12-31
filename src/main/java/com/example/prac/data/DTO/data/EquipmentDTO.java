package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class EquipmentDTO {

    private Integer equipmentId;
    private String type;
    private String description;
    private String status;
    private Long budget;
}