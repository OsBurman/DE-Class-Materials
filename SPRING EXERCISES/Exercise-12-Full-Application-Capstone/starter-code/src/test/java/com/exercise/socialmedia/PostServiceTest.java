package com.exercise.socialmedia;

import com.exercise.socialmedia.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// TODO 11: Add @ExtendWith(MockitoExtension.class) annotation above (it's there, check it works)
// TODO 11: Inject PostRepository, UserRepository, TagRepository, LikeRepository as @Mock fields
// TODO 11: Inject PostService using @InjectMocks
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    // TODO 11: declare @Mock fields here
    // @Mock PostRepository postRepository;
    // etc.

    // TODO 11: @InjectMocks PostService postService;

    // TODO 11: Write a test for createPost() happy path:
    //         - stub userRepository.findByUsername to return a User
    //         - stub postRepository.save to return a Post with id=1 and content
    //         - call postService.createPost(request, "alice")
    //         - assert response.getContent() equals the expected content
    @Test
    void createPost_happyPath_returnsPostResponse() {
        // TODO 11: implement
    }

    // TODO 11: Write a test for getPostById() when not found:
    //         - stub postRepository.findById to return Optional.empty()
    //         - assert ResourceNotFoundException is thrown
    @Test
    void getPostById_notFound_throwsResourceNotFoundException() {
        // TODO 11: implement
    }
}
