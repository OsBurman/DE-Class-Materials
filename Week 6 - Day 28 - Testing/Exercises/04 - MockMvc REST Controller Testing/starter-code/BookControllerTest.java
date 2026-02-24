package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Exercise 04 – MockMvc REST Controller Testing
 *
 * TODO: Complete each test method as described in the comments.
 *       Do NOT modify BookController or BookService.
 */
// TODO: Add @WebMvcTest(BookController.class) — loads only the web layer
@DisplayName("BookController MockMvc Tests")
class BookControllerTest {

    // TODO: @Autowired MockMvc mockMvc;

    // TODO: @MockBean BookService bookService;

    // ── Task 1 — GET all books ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /books returns 200 and list of books")
    void testGetAllBooks() throws Exception {
        // TODO: Create two Book stubs and stub bookService.getAllBooks()

        // TODO: mockMvc.perform(get("/books"))
        //             .andExpect(status().isOk())
        //             .andExpect(jsonPath("$.length()").value(2));
    }

    // ── Task 2 — GET by id (found) ────────────────────────────────────────

    @Test
    @DisplayName("GET /books/1 returns 200 and the correct book")
    void testGetBookById() throws Exception {
        // TODO: Stub bookService.findById(1L) to return a book with title "Clean Code"

        // TODO: perform(get("/books/1"))
        //          .andExpect(status().isOk())
        //          .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    // ── Task 3 — GET by id (not found) ────────────────────────────────────

    @Test
    @DisplayName("GET /books/99 returns 404 when not found")
    void testGetBookByIdNotFound() throws Exception {
        // TODO: Stub bookService.findById(99L) to throw RuntimeException

        // TODO: perform(get("/books/99"))
        //          .andExpect(status().isNotFound());
    }

    // ── Task 4 — POST create book ─────────────────────────────────────────

    @Test
    @DisplayName("POST /books returns 201 and the created book")
    void testCreateBook() throws Exception {
        // TODO: Build a JSON string for a new book (no id)
        String json = ""; // replace with actual JSON

        // TODO: Stub bookService.saveBook(any(Book.class)) to return a book with id = 1L

        // TODO: perform(post("/books")
        //          .contentType(APPLICATION_JSON)
        //          .content(json))
        //          .andExpect(status().isCreated())
        //          .andExpect(jsonPath("$.id").value(1));
    }

    // ── Task 5 — DELETE book ──────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /books/1 returns 204 No Content")
    void testDeleteBook() throws Exception {
        // TODO: perform(delete("/books/1"))
        //          .andExpect(status().isNoContent());

        // TODO: verify(bookService, times(1)).deleteBook(1L);
    }
}
