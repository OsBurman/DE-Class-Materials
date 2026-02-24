package com.academy.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a tag that can be applied to many posts.
 *
 * TODO Task 2: Add JPA annotations:
 *   - @Entity, @Table(name = "tags")
 *   - @Column(unique = true, nullable = false) on name
 *   - @ManyToMany(mappedBy = "tags") on posts (back reference)
 */
// TODO: @Entity, @Table(name = "tags")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: @Column(unique = true, nullable = false)
    private String name;

    // TODO Task 2: @ManyToMany(mappedBy = "tags")
    //              @Builder.Default
    private Set<Post> posts = new HashSet<>();
}
