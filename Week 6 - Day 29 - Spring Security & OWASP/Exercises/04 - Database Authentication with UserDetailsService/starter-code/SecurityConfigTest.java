package com.security.dbauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests that verify the database-backed security configuration.
 *
 * These tests load the full Spring context (including H2 + DataInitializer),
 * so the "user" and "admin" accounts seeded in DataInitializer are available.
 *
 * Run all five tests – they should pass once you complete
 * CustomUserDetailsService and SecurityConfig.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /** /public/books is open – no credentials needed. */
    @Test
    void publicEndpoint_noAuth_returns200() throws Exception {
        mockMvc.perform(get("/public/books"))
               .andExpect(status().isOk());
    }

    /** GET /books without credentials → 401 Unauthorized. */
    @Test
    void securedEndpoint_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/books"))
               .andExpect(status().isUnauthorized());
    }

    /** GET /books with valid USER credentials → 200 OK. */
    @Test
    void securedEndpoint_userAuth_returns200() throws Exception {
        mockMvc.perform(get("/books")
                .with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }

    /** POST /books with USER credentials → 403 Forbidden (ADMIN only). */
    @Test
    void adminEndpoint_userAuth_returns403() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isForbidden());
    }

    /** POST /books with ADMIN credentials → 201 Created. */
    @Test
    void adminEndpoint_adminAuth_returns201() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isCreated());
    }
}
