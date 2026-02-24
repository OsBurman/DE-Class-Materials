package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(MockAiTestConfiguration.class)
class PromptTemplateTest {

    @Autowired ChatModel chatModel;
    @Autowired PromptTemplateService service;

    @Test
    void greetingPrompt_containsNameAndTopic() {
        Prompt prompt = service.buildGreetingPrompt("Alice", "recursion");

        // Verify that PromptTemplate correctly substituted both variables
        String rendered = prompt.getContents();
        assertThat(rendered).contains("Alice");
        assertThat(rendered).contains("recursion");

        // The mock model should return a non-blank response regardless of prompt content
        assertThat(chatModel.call(prompt).getResult().getOutput().getContent()).isNotBlank();
    }

    @Test
    void codeReviewPrompt_containsLanguage() {
        Prompt prompt = service.buildCodeReviewPrompt(
                "Java", "public int add(int a, int b) { return a + b; }");

        assertThat(prompt.getContents()).contains("Java");
        assertThat(chatModel.call(prompt).getResult().getOutput().getContent()).isNotBlank();
    }

    @Test
    void summaryPrompt_containsMaxWords() {
        Prompt prompt = service.buildSummaryPrompt(
                "Spring AI provides a unified abstraction over AI model providers.", 50);

        // The rendered template should include the integer 50 as a string
        assertThat(prompt.getContents()).contains("50");
        assertThat(chatModel.call(prompt).getResult().getOutput().getContent()).isNotBlank();
    }
}
