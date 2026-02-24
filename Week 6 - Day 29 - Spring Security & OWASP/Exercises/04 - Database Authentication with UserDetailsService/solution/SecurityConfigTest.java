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
 * Integration tests for database-backed Spring Security configuration.
 *
 * The full Spring context is loaded, including H2 and DataInitializer, so
 * the seeded accounts (user / admin) are available for each test.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoint_noAuth_returns200() throws Exception {
        mockMvc.perform(get("/public/books"))
               .andExpect(status().isOk());
    }

    @Test
    void securedEndpoint_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/books"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void securedEndpoint_userAuth_returns200() throws Exception {
        mockMvc.perform(get("/books")
                .with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }

    @Test
    void adminEndpoint_userAuth_returns403() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_adminAuth_returns201() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isCreated());
    }
}
