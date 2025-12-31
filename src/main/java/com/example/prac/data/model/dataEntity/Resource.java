package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Integer resourceId;

    private String type;

    private Long quantity;

    private String unit;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}