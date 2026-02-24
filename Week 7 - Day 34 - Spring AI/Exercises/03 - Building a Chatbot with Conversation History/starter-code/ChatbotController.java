package com.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

// TODO 1: Annotate this class as a @RestController and map POST /chat
@RestController
public class ChatbotController {

    private final ChatClient chatClient;

    // TODO 2: In the constructor, build a MessageWindowChatMemory with maxMessages = 10,
    //         then build a ChatClient using ChatClient.builder(chatModel)
    //         and attach new MessageChatMemoryAdvisor(memory) as a default advisor.
    public ChatbotController(ChatModel chatModel) {
        // TODO 2: Build memory and chatClient here
        this.chatClient = null; // replace with real chatClient
    }

    // TODO 1: Map this to POST /chat
    public String chat(@RequestBody ChatRequest request) {
        // TODO 3: Use the chatClient fluent API to send request.message()
        //         and pass the conversationId as the advisor param:
        //
        //   return chatClient.prompt()
        //       .user(request.message())
        //       .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
        //                              request.conversationId()))
        //       .call()
        //       .content();
        return null;
    }

    // Simple record to deserialise the JSON request body
    record ChatRequest(String conversationId, String message) {}
}
