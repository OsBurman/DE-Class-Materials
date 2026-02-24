package com.jwt.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityBestPracticesTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userToken  = jwtUtil.generateToken("user",  "USER");
        adminToken = jwtUtil.generateToken("admin", "ADMIN");
    }

    /** No credentials → 401 with JSON body (AuthenticationEntryPoint) */
    @Test
    void noToken_returns401JsonBody() throws Exception {
        mockMvc.perform(get("/api/public"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().contentType("application/json"))
               .andExpect(content().string(containsString("Unauthorized")));
    }

    /** USER token hitting ADMIN endpoint → 403 with JSON body (AccessDeniedHandler) */
    @Test
    void wrongRole_returns403JsonBody() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden())
               .andExpect(content().contentType("application/json"))
               .andExpect(content().string(containsString("Forbidden")));
    }

    /** Valid API key in X-API-Key header → 200 OK */
    @Test
    void validApiKey_returns200() throws Exception {
        mockMvc.perform(get("/api/public")
                .header("X-API-Key", "secret-api-key-123"))
               .andExpect(status().isOk());
    }

    /** Invalid API key → no auth set → 401 JSON */
    @Test
    void invalidApiKey_returns401() throws Exception {
        mockMvc.perform(get("/api/public")
                .header("X-API-Key", "wrong-key"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(containsString("Unauthorized")));
    }

    /** Exceed rate limit → 429 with JSON body */
    @Test
    void rateLimitExceeded_returns429() throws Exception {
        for (int i = 0; i < RateLimitFilter.MAX_REQUESTS_PER_WINDOW; i++) {
            mockMvc.perform(get("/api/public")
                    .header("Authorization", "Bearer " + userToken));
        }
        mockMvc.perform(get("/api/public")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isTooManyRequests())
               .andExpect(content().string(containsString("Too Many Requests")));
    }

    /** Admin JWT → 200 on admin endpoint */
    @Test
    void adminEndpoint_withAdminToken_returns200() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("ROLE_ADMIN")));
    }
}
