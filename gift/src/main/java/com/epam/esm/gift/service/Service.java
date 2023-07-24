package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Tag;

import java.util.List;

public interface Service<T> {
    /**
     * Retrieves a list of all entities.
     *
     * @return a list of all entities
     */
    List<T> getAll();

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the ID of the entity to retrieve
     * @return the entity with the specified ID, or null if not found
     */
    T getById(int id);

    /**
     * Creates a new entity.
     *
     * @param t the entity to create
     * @return true if the entity was created successfully, false otherwise
     */
    boolean create(T t);

    /**
     * Updates an existing entity.
     *
     * @param t the entity to update
     * @return true if the entity was updated successfully, false otherwise
     */
    boolean update(T t);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     * @return true if the entity was deleted successfully, false otherwise
     */
    boolean delete(int id);
}
