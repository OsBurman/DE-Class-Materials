package com.graphql.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

// TODO 4: This file contains two test classes (one per controller).
//         Complete each test so it:
//         1. Sends the query: { books { title author { name } } }
//         2. Asserts that books[0].author.name equals "Robert C. Martin"

// NOTE: In a real project each test class lives in its own file.
//       They are combined here for convenience.

@GraphQlTest(BookControllerNaive.class)
class NaiveResolverTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void naiveResolver_triggersNPlusOneLog() {
        // TODO 4a: Execute the { books { title author { name } } } query
        //          and assert that path("books[0].author.name").entity(String.class).isEqualTo("Robert C. Martin")
        //
        // Observe in the test output that "[NAIVE] Looking up author: ..." is printed 3 times.
    }
}

@GraphQlTest(BatchBookController.class)
class BatchResolverTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void batchResolver_loadsAllAuthorsOnce() {
        // TODO 4b: Execute the same { books { title author { name } } } query
        //          and assert books[0].author.name equals "Robert C. Martin"
        //
        // Observe in the test output that "[BATCH] Loading 3 authors in one call" is printed once.
    }
}
