package com.springai;

import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// RANDOM_PORT starts a real embedded server so we can test the HTTP endpoint end-to-end
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MockAiTestConfiguration.class)
class ChatbotControllerTest {

    @Autowired TestRestTemplate restTemplate;

    @Test
    void firstMessage_returnsReply() {
        // Send a single message and check we get any non-blank reply
        ChatRequest request = new ChatRequest("test1", "Hello!");
        String reply = restTemplate.postForObject("/chat", request, String.class);
        assertThat(reply).isNotBlank();
    }

    @Test
    void secondMessage_returnsReply() {
        // Two messages in the same conversation — the endpoint should handle both
        ChatRequest msg1 = new ChatRequest("test2", "My name is Alice.");
        ChatRequest msg2 = new ChatRequest("test2", "What is my name?");

        String reply1 = restTemplate.postForObject("/chat", msg1, String.class);
        String reply2 = restTemplate.postForObject("/chat", msg2, String.class);

        // Both responses should be non-blank (exact content depends on the mock model)
        assertThat(reply1).isNotBlank();
        assertThat(reply2).isNotBlank();
    }

    record ChatRequest(String conversationId, String message) {}
}
