package com.graphql.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlTest(BookController.class)
class BookstoreTest {

    @Autowired
    private GraphQlTester graphQlTester;

    // TODO 1: Assert that querying all books returns at least 3 (the preloaded seed data).
    //   Use: .path("books").entityList(Book.class).hasSizeGreaterThanOrEqualTo(3)
    @Test
    void queryAllBooks_returnsPreloadedBooks() {
        // TODO 1
    }

    // TODO 2: Query book(id: "b1") and assert the title equals "Clean Code".
    @Test
    void queryBook_byId_returnsCorrectTitle() {
        // TODO 2
    }

    // TODO 3: Query booksByGenre(genre: "Programming") and assert every returned book has genre "Programming".
    //   Hint: .entityList(Book.class).get() returns a List<Book> you can stream over.
    @Test
    void queryBooksByGenre_returnsFilteredList() {
        // TODO 3
    }

    // TODO 4: Query book(id: "b1") { title author { name } } and assert author.name is not null.
    @Test
    void nestedAuthorResolver_returnsAuthorName() {
        // TODO 4
    }

    // TODO 5: Run mutation addBook(title: "Refactoring", genre: "Programming", year: 1999, authorId: "a1")
    //         and assert the returned title and genre are correct.
    @Test
    void addBook_returnsNewBook() {
        // TODO 5
    }

    // TODO 6: Run mutation setAvailability(id: "b1", available: false) { id available }
    //         and assert available == false.
    @Test
    void setAvailability_updatesFlag() {
        // TODO 6
    }

    // TODO 7: Query books using a named fragment BookFields on Book { id title genre available }
    //         and assert the list has at least 3 items.
    //         Document: "fragment BookFields on Book { id title genre available } query { books { ...BookFields } }"
    @Test
    void usesFragment_inBooksQuery() {
        // TODO 7
    }
}
