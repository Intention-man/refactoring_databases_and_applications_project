package com.example.prac.data.DTO.data;

import lombok.Data;

@Data
public class DocumentDTO {

    private Integer documentId;
    private String name;
    private String type;
    private String version;
    private String modificationDate;
    private Integer projectId;
}