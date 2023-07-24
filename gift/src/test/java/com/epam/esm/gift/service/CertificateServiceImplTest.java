package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.model.repo.CertificateDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificateServiceImplTest {
    @Mock
    private CertificateDao certificateDAO;
    @InjectMocks
    private CertificateServiceImpl certificateServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll() {
        when(certificateDAO.getAll()).thenReturn(Arrays.asList(new Certificate(), new Certificate()));
        assertEquals(2, certificateServiceImpl.getAll().size());
        verify(certificateDAO, times(1)).getAll();
    }

    @Test
    void testGetAll() {
        when(certificateDAO.getAll("tag", "description", "ASC", "ASC")).thenReturn(Arrays.asList(new Certificate(), new Certificate()));
        assertEquals(2, certificateServiceImpl.getAll("tag", "description", "ASC", "ASC").size());
        verify(certificateDAO, times(1)).getAll("tag", "description", "ASC", "ASC");
    }

    @Test
    void getById() {
        Certificate certificate = new Certificate();
        certificate.setId(3);
        certificate.setName("METRO");
        certificate.setDescription("supermarket");
        when(certificateDAO.getById(3)).thenReturn(Optional.of(certificate));
        assertEquals("METRO", certificateServiceImpl.getById(3).getName());
        assertEquals("supermarket", certificateServiceImpl.getById(3).getDescription());
        verify(certificateDAO, times(2)).getById(3);
    }

    @Test
    void create() {
        Certificate certificate = new Certificate();
        certificate.setName("METRO");
        certificate.setDescription("supermarket");
        when(certificateDAO.create(certificate)).thenReturn(1);
        assertEquals(true, certificateServiceImpl.create(certificate));
        verify(certificateDAO, times(1)).create(any(Certificate.class));
    }

    @Test
    void update() {
        Certificate certificateDb = new Certificate();
        certificateDb.setId(3);
        certificateDb.setName("METRO");
        certificateDb.setDescription("supermarket");
        certificateDb.setTags(new ArrayList<>());
        when(certificateDAO.getById(3)).thenReturn(Optional.of(certificateDb));

        Certificate certificate = new Certificate();
        certificate.setId(3);
        certificate.setName("updated");
        certificate.setDescription("new description");
        certificate.setTags(new ArrayList<>());

        assertEquals(true, certificateServiceImpl.update(certificate));
        verify(certificateDAO, times(1)).update(any(Certificate.class));
        verify(certificateDAO, times(1)).update(any(Certificate.class));
    }

    @Test
    void delete() {
        assertEquals(true, certificateServiceImpl.delete(1));
        verify(certificateDAO, times(1)).delete(1);
    }
}