package com.graphql.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

/**
 * Tests the naive N+1 resolver.
 * When running, observe the console output — you will see 3 "[NAIVE] Looking up author:" lines.
 */
@GraphQlTest(BookControllerNaive.class)
class NaiveResolverTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void naiveResolver_triggersNPlusOneLog() {
        // The query asks for all books with their nested author.
        // Spring will call BookControllerNaive.author(Book) once per book = N+1 calls.
        graphQlTester
                .document("{ books { title author { name } } }")
                .execute()
                .path("books[0].author.name")
                .entity(String.class)
                .isEqualTo("Robert C. Martin");
        // Console will show:
        // [NAIVE] Looking up author: a1
        // [NAIVE] Looking up author: a2
        // [NAIVE] Looking up author: a1
    }
}

/**
 * Tests the batch resolver.
 * When running, observe the console output — you will see exactly ONE "[BATCH] Loading 3 authors" line.
 */
@GraphQlTest(BatchBookController.class)
class BatchResolverTest {

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void batchResolver_loadsAllAuthorsOnce() {
        // Same query — but this time @BatchMapping collects all 3 books first,
        // then calls BatchBookController.author(List<Book>) exactly once.
        graphQlTester
                .document("{ books { title author { name } } }")
                .execute()
                .path("books[0].author.name")
                .entity(String.class)
                .isEqualTo("Robert C. Martin");
        // Console will show:
        // [BATCH] Loading 3 authors in one call
    }
}
