package com.academy.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a blog author.
 *
 * TODO Task 1: Add JPA annotations:
 *   @Entity
 *   @Table(name = "authors")
 *   Add @Column annotations where appropriate (e.g., unique = true on email)
 */
// TODO: @Entity, @Table(name = "authors")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // TODO Task 1: @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // TODO Task 1: Add OneToMany relationship
    //   @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //   @Builder.Default
    private List<Post> posts = new ArrayList<>();
}
