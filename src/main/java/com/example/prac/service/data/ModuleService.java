package com.example.prac.service.data;

import com.example.prac.data.DTO.data.ModuleDTO;
import com.example.prac.data.model.dataEntity.Module;
import com.example.prac.mappers.impl.ModuleMapper;
import com.example.prac.repository.data.ModuleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    public ModuleDTO save(ModuleDTO moduleDTO) {
        Module module = moduleMapper.mapFrom(moduleDTO);
        return moduleMapper.mapTo(moduleRepository.save(module));
    }

    public List<ModuleDTO> findAll() {
        return StreamSupport.stream(moduleRepository.findAll().spliterator(), false)
                .map(moduleMapper::mapTo)
                .toList();
    }

    public Optional<ModuleDTO> findById(Long id) {
        return moduleRepository.findById(id.intValue())
                .map(moduleMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return moduleRepository.existsById(id.intValue());
    }

    public ModuleDTO partialUpdate(Long id, ModuleDTO moduleDTO) {
        moduleDTO.setModuleId(id.intValue());
        return moduleRepository.findById(id.intValue()).map(existing -> {
            ModuleDTO existingDTO = moduleMapper.mapTo(existing);
            // Обновляйте поля, которые необходимо.
            return moduleMapper.mapTo(moduleRepository.save(moduleMapper.mapFrom(existingDTO)));
        }).orElseThrow(() -> new RuntimeException("Module doesn't exist"));
    }

    public void delete(Long id) {
        moduleRepository.deleteById(id.intValue());
    }
}
