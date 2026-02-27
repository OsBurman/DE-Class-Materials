package com.exercise.blog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// TODO 4: Add @Entity and @Table(name = "posts")
//         Add @Id and @GeneratedValue on the id field
public class Post {

    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // TODO 5: Add @ManyToOne(fetch = FetchType.LAZY) — many posts belong to ONE
    // author
    // Add @JoinColumn(name = "author_id") — this creates the FK column in the posts
    // table
    //
    // TODO 6: Add @JsonBackReference — this side is NOT included in JSON to prevent
    // the Author → posts → author → posts infinite loop.
    // (The @JsonManagedReference on Author.posts IS serialized, this one is NOT)
    private Author author;

    // TODO 7: Add @ManyToMany — many posts can have many tags
    // Add @JoinTable(
    // name = "post_tags",
    // joinColumns = @JoinColumn(name = "post_id"),
    // inverseJoinColumns = @JoinColumn(name = "tag_id")
    // )
    // This creates the join/bridge table: post_tags(post_id, tag_id)
    private Set<Tag> tags = new HashSet<>();

    // TODO 8: Add @CreationTimestamp so Hibernate auto-fills this on insert
    // Also add @Column(updatable = false)
    private LocalDateTime createdAt;

    public Post() {
    }

    public Post(String title, String content, Author author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
