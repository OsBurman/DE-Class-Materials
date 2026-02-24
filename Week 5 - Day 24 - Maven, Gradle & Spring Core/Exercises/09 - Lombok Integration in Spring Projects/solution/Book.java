package com.library;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Represents a book in the library catalog.
 *
 * Individual Lombok annotations are used instead of @Data to allow
 * fine-grained control — in particular, equals/hashCode is based on
 * the 'id' field only (not the mutable title or authorId).
 *
 * What each annotation generates:
 *   @Getter            → public int getId(), public String getTitle(), public int getAuthorId()
 *   @Setter            → public void setId(int), public void setTitle(String), ...
 *   @NoArgsConstructor → public Book() {}
 *   @AllArgsConstructor → public Book(int id, String title, int authorId) {}
 *   @ToString          → public String toString() { return "Book(id=..., title=..., authorId=...)"; }
 *   @EqualsAndHashCode(of = "id")
 *                      → equals/hashCode use only the id field — two books with the same id
 *                        are considered equal even if their titles differ
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Book {

    private int id;
    private String title;
    private int authorId;
}
