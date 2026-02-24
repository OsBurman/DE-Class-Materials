package com.security.csrfauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for Exercise 05 – CSRF Protection and URL Authorization Rules.
 *
 * Notice:
 *  • REST chain tests use httpBasic() and never send a CSRF token –
 *    because CSRF is disabled for the REST chain.
 *  • Form chain tests use user() (simulates a logged-in session) and
 *    require csrf() for state-changing requests.
 *  • Omitting csrf() on a form POST returns 403 Forbidden.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CsrfAndAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    // ── REST chain tests ────────────────────────────────────────────────────

    /** Public REST endpoint – no auth, no CSRF token needed. */
    @Test
    void restPublicGet_noAuth_returns200() throws Exception {
        mockMvc.perform(get("/public/books"))
               .andExpect(status().isOk());
    }

    /** Authenticated REST GET – valid credentials required. */
    @Test
    void restSecuredGet_userAuth_returns200() throws Exception {
        mockMvc.perform(get("/books")
                .with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }

    /** REST POST without credentials → 401. */
    @Test
    void restPost_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/books")
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isUnauthorized());
    }

    /**
     * REST POST with ADMIN – no CSRF token sent – still succeeds (CSRF disabled).
     * This is the key contrast with the form chain test below.
     */
    @Test
    void restPost_adminAuth_noCsrf_returns201() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isCreated());
    }

    /** REST POST with USER role → 403 (ADMIN only). */
    @Test
    void restPost_userRole_returns403() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isForbidden());
    }

    // ── Form chain tests ────────────────────────────────────────────────────

    /** Form GET – any authenticated user (editor or viewer). */
    @Test
    void formGet_viewerAuth_returns200() throws Exception {
        mockMvc.perform(get("/form/dashboard")
                .with(user("viewer").roles("VIEWER")))
               .andExpect(status().isOk());
    }

    /**
     * Form POST WITH a valid CSRF token + EDITOR role → 200.
     * csrf() injects a matching token automatically.
     */
    @Test
    void formPost_editorWithCsrf_returns200() throws Exception {
        mockMvc.perform(post("/form/submit")
                .with(user("editor").roles("EDITOR"))
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello"))
               .andExpect(status().isOk());
    }

    /**
     * Form POST WITHOUT a CSRF token → 403 Forbidden.
     * Even though the user is authenticated, the missing token blocks the request.
     */
    @Test
    void formPost_editorWithoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/form/submit")
                .with(user("editor").roles("EDITOR"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello"))
               .andExpect(status().isForbidden());
    }

    /** Form POST with VIEWER role (even with CSRF token) → 403 (EDITOR only). */
    @Test
    void formPost_viewerWithCsrf_returns403() throws Exception {
        mockMvc.perform(post("/form/submit")
                .with(user("viewer").roles("VIEWER"))
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello"))
               .andExpect(status().isForbidden());
    }
}
