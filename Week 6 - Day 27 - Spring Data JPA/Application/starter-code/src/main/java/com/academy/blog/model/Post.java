package com.academy.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a blog post.
 *
 * TODO Task 3: Add ALL JPA annotations:
 *   - @Entity, @Table(name = "posts")
 *   - @ManyToOne with LAZY fetch for author
 *   - @OneToMany with CascadeType.ALL for comments
 *   - @ManyToMany with @JoinTable for tags
 *   - @Enumerated(EnumType.STRING) on status
 */
// TODO: @Entity, @Table(name = "posts")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime publishedAt;

    // TODO Task 3: @Enumerated(EnumType.STRING)
    private Status status;

    // TODO Task 3: Add @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id")
    private Author author;

    // TODO Task 3: Add @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    //              @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    // TODO Task 3: Add @ManyToMany
    //   @JoinTable(name = "post_tags",
    //              joinColumns = @JoinColumn(name = "post_id"),
    //              inverseJoinColumns = @JoinColumn(name = "tag_id"))
    //   @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public enum Status {
        DRAFT, PUBLISHED
    }
}
