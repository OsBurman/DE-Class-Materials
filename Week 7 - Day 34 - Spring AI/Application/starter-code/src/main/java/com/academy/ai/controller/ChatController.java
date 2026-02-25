package com.academy.ai.controller;

import com.academy.ai.service.StudyAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REST endpoints for the study assistant.
 *
 * TODO Task 3: Implement all endpoints.
 */
@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class ChatController {

    private final StudyAssistantService assistantService;

    // In-memory session store: sessionId → chat history
    private final Map<String, java.util.List<org.springframework.ai.chat.messages.Message>> sessions = new ConcurrentHashMap<>();

    // TODO Task 3a: POST /api/assistant/ask
    // Body: { "question": "..." }
    // Response: { "answer": "..." }
    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> body) {
        String question = body.get("question");
        // TODO
        String answer = assistantService.askQuestion(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    // TODO Task 3b: POST /api/assistant/ask-with-context
    // Body: { "topic": "Java Streams", "question": "..." }
    @PostMapping("/ask-with-context")
    public ResponseEntity<Map<String, String>> askWithContext(@RequestBody Map<String, String> body) {
        // TODO
        return ResponseEntity.ok(Map.of("answer", "TODO"));
    }

    // TODO Task 3c: POST /api/assistant/chat/{sessionId}
    // Maintains conversation history per session
    // Body: { "message": "..." }
    @PostMapping("/chat/{sessionId}")
    public ResponseEntity<Map<String, String>> chat(
            @PathVariable String sessionId,
            @RequestBody Map<String, String> body) {
        String message = body.get("message");
        // TODO: get or create history for sessionId, call assistantService.chat()
        var history = sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
        String response = assistantService.chat(sessionId, message, history);
        return ResponseEntity.ok(Map.of("response", response));
    }

    // TODO Task 3d: POST /api/assistant/explain-code
    // Body: { "code": "..." }
    @PostMapping("/explain-code")
    public ResponseEntity<Map<String, String>> explainCode(@RequestBody Map<String, String> body) {
        // TODO
        return ResponseEntity.ok(Map.of("explanation", "TODO"));
    }

    // TODO Task 3e: POST /api/assistant/summarize
    // Body: { "text": "..." }
    @PostMapping("/summarize")
    public ResponseEntity<Map<String, String>> summarize(@RequestBody Map<String, String> body) {
        // TODO
        return ResponseEntity.ok(Map.of("summary", "TODO"));
    }
}
