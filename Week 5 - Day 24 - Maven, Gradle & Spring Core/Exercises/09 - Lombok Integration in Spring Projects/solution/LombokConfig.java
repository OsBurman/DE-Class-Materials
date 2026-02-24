package com.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Java configuration for the Lombok demo.
 *
 * @ComponentScan("com.library") picks up @Service on BookCatalogService.
 * BookRepository is registered manually as a @Bean so Spring can inject
 * it into BookCatalogService's constructor.
 */
@Configuration
@ComponentScan("com.library")
public class LombokConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepository();
    }
}
