package com.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// TODO 6: Make BookRepository extend MongoRepository<Book, String>.
//         This gives you save(), findAll(), findById(), deleteAll(), etc. for free.
public interface BookRepository {

    // TODO 7: Declare a derived query method that finds books by genre.
    //         Spring Data generates the query from the method name automatically.
    //         Method signature: List<Book> findByGenre(String genre)

    // TODO 8: Declare a derived query method that finds books published after a given year.
    //         Method signature: List<Book> findByYearGreaterThan(int year)
}
