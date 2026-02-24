package com.library.model;

/**
 * Simple book model.
 * TODO: Create this as a Java record (or POJO) with fields: int id, String title, String genre
 *       If using a record: public record Book(int id, String title, String genre) {}
 *       If using a class: include a no-arg constructor, all-args constructor, and getters/setters
 *       (Spring's Jackson deserializer needs either a no-arg constructor + setters, or a record)
 */
public record Book(int id, String title, String genre) {}
