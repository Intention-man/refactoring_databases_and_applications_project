package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    private String name;

    private String description;

    private String status;

    private LocalDate deadline;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
