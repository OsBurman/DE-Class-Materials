package com.springai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ResilientChatService {

    private static final Logger log = LoggerFactory.getLogger(ResilientChatService.class);

    public static final String FALLBACK_RESPONSE =
            "I'm sorry, I'm unable to process your request right now. Please try again later.";

    private final ChatModel chatModel;

    public ResilientChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String userMessage) {
        try {
            Prompt prompt = new Prompt(new UserMessage(userMessage));
            ChatResponse response = chatModel.call(prompt);

            long promptTokens     = response.getMetadata().getUsage().getPromptTokens();
            long completionTokens = response.getMetadata().getUsage().getGenerationTokens();

            log.info("Prompt tokens: {}", promptTokens);
            log.info("Completion tokens: {}", completionTokens);

            return response.getResult().getOutput().getText();

        } catch (Exception e) {
            log.warn("ChatModel call failed, using fallback: {}", e.getMessage());
            return FALLBACK_RESPONSE;
        }
    }
}
