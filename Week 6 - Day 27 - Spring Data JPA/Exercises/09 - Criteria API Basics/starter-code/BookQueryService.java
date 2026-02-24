package com.library.service;

import com.library.model.Book;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Service
public class BookQueryService {

    // TODO 1: Inject EntityManager
    //         @PersistenceContext
    //         private EntityManager entityManager;

    // TODO 2: findByGenre — WHERE b.genre = :genre
    public List<Book> findByGenre(String genre) {
        // Step 1: CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // Step 2: CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        // Step 3: Root<Book> root = cq.from(Book.class);
        // Step 4: Predicate predicate = cb.equal(root.get("genre"), genre);
        // Step 5: cq.where(predicate);
        // Step 6: return entityManager.createQuery(cq).getResultList();
        return List.of(); // replace with implementation
    }

    // TODO 3: findByGenreAndMinYear — WHERE b.genre = :genre AND b.publishedYear > :minYear
    public List<Book> findByGenreAndMinYear(String genre, int minYear) {
        // Hint: create two predicates and combine with cb.and(p1, p2)
        return List.of(); // replace with implementation
    }

    // TODO 4: findByTitleKeyword — WHERE b.title LIKE %keyword%
    public List<Book> findByTitleKeyword(String keyword) {
        // Hint: cb.like(root.get("title"), "%" + keyword + "%")
        return List.of(); // replace with implementation
    }
}
