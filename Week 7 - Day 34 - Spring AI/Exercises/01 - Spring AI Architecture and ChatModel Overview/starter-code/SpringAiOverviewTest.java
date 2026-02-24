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

// TODO 2: Add @SpringBootTest and @Import(MockAiTestConfiguration.class)
//         MockAiTestConfiguration registers a mocked ChatModel — no real API key required.
class SpringAiOverviewTest {

    // TODO 2: Inject the ChatModel bean with @Autowired
    ChatModel chatModel;

    @Test
    void basicChatCall_returnsResponse() {
        // TODO 3a: Create a UserMessage with text "What is Spring AI?"
        // TODO 3b: Wrap it in a new Prompt(message)
        // TODO 3c: Call chatModel.call(prompt) and store the result
        // TODO 3d: Assert that the ChatResponse is not null
        // TODO 3e: Assert that response.getResult().getOutput().getContent() is not blank
    }

    @Test
    void chatModel_supportsMultipleMessages() {
        // TODO 4a: Create a SystemMessage: "You are a helpful Java tutor."
        // TODO 4b: Create a UserMessage: "Explain dependency injection in one sentence."
        // TODO 4c: Build a Prompt from List.of(systemMsg, userMsg)
        // TODO 4d: Call chatModel.call(prompt) and assert the content is not blank
    }
}
