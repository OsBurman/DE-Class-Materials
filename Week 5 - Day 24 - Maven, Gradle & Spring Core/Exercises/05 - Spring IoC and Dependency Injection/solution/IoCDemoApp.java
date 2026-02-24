package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IoCDemoApp {

    public static void main(String[] args) {
        System.out.println("=== Inversion of Control Demo ===");

        // Start the Spring container; processes @Configuration and registers @Beans
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve beans — Spring injects all dependencies automatically
        BookService   bookService   = context.getBean(BookService.class);
        ReviewService reviewService = context.getBean(ReviewService.class);
        LoanService   loanService   = context.getBean(LoanService.class);

        System.out.println("[Constructor Injection] BookService.getBookTitle(5)     → "
                + bookService.getBookTitle(5));
        System.out.println("[Setter Injection]      ReviewService.getReviewCount(5) → "
                + reviewService.getReviewCount(5));
        System.out.println("[Field Injection]       LoanService.isAvailable(5)      → "
                + loanService.isAvailable(5));

        // Always close the context to release resources and trigger @PreDestroy callbacks
        context.close();
        System.out.println("Spring context closed.");
    }
}
