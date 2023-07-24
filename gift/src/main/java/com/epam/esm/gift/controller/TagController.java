package com.epam.esm.gift.controller;

import com.epam.esm.gift.model.Tag;
import com.epam.esm.gift.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<Tag> getAll() {
        List<Tag> tags = tagService.getAll();
        return tags;
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable int id) {
        Tag tag = tagService.getById(id);
        return tag;
    }

    @PostMapping
    public ResponseEntity<String> addTag(@RequestBody Tag tag) {
        tagService.create(tag);
        return ResponseEntity.ok("Resource created successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable int id) {
        tagService.delete(id);
        return ResponseEntity.ok("Resource deleted successfully");
    }
}
