package io.contextr.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.contextr.model.PersistModel;

public interface Repository extends MongoRepository<PersistModel, String> {
}
