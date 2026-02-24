package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepository();
    }

    @Bean
    public BookService bookService() {
        // Constructor injection: pass the bean explicitly
        return new BookService(bookRepository());
    }

    @Bean
    public ReviewService reviewService() {
        // Setter injection: create first, then call the setter
        ReviewService service = new ReviewService();
        service.setBookRepository(bookRepository());
        return service;
    }

    @Bean
    public LoanService loanService() {
        // Field injection: Spring handles it after the bean is created
        // Just return a new instance; @Autowired on the field does the rest
        return new LoanService();
    }
}
