package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.model.repo.CertificateDao;
import com.epam.esm.gift.model.repo.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {
    private final CertificateDao certificateDAO;
    private final TagDao tagDAO;

    @Autowired
    public CertificateServiceImpl(CertificateDao certificateDAO, TagDao tagDAO) {
        this.certificateDAO = certificateDAO;
        this.tagDAO = tagDAO;
    }

    @Override
    public List<Certificate> getAll() {
        List<Certificate> certificates;
        certificates = certificateDAO.getAll();
        return certificates;
    }

    @Override
    public List<Certificate> getAll(String tagName, String description, String sortByDate, String sortByName) {
        List<Certificate> certificates;
        certificates = certificateDAO.getAll(tagName, description, sortByDate, sortByName);
        return certificates;
    }

    @Override
    public Certificate getById(int id) {
        Certificate certificate = certificateDAO.getById(id).orElse(null);
        return certificate;
    }

    @Override
    public boolean create(Certificate certificate) {
        certificateDAO.create(certificate);
        return true;
    }

    @Override
    public boolean update(Certificate certificate) {
        Certificate certificateDB = certificateDAO.getById(certificate.getId()).orElse(null);
        certificate.setLastUpdateDate(LocalDateTime.now());
        if (certificate.getName() != null) certificateDB.setName(certificate.getName());
        if (certificate.getDescription() != null) certificateDB.setDescription(certificate.getDescription());
        if (certificate.getPrice() != certificateDB.getPrice()) certificateDB.setPrice(certificate.getPrice());
        if (certificate.getDuration() != certificateDB.getDuration())
            certificateDB.setDuration(certificate.getDuration());
        if (!certificateDB.getTags().equals(certificate.getTags())) {
            certificateDB.setTags(certificate.getTags());
        }
        certificateDAO.update(certificateDB);

        return true;
    }

    @Override
    public boolean delete(int id) {
        certificateDAO.delete(id);
        return true;
    }
}
