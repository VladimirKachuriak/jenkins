package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Tag;
import com.epam.esm.gift.model.repo.TagDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TagServiceImplTest {
    @Mock
    private TagDao tagDao;
    @InjectMocks
    private TagServiceImpl tagServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getAll() {
        when(tagDao.getAll()).thenReturn(Arrays.asList(new Tag(), new Tag()));
        assertEquals(2, tagServiceImpl.getAll().size());
        verify(tagDao, times(1)).getAll();
    }

    @Test
    void getById() {
        Tag tagDb = new Tag();
        tagDb.setId(3);
        tagDb.setName("tag");
        when(tagDao.getById(3)).thenReturn(Optional.of(tagDb));
        assertEquals("tag", tagServiceImpl.getById(3).getName());
        verify(tagDao, times(1)).getById(3);
    }

    @Test
    void create() {
        Tag tag = new Tag();
        tag.setName("tag");
        when(tagDao.getById(3)).thenReturn(Optional.of(tag));
        assertEquals(true, tagServiceImpl.create(tag));
        verify(tagDao, times(1)).create(any(Tag.class));
    }

    @Test
    void update() {
        Tag tag = new Tag();
        tag.setId(3);
        tag.setName("tag");
        when(tagDao.getById(3)).thenReturn(Optional.of(tag));
        assertEquals(true, tagServiceImpl.update(tag));
        verify(tagDao, times(1)).update(tag);
    }

    @Test
    void delete() {
        assertEquals(true, tagServiceImpl.delete(3));
        verify(tagDao, times(1)).delete(3);
    }
}