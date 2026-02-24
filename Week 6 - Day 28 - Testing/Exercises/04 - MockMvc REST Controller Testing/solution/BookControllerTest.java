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

@WebMvcTest(BookController.class)   // loads only the web layer — fast!
@DisplayName("BookController MockMvc Tests")
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean                       // replaces real BookService in the Spring context
    BookService bookService;

    // ── Task 1 — GET all books ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /books returns 200 and array of 2 books")
    void testGetAllBooks() throws Exception {
        Book b1 = new Book(1L, "Clean Code", "Robert C. Martin", "Tech");
        Book b2 = new Book(2L, "Effective Java", "Joshua Bloch", "Tech");

        when(bookService.getAllBooks()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/books"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    // ── Task 2 — GET by id (found) ────────────────────────────────────────

    @Test
    @DisplayName("GET /books/1 returns 200 and correct book")
    void testGetBookById() throws Exception {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "Tech");

        when(bookService.findById(1L)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Clean Code"))
               .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }

    // ── Task 3 — GET by id (not found) ────────────────────────────────────

    @Test
    @DisplayName("GET /books/99 returns 404 when book does not exist")
    void testGetBookByIdNotFound() throws Exception {
        when(bookService.findById(99L)).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/books/99"))
               .andExpect(status().isNotFound());
    }

    // ── Task 4 — POST create book ─────────────────────────────────────────

    @Test
    @DisplayName("POST /books returns 201 and persisted book")
    void testCreateBook() throws Exception {
        String json = """
                {
                  "title": "The Pragmatic Programmer",
                  "author": "Hunt & Thomas",
                  "genre": "Tech"
                }
                """;

        Book saved = new Book(1L, "The Pragmatic Programmer", "Hunt & Thomas", "Tech");
        when(bookService.saveBook(any(Book.class))).thenReturn(saved);

        mockMvc.perform(post("/books")
                       .contentType(APPLICATION_JSON)
                       .content(json))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.title").value("The Pragmatic Programmer"));
    }

    // ── Task 5 — DELETE book ──────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /books/1 returns 204 No Content")
    void testDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/1"))
               .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }
}
