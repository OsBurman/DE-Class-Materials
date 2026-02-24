package com.jwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercise 04 – Full JWT integration tests.
 * Complete AuthController and SecurityConfig so all 7 tests pass.
 */
@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // ── Authentication tests ─────────────────────────────────────────────────

    /** Valid credentials → 200 with a token field. */
    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        // TODO: POST /auth/login with body {"username":"user","password":"password"}
        //       Expect status 200 and jsonPath("$.token").exists()
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Wrong password → 401. */
    @Test
    void login_invalidCredentials_returns401() throws Exception {
        // TODO: POST /auth/login with body {"username":"user","password":"wrong"}
        //       Expect status 401.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // ── Protected endpoint tests ─────────────────────────────────────────────

    /** No token → 401. */
    @Test
    void getBooks_noToken_returns401() throws Exception {
        // TODO: GET /api/books with no Authorization header → expect 401.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Valid USER token → 200. */
    @Test
    void getBooks_userToken_returns200() throws Exception {
        // TODO: Log in as user, extract token, GET /api/books with Bearer header → expect 200.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** USER tries to POST → 403. */
    @Test
    void postBook_userToken_returns403() throws Exception {
        // TODO: Log in as user, POST /api/books → expect 403.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** ADMIN can POST → 201. */
    @Test
    void postBook_adminToken_returns201() throws Exception {
        // TODO: Log in as admin, POST /api/books with a title body → expect 201.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Token obtained from /auth/login works on /api/books. */
    @Test
    void loginThenGetBooks_end_to_end() throws Exception {
        // TODO: Perform full login → extract token from JSON response → use it on GET /api/books → expect 200.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /**
     * Helper: post credentials and return the token string.
     * TODO: implement using mockMvc.perform(post("/auth/login")...) and parse the JSON response.
     */
    String loginAndGetToken(String username, String password) throws Exception {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
