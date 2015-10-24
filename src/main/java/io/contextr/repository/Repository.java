package io.contextr.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.contextr.model.RepositoryModel;

public interface Repository extends MongoRepository<RepositoryModel, String> {
}
