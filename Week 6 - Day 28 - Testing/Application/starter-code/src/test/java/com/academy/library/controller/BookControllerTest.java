package com.academy.library.controller;

import com.academy.library.exception.BookNotFoundException;
import com.academy.library.model.Book;
import com.academy.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for BookController.
 *
 * TODO Task 7: Implement all 5 test methods.
 *
 * Key MockMvc patterns:
 *   mockMvc.perform(get("/api/books/1"))
 *          .andExpect(status().isOk())
 *          .andExpect(jsonPath("$.title").value("Effective Java"));
 *
 *   mockMvc.perform(post("/api/books")
 *          .contentType(MediaType.APPLICATION_JSON)
 *          .content(objectMapper.writeValueAsString(book)))
 *          .andExpect(status().isCreated());
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Book sampleBook = Book.builder()
            .id(1L)
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("978-0-13-468599-1")
            .available(true)
            .build();

    @Test
    void getBook_shouldReturn200_withValidId() throws Exception {
        // TODO: stub bookService.findById(1L) to return sampleBook
        //       perform GET /api/books/1
        //       andExpect: status 200, jsonPath $.title == "Effective Java"
    }

    @Test
    void getBook_shouldReturn404_whenNotFound() throws Exception {
        // TODO: stub bookService.findById(99L) to throw new BookNotFoundException(99L)
        //       perform GET /api/books/99
        //       andExpect: status 404
    }

    @Test
    void createBook_shouldReturn201_withValidBody() throws Exception {
        // TODO: stub bookService.save(any()) to return sampleBook
        //       perform POST /api/books with JSON body of sampleBook (without id)
        //       andExpect: status 201, jsonPath $.id == 1
    }

    @Test
    void createBook_shouldReturn400_withMissingTitle() throws Exception {
        // TODO: create a book with null title
        //       perform POST /api/books
        //       andExpect: status 400
        // Note: @NotBlank on Book.title needs to be present for this to work
    }

    @Test
    void deleteBook_shouldReturn204() throws Exception {
        // TODO: perform DELETE /api/books/1
        //       andExpect: status 204
        //       verify bookService.delete(1L) was called
    }
}
