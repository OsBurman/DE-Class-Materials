package com.graphql.bookstore;

import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Batch controller — resolves Book.author for ALL books in a single call.
 * This is the AFTER state that solves the N+1 problem.
 */
@Controller
public class BatchBookController {

    private static final List<Author> AUTHORS = List.of(
            new Author("a1", "Robert C. Martin", "American"),
            new Author("a2", "Joshua Bloch", "American")
    );

    private static final List<Book> BOOKS = List.of(
            new Book("b1", "Clean Code",     "Programming", 2008, "a1"),
            new Book("b2", "Effective Java", "Programming", 2018, "a2"),
            new Book("b3", "The Clean Coder","Programming", 2011, "a1")
    );

    private static final Map<String, Author> AUTHOR_MAP = Map.of(
            "a1", AUTHORS.get(0),
            "a2", AUTHORS.get(1)
    );

    // TODO 3a: Annotate with @QueryMapping — return BOOKS
    public List<Book> books() {
        // TODO 3a: return BOOKS;
        return null;
    }

    // TODO 3b: Annotate with @BatchMapping — Spring will call this method ONCE
    //          with the full list of books. Print the log line, then return
    //          a List<Author> in the SAME ORDER as the input books list.
    //
    //          @BatchMapping
    //          public List<Author> author(List<Book> books) {
    //              System.out.println("[BATCH] Loading " + books.size() + " authors in one call");
    //              return books.stream().map(b -> AUTHOR_MAP.get(b.authorId())).toList();
    //          }
    public List<Author> author(List<Book> books) {
        // TODO 3b: print log line and return authors in same order as input books
        return null;
    }
}
