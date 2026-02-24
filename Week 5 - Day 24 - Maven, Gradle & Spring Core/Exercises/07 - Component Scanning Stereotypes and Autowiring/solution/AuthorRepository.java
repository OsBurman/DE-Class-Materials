package com.library;

import org.springframework.stereotype.Repository;

@Repository  // signals: data-access layer; Spring also wraps persistence exceptions
public class AuthorRepository {

    public String findById(int id) {
        return "Author #" + id;
    }
}
