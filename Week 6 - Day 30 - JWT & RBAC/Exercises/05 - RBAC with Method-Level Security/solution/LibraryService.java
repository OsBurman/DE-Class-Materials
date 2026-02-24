package com.jwt.rbac;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Library business-logic service.
 *
 * Role enforcement is declared directly on each method using Spring Security
 * annotations rather than in the HTTP security chain:
 *
 *   @PreAuthorize  — evaluated by Spring AOP before the method executes
 *   @Secured       — older annotation; takes authority strings (ROLE_ prefix required)
 *
 * Both require @EnableMethodSecurity on a @Configuration class.
 */
@Service
public class LibraryService {

    // Mutable in-memory "database"
    private final List<String> books = new ArrayList<>(List.of("Clean Code", "Effective Java"));

    /**
     * Any authenticated user may list books.
     * isAuthenticated() is a built-in Spring Security expression.
     */
    @PreAuthorize("isAuthenticated()")
    public List<String> listAllBooks() {
        return List.copyOf(books);
    }

    /**
     * Only ADMIN users may add books.
     * hasRole('ADMIN') matches GrantedAuthority "ROLE_ADMIN".
     */
    @PreAuthorize("hasRole('ADMIN')")
    public String addBook(String title) {
        books.add(title);
        return "Added: " + title;
    }

    /**
     * ADMIN or LIBRARIAN may delete books.
     * @Secured takes the full authority string (ROLE_ prefix required).
     */
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    public String deleteBook(String title) {
        boolean removed = books.remove(title);
        return removed ? "Deleted: " + title : "Not found: " + title;
    }

    /**
     * Returns the username of the currently authenticated principal.
     * Reads directly from the SecurityContextHolder (thread-local).
     * No security annotation needed – any authenticated request reaching
     * this method has already been verified by the JWT filter.
     */
    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
