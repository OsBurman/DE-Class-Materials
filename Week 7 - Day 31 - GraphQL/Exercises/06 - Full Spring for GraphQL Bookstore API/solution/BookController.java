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

    private final List<Author> authors = new ArrayList<>(List.of(
            new Author("a1", "Robert C. Martin", "American"),
            new Author("a2", "Joshua Bloch",     "American")
    ));

    private final List<Book> books = new ArrayList<>(List.of(
            new Book("b1", "Clean Code",               "Programming", 2008, true,  4.8, "a1"),
            new Book("b2", "Effective Java",            "Programming", 2018, true,  4.9, "a2"),
            new Book("b3", "The Pragmatic Programmer",  "Programming", 1999, true,  4.7, "a1")
    ));

    // Hot multicast sink for subscriptions
    private final Sinks.Many<Book> bookSink =
            Sinks.many().multicast().onBackpressureBuffer();

    // ── Queries ───────────────────────────────────────────────────────────

    @QueryMapping
    public List<Book> books() {
        return List.copyOf(books);
    }

    @QueryMapping
    public Book book(@Argument String id) {
        return books.stream().filter(b -> b.id().equals(id)).findFirst().orElse(null);
    }

    /** Case-insensitive genre filter */
    @QueryMapping
    public List<Book> booksByGenre(@Argument String genre) {
        return books.stream()
                    .filter(b -> b.genre().equalsIgnoreCase(genre))
                    .toList();
    }

    // ── Nested resolver ───────────────────────────────────────────────────

    /**
     * @SchemaMapping resolves the "author" field on the Book type.
     * Spring for GraphQL passes the parent Book automatically.
     * This is called once per Book in the response (N+1 – acceptable for this exercise).
     */
    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book book) {
        return authors.stream()
                      .filter(a -> a.id().equals(book.authorId()))
                      .findFirst()
                      .orElse(null);
    }

    // ── Mutations ─────────────────────────────────────────────────────────

    @MutationMapping
    public Book addBook(@Argument String title,
                        @Argument String genre,
                        @Argument int year,
                        @Argument String authorId) {
        Book newBook = new Book(UUID.randomUUID().toString(), title, genre, year, true, null, authorId);
        books.add(newBook);
        bookSink.tryEmitNext(newBook);   // notify all subscription clients
        return newBook;
    }

    @MutationMapping
    public Book setAvailability(@Argument String id, @Argument boolean available) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).id().equals(id)) {
                Book updated = books.get(i).withAvailable(available);
                books.set(i, updated);
                return updated;
            }
        }
        return null;
    }

    // ── Subscription ──────────────────────────────────────────────────────

    @SubscriptionMapping
    public Flux<Book> bookAdded() {
        return bookSink.asFlux();
    }
}
