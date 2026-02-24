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

@SpringBootTest
@AutoConfigureMockMvc
class CsrfAndAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    // ── REST chain ──────────────────────────────────────────────────────────

    @Test
    void restPublicGet_noAuth_returns200() throws Exception {
        mockMvc.perform(get("/public/books"))
               .andExpect(status().isOk());
    }

    @Test
    void restSecuredGet_userAuth_returns200() throws Exception {
        mockMvc.perform(get("/books")
                .with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }

    @Test
    void restPost_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/books")
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isUnauthorized());
    }

    /** CSRF token intentionally omitted – still 201 because CSRF is disabled for REST. */
    @Test
    void restPost_adminAuth_noCsrf_returns201() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isCreated());
    }

    @Test
    void restPost_userRole_returns403() throws Exception {
        mockMvc.perform(post("/books")
                .with(httpBasic("user", "password"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("New Book"))
               .andExpect(status().isForbidden());
    }

    // ── Form chain ──────────────────────────────────────────────────────────

    @Test
    void formGet_viewerAuth_returns200() throws Exception {
        mockMvc.perform(get("/form/dashboard")
                .with(user("viewer").roles("VIEWER")))
               .andExpect(status().isOk());
    }

    /** csrf() injects a valid CSRF token – request succeeds. */
    @Test
    void formPost_editorWithCsrf_returns200() throws Exception {
        mockMvc.perform(post("/form/submit")
                .with(user("editor").roles("EDITOR"))
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello"))
               .andExpect(status().isOk());
    }

    /** No csrf() → 403 even though the user is authenticated and has the right role. */
    @Test
    void formPost_editorWithoutCsrf_returns403() throws Exception {
        mockMvc.perform(post("/form/submit")
                .with(user("editor").roles("EDITOR"))
                .contentType(MediaType.TEXT_PLAIN)
                .content("hello"))
               .andExpect(status().isForbidden());
    }

    /** VIEWER cannot POST to /form/submit (EDITOR only). */
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
