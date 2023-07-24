package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.Certificate;
import com.epam.esm.gift.service.CertificateService;
import com.epam.esm.gift.service.CertificateServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CertificateControllerTest {
    @Mock
    private CertificateService certificateService;
    @InjectMocks
    private CertificateController certificateController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(certificateController).build();
    }

    @Test
    void getAll() throws Exception {
        Certificate certificate = new Certificate();
        certificate.setName("name");
        when(certificateService.getAll(null, null, null, null)).thenReturn(List.of(certificate));
        mockMvc.perform(get("/certificate")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andDo(print());
        verify(certificateService, times(1)).getAll(null, null, null, null);
    }

    @Test
    void getCertificateById() throws Exception {
        Certificate certificate = new Certificate();
        certificate.setId(3);
        certificate.setName("METRO");
        when(certificateService.getById(3)).thenReturn(certificate);
        mockMvc.perform(get("/certificate/3")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("METRO")))
                .andDo(print());
        verify(certificateService, times(1)).getById(3);
    }

    @Test
    void create() throws Exception {
        Certificate certificate = new Certificate();
        certificate.setName("METRO");
        certificate.setDescription("description");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(certificate);
        when(certificateService.create(certificate)).thenReturn(true);
        mockMvc.perform(post("/certificate").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$", is("Resource created successfully")));

        verify(certificateService, times(1)).create(any(Certificate.class));
    }

    @Test
    void update() throws Exception {
        Certificate certificate = new Certificate();
        certificate.setId(3);
        certificate.setName("METRO");
        certificate.setDescription("description");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(certificate);
        when(certificateService.update(certificate)).thenReturn(true);
        mockMvc.perform(put("/certificate/3").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON).content(json))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$", is("Resource updated successfully")));

        verify(certificateService, times(1)).update(any(Certificate.class));
    }

    @Test
    void deleteById() throws Exception {
        when(certificateService.delete(3)).thenReturn(true);
        mockMvc.perform(delete("/certificate/3").characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$", is("Resource deleted successfully")));
        verify(certificateService, times(1)).delete(3);
    }
}