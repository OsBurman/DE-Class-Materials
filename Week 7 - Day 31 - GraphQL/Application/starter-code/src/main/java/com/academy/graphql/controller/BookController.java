package com.academy.graphql.controller;

import com.academy.graphql.model.Author;
import com.academy.graphql.model.Book;
import com.academy.graphql.model.InputTypes.*;
import com.academy.graphql.model.Review;
import com.academy.graphql.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

/**
 * GraphQL Query and Mutation resolvers.
 *
 * TODO Task 2: Implement all query and mutation methods.
 * TODO Task 3: Implement the SchemaMapping for Book.author.
 */
@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookRepository repository;

    // ---- Queries ----

    // TODO Task 2a: Return all books
    @QueryMapping
    public List<Book> books() {
        return repository.findAllBooks();
    }

    // TODO Task 2b: Return a single book by ID
    @QueryMapping
    public Book book(@Argument String id) {
        // TODO
        return null;
    }

    // TODO Task 2c: Return all authors
    @QueryMapping
    public List<Author> authors() {
        // TODO
        return null;
    }

    // TODO Task 2d: Return single author
    @QueryMapping
    public Author author(@Argument String id) {
        // TODO
        return null;
    }

    // TODO Task 2e: Search books by title
    @QueryMapping
    public List<Book> searchBooks(@Argument String titleContains) {
        // TODO
        return null;
    }

    // ---- Mutations ----

    // TODO Task 2f: Add a new book
    @MutationMapping
    public Book addBook(@Argument AddBookInput input) {
        // TODO: verify author exists, create Book, call repository.saveBook()
        return null;
    }

    // TODO Task 2g: Update a book
    @MutationMapping
    public Book updateBook(@Argument String id, @Argument UpdateBookInput input) {
        // TODO
        return null;
    }

    // TODO Task 2h: Delete a book
    @MutationMapping
    public boolean deleteBook(@Argument String id) {
        // TODO
        return false;
    }

    // TODO Task 2i: Add a review
    @MutationMapping
    public Review addReview(@Argument String bookId, @Argument AddReviewInput input) {
        // TODO: validate rating 1-5, call repository.addReview()
        return null;
    }

    // ---- Sub-field resolvers ----

    // TODO Task 3a: Resolve Book.author (maps the authorId to a full Author object)
    // @SchemaMapping(typeName = "Book", field = "author")
    // public Author authorForBook(Book book) { ... }

    // TODO Task 3b: Resolve Author.books
    // @SchemaMapping(typeName = "Author", field = "books")
    // public List<Book> booksForAuthor(Author author) { ... }
}
