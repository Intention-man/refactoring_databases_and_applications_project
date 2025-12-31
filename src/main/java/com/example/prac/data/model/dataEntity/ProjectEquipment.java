package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "project_equipment")
public class ProjectEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
}