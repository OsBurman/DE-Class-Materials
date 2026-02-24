package com.academy.library.service;

import com.academy.library.exception.BookNotFoundException;
import com.academy.library.model.Book;
import com.academy.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CRUD service for books.
 * Used to demo @WebMvcTest + @MockBean in BookControllerTest.
 * This class is partially implemented — fill in the TODOs.
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book save(Book book) {
        // TODO Task 6: save via repository and return the saved entity
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        // TODO Task 6: delete via repository
        bookRepository.deleteById(id);
    }
}
