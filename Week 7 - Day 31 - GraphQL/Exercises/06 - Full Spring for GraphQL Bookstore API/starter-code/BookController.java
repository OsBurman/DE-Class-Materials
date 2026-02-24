package com.graphql.bookstore;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class BookController {

    // ── Seed data ─────────────────────────────────────────────────────────

    // TODO 8: Pre-populate authors list with 2 authors:
    //         new Author("a1", "Robert C. Martin", "American")
    //         new Author("a2", "Joshua Bloch", "American")
    private final List<Author> authors = new ArrayList<>(List.of(
            // TODO 8
    ));

    // TODO 9: Pre-populate books list with 3 books spanning at least 2 genres.
    //         Use authorId "a1" or "a2" to link to the authors above.
    //         Example: new Book("b1","Clean Code","Programming",2008,true,4.8,"a1")
    private final List<Book> books = new ArrayList<>(List.of(
            // TODO 9
    ));

    private final Sinks.Many<Book> bookSink =
            Sinks.many().multicast().onBackpressureBuffer();

    // ── Queries ───────────────────────────────────────────────────────────

    // TODO 10: Annotate with @QueryMapping and return all books
    public List<Book> books() {
        return null; // TODO 10
    }

    // TODO 11: Annotate with @QueryMapping. Use @Argument on id.
    //          Return the book whose id matches, or null.
    public Book book(String id) {
        return null; // TODO 11
    }

    // TODO 12: Annotate with @QueryMapping. Use @Argument on genre.
    //          Return all books whose genre equals the argument (case-insensitive).
    public List<Book> booksByGenre(String genre) {
        return null; // TODO 12
    }

    // ── Nested resolver ───────────────────────────────────────────────────

    /**
     * TODO 13: Annotate with @SchemaMapping(typeName = "Book", field = "author")
     *          Spring for GraphQL passes the parent Book as the parameter.
     *          Find and return the Author whose id equals book.authorId().
     */
    public Author author(Book book) {
        return null; // TODO 13
    }

    // ── Mutations ─────────────────────────────────────────────────────────

    /**
     * TODO 14: Annotate with @MutationMapping. Use @Argument on each parameter.
     *          Create a new Book with UUID id, rating = null.
     *          Add to list, emit to sink, return the new book.
     */
    public Book addBook(String title, String genre, int year, String authorId) {
        return null; // TODO 14
    }

    /**
     * TODO 15: Annotate with @MutationMapping. Use @Argument on id and available.
     *          Find the book by id. If found, replace it with a copy where available = the new value.
     *          Return the updated book or null if not found.
     *          Hint: records are immutable – construct a new Book with the updated value.
     */
    public Book setAvailability(String id, boolean available) {
        return null; // TODO 15
    }

    // ── Subscription ──────────────────────────────────────────────────────

    // TODO 16: Annotate with @SubscriptionMapping and return bookSink.asFlux()
    public Flux<Book> bookAdded() {
        return null; // TODO 16
    }
}
