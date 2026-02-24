package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java-based Spring configuration class.
 * Registers all beans that the Spring container will manage.
 */
@Configuration
public class AppConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepository();
    }

    @Bean
    public BookService bookService() {
        // TODO: Create and return a BookService, injecting bookRepository() via the constructor
        // Hint: call new BookService(bookRepository())
        return null;
    }

    @Bean
    public ReviewService reviewService() {
        // TODO: Create a ReviewService, then call its setter to inject bookRepository()
        // Hint: create the object first, call setBookRepository(), then return it
        return null;
    }

    @Bean
    public LoanService loanService() {
        // LoanService uses field injection — Spring handles it automatically
        // No manual wiring needed here; just return a new instance
        return new LoanService();
    }
}
