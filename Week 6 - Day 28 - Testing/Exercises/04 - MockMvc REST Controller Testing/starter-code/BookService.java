package com.testing;

import java.util.List;

/**
 * Service interface used by the controller.
 * Do NOT modify this class.
 */
public interface BookService {
    List<Book> getAllBooks();
    Book findById(Long id);
    Book saveBook(Book book);
    void deleteBook(Long id);
}
