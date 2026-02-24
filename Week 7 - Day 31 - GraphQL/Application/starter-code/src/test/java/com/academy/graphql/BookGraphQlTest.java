package com.academy.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

/**
 * GraphQL integration tests.
 *
 * TODO Task 4: Implement each test.
 */
@SpringBootTest
@AutoConfigureHttpGraphQlTester
class BookGraphQlTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    // TODO Task 4a: Query all books — assert at least 4 results
    @Test
    void booksQuery_shouldReturnAllBooks() {
        // TODO
        // graphQlTester.document("{ books { id title genre } }")
        //     .execute()
        //     .path("books")
        //     .entityList(Object.class)
        //     .hasSizeGreaterThan(3);
    }

    // TODO Task 4b: Query a book by ID — assert title matches
    @Test
    void bookByIdQuery_shouldReturnCorrectBook() {
        // TODO
    }

    // TODO Task 4c: Nested resolver — book query returns author name
    @Test
    void bookQuery_shouldResolveAuthorField() {
        // TODO
        // graphQlTester.document("{ book(id: \"1\") { title author { name } } }")
        //     .execute()
        //     .path("book.author.name")
        //     .entity(String.class)
        //     .isEqualTo("J.R.R. Tolkien");
    }

    // TODO Task 4d: addBook mutation — assert new book has correct title
    @Test
    void addBookMutation_shouldCreateNewBook() {
        // TODO
    }

    // TODO Task 4e: addReview mutation — assert averageRating is calculated
    @Test
    void addReviewMutation_shouldUpdateAverageRating() {
        // TODO
    }
}
