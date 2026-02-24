package com.mongodb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// @DataMongoTest loads only MongoDB-related beans and uses the embedded Flapdoodle MongoDB
@DataMongoTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // TODO 9: Clear the collection and seed it with exactly three books:
        //   Book 1: title="The Pragmatic Programmer", author="David Thomas",  genre="Technology", year=1999, price=39.99
        //   Book 2: title="Sapiens",                  author="Yuval Harari",  genre="History",    year=2011, price=19.99
        //   Book 3: title="Atomic Habits",             author="James Clear",   genre="Psychology", year=2018, price=27.99
        //
        // bookRepository.deleteAll();
        // bookRepository.saveAll(List.of( new Book(null, ...), new Book(null, ...), new Book(null, ...) ));
    }

    @Test
    void findAll_returnsAllBooks() {
        // TODO 10: Call bookRepository.findAll() and assert the list has exactly 3 elements.
    }

    @Test
    void findByGenre_returnsCorrectBooks() {
        // TODO 11: Call bookRepository.findByGenre("Technology")
        //          Assert the result has 1 element and its title is "The Pragmatic Programmer".
    }

    @Test
    void findByYearGreaterThan_filtersCorrectly() {
        // TODO 12: Call bookRepository.findByYearGreaterThan(2000)
        //          Assert the result has 2 elements (Sapiens 2011, Atomic Habits 2018).
    }

    @Test
    void save_andFindById_works() {
        // TODO 13: Create a new Book with null id, save it, capture the returned book's id.
        //          Call bookRepository.findById(savedId) and assert the returned Optional
        //          is present and has the correct title.
    }
}
