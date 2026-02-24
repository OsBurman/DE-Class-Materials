package com.jwt.rbac.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 05 – Library service with method-level security.
 *
 * You must add security annotations to the appropriate methods and
 * implement the getCurrentUser() method body.
 */
@Service
public class LibraryService {

    private final List<String> books = new ArrayList<>(List.of("Clean Code", "Effective Java"));

    /**
     * TODO: Add @PreAuthorize("isAuthenticated()") to this method.
     * Any authenticated user may list books.
     */
    public List<String> listAllBooks() {
        return List.copyOf(books);
    }

    /**
     * TODO: Add @PreAuthorize("hasRole('ADMIN')") to this method.
     * Only ADMIN may add books.
     */
    public String addBook(String title) {
        books.add(title);
        return "Added: " + title;
    }

    /**
     * TODO: Add @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"}) to this method.
     * ADMIN or LIBRARIAN may delete books.
     *
     * Note: @Secured takes the full "ROLE_" prefixed strings.
     */
    public String deleteBook(String title) {
        boolean removed = books.remove(title);
        return removed ? "Deleted: " + title : "Not found: " + title;
    }

    /**
     * TODO: Implement this method – no annotation needed.
     *
     * Retrieve the currently authenticated user's name from:
     *   SecurityContextHolder.getContext().getAuthentication()
     *
     * Return authentication.getName() if authentication is not null,
     * otherwise return "anonymous".
     */
    public String getCurrentUser() {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
