package com.exercise.socialmedia.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class PostRequest {
    @NotBlank(message = "Post content is required")
    @Size(max = 2000, message = "Post content must not exceed 2000 characters")
    private String content;

    private List<String> tags;

    public PostRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
