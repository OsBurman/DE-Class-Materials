package com.library;

// TODO: Import the necessary Lombok annotations:
//       @Data, @NoArgsConstructor, @AllArgsConstructor

/**
 * Represents an author.
 *
 * TODO:
 *   1. Add @Data             — bundles @Getter, @Setter, @RequiredArgsConstructor,
 *                              @ToString, and @EqualsAndHashCode
 *   2. Add @NoArgsConstructor — @Data generates @RequiredArgsConstructor but NOT
 *                              a no-arg constructor; add it explicitly
 *   3. Add @AllArgsConstructor — add a full constructor as well
 */
public class Author {

    private int id;
    private String name;
    private String email;
}
