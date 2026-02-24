package com.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatbotController {

    private final ChatClient chatClient;

    // Build the ChatClient once and reuse it — the memory persists across requests
    public ChatbotController(ChatModel chatModel) {
        // MessageWindowChatMemory stores the last 10 messages per conversation ID
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        // Attach the memory advisor so it automatically prepends history to each prompt
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
                .build();
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        // The advisor reads and writes history keyed by conversationId
        return chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param(
                        MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
                        request.conversationId()))
                .call()
                .content();
    }

    // Record for JSON deserialisation — field names match the JSON keys
    record ChatRequest(String conversationId, String message) {}
}
