package com.library;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point for the Lombok integration demo.
 *
 * TODO:
 *   1. Create an AnnotationConfigApplicationContext using LombokConfig.class
 *   2. Retrieve the BookCatalogService bean from the context
 *   3. Build a BookRequest using the builder pattern:
 *          BookRequest.builder().title("Effective Java").authorId(1).build()
 *   4. Call service.createBook(request) and store the result in a Book variable
 *   5. Print: "Created: " + book   (uses Lombok-generated toString())
 *   6. Close the context
 */
public class LombokDemoApp {

    public static void main(String[] args) {
        // TODO: implement
    }
}
