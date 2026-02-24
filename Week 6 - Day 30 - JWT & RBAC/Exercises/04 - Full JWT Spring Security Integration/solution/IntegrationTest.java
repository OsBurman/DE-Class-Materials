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
 * Exercise 04 – Full JWT integration tests  (SOLUTION)
 */
@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"password\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"wrong\"}"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getBooks_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/books"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getBooks_userToken_returns200() throws Exception {
        String token = loginAndGetToken("user", "password");
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + token))
               .andExpect(status().isOk());
    }

    @Test
    void postBook_userToken_returns403() throws Exception {
        String token = loginAndGetToken("user", "password");
        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isForbidden());
    }

    @Test
    void postBook_adminToken_returns201() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Spring Security in Action"))
               .andExpect(status().isCreated());
    }

    @Test
    void loginThenGetBooks_end_to_end() throws Exception {
        String token = loginAndGetToken("user", "password");
        assertNotNull(token, "Token from login must not be null");

        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + token))
               .andExpect(status().isOk());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    String loginAndGetToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
               .andReturn();

        String json = result.getResponse().getContentAsString();
        Map<String, String> map = objectMapper.readValue(json, Map.class);
        return map.get("token");
    }
}
