package com.library;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller demonstrating that auto-configuration wired up Spring MVC.
 *
 * @RestController = @Controller + @ResponseBody
 *   - @Controller   → registers this as a Spring MVC controller bean
 *   - @ResponseBody → writes the return value directly to the HTTP response body
 *                     (no view resolution, no template — just the String as plain text / JSON)
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    /**
     * GET /api/books
     * Returns a greeting that confirms the app and auto-configuration are working.
     */
    @GetMapping
    public String getGreeting() {
        return "Spring Boot is running! Auto-configuration works.";
    }
}
