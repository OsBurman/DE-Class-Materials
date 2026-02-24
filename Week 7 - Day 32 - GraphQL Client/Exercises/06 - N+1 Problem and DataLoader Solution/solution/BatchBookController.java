package com.graphql.bookstore;

import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Batch resolver — solves the N+1 problem with @BatchMapping.
 * Spring collects ALL books needing an author in a single execution tick,
 * then calls author(List<Book>) exactly ONCE with the complete list.
 */
@Controller
public class BatchBookController {

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

    // @BatchMapping replaces @SchemaMapping for the author field.
    // Spring passes the entire list of books that need their author resolved.
    // The return list MUST be in the same order as the input list — Spring pairs by index.
    @BatchMapping
    public List<Author> author(List<Book> books) {
        System.out.println("[BATCH] Loading " + books.size() + " authors in one call");
        // In a real app this would be a single SELECT ... WHERE id IN (...) query.
        // Here we just map each book's authorId to the in-memory AUTHOR_MAP.
        return books.stream()
                .map(b -> AUTHOR_MAP.get(b.authorId()))
                .toList();
    }
}
