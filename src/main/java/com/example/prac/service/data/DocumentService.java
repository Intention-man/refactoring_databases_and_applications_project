package com.example.prac.service.data;

import com.example.prac.data.DTO.data.DocumentDTO;
import com.example.prac.data.model.dataEntity.Document;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.mappers.impl.DocumentMapper;
import com.example.prac.repository.data.DocumentRepository;
import com.example.prac.repository.data.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final DocumentMapper documentMapper;

    public DocumentDTO save(DocumentDTO documentDTO) {
        Document document = documentMapper.mapFrom(documentDTO);
        return documentMapper.mapTo(documentRepository.save(document));
    }

    public List<DocumentDTO> findAll() {
        return StreamSupport.stream(documentRepository.findAll().spliterator(), false)
                .map(documentMapper::mapTo)
                .toList();
    }

    public Optional<DocumentDTO> findById(Long id) {
        return documentRepository.findById(id.intValue())
                .map(documentMapper::mapTo);
    }

    public boolean isExists(Long id) {
        return documentRepository.existsById(id.intValue());
    }

    public DocumentDTO partialUpdate(Integer id, DocumentDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return documentRepository.findById(id).map(existing -> {
            Optional.ofNullable(dto.getName()).ifPresent(existing::setName);
            Optional.ofNullable(dto.getType()).ifPresent(existing::setType);
            Optional.ofNullable(dto.getVersion()).ifPresent(existing::setVersion);

            Optional.ofNullable(dto.getModificationDate())
                    .ifPresent(date -> existing.setModificationDate(LocalDate.parse(date, formatter)));

            if (dto.getProjectId() != null) {
                Project project = projectRepository.findById(dto.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Project doesn't exist"));
                existing.setProject(project);
            }

            Document saved = documentRepository.save(existing);
            return documentMapper.mapTo(saved);
        }).orElseThrow(() -> new RuntimeException("Document doesn't exist"));
    }

    public void delete(Long id) {
        documentRepository.deleteById(id.intValue());
    }
}
