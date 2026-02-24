package com.graphql.bookstore;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Naive resolver — demonstrates the N+1 problem.
 * Each call to author(Book book) is triggered individually for every book in the list.
 * With 3 books this results in 3 separate author lookups (1 + 3 = N+1).
 */
@Controller
public class BookControllerNaive {

    private static final List<Author> AUTHORS = List.of(
            new Author("a1", "Robert C. Martin", "American"),
            new Author("a2", "Joshua Bloch", "American")
    );

    private static final List<Book> BOOKS = List.of(
            new Book("b1", "Clean Code",      "Programming", 2008, "a1"),
            new Book("b2", "Effective Java",  "Programming", 2018, "a2"),
            new Book("b3", "The Clean Coder", "Programming", 2011, "a1")
    );

    private static final Map<String, Author> AUTHOR_MAP = Map.of(
            "a1", AUTHORS.get(0),
            "a2", AUTHORS.get(1)
    );

    @QueryMapping
    public List<Book> books() {
        return BOOKS;
    }

    // @SchemaMapping is called once PER book — this is the N+1 pattern.
    // For 3 books, this method is called 3 times, producing 3 log lines.
    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book book) {
        System.out.println("[NAIVE] Looking up author: " + book.authorId());
        return AUTHOR_MAP.get(book.authorId());
    }

    @QueryMapping
    public Author authorById(@Argument String id) {
        return AUTHOR_MAP.get(id);
    }
}
