package com.exercise.socialmedia.controller;

import com.exercise.socialmedia.dto.PostRequest;
import com.exercise.socialmedia.dto.PostResponse;
import com.exercise.socialmedia.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, principal.getName()));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request,
            Principal principal) {
        return ResponseEntity.ok(postService.updatePost(id, request, principal.getName()));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(postService.likePost(id, principal.getName()));
    }

    @DeleteMapping("/posts/{id}/like")
    public ResponseEntity<PostResponse> unlikePost(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(postService.unlikePost(id, principal.getName()));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostResponse>> getFeed(Principal principal) {
        return ResponseEntity.ok(postService.getFeed(principal.getName()));
    }
}
