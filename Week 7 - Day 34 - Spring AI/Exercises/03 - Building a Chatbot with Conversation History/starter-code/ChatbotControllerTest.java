package com.springai;

import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// TODO 4: Add @SpringBootTest(webEnvironment = RANDOM_PORT) and @Import(MockAiTestConfiguration.class)
class ChatbotControllerTest {

    // TODO 4: Inject TestRestTemplate with @Autowired
    TestRestTemplate restTemplate;

    @Test
    void firstMessage_returnsReply() {
        // TODO 4a: POST to /chat with body {"conversationId":"test1","message":"Hello!"}
        //          Assert the response body is not blank
        //          Hint: restTemplate.postForObject("/chat", request, String.class)
    }

    @Test
    void secondMessage_returnsReply() {
        // TODO 4b: Send two sequential POSTs with the same conversationId "test2"
        //          First: {"conversationId":"test2","message":"My name is Alice."}
        //          Second: {"conversationId":"test2","message":"What is my name?"}
        //          Assert both response bodies are not blank
    }

    record ChatRequest(String conversationId, String message) {}
}
