package com.graphql.bookstore;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Naive controller — resolves Book.author one at a time.
 * This is the BEFORE state that demonstrates the N+1 problem.
 */
@Controller
public class BookControllerNaive {

    // In-memory seed data — 3 books across 2 authors
    private static final List<Author> AUTHORS = List.of(
            new Author("a1", "Robert C. Martin", "American"),
            new Author("a2", "Joshua Bloch", "American")
    );

    private static final List<Book> BOOKS = List.of(
            new Book("b1", "Clean Code",              "Programming", 2008, "a1"),
            new Book("b2", "Effective Java",           "Programming", 2018, "a2"),
            new Book("b3", "The Clean Coder",          "Programming", 2011, "a1")
    );

    private static final Map<String, Author> AUTHOR_MAP = Map.of(
            "a1", AUTHORS.get(0),
            "a2", AUTHORS.get(1)
    );

    // TODO 2a: Annotate this method with @QueryMapping so it maps to Query.books
    //          Return the BOOKS list
    public List<Book> books() {
        // TODO 2a: return BOOKS;
        return null;
    }

    // TODO 2b: Annotate with @SchemaMapping(typeName="Book", field="author")
    //          Look up AUTHOR_MAP.get(book.authorId()), print a log line first:
    //          System.out.println("[NAIVE] Looking up author: " + book.authorId());
    public Author author(Book book) {
        // TODO 2b: print log line then return AUTHOR_MAP.get(book.authorId());
        return null;
    }

    // TODO 2c: Annotate with @QueryMapping so it maps to Query.authorById
    //          Return AUTHOR_MAP.get(id)
    public Author authorById(@Argument String id) {
        // TODO 2c: return AUTHOR_MAP.get(id);
        return null;
    }
}
