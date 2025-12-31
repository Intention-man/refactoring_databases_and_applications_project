package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "experiment")
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experiment_id", nullable = false, unique = true)
    private Integer experimentId;

    private String name;

    private String description;

    private String status;

    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
