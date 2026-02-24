package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  // generic stereotype — not web-specific in this exercise
public class AuthorController {

    private final AuthorService service;

    @Autowired
    public AuthorController(AuthorService service) {
        this.service = service;
    }

    public String handleRequest(int id) {
        String result = service.getAuthorName(id);
        return "Controller response: " + result;
    }
}
