package com.epam.esm.gift.model.repo;

import java.util.List;
import java.util.Optional;

/**
 * An interface for a generic repository that provides CRUD operations for entities.
 *
 * @param <T> the type of entity managed by the repository
 */
public interface EntityRepo<T> {
    /**
     * Retrieves all entities from the repository.
     *
     * @return a list of all entities
     */
    List<T> getAll();

    /**
     * Retrieves an entity from the repository based on the specified ID.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity with the specified ID, or null if not found
     */
    Optional<T> getById(int id);

    /**
     * Creates a new entity in the repository.
     *
     * @param t the entity to create
     */
    int create(T t);

    /**
     * Updates an existing entity in the repository.
     *
     * @param t the entity to update
     */
    void update(T t);

    /**
     * Deletes an entity from the repository based on the specified ID.
     *
     * @param id the ID of the entity to delete
     */
    void delete(int id);
}
