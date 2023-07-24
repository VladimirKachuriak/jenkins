package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Certificate;

import java.util.List;
/**
 * Service interface for managing Certificate entities.
 */
public interface CertificateService extends Service<Certificate>{


    /**
     * Retrieves a filtered list of certificates based on the provided criteria.
     *
     * @param tagName       the name of the tag to filter by (optional)
     * @param description   the description to filter by (optional)
     * @param sortByDate    the sort order for date (optional, values: "asc" or "desc")
     * @param sortByName    the sort order for name (optional, values: "asc" or "desc")
     * @return a filtered list of certificates
     */
    List<Certificate> getAll(String tagName, String description, String sortByDate, String sortByName);
}
