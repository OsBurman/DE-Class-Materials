package com.springai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ResilientChatService {

    private static final Logger log = LoggerFactory.getLogger(ResilientChatService.class);

    public static final String FALLBACK_RESPONSE =
            "I'm sorry, I'm unable to process your request right now. Please try again later.";

    private final ChatModel chatModel;

    // TODO 1: Add a constructor that accepts ChatModel and assigns it to the field
    //         public ResilientChatService(ChatModel chatModel) { ... }

    /**
     * Sends a message to the model and returns its response.
     * Falls back to FALLBACK_RESPONSE if the model call fails.
     *
     * @param userMessage the user's input text
     * @return model response content, or FALLBACK_RESPONSE on error
     */
    public String chat(String userMessage) {

        // TODO 2: Wrap the model call in try/catch
        //
        // try {
        //     Prompt prompt = new Prompt(new UserMessage(userMessage));
        //     ChatResponse response = chatModel.call(prompt);
        //
        //     // TODO 2a: Read token usage
        //     long promptTokens     = response.getMetadata().getUsage().getPromptTokens();
        //     long completionTokens = response.getMetadata().getUsage().getGenerationTokens();
        //
        //     // TODO 2b: Log token counts at INFO level
        //     log.info("Prompt tokens: {}", promptTokens);
        //     log.info("Completion tokens: {}", completionTokens);
        //
        //     return response.getResult().getOutput().getText();
        //
        // } catch (Exception e) {
        //     // TODO 2c: Log the error at WARN level and return the fallback
        //     log.warn("ChatModel call failed, using fallback: {}", e.getMessage());
        //     return FALLBACK_RESPONSE;
        // }

        return null; // replace with your implementation
    }
}
