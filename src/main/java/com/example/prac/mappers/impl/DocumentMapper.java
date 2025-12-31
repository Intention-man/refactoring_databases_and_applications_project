package com.example.prac.mappers.impl;

import com.example.prac.data.DTO.data.DocumentDTO;
import com.example.prac.data.model.dataEntity.Document;
import com.example.prac.data.model.dataEntity.Project;
import com.example.prac.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DocumentMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DocumentDTO mapTo(Document document) {
        if (document == null) {
            return null;
        }

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setDocumentId(document.getDocumentId());
        documentDTO.setName(document.getName());
        documentDTO.setType(document.getType());
        documentDTO.setVersion(document.getVersion());

        if (document.getModificationDate() != null) {
            documentDTO.setModificationDate(document.getModificationDate().format(DATE_FORMATTER));
        }

        if (document.getProject() != null) {
            documentDTO.setProjectId(document.getProject().getProjectId());
        }

        return documentDTO;
    }

    public Document mapFrom(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return null;
        }

        Document document = new Document();
        document.setDocumentId(documentDTO.getDocumentId());
        document.setName(documentDTO.getName());
        document.setType(documentDTO.getType());
        document.setVersion(documentDTO.getVersion());

        if (documentDTO.getModificationDate() != null) {
            document.setModificationDate(LocalDate.parse(documentDTO.getModificationDate(), DATE_FORMATTER));
        }

        if (documentDTO.getProjectId() != null) {
            Project project = new Project();
            project.setProjectId(documentDTO.getProjectId());
            document.setProject(project);
        }

        return document;
    }
}