package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ResilientChatService.class)
@Import(MockAiTestConfiguration.class)
class ResilientChatServiceTest {

    @Autowired
    private ResilientChatService service;

    @Test
    void chat_happyPath_returnsModelResponse() {
        // TODO 4: Call service.chat("Hello") and assert the result is not blank
    }

    @Test
    void chat_whenModelThrows_returnsFallback() {
        // TODO 5: Create a mock ChatModel that throws RuntimeException("boom"):
        //         ChatModel throwingModel = mock(ChatModel.class);
        //         when(throwingModel.call(any())).thenThrow(new RuntimeException("boom"));
        //
        //         Create a ResilientChatService with the throwing mock:
        //         ResilientChatService failingService = new ResilientChatService(throwingModel);
        //
        //         Call failingService.chat("Hello") and assert the result equals
        //         ResilientChatService.FALLBACK_RESPONSE
    }
}
