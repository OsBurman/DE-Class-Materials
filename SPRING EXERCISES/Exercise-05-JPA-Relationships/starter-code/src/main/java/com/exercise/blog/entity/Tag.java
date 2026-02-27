package com.exercise.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

// TODO 9: Add @Entity and @Table(name = "tags")
//         Add @Id and @GeneratedValue on id
//         Add @Column(unique = true) on name (tag names must be unique)
public class Tag {

    private Long id;

    private String name;

    // TODO 10: Add @ManyToMany(mappedBy = "tags")
    // - mappedBy = "tags" means Post.tags is the OWNING side
    // (the join table is defined there)
    // - Tag is the INVERSE side — it just references the mapping
    //
    // TODO 11: Add @JsonIgnore to prevent Tag → posts → tags → posts infinite loop
    // in JSON
    private Set<Post> posts = new HashSet<>();

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
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

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}
