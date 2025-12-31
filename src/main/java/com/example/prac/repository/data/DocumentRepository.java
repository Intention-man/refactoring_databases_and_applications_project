package com.example.prac.repository.data;

import com.example.prac.data.model.dataEntity.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Integer> {
}