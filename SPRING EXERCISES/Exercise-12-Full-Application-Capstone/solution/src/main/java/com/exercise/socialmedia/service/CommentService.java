package com.exercise.socialmedia.service;

import com.exercise.socialmedia.dto.CommentRequest;
import com.exercise.socialmedia.dto.CommentResponse;
import com.exercise.socialmedia.entity.Comment;
import com.exercise.socialmedia.entity.Post;
import com.exercise.socialmedia.entity.User;
import com.exercise.socialmedia.exception.ResourceNotFoundException;
import com.exercise.socialmedia.repository.CommentRepository;
import com.exercise.socialmedia.repository.PostRepository;
import com.exercise.socialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(user);
        return toCommentResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toCommentResponse).collect(Collectors.toList());
    }

    private CommentResponse toCommentResponse(Comment c) {
        CommentResponse r = new CommentResponse();
        r.setId(c.getId());
        r.setContent(c.getContent());
        r.setAuthorUsername(c.getAuthor().getUsername());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
