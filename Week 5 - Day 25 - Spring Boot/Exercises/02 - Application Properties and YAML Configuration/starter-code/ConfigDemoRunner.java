package com.library;

// TODO: Import @Component from org.springframework.stereotype
// TODO: Import CommandLineRunner from org.springframework.boot
// TODO: Import LibraryProperties from com.library.config

/**
 * Runs on startup and prints the bound configuration values.
 *
 * CommandLineRunner is a Spring Boot callback interface. Any @Component
 * that implements it will have its run() method called right after the
 * application context is fully started.
 *
 * TODO:
 *   1. Add @Component so Spring detects this class
 *   2. Implement CommandLineRunner
 *   3. Declare a private final LibraryProperties props field
 *   4. Add a constructor that accepts LibraryProperties and assigns it to props
 *      (constructor injection — no @Autowired needed with a single constructor)
 *   5. Implement run(String... args):
 *        - Print "Max loan days: " + props.getMaxLoanDays()
 *        - Print "Welcome: " + props.getWelcomeMessage()
 */
public class ConfigDemoRunner {

    // TODO: declare the LibraryProperties field

    // TODO: constructor

    // TODO: implement run(String... args)
}
