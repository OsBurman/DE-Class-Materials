package com.exercise.blog.controller;

import com.exercise.blog.entity.Author;
import com.exercise.blog.entity.Post;
import com.exercise.blog.entity.Tag;
import com.exercise.blog.service.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(blogService.getAllAuthors());
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogService.createAuthor(author));
    }

    @GetMapping("/authors/{id}/posts")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(blogService.getPostsByAuthor(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(blogService.getAllPosts(page, size));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return blogService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> payload) {
        try {
            Long authorId = Long.valueOf(payload.get("authorId").toString());
            Post post = new Post();
            post.setTitle((String) payload.get("title"));
            post.setContent((String) payload.get("content"));
            return ResponseEntity.status(HttpStatus.CREATED).body(blogService.createPost(authorId, post));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/posts/{id}/tags")
    public ResponseEntity<Post> addTagToPost(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(blogService.addTagToPost(id, body.get("name")));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(blogService.getAllTags());
    }

    @GetMapping("/tags/{id}/posts")
    public ResponseEntity<List<Post>> getPostsByTag(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getPostsByTag(id));
    }
}
