package com.exercise.socialmedia.service;

import com.exercise.socialmedia.annotation.Audited;
import com.exercise.socialmedia.dto.PostRequest;
import com.exercise.socialmedia.dto.PostResponse;
import com.exercise.socialmedia.entity.*;
import com.exercise.socialmedia.exception.ResourceNotFoundException;
import com.exercise.socialmedia.exception.UnauthorizedException;
import com.exercise.socialmedia.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository,
            TagRepository tagRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.likeRepository = likeRepository;
    }

    @Audited(action = "CREATE_POST")
    @Transactional
    public PostResponse createPost(PostRequest request, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Post post = new Post();
        post.setContent(request.getContent());
        post.setAuthor(author);
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
                post.getTags().add(tag);
            }
        }
        return toPostResponse(postRepository.save(post));
    }

    public PostResponse getPostById(Long id) {
        return toPostResponse(postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", id)));
    }

    public List<PostResponse> getAllPosts(int page, int size) {
        return postRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .stream().map(this::toPostResponse).collect(Collectors.toList());
    }

    public List<PostResponse> getFeed(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        List<User> following = new ArrayList<>(currentUser.getFollowing());
        if (following.isEmpty())
            return List.of();
        return postRepository.findFeedPosts(following).stream()
                .map(this::toPostResponse).collect(Collectors.toList());
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, String username) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", id));
        if (!post.getAuthor().getUsername().equals(username))
            throw new UnauthorizedException("You can only edit your own posts");
        post.setContent(request.getContent());
        return toPostResponse(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", id));
        if (!post.getAuthor().getUsername().equals(username))
            throw new UnauthorizedException("You can only delete your own posts");
        postRepository.delete(post);
    }

    @Transactional
    public PostResponse likePost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        if (!likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }
        return toPostResponse(post);
    }

    @Transactional
    public PostResponse unlikePost(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        likeRepository.findByPostIdAndUserId(postId, user.getId()).ifPresent(likeRepository::delete);
        return toPostResponse(post);
    }

    private PostResponse toPostResponse(Post post) {
        PostResponse r = new PostResponse();
        r.setId(post.getId());
        r.setContent(post.getContent());
        r.setAuthorUsername(post.getAuthor().getUsername());
        r.setTags(post.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        r.setLikeCount(post.getLikes().size());
        r.setCommentCount(post.getComments().size());
        r.setCreatedAt(post.getCreatedAt());
        r.setUpdatedAt(post.getUpdatedAt());
        return r;
    }
}
