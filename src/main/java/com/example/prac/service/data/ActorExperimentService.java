package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ActorExperimentDTO;
import com.example.prac.data.model.authEntity.Actor;
import com.example.prac.data.model.dataEntity.ActorExperiment;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.mappers.impl.ActorExperimentMapper;
import com.example.prac.repository.auth.ActorRepository;
import com.example.prac.repository.data.ActorExperimentRepository;
import com.example.prac.repository.data.ExperimentRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ActorExperimentService {
    private final ActorExperimentRepository actorExperimentRepository;
    private final ActorRepository actorRepository;
    private final ExperimentRepository experimentRepository;
    private final ActorExperimentMapper actorExperimentMapper;
    private final SessionFactory sessionFactory;

    public ActorExperimentDTO save(ActorExperimentDTO actorExperimentDTO) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        if (!actorRepository.existsById(actorExperimentDTO.getActorId())) {
            throw new RuntimeException("Actor doesn't exist");
        }

        Experiment experiment = experimentRepository.findById(actorExperimentDTO.getExperimentId())
                .orElseThrow(() -> new RuntimeException("Experiment doesn't exist"));

        if (!Objects.equals(experiment.getStatus(), "OPEN")) {
            throw new RuntimeException("Experiment is not open");
        }

        session.createNativeQuery("CALL add_actor_to_experiment(:actorId, :experimentId)")
                .setParameter("actorId", actorExperimentDTO.getActorId())
                .setParameter("experimentId", actorExperimentDTO.getExperimentId())
                .executeUpdate();

        session.getTransaction().commit();

        return actorExperimentDTO;
    }


    public List<ActorExperimentDTO> findAll() {
        return StreamSupport.stream(actorExperimentRepository.findAll().spliterator(), false)
                .map(actorExperimentMapper::mapTo)
                .toList();
    }

    public Optional<ActorExperimentDTO> findById(Long id) {
        return actorExperimentRepository.findById(id.intValue())
                .map(actorExperimentMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return actorExperimentRepository.existsById(id.intValue());
    }

    public ActorExperimentDTO partialUpdate(Integer id, ActorExperimentDTO dto) {
        return actorExperimentRepository.findById(id).map(existing -> {
            if (dto.getActorId() != null) {
                Actor actor = actorRepository.findById(dto.getActorId())
                        .orElseThrow(() -> new RuntimeException("Actor doesn't exist"));
                existing.setActor(actor);
            }

            if (dto.getExperimentId() != null) {
                Experiment experiment = experimentRepository.findById(dto.getExperimentId())
                        .orElseThrow(() -> new RuntimeException("Experiment doesn't exist"));
                existing.setExperiment(experiment);
            }

            ActorExperiment saved = actorExperimentRepository.save(existing);
            return actorExperimentMapper.mapTo(saved);
        }).orElseThrow(() -> new RuntimeException("ActorExperiment doesn't exist"));
    }

    public void delete(Long id) {
        actorExperimentRepository.deleteById(id.intValue());
    }
}
