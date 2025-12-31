package com.example.prac.data.model.dataEntity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    private String name;

    private String type;

    private String version;

    @Column(name = "modification_date")
    private LocalDate modificationDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}