package com.exercise.socialmedia;

import com.exercise.socialmedia.dto.PostRequest;
import com.exercise.socialmedia.dto.PostResponse;
import com.exercise.socialmedia.entity.Post;
import com.exercise.socialmedia.entity.User;
import com.exercise.socialmedia.exception.ResourceNotFoundException;
import com.exercise.socialmedia.repository.LikeRepository;
import com.exercise.socialmedia.repository.PostRepository;
import com.exercise.socialmedia.repository.TagRepository;
import com.exercise.socialmedia.repository.UserRepository;
import com.exercise.socialmedia.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    LikeRepository likeRepository;
    @InjectMocks
    PostService postService;

    @Test
    void createPost_happyPath_returnsPostResponse() {
        User user = new User();
        user.setUsername("alice");
        user.setId(1L);
        Post saved = new Post();
        saved.setId(1L);
        saved.setContent("Hello World");
        saved.setAuthor(user);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(saved);

        PostRequest request = new PostRequest();
        request.setContent("Hello World");
        PostResponse response = postService.createPost(request, "alice");

        assertThat(response.getContent()).isEqualTo("Hello World");
        assertThat(response.getAuthorUsername()).isEqualTo("alice");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPostById_notFound_throwsResourceNotFoundException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.getPostById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
