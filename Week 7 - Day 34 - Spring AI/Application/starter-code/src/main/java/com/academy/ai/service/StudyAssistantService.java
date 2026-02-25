package com.academy.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Study Assistant Service — uses Spring AI to answer student questions.
 *
 * TODO Task 1: Implement all methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudyAssistantService {

    private final ChatClient chatClient;

    // TODO Task 1a: Simple one-shot answer
    // Use chatClient.call(String message) for a simple prompt
    public String askQuestion(String question) {
        log.info("Processing question: {}", question);
        // TODO
        // return chatClient.call(question);
        return "TODO: implement askQuestion";
    }

    // TODO Task 1b: Template-based answer using a .st prompt file
    // Load src/main/resources/prompts/study-assistant.st
    // Substitute {topic} and {question} variables
    public String askWithContext(String topic, String question) {
        log.info("Processing question about '{}': {}", topic, question);
        // TODO:
        // PromptTemplate template = new PromptTemplate(new
        // ClassPathResource("prompts/study-assistant.st"));
        // Prompt prompt = template.create(Map.of("topic", topic, "question",
        // question));
        // ChatResponse response = chatClient.call(prompt);
        // return response.getResult().getOutput().getContent();
        return "TODO: implement askWithContext";
    }

    // TODO Task 1c: Multi-turn conversation — maintain chat history
    // Keep a list of Messages (SystemMessage + UserMessage + AssistantMessage)
    public String chat(String sessionId, String userMessage, List<Message> history) {
        log.info("Chat [{}]: {}", sessionId, userMessage);
        // TODO:
        // history.add(new UserMessage(userMessage));
        // Prompt prompt = new Prompt(history);
        // ChatResponse response = chatClient.call(prompt);
        // String assistantResponse = response.getResult().getOutput().getContent();
        // history.add(new AssistantMessage(assistantResponse));
        // return assistantResponse;
        return "TODO: implement chat";
    }

    // TODO Task 1d: Explain a code snippet
    // System prompt: "You are an expert Java developer explaining code to a
    // student."
    // User prompt: "Explain this code:\n" + code
    public String explainCode(String code) {
        log.info("Explaining code snippet ({} chars)", code.length());
        // TODO
        return "TODO: implement explainCode";
    }

    // TODO Task 1e: Summarize long text
    // System prompt: "Summarize the following text in 3-5 bullet points for a
    // student."
    public String summarize(String text) {
        log.info("Summarizing text ({} chars)", text.length());
        // TODO
        return "TODO: implement summarize";
    }
}
