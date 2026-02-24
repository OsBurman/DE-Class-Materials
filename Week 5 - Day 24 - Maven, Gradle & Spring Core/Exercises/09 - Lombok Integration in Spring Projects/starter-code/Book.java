package com.library;

// TODO: Import the necessary Lombok annotations:
//       @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor,
//       @ToString, @EqualsAndHashCode

/**
 * Represents a book in the library catalog.
 *
 * Using individual Lombok annotations (instead of @Data) gives
 * fine-grained control — e.g., equals/hashCode based on id only.
 *
 * TODO:
 *   1. Add @Getter        — generates getters for all fields
 *   2. Add @Setter        — generates setters for all fields
 *   3. Add @NoArgsConstructor   — generates public Book() {}
 *   4. Add @AllArgsConstructor  — generates public Book(int id, String title, int authorId) {}
 *   5. Add @ToString      — generates toString()
 *   6. Add @EqualsAndHashCode(of = "id")
 *                         — bases equals/hashCode on the id field only
 */
public class Book {

    private int id;
    private String title;
    private int authorId;
}
