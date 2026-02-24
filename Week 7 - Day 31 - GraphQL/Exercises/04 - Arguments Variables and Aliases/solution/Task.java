package com.graphql.tasks;

/**
 * Java record – immutable value type.
 * Records auto-generate constructor, getters (id(), title(), done()), equals, hashCode, toString.
 */
public record Task(String id, String title, boolean done) {

    /** Convenience "wither" – returns a new Task with done set to true */
    public Task withDone(boolean newDone) {
        return new Task(this.id, this.title, newDone);
    }
}
