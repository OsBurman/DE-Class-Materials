package com.academy.auth;

import com.academy.auth.dto.LoginRequestDto;
import com.academy.auth.dto.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for JWT authentication flow.
 *
 * TODO Task 6: Implement each test stub.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // TODO Task 6a: Login with valid credentials → 200 + access token in response
    @Test
    void login_withValidCredentials_shouldReturnTokens() throws Exception {
        // TODO
        // 1. POST /auth/login with { "username": "user", "password": "user123" }
        // 2. Assert HTTP 200
        // 3. Deserialize response to LoginResponseDto
        // 4. Assert accessToken is not blank
        // 5. Assert refreshToken is not blank
    }

    // TODO Task 6b: Login with wrong password → 401 Unauthorized
    @Test
    void login_withInvalidCredentials_shouldReturn401() throws Exception {
        // TODO
        // POST /auth/login with { "username": "user", "password": "wrongpassword" }
        // Assert HTTP 401
    }

    // TODO Task 6c: Access protected endpoint with valid token → 200
    @Test
    void accessProtectedEndpoint_withValidToken_shouldReturn200() throws Exception {
        // TODO
        // 1. Login to get access token (reuse login logic)
        // 2. GET /api/resources with Authorization: Bearer <accessToken>
        // 3. Assert HTTP 200
    }

    // TODO Task 6d: Access protected endpoint without token → 401 or 403
    @Test
    void accessProtectedEndpoint_withoutToken_shouldReturn401() throws Exception {
        // TODO
        // GET /api/resources (no Authorization header)
        // Assert HTTP 401 or 403
    }

    // TODO Task 6e: VIEWER cannot access ADMIN-only endpoint → 403
    @Test
    void viewerAccessAdminEndpoint_shouldReturn403() throws Exception {
        // TODO
        // 1. Login as viewer (password: "viewer123")
        // 2. GET /api/admin/users with viewer token
        // 3. Assert HTTP 403
    }

    // ---- Helper ----

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequestDto request = new LoginRequestDto(username, password);
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDto.class);
        return response.getAccessToken();
    }
}
