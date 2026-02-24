package com.library;

// TODO: Import the Spring MVC annotations:
//       @RestController, @RequestMapping, @GetMapping
//       from org.springframework.web.bind.annotation

/**
 * REST controller for book-related endpoints.
 *
 * TODO:
 *   1. Add @RestController — marks this as a REST controller (combines @Controller + @ResponseBody)
 *   2. Add @RequestMapping("/api/books") — all methods in this class are rooted at /api/books
 *   3. Annotate getGreeting() with @GetMapping so it responds to GET /api/books
 *   4. Implement getGreeting() to return:
 *         "Spring Boot is running! Auto-configuration works."
 */
public class BookController {

    // TODO: Add @GetMapping
    public String getGreeting() {
        // TODO: Return the greeting string
        return null;
    }
}
