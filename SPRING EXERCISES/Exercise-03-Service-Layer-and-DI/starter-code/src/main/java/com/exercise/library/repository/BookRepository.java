package com.exercise.library.repository;

import com.exercise.library.model.Book;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, Book> store = new HashMap<>();

    public BookRepository() {
        save(new Book(null, "Clean Code", "Robert C. Martin", "978-0132350884", "Programming", true, LocalDate.now().minusYears(2)));
        save(new Book(null, "The Pragmatic Programmer", "David Thomas", "978-0201616224", "Programming", false, LocalDate.now().minusYears(1)));
        save(new Book(null, "Design Patterns", "Gang of Four", "978-0201633610", "Programming", true, LocalDate.now().minusMonths(6)));
        save(new Book(null, "Spring in Action", "Craig Walls", "978-1617294945", "Programming", true, LocalDate.now().minusMonths(3)));
        save(new Book(null, "Dune", "Frank Herbert", "978-0441172719", "Science Fiction", false, LocalDate.now().minusYears(3)));
    }

    public List<Book> findAll() { return new ArrayList<>(store.values()); }
    public Optional<Book> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    public Book save(Book book) {
        if (book.getId() == null) book.setId(idCounter.getAndIncrement());
        store.put(book.getId(), book);
        return book;
    }
    public boolean deleteById(Long id) { return store.remove(id) != null; }
    public List<Book> findByAvailableTrue() {
        return store.values().stream().filter(Book::isAvailable).collect(Collectors.toList());
    }
}
