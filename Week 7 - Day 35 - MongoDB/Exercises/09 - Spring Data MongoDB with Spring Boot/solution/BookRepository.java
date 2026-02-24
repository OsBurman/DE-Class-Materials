package com.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// MongoRepository<T, ID> — T is the entity class, ID is the type of the @Id field
// Spring Data generates the implementation at startup — no @Repository annotation needed
public interface BookRepository extends MongoRepository<Book, String> {

    // Derived query — Spring Data translates "findBy" + "Genre" into
    // db.books.find({ genre: <value> }) automatically
    List<Book> findByGenre(String genre);

    // Derived query — "GreaterThan" maps to $gt in MQL
    // Equivalent to: db.books.find({ year: { $gt: year } })
    List<Book> findByYearGreaterThan(int year);
}
