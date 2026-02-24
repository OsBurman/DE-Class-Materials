package com.library.service;

import com.library.model.Book;
import com.library.model.BookDto;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<BookDto> getBookById(int id) {
        return bookRepository.findById(id).map(this::toDto);
    }

    public BookDto createBook(BookDto dto) {
        int newId = bookRepository.findAll().size() + 1;
        Book book = new Book(newId, dto.title(), dto.genre());
        return toDto(bookRepository.save(book));
    }

    public Optional<BookDto> updateBook(int id, BookDto dto) {
        Book book = new Book(id, dto.title(), dto.genre());
        return bookRepository.update(id, book).map(this::toDto);
    }

    public boolean deleteBook(int id) {
        return bookRepository.delete(id);
    }

    private BookDto toDto(Book book) {
        return new BookDto(book.title(), book.genre());
    }
}
