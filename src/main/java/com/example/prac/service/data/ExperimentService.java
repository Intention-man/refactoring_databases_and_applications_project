package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ExperimentDTO;
import com.example.prac.data.model.dataEntity.Experiment;
import com.example.prac.mappers.impl.ExperimentMapper;
import com.example.prac.repository.data.ExperimentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ExperimentService {
    private final ExperimentRepository experimentRepository;
    private final ExperimentMapper experimentMapper;

    @Transactional
    public ExperimentDTO save(ExperimentDTO experimentDTO) {
        Experiment experiment = experimentMapper.mapFrom(experimentDTO);
        checkAndSetOverdueStatus(experiment);
        return experimentMapper.mapTo(experimentRepository.save(experiment));
    }

    public List<ExperimentDTO> findAll() {
        return StreamSupport.stream(experimentRepository.findAll().spliterator(), false)
                .map(experimentMapper::mapTo)
                .toList();
    }

    public Optional<ExperimentDTO> findById(Long id) {
        return experimentRepository.findById(id.intValue())
                .map(experimentMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return experimentRepository.existsById(id.intValue());
    }

//    public ExperimentDTO partialUpdate(Long id, ExperimentDTO experimentDTO) {
//        experimentDTO.setExperimentId(id.intValue());
//        return experimentRepository.findById(id.intValue()).map(existing -> {
//            ExperimentDTO existingDTO = experimentMapper.mapTo(existing);
//            return experimentMapper.mapTo(experimentRepository.save(experimentMapper.mapFrom(existingDTO)));
//        }).orElseThrow(() -> new RuntimeException("Experiment doesn't exist"));
//    }


    public ExperimentDTO partialUpdate(int id, ExperimentDTO experimentDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        experimentDTO.setExperimentId(id);
        return experimentRepository.findById(id).map(existing -> {
            Optional.ofNullable(experimentDTO.getName()).ifPresent(existing::setName);
            Optional.ofNullable(experimentDTO.getDescription()).ifPresent(existing::setDescription);
            Optional.ofNullable(experimentDTO.getStatus()).ifPresent(existing::setStatus);

            Optional.ofNullable(experimentDTO.getDeadline())
                    .map(deadlineStr -> LocalDate.parse(deadlineStr, formatter))
                    .ifPresent(existing::setDeadline);

            checkAndSetOverdueStatus(existing);
            Experiment updatedExperiment = experimentRepository.save(existing);
            return experimentMapper.mapTo(updatedExperiment);
        }).orElseThrow(() -> new com.example.prac.exception.ResourceNotFoundException("Experiment", (long) id));
    }

    public void delete(Long id) {
        experimentRepository.deleteById(id.intValue());
    }

    /**
     * Проверяет и устанавливает статус OVERDUE для эксперимента, если он просрочен.
     * Логика перенесена из триггера check_experiment_overdue().
     */
    private void checkAndSetOverdueStatus(Experiment experiment) {
        if (experiment.getStatus() != null && !"COMPLETED".equals(experiment.getStatus())
                && experiment.getDeadline() != null
                && LocalDate.now().isAfter(experiment.getDeadline())) {
            experiment.setStatus("OVERDUE");
        }
    }
}
