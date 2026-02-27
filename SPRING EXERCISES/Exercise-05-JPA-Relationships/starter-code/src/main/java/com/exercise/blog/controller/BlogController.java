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

// TODO 17: Add @RestController
//          Implement all endpoints below by delegating to BlogService.
//          Use appropriate HTTP status codes and handle NoSuchElementException → 404.
@RequestMapping("/api")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // GET /api/authors
    public ResponseEntity<List<Author>> getAllAuthors() {
        return null;
    }

    // POST /api/authors
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return null;
    }

    // GET /api/authors/{id}/posts
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long id) {
        return null;
    }

    // GET /api/posts?page=0&size=5
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return null;
    }

    // GET /api/posts/{id}
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return null;
    }

    // POST /api/posts — request body should include authorId field
    // { "title": "...", "content": "...", "authorId": 1 }
    // Use @RequestBody Map<String, Object> payload to extract authorId and post
    // fields
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> payload) {
        return null;
    }

    // POST /api/posts/{id}/tags — request body: { "name": "spring-boot" }
    public ResponseEntity<Post> addTagToPost(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return null;
    }

    // GET /api/tags
    public ResponseEntity<List<Tag>> getAllTags() {
        return null;
    }

    // GET /api/tags/{id}/posts
    public ResponseEntity<List<Post>> getPostsByTag(@PathVariable Long id) {
        return null;
    }
}
