package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.Tag;
import com.epam.esm.gift.service.TagService;
import com.epam.esm.gift.service.TagServiceImpl;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TagControllerTest {
    @Mock
    private TagService tagServiceImpl;
    @InjectMocks
    private TagController tagController;
    private MockMvc mockMvc;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }
    @Test
    void getAll() throws Exception {
        Tag tag1 = new Tag();
        tag1.setId(1);
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setId(2);
        tag2.setName("tag2");
        when(tagServiceImpl.getAll()).thenReturn(List.of(tag1, tag2));
        mockMvc.perform(get("/tag")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name",is("tag1")))
                .andExpect(jsonPath("$[1].name",is("tag2")))
                .andDo(print());
        verify(tagServiceImpl, times(1)).getAll();
    }

    @Test
    void getTagById() throws Exception {
        Tag tag = new Tag();
        tag.setId(3);
        tag.setName("tag3");
        when(tagServiceImpl.getById(3)).thenReturn(tag);
        mockMvc.perform(get("/tag/3")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name",is("tag3")))
                .andDo(print());
        verify(tagServiceImpl, times(1)).getById(3);
    }

    @Test
    void addTag() throws Exception {
        Tag tag = new Tag();
        tag.setName("newTag");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(tag);
        when(tagServiceImpl.create(tag)).thenReturn(true);
        mockMvc.perform(post("/tag").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$",is("Resource created successfully")));
        verify(tagServiceImpl, times(1)).create(any(Tag.class));
    }

    @Test
    void deleteTag() throws Exception {
        Tag tag = new Tag();
        tag.setName("neTag");
        when(tagServiceImpl.delete(3)).thenReturn(true);
        mockMvc.perform(delete("/tag/3")).andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(jsonPath("$", is("Resource deleted successfully")));;
        verify(tagServiceImpl, times(1)).delete(3);
    }
}