package com.library;

import org.springframework.beans.factory.annotation.Autowired;

// TODO: Add @Component (generic stereotype — not web-specific here)
public class AuthorController {

    // TODO: Declare a private final AuthorService field


    // TODO: Add a constructor that accepts AuthorService
    //       Annotate it with @Autowired


    /**
     * Handles an incoming request for author info.
     *
     * @param id the author ID from the request
     * @return a response string
     */
    public String handleRequest(int id) {
        // TODO: Call service.getAuthorName(id) and return "Controller response: " + result
        return null;
    }
}
