package com.security;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SecurityConfig – In-Memory Authentication Tests")
class SecurityConfigTest {

    @Autowired MockMvc mockMvc;

    @Test @DisplayName("GET /public/books → 200 without credentials")
    void publicEndpointIsOpen() throws Exception {
        mockMvc.perform(get("/public/books")).andExpect(status().isOk());
    }

    @Test @DisplayName("GET /books → 401 without credentials")
    void protectedEndpointRejectsAnonymous() throws Exception {
        mockMvc.perform(get("/books")).andExpect(status().isUnauthorized());
    }

    @Test @DisplayName("GET /books → 200 with USER credentials")
    void protectedEndpointAllowsUser() throws Exception {
        mockMvc.perform(get("/books").with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }

    @Test @DisplayName("POST /books → 403 when authenticated as USER (no ADMIN role)")
    void adminEndpointForbidsUser() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("user", "password"))
                .contentType(APPLICATION_JSON)
                .content("\"New Book\""))
               .andExpect(status().isForbidden());
    }

    @Test @DisplayName("POST /books → 201 when authenticated as ADMIN")
    void adminEndpointAllowsAdmin() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("admin", "admin123"))
                .contentType(APPLICATION_JSON)
                .content("\"New Book\""))
               .andExpect(status().isCreated());
    }
}
