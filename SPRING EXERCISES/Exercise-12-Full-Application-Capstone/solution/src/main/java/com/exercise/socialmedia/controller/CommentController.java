package com.exercise.socialmedia.controller;

import com.exercise.socialmedia.dto.CommentRequest;
import com.exercise.socialmedia.dto.CommentResponse;
import com.exercise.socialmedia.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) { this.commentService = commentService; }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                       @Valid @RequestBody CommentRequest request,
                                                       Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(postId, request, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }
}
