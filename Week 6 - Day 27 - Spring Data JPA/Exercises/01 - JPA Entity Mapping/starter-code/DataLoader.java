package com.library;

import com.library.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements ApplicationRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // TODO: Create three Book objects using the all-args constructor:
        //         Book("Clean Code", "Programming", 2008)
        //         Book("Dune", "Science Fiction", 1965)
        //         Book("The Pragmatic Programmer", "Programming", 1999)

        // TODO: Persist each book using entityManager.persist(book)

        // TODO: Print each saved book using System.out.println("Saved: " + book)
    }
}
