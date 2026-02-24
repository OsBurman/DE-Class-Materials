package com.springai;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptTemplateService {

    // Renders a greeting prompt by substituting {name} and {topic}
    public Prompt buildGreetingPrompt(String name, String topic) {
        String template = "You are a helpful assistant. Greet {name} and explain {topic} in two sentences.";
        // PromptTemplate.create() renders the template with the provided variable map
        return new PromptTemplate(template).create(Map.of("name", name, "topic", topic));
    }

    // Renders a code review prompt with the given programming language and code snippet
    public Prompt buildCodeReviewPrompt(String language, String code) {
        String template = "Review the following {language} code and list any issues:\n\n{code}";
        return new PromptTemplate(template).create(Map.of("language", language, "code", code));
    }

    // Renders a summary prompt with a configurable word-count ceiling
    public Prompt buildSummaryPrompt(String text, int maxWords) {
        String template = "Summarise the following text in no more than {maxWords} words:\n\n{text}";
        // Integer is automatically converted to String in the rendered output
        return new PromptTemplate(template).create(Map.of("maxWords", maxWords, "text", text));
    }
}
