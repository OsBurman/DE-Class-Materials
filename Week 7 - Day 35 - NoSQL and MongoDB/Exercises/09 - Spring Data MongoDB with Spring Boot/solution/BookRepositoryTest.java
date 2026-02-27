package com.mongodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// @DataMongoTest — lightweight test slice that loads only MongoDB/repository beans
// The Flapdoodle embedded MongoDB is auto-configured because it's on the test classpath
@DataMongoTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Clear state between tests — embedded MongoDB persists across @BeforeEach calls
        bookRepository.deleteAll();

        // Seed three books; passing null for id lets MongoDB assign an ObjectId
        bookRepository.saveAll(List.of(
            new Book(null, "The Pragmatic Programmer", "David Thomas",  "Technology", 1999, 39.99),
            new Book(null, "Sapiens",                  "Yuval Harari",  "History",    2011, 19.99),
            new Book(null, "Atomic Habits",             "James Clear",   "Psychology", 2018, 27.99)
        ));
    }

    @Test
    void findAll_returnsAllBooks() {
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(3);
    }

    @Test
    void findByGenre_returnsCorrectBooks() {
        List<Book> techBooks = bookRepository.findByGenre("Technology");
        // Only "The Pragmatic Programmer" is Technology
        assertThat(techBooks).hasSize(1);
        assertThat(techBooks.get(0).getTitle()).isEqualTo("The Pragmatic Programmer");
    }

    @Test
    void findByYearGreaterThan_filtersCorrectly() {
        // Sapiens (2011) and Atomic Habits (2018) are after 2000
        List<Book> recentBooks = bookRepository.findByYearGreaterThan(2000);
        assertThat(recentBooks).hasSize(2);
        assertThat(recentBooks)
            .extracting(Book::getTitle)
            .containsExactlyInAnyOrder("Sapiens", "Atomic Habits");
    }

    @Test
    void save_andFindById_works() {
        // save() returns the persisted entity with the generated id populated
        Book saved = bookRepository.save(
            new Book(null, "Deep Work", "Cal Newport", "Productivity", 2016, 21.99)
        );

        // The returned object has the MongoDB-assigned _id
        String savedId = saved.getId();
        assertThat(savedId).isNotBlank();

        Optional<Book> found = bookRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Deep Work");
    }
}
