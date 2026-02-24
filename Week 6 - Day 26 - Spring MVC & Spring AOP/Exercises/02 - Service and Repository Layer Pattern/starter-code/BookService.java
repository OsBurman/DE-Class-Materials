package com.library.service;

import com.library.model.Book;
import com.library.model.BookDto;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// TODO: Add @Service annotation
public class BookService {

    private final BookRepository bookRepository;

    // TODO: Add constructor injection — accept BookRepository as a parameter and assign it
    public BookService() {
        this.bookRepository = null;
    }

    // TODO: Implement getAllBooks()
    //       Fetch all books from the repository, map each to BookDto using toDto(), return the list
    public List<BookDto> getAllBooks() {
        return null;
    }

    // TODO: Implement getBookById(int id)
    //       Delegate to bookRepository.findById(id), map the result to BookDto if present
    public Optional<BookDto> getBookById(int id) {
        return null;
    }

    // TODO: Implement createBook(BookDto dto)
    //       Generate a new id (use bookRepository.findAll().size() + 1)
    //       Build a new Book from the dto and the generated id
    //       Save via bookRepository.save(...) and return toDto(savedBook)
    public BookDto createBook(BookDto dto) {
        return null;
    }

    // TODO: Implement updateBook(int id, BookDto dto)
    //       Build a Book from the dto (keep the existing id)
    //       Delegate to bookRepository.update(id, book), map result to BookDto
    public Optional<BookDto> updateBook(int id, BookDto dto) {
        return null;
    }

    // TODO: Implement deleteBook(int id)
    //       Delegate to bookRepository.delete(id) and return the boolean result
    public boolean deleteBook(int id) {
        return false;
    }

    // Helper — converts a Book entity to a BookDto
    // TODO: Implement toDto — return a new BookDto with book.title() and book.genre()
    private BookDto toDto(Book book) {
        return null;
    }
}
