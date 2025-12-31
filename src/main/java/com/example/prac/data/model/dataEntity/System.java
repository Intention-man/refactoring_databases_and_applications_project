package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "system")
public class System {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_id")
    private Integer systemId;

    private String type;

    private String description;

    private String status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}