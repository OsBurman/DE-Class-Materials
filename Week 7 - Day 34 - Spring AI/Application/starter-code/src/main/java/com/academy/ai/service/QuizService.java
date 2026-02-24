package com.academy.ai.service;

import com.academy.ai.dto.QuizQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Quiz Generator Service.
 *
 * TODO Task 2: Implement quiz generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    // TODO Task 2a: Generate a single quiz question
    // Use the quiz-generator.st prompt template
    // Parse the JSON response into a QuizQuestion object
    public QuizQuestion generateQuestion(String topic, String difficulty) {
        log.info("Generating {} question about '{}'", difficulty, topic);
        // TODO:
        // PromptTemplate template = new PromptTemplate(new ClassPathResource("prompts/quiz-generator.st"));
        // Prompt prompt = template.create(Map.of("topic", topic, "difficulty", difficulty));
        // String json = chatClient.call(prompt).getResult().getOutput().getContent();
        // return objectMapper.readValue(json, QuizQuestion.class);
        return new QuizQuestion(
            "TODO: implement generateQuestion",
            Map.of("A", "opt A", "B", "opt B", "C", "opt C", "D", "opt D"),
            "A",
            "Not yet implemented"
        );
    }

    // TODO Task 2b: Generate a 5-question quiz on a topic
    public java.util.List<QuizQuestion> generateQuiz(String topic, int count) {
        log.info("Generating {}-question quiz about '{}'", count, topic);
        // TODO: call generateQuestion() multiple times with varying difficulty levels
        return java.util.List.of();
    }
}
