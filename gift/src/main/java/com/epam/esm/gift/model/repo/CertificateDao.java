package com.epam.esm.gift.model.repo;

import com.epam.esm.gift.model.Certificate;

import java.util.List;

/**
 * An interface for a repository that provides CRUD operations for Certificate entities.
 */
public interface CertificateDao extends EntityRepo<Certificate> {
    /**
     * Retrieves all certificates from the repository based on the specified filters and sorting options.
     *
     * @param tagName     the tag name to filter by (optional)
     * @param description the description to filter by (optional)
     * @param sortByDate  the sorting order for date (optional)
     * @param sortByName  the sorting order for name (optional)
     * @return a list of certificates matching the specified filters and sorting options
     */
    List<Certificate> getAll(String tagName, String description, String sortByDate, String sortByName);


}
