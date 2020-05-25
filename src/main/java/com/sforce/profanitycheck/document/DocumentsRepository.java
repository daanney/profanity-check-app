package com.sforce.profanitycheck.document;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DocumentsRepository extends CrudRepository<Document, Long> {
    Optional<Document> findByName(String name);
}
