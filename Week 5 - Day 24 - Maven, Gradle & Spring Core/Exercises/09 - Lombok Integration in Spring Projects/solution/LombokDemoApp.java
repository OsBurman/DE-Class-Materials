package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point demonstrating Lombok integration with Spring.
 *
 * Key takeaways visible in this app:
 *   - BookRequest is constructed with @Builder (no new keyword, no setters)
 *   - Book.toString() is Lombok-generated (no hand-written method in Book.java)
 *   - BookCatalogService uses @Slf4j — the log.info() call fires inside createBook()
 *   - Spring injects BookRepository via the Lombok-generated constructor (@RequiredArgsConstructor)
 */
public class LombokDemoApp {

    public static void main(String[] args) {
        // Bootstrap the Spring context using our Java config class
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LombokConfig.class);

        // Retrieve the service — Spring injected BookRepository via constructor
        BookCatalogService service = context.getBean(BookCatalogService.class);

        // Build a request DTO using Lombok's @Builder pattern
        BookRequest request = BookRequest.builder()
                .title("Effective Java")
                .authorId(1)
                .build();

        // createBook() logs via @Slf4j and returns a Book with Lombok-generated toString()
        Book book = service.createBook(request);

        System.out.println("Created: " + book);

        context.close();
    }
}
