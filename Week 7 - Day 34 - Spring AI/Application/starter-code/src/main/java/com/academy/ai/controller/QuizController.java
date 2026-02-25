package com.academy.ai.controller;

import com.academy.ai.dto.QuizQuestion;
import com.academy.ai.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for quiz generation.
 *
 * TODO Task 4: Implement quiz endpoints.
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // TODO Task 4a: GET /api/quiz/question?topic=Java+Streams&difficulty=medium
    @GetMapping("/question")
    public ResponseEntity<QuizQuestion> generateQuestion(
            @RequestParam String topic,
            @RequestParam(defaultValue = "medium") String difficulty) {
        // TODO
        return ResponseEntity.ok(quizService.generateQuestion(topic, difficulty));
    }

    // TODO Task 4b: GET /api/quiz?topic=Spring+Boot&count=5
    @GetMapping
    public ResponseEntity<List<QuizQuestion>> generateQuiz(
            @RequestParam String topic,
            @RequestParam(defaultValue = "5") int count) {
        // TODO
        return ResponseEntity.ok(quizService.generateQuiz(topic, count));
    }

    // TODO Task 4c: POST /api/quiz/check-answer
    // Body: { "question": "...", "userAnswer": "A", "correctAnswer": "B" }
    @PostMapping("/check-answer")
    public ResponseEntity<Map<String, Object>> checkAnswer(@RequestBody Map<String, String> body) {
        boolean correct = body.get("userAnswer").equals(body.get("correctAnswer"));
        return ResponseEntity.ok(Map.of(
                "correct", correct,
                "message",
                correct ? "Correct! 🎉" : "Not quite — the correct answer was " + body.get("correctAnswer")));
    }
}
