package com.exercise.socialmedia.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;

    public CommentResponse() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
