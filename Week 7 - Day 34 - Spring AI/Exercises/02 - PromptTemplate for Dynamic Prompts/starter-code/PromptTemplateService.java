package com.springai;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PromptTemplateService {

    /**
     * TODO 1a: Create a PromptTemplate from:
     *   "You are a helpful assistant. Greet {name} and explain {topic} in two sentences."
     * Render it with a Map containing "name" -> name and "topic" -> topic.
     * Return the rendered Prompt.
     *
     * Hint: new PromptTemplate("...").create(Map.of("name", name, "topic", topic))
     */
    public Prompt buildGreetingPrompt(String name, String topic) {
        // TODO 1a: implement here
        return null;
    }

    /**
     * TODO 1b: Create a PromptTemplate from:
     *   "Review the following {language} code and list any issues:\n\n{code}"
     * Render it with "language" -> language and "code" -> code.
     * Return the rendered Prompt.
     */
    public Prompt buildCodeReviewPrompt(String language, String code) {
        // TODO 1b: implement here
        return null;
    }

    /**
     * TODO 1c: Create a PromptTemplate from:
     *   "Summarise the following text in no more than {maxWords} words:\n\n{text}"
     * Render it with "maxWords" -> maxWords and "text" -> text.
     * Return the rendered Prompt.
     */
    public Prompt buildSummaryPrompt(String text, int maxWords) {
        // TODO 1c: implement here
        return null;
    }
}
