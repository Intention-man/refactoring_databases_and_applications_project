package com.example.prac.data.model.dataEntity;

import com.example.prac.data.model.authEntity.Actor;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "actor_experiment")
public class ActorExperiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private Actor actor;

    @ManyToOne
    @JoinColumn(name = "experiment_id")
    private Experiment experiment;
}
