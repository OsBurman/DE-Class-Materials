package com.academy.blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a comment on a post.
 *
 * TODO Task 4: Add JPA annotations:
 * - @Entity, @Table(name = "comments")
 * - @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id") on the
 * post field
 * - @Column(nullable = false) on content and commenterName
 * - @Column(nullable = false) and default value logic for createdAt
 */
// TODO: @Entity, @Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String commenterName;

    // TODO: @Column(nullable = false)
    // TODO: Set a default in a @PrePersist method or use @Builder.Default
    private LocalDateTime createdAt;

    // TODO Task 4: @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id")
    private Post post;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
