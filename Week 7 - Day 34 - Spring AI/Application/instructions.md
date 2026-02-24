# Day 34 Application — Spring AI: AI-Powered Study Assistant

## Overview

Build a **Study Assistant chatbot** using **Spring AI** that can answer questions about course material, generate quiz questions, and summarize content using an OpenAI LLM.

---

## Learning Goals

- Integrate Spring AI into a Spring Boot project
- Use `ChatClient` to interact with LLMs
- Write effective system prompts
- Implement streaming responses
- Use prompt templates with variables
- Build a simple chat memory for multi-turn conversations

---

## Prerequisites

- Java 17+, Maven
- OpenAI API key in `application.yml` (`spring.ai.openai.api-key`)
- `mvn spring-boot:run` → `http://localhost:8080`

---

## Part 1 — Basic Chat

**Task 1 — `ChatController.java`**  
```java
@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    // TODO: inject ChatClient via constructor (Spring AI auto-configures it)

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        // TODO: use chatClient.prompt(message).call().content()
    }
}
```
Test: `POST /api/ai/chat` with body `"What is polymorphism in Java?"`

---

## Part 2 — System Prompts

**Task 2 — `StudyAssistantService.java`**  
```java
@Service
public class StudyAssistantService {

    // TODO: Define a SYSTEM_PROMPT constant:
    // "You are a helpful Java/Spring study assistant for junior developers.
    //  When explaining concepts, always include:
    //  1. A clear definition
    //  2. A real-world analogy
    //  3. A Java code example
    //  Keep answers concise and beginner-friendly."

    public String askQuestion(String question) {
        // TODO: use chatClient with system prompt + user question
    }
}
```

---

## Part 3 — Prompt Templates

**Task 3**  
Create `src/main/resources/prompts/quiz-generator.st`:
```
Generate {count} multiple-choice quiz questions about {topic} for a Java developer.

Format each question as:
Q: [question]
A) [option]
B) [option]
C) [option]
D) [option]
Answer: [letter]
```

**Task 4 — `QuizController.java`**  
```java
@GetMapping("/quiz")
public String generateQuiz(
    @RequestParam String topic,
    @RequestParam(defaultValue = "3") int count) {
    // TODO: use PromptTemplate to fill in the .st template
    // return the generated quiz
}
```
Test: `GET /api/ai/quiz?topic=Spring+Boot&count=3`

---

## Part 4 — Streaming Response

**Task 5**  
```java
@PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(@RequestBody String message) {
    // TODO: use chatClient.prompt(message).stream().content()
}
```
Test with `curl -N -X POST localhost:8080/api/ai/chat/stream -H "Content-Type: text/plain" -d "Explain microservices"`

---

## Part 5 — Chat Memory (Multi-Turn)

**Task 6**  
```java
@PostMapping("/chat/session/{sessionId}")
public String chatWithMemory(
    @PathVariable String sessionId,
    @RequestBody String message) {
    // TODO: use InMemoryChatMemory keyed by sessionId
    // Spring AI: ChatClient.builder().defaultAdvisors(new MessageChatMemoryAdvisor(memory))
}
```
Test: Send 3 related messages in the same session and verify the AI remembers context.

---

## Part 6 — Summarizer

**Task 7**  
```java
@PostMapping("/summarize")
public String summarize(@RequestBody String longText) {
    // TODO: prompt = "Summarize the following in 3 bullet points for a student: {text}"
}
```

---

## Submission Checklist

- [ ] `/api/ai/chat` returns answers with system prompt applied
- [ ] `/api/ai/quiz` uses a prompt template from a `.st` file
- [ ] `/api/ai/chat/stream` returns `text/event-stream`
- [ ] Chat memory persists context across messages in the same session
- [ ] `/api/ai/summarize` returns bullet-point summary
- [ ] API key in `application.yml` (NOT hardcoded in Java)
