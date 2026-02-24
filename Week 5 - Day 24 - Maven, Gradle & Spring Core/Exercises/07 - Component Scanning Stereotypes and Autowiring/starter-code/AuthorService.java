package com.library;

import org.springframework.beans.factory.annotation.Autowired;

// TODO: Add the correct Spring stereotype annotation for a BUSINESS LOGIC class
public class AuthorService {

    // TODO: Declare a private final AuthorRepository field


    // TODO: Add a constructor that accepts AuthorRepository
    //       Annotate it with @Autowired


    /**
     * Returns the author name for the given ID.
     *
     * @param id the author's numeric ID
     * @return the author name string from the repository
     */
    public String getAuthorName(int id) {
        // TODO: Delegate to repository.findById(id) and return the result
        return null;
    }
}
