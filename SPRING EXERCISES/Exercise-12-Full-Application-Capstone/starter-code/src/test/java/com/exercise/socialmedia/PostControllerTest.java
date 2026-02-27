package com.exercise.socialmedia;

import com.exercise.socialmedia.controller.PostController;
import com.exercise.socialmedia.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

// TODO 12: @WebMvcTest(PostController.class) is already present
// TODO 12: Declare @Autowired MockMvc and @MockBean PostService fields
@WebMvcTest(PostController.class)
class PostControllerTest {

    // TODO 12: @Autowired MockMvc mockMvc;
    // TODO 12: @MockBean PostService postService;

    // TODO 12: Write a test: GET /api/posts returns 200 (use @WithMockUser)
    @Test
    void getAllPosts_returnsOk() throws Exception {
        // TODO 12: implement
    }

    // TODO 12: Write a test: POST /api/posts with empty body {} returns 400
    @Test
    void createPost_withInvalidBody_returns400() throws Exception {
        // TODO 12: implement
    }
}
