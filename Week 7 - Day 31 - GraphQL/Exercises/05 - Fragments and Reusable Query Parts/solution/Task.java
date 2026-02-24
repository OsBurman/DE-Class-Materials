package com.graphql.tasks;

public record Task(String id, String title, boolean done) implements TaskInterface {
    public Task withDone(boolean newDone) {
        return new Task(this.id, this.title, newDone);
    }
}
