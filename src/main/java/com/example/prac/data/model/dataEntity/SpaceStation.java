package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "spacestation")
public class SpaceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Integer stationId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "launch_date")
    private LocalDate launchDate;

    private String orbit;
}