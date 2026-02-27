package com.exercise.blog.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// TODO 1: Add @Entity and @Table(name = "authors")
//         Add @Id and @GeneratedValue(strategy = GenerationType.IDENTITY) on the id field
public class Author {

    private Long id;

    private String name;
    private String email;
    private String bio;

    // TODO 2: Add @OneToMany relationship annotation:
    // @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    // - mappedBy = "author" means Post.author is the owning side (has the FK
    // column)
    // - cascade = ALL means saving/deleting an Author cascades to their Posts
    // - fetch = LAZY means posts are NOT loaded until you access them (efficient)
    //
    // TODO 3: Add @JsonManagedReference to prevent infinite JSON serialization
    // loop.
    // (Author → posts → author → posts → ...)
    private List<Post> posts = new ArrayList<>();

    public Author() {
    }

    public Author(String name, String email, String bio) {
        this.name = name;
        this.email = email;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
