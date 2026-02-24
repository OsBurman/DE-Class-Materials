package com.jwt.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercise 03 – JWT filter integration tests  (SOLUTION)
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userToken  = jwtUtil.generateToken("user",  "USER");
        adminToken = jwtUtil.generateToken("admin", "ADMIN");
    }

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/hello"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer not.a.token"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void userToken_hello_returns200() throws Exception {
        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isOk());
    }

    @Test
    void adminToken_admin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk());
    }

    @Test
    void userToken_admin_returns403() throws Exception {
        // USER role does not have ADMIN – authorization rules return 403
        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden());
    }
}
