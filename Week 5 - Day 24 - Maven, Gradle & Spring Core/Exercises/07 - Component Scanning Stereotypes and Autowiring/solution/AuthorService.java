package com.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service  // signals: business/domain logic layer
public class AuthorService {

    private final AuthorRepository repository;

    @Autowired  // Spring injects AuthorRepository (scanned as @Repository) here
    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public String getAuthorName(int id) {
        return repository.findById(id);
    }
}
