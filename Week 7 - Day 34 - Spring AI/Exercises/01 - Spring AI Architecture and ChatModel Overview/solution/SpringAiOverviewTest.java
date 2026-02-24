package com.springai;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest loads the full application context
// @Import(MockAiTestConfiguration.class) registers a mock ChatModel that returns canned responses
// — no real OpenAI / Anthropic API key required
@SpringBootTest
@Import(MockAiTestConfiguration.class)
class SpringAiOverviewTest {

    @Autowired
    ChatModel chatModel;

    @Test
    void basicChatCall_returnsResponse() {
        // Build the simplest possible prompt: a single UserMessage
        UserMessage message = new UserMessage("What is Spring AI?");
        Prompt prompt = new Prompt(message);

        // call() sends the prompt to the model and returns a structured ChatResponse
        ChatResponse response = chatModel.call(prompt);

        // The mock model always returns a non-null response with non-blank content
        assertThat(response).isNotNull();
        assertThat(response.getResult().getOutput().getContent()).isNotBlank();
    }

    @Test
    void chatModel_supportsMultipleMessages() {
        // SystemMessage sets the model's persona / instructions
        SystemMessage systemMsg = new SystemMessage("You are a helpful Java tutor.");
        // UserMessage is the actual question from the user
        UserMessage userMsg = new UserMessage("Explain dependency injection in one sentence.");

        // Pass both messages in a list — order matters: system first, then user
        Prompt prompt = new Prompt(List.of(systemMsg, userMsg));

        ChatResponse response = chatModel.call(prompt);

        assertThat(response.getResult().getOutput().getContent()).isNotBlank();
    }
}
