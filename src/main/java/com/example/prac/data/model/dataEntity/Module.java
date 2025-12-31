package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "module")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Integer moduleId;

    private String type;

    private String description;

    private String status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}