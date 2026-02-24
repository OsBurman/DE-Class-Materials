package com.jwt.rbac;

import com.jwt.rbac.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RBAC.
 *
 * Three identities are used:
 *   user      → role USER   – may only list books and view /me
 *   admin     → role ADMIN  – may list, add, and delete books
 *   librarian → role LIBRARIAN – may list and delete books, not add
 */
@SpringBootTest
@AutoConfigureMockMvc
class RbacTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;
    private String librarianToken;

    @BeforeEach
    void setUp() {
        // Generate a fresh token for each test run
        userToken      = jwtUtil.generateToken("user",      "USER");
        adminToken     = jwtUtil.generateToken("admin",     "ADMIN");
        librarianToken = jwtUtil.generateToken("librarian", "LIBRARIAN");
    }

    // ─── listAllBooks ─────────────────────────────────────────────────────────

    /** Any authenticated user may list books → 200 */
    @Test
    void listBooks_asUser_returns200() throws Exception {
        mockMvc.perform(get("/library/books")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isOk());
    }

    /** No token → 401 Unauthorized */
    @Test
    void listBooks_noToken_returns401() throws Exception {
        mockMvc.perform(get("/library/books"))
               .andExpect(status().isUnauthorized());
    }

    // ─── addBook ──────────────────────────────────────────────────────────────

    /** Admin may add a book → 201 Created */
    @Test
    void addBook_asAdmin_returns201() throws Exception {
        mockMvc.perform(post("/library/books")
                .param("title", "Spring in Action")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isCreated())
               .andExpect(content().string("Added: Spring in Action"));
    }

    /** USER cannot add books – @PreAuthorize("hasRole('ADMIN')") → 403 Forbidden */
    @Test
    void addBook_asUser_returns403() throws Exception {
        mockMvc.perform(post("/library/books")
                .param("title", "Unauthorized Book")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden());
    }

    // ─── deleteBook ───────────────────────────────────────────────────────────

    /** Librarian may delete books via @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"}) → 200 */
    @Test
    void deleteBook_asLibrarian_returns200() throws Exception {
        mockMvc.perform(delete("/library/books/Clean Code")
                .header("Authorization", "Bearer " + librarianToken))
               .andExpect(status().isOk());
    }

    /** USER cannot delete books → 403 Forbidden */
    @Test
    void deleteBook_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/library/books/Clean Code")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden());
    }

    // ─── getCurrentUser (/me) ─────────────────────────────────────────────────

    /** /me returns the authenticated username in the response body → 200 */
    @Test
    void me_asUser_returns200WithUsername() throws Exception {
        mockMvc.perform(get("/library/me")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isOk())
               .andExpect(content().string("user"));
    }

    /** /me without a token → 401 Unauthorized */
    @Test
    void me_noToken_returns401() throws Exception {
        mockMvc.perform(get("/library/me"))
               .andExpect(status().isUnauthorized());
    }
}
