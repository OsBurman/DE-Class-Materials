package com.testing;

import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    Book findById(Long id);
    Book saveBook(Book book);
    void deleteBook(Long id);
}
