package com.academy.library.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a book in the library.
 * This class is COMPLETE — do not modify.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private boolean available = true;
}
