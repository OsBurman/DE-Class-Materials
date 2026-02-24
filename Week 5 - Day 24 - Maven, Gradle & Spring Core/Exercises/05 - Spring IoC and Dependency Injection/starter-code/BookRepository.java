package com.library;

/**
 * A simple stub repository — no real database in this exercise.
 * Spring will manage this as a bean and inject it into service classes.
 */
public class BookRepository {

    public String findTitleById(int id) {
        return "Stub Book #" + id;
    }
}
