package com.exercise.socialmedia;

import com.exercise.socialmedia.controller.PostController;
import com.exercise.socialmedia.dto.PostResponse;
import com.exercise.socialmedia.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    PostService postService;

    @Test
    @WithMockUser
    void getAllPosts_returnsOk() throws Exception {
        when(postService.getAllPosts(0, 20)).thenReturn(List.of(new PostResponse()));
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void createPost_withInvalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
