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

/**
 * Tests verifying structured JSON error responses, API key auth,
 * rate limiting, and happy-path scenarios.
 *
 * Complete the TODOs in the source files to make all tests pass.
 */
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

    // ─── 401 JSON body ────────────────────────────────────────────────────────

    /**
     * No credentials at all → expect HTTP 401 with JSON body.
     * (Tests CustomAuthEntryPoint)
     */
    @Test
    void noToken_returns401JsonBody() throws Exception {
        mockMvc.perform(get("/api/public"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().contentType("application/json"))
               .andExpect(content().string(containsString("Unauthorized")));
    }

    // ─── 403 JSON body ────────────────────────────────────────────────────────

    /**
     * Authenticated as USER but accessing ADMIN endpoint → 403 with JSON body.
     * (Tests CustomAccessDeniedHandler)
     */
    @Test
    void wrongRole_returns403JsonBody() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden())
               .andExpect(content().contentType("application/json"))
               .andExpect(content().string(containsString("Forbidden")));
    }

    // ─── API key ──────────────────────────────────────────────────────────────

    /** A valid API key in X-API-Key header → 200 OK */
    @Test
    void validApiKey_returns200() throws Exception {
        mockMvc.perform(get("/api/public")
                .header("X-API-Key", "secret-api-key-123"))
               .andExpect(status().isOk());
    }

    /** An invalid API key → no authentication set → 401 */
    @Test
    void invalidApiKey_returns401() throws Exception {
        mockMvc.perform(get("/api/public")
                .header("X-API-Key", "wrong-key"))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(containsString("Unauthorized")));
    }

    // ─── Rate limiting ────────────────────────────────────────────────────────

    /**
     * Exceed MAX_REQUESTS_PER_WINDOW (5) requests → HTTP 429.
     *
     * NOTE: RateLimitFilter counts requests per IP across the test JVM.
     * Six consecutive requests with the same IP will trigger the limit.
     */
    @Test
    void rateLimitExceeded_returns429() throws Exception {
        // Exhaust the limit (first 5 may succeed)
        for (int i = 0; i < RateLimitFilter.MAX_REQUESTS_PER_WINDOW; i++) {
            mockMvc.perform(get("/api/public")
                    .header("Authorization", "Bearer " + userToken));
        }
        // The next request must be rejected
        mockMvc.perform(get("/api/public")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isTooManyRequests())
               .andExpect(content().string(containsString("Too Many Requests")));
    }

    // ─── Happy path ───────────────────────────────────────────────────────────

    /** Admin JWT token → 200 on admin endpoint */
    @Test
    void adminEndpoint_withAdminToken_returns200() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("ROLE_ADMIN")));
    }
}
