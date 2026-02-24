package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO 2: Add @SpringBootTest and @Import(MockAiTestConfiguration.class)
class PromptTemplateTest {

    // TODO 2: Inject ChatModel and PromptTemplateService
    ChatModel chatModel;
    PromptTemplateService service;

    @Test
    void greetingPrompt_containsNameAndTopic() {
        // TODO 3a: Call service.buildGreetingPrompt("Alice", "recursion")
        // TODO 3b: Assert the rendered prompt text contains "Alice" and "recursion"
        //          Hint: prompt.getContents() returns the rendered string
        // TODO 3c: Call chatModel.call(prompt) and assert the response is not blank
    }

    @Test
    void codeReviewPrompt_containsLanguage() {
        // TODO 3d: Call service.buildCodeReviewPrompt("Java", "public int add(int a, int b) { return a+b; }")
        // TODO 3e: Assert the rendered prompt contains "Java"
        // TODO 3f: Call chatModel.call(prompt) and assert the response is not blank
    }

    @Test
    void summaryPrompt_containsMaxWords() {
        // TODO 3g: Call service.buildSummaryPrompt("Spring AI provides...", 50)
        // TODO 3h: Assert the rendered prompt contains "50"
        // TODO 3i: Call chatModel.call(prompt) and assert the response is not blank
    }
}
