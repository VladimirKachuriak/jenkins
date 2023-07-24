package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Tag;

/**
 * An interface for a repository that provides CRUD operations for Tag entities.
 */
public interface TagDao extends EntityRepo<Tag> {
    /**
     * Retrieves a Tag entity based on its name.
     *
     * @param name the name of the Tag to retrieve
     * @return the Tag entity with the specified name, or null if not found
     */
    Tag getByName(String name);
}
