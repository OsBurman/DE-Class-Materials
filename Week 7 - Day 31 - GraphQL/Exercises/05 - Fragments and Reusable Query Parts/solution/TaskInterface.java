package com.graphql.tasks;

/** Java interface mirroring the GraphQL TaskInterface — both Task and PriorityTask implement it. */
public interface TaskInterface {
    String id();
    String title();
    boolean done();
}
