package com.graphql.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlTest(BookController.class)
class BookstoreTest {

    @Autowired
    private GraphQlTester graphQlTester;

    /** Seed data contains 3 books — all should be returned */
    @Test
    void queryAllBooks_returnsPreloadedBooks() {
        graphQlTester
                .document("{ books { id title genre } }")
                .execute()
                .path("books").entityList(Book.class)
                .hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void queryBook_byId_returnsCorrectTitle() {
        graphQlTester
                .document("{ book(id: \"b1\") { id title } }")
                .execute()
                .path("book.title").entity(String.class).isEqualTo("Clean Code");
    }

    /** Every book returned for "Programming" must actually have that genre */
    @Test
    void queryBooksByGenre_returnsFilteredList() {
        List<Book> result = graphQlTester
                .document("{ booksByGenre(genre: \"Programming\") { id title genre } }")
                .execute()
                .path("booksByGenre").entityList(Book.class).get();

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(b -> "Programming".equalsIgnoreCase(b.genre()));
    }

    /** @SchemaMapping must resolve the Author nested inside Book */
    @Test
    void nestedAuthorResolver_returnsAuthorName() {
        graphQlTester
                .document("{ book(id: \"b1\") { title author { name } } }")
                .execute()
                .path("book.author.name").entity(String.class)
                .satisfies(name -> assertThat(name).isNotBlank());
    }

    @Test
    void addBook_returnsNewBook() {
        graphQlTester
                .document("""
                    mutation {
                      addBook(title: "Refactoring", genre: "Programming", year: 1999, authorId: "a1") {
                        id title genre available
                      }
                    }
                    """)
                .execute()
                .path("addBook.title").entity(String.class).isEqualTo("Refactoring")
                .path("addBook.genre").entity(String.class).isEqualTo("Programming")
                .path("addBook.available").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    void setAvailability_updatesFlag() {
        graphQlTester
                .document("mutation { setAvailability(id: \"b1\", available: false) { id available } }")
                .execute()
                .path("setAvailability.available").entity(Boolean.class).isEqualTo(false);
    }

    /** Named fragment avoids duplicating the field list */
    @Test
    void usesFragment_inBooksQuery() {
        String doc = """
                fragment BookFields on Book { id title genre available }
                query { books { ...BookFields } }
                """;

        graphQlTester
                .document(doc)
                .execute()
                .path("books").entityList(Book.class)
                .hasSizeGreaterThanOrEqualTo(3);
    }
}
