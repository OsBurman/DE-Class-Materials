package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point for the IoC/DI demonstration.
 */
public class IoCDemoApp {

    public static void main(String[] args) {
        System.out.println("=== Inversion of Control Demo ===");

        // TODO: Create an AnnotationConfigApplicationContext, passing AppConfig.class
        //       This starts the Spring container and registers all @Bean methods


        // TODO: Retrieve BookService from the context using context.getBean(BookService.class)
        //       Then call getBookTitle(5) and print:
        //       "[Constructor Injection] BookService.getBookTitle(5)     → <result>"


        // TODO: Retrieve ReviewService and call getReviewCount(5)
        //       Print: "[Setter Injection]      ReviewService.getReviewCount(5) → <result>"


        // TODO: Retrieve LoanService and call isAvailable(5)
        //       Print: "[Field Injection]       LoanService.isAvailable(5)      → <result>"


        // TODO: Close the context to trigger any @PreDestroy lifecycle callbacks
        //       Call context.close()
        System.out.println("Spring context closed.");
    }
}
