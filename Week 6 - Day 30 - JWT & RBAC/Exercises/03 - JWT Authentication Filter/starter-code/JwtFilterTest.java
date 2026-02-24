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
 * Exercise 03 – JWT filter integration tests.
 *
 * Complete JwtAuthenticationFilter and SecurityConfig so all five tests pass.
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
        // TODO: Generate a token for username="user", role="USER" and assign to userToken.
        // TODO: Generate a token for username="admin", role="ADMIN" and assign to adminToken.
        userToken  = null; // replace null
        adminToken = null; // replace null
    }

    /** No Authorization header → 401 Unauthorized. */
    @Test
    void noToken_returns401() throws Exception {
        // TODO: Perform GET /api/hello without any header and expect 401.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Bad token → 401 Unauthorized. */
    @Test
    void invalidToken_returns401() throws Exception {
        // TODO: Perform GET /api/hello with header "Authorization: Bearer not.a.token" and expect 401.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Valid USER token on /api/hello → 200 OK. */
    @Test
    void userToken_hello_returns200() throws Exception {
        // TODO: Perform GET /api/hello with "Authorization: Bearer " + userToken and expect 200.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** Valid ADMIN token on /api/admin → 200 OK. */
    @Test
    void adminToken_admin_returns200() throws Exception {
        // TODO: Perform GET /api/admin with adminToken header and expect 200.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** USER token on /api/admin → 403 Forbidden. */
    @Test
    void userToken_admin_returns403() throws Exception {
        // TODO: Perform GET /api/admin with userToken header and expect 403.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
