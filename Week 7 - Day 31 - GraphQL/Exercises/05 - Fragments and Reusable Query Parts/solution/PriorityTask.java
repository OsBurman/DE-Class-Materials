package com.graphql.tasks;

/** Adds a priority field on top of the base TaskInterface fields. */
public record PriorityTask(String id, String title, boolean done, int priority)
        implements TaskInterface {}
