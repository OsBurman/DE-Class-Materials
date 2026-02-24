package com.jwt.rbac;

import com.jwt.rbac.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercise 05 – RBAC method-level security tests.
 * Implement LibraryService annotations, SecurityConfig, and getCurrentUser()
 * so all 8 tests pass.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RbacTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;
    private String librarianToken;

    @BeforeEach
    void setUp() {
        // TODO: Generate tokens for user/USER, admin/ADMIN, librarian/LIBRARIAN
        userToken      = null; // replace null
        adminToken     = null; // replace null
        librarianToken = null; // replace null
    }

    /** Authenticated user can list books. */
    @Test
    void listBooks_userToken_returns200() throws Exception {
        // TODO: GET /library/books with userToken → expect 200
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** No token → 401 at URL layer. */
    @Test
    void listBooks_noToken_returns401() throws Exception {
        // TODO: GET /library/books without header → expect 401
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** ADMIN can add books → 201. */
    @Test
    void addBook_adminToken_returns201() throws Exception {
        // TODO: POST /library/books with adminToken → expect 201
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** USER cannot add books → 403. */
    @Test
    void addBook_userToken_returns403() throws Exception {
        // TODO: POST /library/books with userToken → expect 403
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** LIBRARIAN can delete books → 200. */
    @Test
    void deleteBook_librarianToken_returns200() throws Exception {
        // TODO: DELETE /library/books/Clean%20Code with librarianToken → expect 200
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** USER cannot delete books → 403. */
    @Test
    void deleteBook_userToken_returns403() throws Exception {
        // TODO: DELETE /library/books/Clean%20Code with userToken → expect 403
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** /me returns the username from the SecurityContext. */
    @Test
    void me_userToken_returnsUsername() throws Exception {
        // TODO: GET /library/me with userToken → expect 200 and body containing "user"
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /** /me without auth → 401. */
    @Test
    void me_noToken_returns401() throws Exception {
        // TODO: GET /library/me without token → expect 401
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
