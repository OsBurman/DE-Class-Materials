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
        Book b1 = new Book("Clean Code", "Programming", 2008);
        Book b2 = new Book("Dune", "Science Fiction", 1965);
        Book b3 = new Book("The Pragmatic Programmer", "Programming", 1999);

        // persist() makes each Book managed and inserts it into the DB
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);

        System.out.println("Saved: " + b1);
        System.out.println("Saved: " + b2);
        System.out.println("Saved: " + b3);
    }
}
