package com.exercise.socialmedia.dto;

import jakarta.validation.constraints.*;
import java.util.List;

// TODO 10: Add validation annotations
public class PostRequest {
    // TODO 10: @NotBlank @Size(max=2000)
    private String content;
    private List<String> tags;

    public PostRequest() {}
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
