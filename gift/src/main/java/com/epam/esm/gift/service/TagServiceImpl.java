package com.epam.esm.gift.service;

import com.epam.esm.gift.model.Tag;
import com.epam.esm.gift.model.repo.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDAO;

    @Autowired
    public TagServiceImpl(TagDao tagDAO) {
        this.tagDAO = tagDAO;
    }

    @Override
    public List<Tag> getAll() {
        return tagDAO.getAll();
    }

    @Override
    public Tag getById(int id) {
        return tagDAO.getById(id).orElse(null);
    }

    @Override
    public boolean create(Tag tag) {
        Tag tagDB = tagDAO.getByName(tag.getName());
        if (tagDB != null) return false;
        tagDAO.create(tag);
        return true;
    }

    @Override
    public boolean update(Tag tag) {
        Tag tagDB = tagDAO.getById(tag.getId()).orElse(null);
        if (tagDB == null || tagDAO.getByName(tag.getName()) != null) return false;
        tagDB.setName(tag.getName());
        tagDAO.update(tagDB);
        return true;
    }

    @Override
    public boolean delete(int id) {
        tagDAO.delete(id);
        return true;
    }
}
