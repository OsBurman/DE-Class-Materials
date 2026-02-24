package com.library.service;

import com.library.model.Book;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Service
public class BookQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    // WHERE b.genre = :genre
    public List<Book> findByGenre(String genre) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);

        Predicate predicate = cb.equal(root.get("genre"), genre);
        cq.where(predicate);

        return entityManager.createQuery(cq).getResultList();
    }

    // WHERE b.genre = :genre AND b.publishedYear > :minYear
    public List<Book> findByGenreAndMinYear(String genre, int minYear) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);

        Predicate genrePredicate = cb.equal(root.get("genre"), genre);
        Predicate yearPredicate  = cb.greaterThan(root.get("publishedYear"), minYear);
        cq.where(cb.and(genrePredicate, yearPredicate));

        return entityManager.createQuery(cq).getResultList();
    }

    // WHERE b.title LIKE %keyword%
    public List<Book> findByTitleKeyword(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);

        Predicate predicate = cb.like(root.get("title"), "%" + keyword + "%");
        cq.where(predicate);

        return entityManager.createQuery(cq).getResultList();
    }
}
