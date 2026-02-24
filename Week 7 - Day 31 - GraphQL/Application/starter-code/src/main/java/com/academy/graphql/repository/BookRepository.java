package com.academy.graphql.repository;

import com.academy.graphql.model.Author;
import com.academy.graphql.model.Book;
import com.academy.graphql.model.Review;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory data store. This class is COMPLETE.
 */
@Repository
public class BookRepository {

    private final Map<String, Author> authors = new ConcurrentHashMap<>();
    private final Map<String, Book>   books   = new ConcurrentHashMap<>();
    private final AtomicLong authorIdSeq = new AtomicLong(10);
    private final AtomicLong bookIdSeq   = new AtomicLong(10);
    private final AtomicLong reviewIdSeq = new AtomicLong(100);

    public BookRepository() {
        // Seed data
        Author tolkien = Author.builder().id("1").name("J.R.R. Tolkien")
                .bio("English author and professor.").build();
        Author orwell  = Author.builder().id("2").name("George Orwell")
                .bio("English novelist and essayist.").build();
        Author le_guin = Author.builder().id("3").name("Ursula K. Le Guin")
                .bio("American author of speculative fiction.").build();
        authors.put("1", tolkien);
        authors.put("2", orwell);
        authors.put("3", le_guin);

        Book lotr = Book.builder().id("1").title("The Fellowship of the Ring")
                .genre("Fantasy").publishedYear(1954).authorId("1").build();
        lotr.getReviews().add(Review.builder().id("101").rating(5).comment("A masterpiece").reviewer("Alice").build());

        Book hobbit = Book.builder().id("2").title("The Hobbit")
                .genre("Fantasy").publishedYear(1937).authorId("1").build();

        Book nineteen84 = Book.builder().id("3").title("1984")
                .genre("Dystopian").publishedYear(1949).authorId("2").build();
        nineteen84.getReviews().add(Review.builder().id("102").rating(5).comment("Chilling and essential").reviewer("Bob").build());

        Book lhod = Book.builder().id("4").title("The Left Hand of Darkness")
                .genre("Science Fiction").publishedYear(1969).authorId("3").build();

        books.put("1", lotr);
        books.put("2", hobbit);
        books.put("3", nineteen84);
        books.put("4", lhod);
    }

    public List<Author> findAllAuthors()             { return new ArrayList<>(authors.values()); }
    public Optional<Author> findAuthorById(String id){ return Optional.ofNullable(authors.get(id)); }

    public List<Book>   findAllBooks()               { return new ArrayList<>(books.values()); }
    public Optional<Book> findBookById(String id)    { return Optional.ofNullable(books.get(id)); }
    public List<Book> findBooksByAuthorId(String id) {
        return books.values().stream().filter(b -> b.getAuthorId().equals(id)).toList();
    }
    public List<Book> searchByTitle(String fragment) {
        String lower = fragment.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower))
                .toList();
    }

    public Book saveBook(Book book) {
        if (book.getId() == null) book = Book.builder()
                .id(String.valueOf(bookIdSeq.getAndIncrement()))
                .title(book.getTitle()).genre(book.getGenre())
                .publishedYear(book.getPublishedYear()).authorId(book.getAuthorId())
                .reviews(book.getReviews()).build();
        books.put(book.getId(), book);
        return book;
    }

    public boolean deleteBook(String id) {
        return books.remove(id) != null;
    }

    public Review addReview(String bookId, Review review) {
        Book book = books.get(bookId);
        if (book == null) throw new IllegalArgumentException("Book not found: " + bookId);
        Review saved = Review.builder()
                .id(String.valueOf(reviewIdSeq.getAndIncrement()))
                .rating(review.getRating()).comment(review.getComment())
                .reviewer(review.getReviewer()).build();
        book.getReviews().add(saved);
        return saved;
    }
}
