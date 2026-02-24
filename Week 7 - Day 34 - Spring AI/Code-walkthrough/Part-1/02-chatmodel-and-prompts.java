// =============================================================================
// Day 34 — Spring AI: ChatModel, EmbeddingModel, PromptTemplate & Chatbot
// Bookstore AI Assistant Application
//
// Topics covered:
//   1. Spring AI overview (ChatModel injection via DI)
//   2. ChatModel basic usage
//   3. PromptTemplate for dynamic prompts
//   4. System messages and user messages
//   5. Conversation history (multi-turn chatbot)
//   6. Streaming responses with Flux
//   7. EmbeddingModel basic usage
//   8. @AiService declarative pattern
//   9. REST endpoints wiring it all together
// =============================================================================

package com.bookstore.ai;

// ── Maven Dependencies (in pom.xml) ─────────────────────────────────────────
// <dependency>
//   <groupId>org.springframework.ai</groupId>
//   <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
// </dependency>
//
// Spring AI BOM in dependencyManagement:
// <dependency>
//   <groupId>org.springframework.ai</groupId>
//   <artifactId>spring-ai-bom</artifactId>
//   <version>1.0.0</version>
//   <type>pom</type>
//   <scope>import</scope>
// </dependency>
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.*;

// =============================================================================
// SECTION 1: application.properties configuration
// =============================================================================
//
// # src/main/resources/application.properties
// spring.ai.openai.api-key=${OPENAI_API_KEY}
// spring.ai.openai.chat.options.model=gpt-4o
// spring.ai.openai.chat.options.temperature=0.7
// spring.ai.openai.chat.options.max-tokens=500
//
// Spring Boot auto-configures the OpenAiChatModel bean automatically.
// All you do is @Autowired ChatModel — the implementation is injected.

// =============================================================================
// SECTION 2: Basic ChatModel Usage
// =============================================================================

@Service
public class BasicChatService {

    // ChatModel is the Spring AI interface — Spring injects OpenAiChatModel
    // (or whichever provider you configured) automatically.
    // Your code never imports anything vendor-specific.
    private final ChatModel chatModel;

    @Autowired
    public BasicChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // ── 2a: Simplest possible call ──────────────────────────────────────────
    // chatModel.call(String) is the convenience overload.
    // Returns the response text as a plain String.
    public String askSimpleQuestion(String question) {
        // Under the hood this creates a Prompt with one UserMessage
        return chatModel.call(question);
    }

    // ── 2b: Calling with a full Prompt object ───────────────────────────────
    // Use Prompt when you need more control:
    //   - Set a system message (persona/context for the AI)
    //   - Override model options per-request
    //   - Pass multiple messages (conversation history)
    public String askWithSystemContext(String userQuestion) {
        // SystemMessage sets the AI's persona and rules for this conversation
        Message systemMessage = new SystemMessage("""
                You are a knowledgeable bookstore assistant named "BookBot".
                You specialize in literature recommendations and author information.
                Be helpful, concise, and occasionally enthusiastic about books.
                If asked about topics unrelated to books, politely redirect.
                """);

        // UserMessage is what the user actually typed
        Message userMessage = new UserMessage(userQuestion);

        // Build the Prompt from an ordered list of messages
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // call() returns a ChatResponse — a rich object with metadata
        ChatResponse response = chatModel.call(prompt);

        // Extract the generated text from the response
        // getResult() returns the first (and usually only) generation
        // getOutput().getContent() gets the text string
        return response.getResult().getOutput().getContent();
    }

    // ── 2c: Overriding model options per-request ────────────────────────────
    // You can override model, temperature, maxTokens etc. per call
    // without changing application.properties
    public String askWithCustomOptions(String question) {
        OpenAiChatOptions perRequestOptions = OpenAiChatOptions.builder()
                .withModel("gpt-4o")
                .withTemperature(0.2f)    // lower = more deterministic answers
                .withMaxTokens(200)
                .build();

        Prompt prompt = new Prompt(
                new UserMessage(question),
                perRequestOptions
        );

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}

// =============================================================================
// SECTION 3: PromptTemplate — Dynamic Prompts
// =============================================================================

@Service
class PromptTemplateService {

    private final ChatModel chatModel;

    PromptTemplateService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // PromptTemplate uses {placeholder} syntax — like Spring's JdbcTemplate
    // for SQL but for AI prompts. This separates your prompt text from your
    // code logic — prompts can even be loaded from classpath resource files.
    public String getBookRecommendation(String genre, String readingLevel) {
        // Template string with named placeholders
        String templateText = """
                You are a librarian helping a customer find books.
                
                The customer enjoys the {genre} genre and reads at a {level} level.
                
                Recommend exactly 3 books that would suit them.
                For each book, provide: title, author, and one sentence describing why it fits.
                Format as a numbered list.
                """;

        PromptTemplate template = new PromptTemplate(templateText);

        // Create the Prompt by filling in the placeholders
        Prompt prompt = template.create(Map.of(
                "genre", genre,
                "level", readingLevel
        ));

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    // ── Loading a prompt template from a file ───────────────────────────────
    // For production, store prompts in src/main/resources/prompts/
    // This makes prompts editable without recompiling.
    //
    // PromptTemplate template = new PromptTemplate(
    //     new ClassPathResource("prompts/book-recommendation.st")
    // );
    //
    // The .st extension = StringTemplate format (used by Spring AI)
    //
    // Content of prompts/book-recommendation.st:
    // """
    // Recommend 3 books in the {genre} genre for a {level} reader.
    // """

    // ── SystemPromptTemplate ────────────────────────────────────────────────
    // Like PromptTemplate but creates a SystemMessage instead of UserMessage
    public String getBookSummary(String title, String author) {
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(
                "You are an expert on {author}'s works. Provide concise, accurate summaries."
        );
        Message systemMessage = systemTemplate.createMessage(Map.of("author", author));

        PromptTemplate userTemplate = new PromptTemplate(
                "Please give me a 3-sentence summary of '{title}'."
        );
        Message userMessage = userTemplate.createMessage(Map.of("title", title));

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}

// =============================================================================
// SECTION 4: Chatbot — Multi-Turn Conversation (Conversational Interface)
// =============================================================================
// A chatbot maintains conversation history across multiple exchanges.
// Without history, every message is treated as a fresh conversation.
// With history, the AI remembers what was said earlier in the session.

@Service
class BookstoreChatbotService {

    private final ChatModel chatModel;

    // In-memory conversation store: sessionId → list of messages
    // In production, use Redis or a database for persistence across restarts
    private final Map<String, List<Message>> conversationHistory = new HashMap<>();

    // The system message defines the AI's persona for the entire session
    private static final String SYSTEM_PROMPT = """
            You are BookBot, a friendly and knowledgeable assistant for a bookstore.
            You help customers discover books, understand genres, find authors,
            and get reading recommendations.
            Keep responses friendly and under 150 words.
            Remember details the customer shares during our conversation.
            """;

    BookstoreChatbotService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String sessionId, String userInput) {
        // Retrieve or initialize the conversation history for this session
        List<Message> history = conversationHistory.computeIfAbsent(
                sessionId, k -> new ArrayList<>()
        );

        // Add the new user message to history
        history.add(new UserMessage(userInput));

        // Build the full prompt:
        // [SystemMessage] + [all previous messages] + [current user message]
        // The system message goes first and sets context for the entire session
        List<Message> fullMessages = new ArrayList<>();
        fullMessages.add(new SystemMessage(SYSTEM_PROMPT));
        fullMessages.addAll(history);  // includes the current user message

        Prompt prompt = new Prompt(fullMessages);
        ChatResponse response = chatModel.call(prompt);

        String assistantReply = response.getResult().getOutput().getContent();

        // Store the assistant's reply in history so the NEXT turn can reference it
        // AssistantMessage represents the AI's response in the conversation
        history.add(new AssistantMessage(assistantReply));

        return assistantReply;
    }

    public void clearSession(String sessionId) {
        conversationHistory.remove(sessionId);
    }

    // ⚠️ Watch out: conversation history grows with every exchange.
    // For long sessions, implement a sliding window to keep only the last N turns:
    //
    // if (history.size() > 20) {
    //     history = new ArrayList<>(history.subList(history.size() - 20, history.size()));
    // }
    //
    // Otherwise, you'll eventually exceed the model's context window (token limit).
}

// =============================================================================
// SECTION 5: Streaming Responses
// =============================================================================
// For long responses (summaries, detailed explanations), streaming lets the
// user see tokens as they're generated — much better UX than waiting 10 seconds
// for the complete response to appear.

@Service
class StreamingChatService {

    private final ChatModel chatModel;

    StreamingChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // stream() returns Flux<ChatResponse> — each emission is a partial token
    // The Spring controller can stream this directly to the browser using SSE
    public Flux<String> streamBookSummary(String bookTitle) {
        Message systemMessage = new SystemMessage(
                "You are a book expert. Provide detailed but engaging summaries."
        );
        Message userMessage = new UserMessage(
                "Give me a comprehensive summary of the book: " + bookTitle
        );

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        // stream() instead of call() — returns a reactive Flux
        return chatModel.stream(prompt)
                // Extract the text from each partial response
                .map(response -> response.getResult().getOutput().getContent())
                // Filter out null/empty tokens (some providers emit empty strings)
                .filter(text -> text != null && !text.isEmpty());
    }
}

// =============================================================================
// SECTION 6: EmbeddingModel — Turning Text into Vectors
// =============================================================================

@Service
class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    // embed() converts text to a float array (vector)
    // Similar texts produce similar vectors (close in vector space)
    public float[] embedText(String text) {
        return embeddingModel.embed(text);
    }

    // Compute cosine similarity between two texts
    // Returns a value from -1 (opposite) to 1 (identical meaning)
    public double computeSimilarity(String text1, String text2) {
        float[] vec1 = embeddingModel.embed(text1);
        float[] vec2 = embeddingModel.embed(text2);
        return cosineSimilarity(vec1, vec2);
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

// =============================================================================
// SECTION 7: @AiService — Declarative AI Services
// =============================================================================
// @AiService is the Spring AI equivalent of a Spring Data repository or
// Feign client — you define an interface with annotations, Spring generates
// the implementation at startup.

// Note: @AiService requires the spring-ai-spring-boot-autoconfigure module
// and is an experimental feature as of Spring AI 1.0.

import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;

// Define the AI service as an interface
// (Shown as a comment block since @AiService availability varies by version)
/*
@AiService
public interface BookstoreAiService {

    // The method name becomes part of the prompt context
    // @UserMessage templates the user's input
    @UserMessage("Recommend 3 books in the {genre} genre. Format as a numbered list.")
    String recommendByGenre(@V("genre") String genre);

    // @SystemMessage sets the AI persona
    @SystemMessage("You are an expert literary critic. Be concise and insightful.")
    @UserMessage("Write a one-paragraph critical analysis of the book '{title}' by {author}.")
    String criticalAnalysis(@V("title") String title, @V("author") String author);

    // Return Flux<String> for streaming
    @SystemMessage("You are a storyteller.")
    Flux<String> generateBookBlurb(@UserMessage String bookDescription);
}
*/

// =============================================================================
// SECTION 8: ChatClient — Fluent API (alternative to ChatModel)
// =============================================================================
// ChatClient is a higher-level fluent builder API introduced in Spring AI 1.0.
// It's the recommended way to build prompts for most use cases.

@Service
class ChatClientService {

    // ChatClient.Builder is auto-configured by Spring AI
    private final ChatClient chatClient;

    ChatClientService(ChatClient.Builder builder) {
        // Configure a default system message for all requests from this service
        this.chatClient = builder
                .defaultSystem("You are a helpful bookstore assistant named BookBot.")
                .build();
    }

    public String ask(String question) {
        // Fluent API — reads like English
        return chatClient.prompt()
                .user(question)
                .call()
                .content();  // returns the response text directly
    }

    public String askWithContext(String bookTitle, String question) {
        return chatClient.prompt()
                .system("You are an expert on the book: " + bookTitle)
                .user(question)
                .call()
                .content();
    }
}

// =============================================================================
// SECTION 9: REST Controller — Wiring AI Features to HTTP Endpoints
// =============================================================================

@RestController
@RequestMapping("/api/ai")
class BookstoreAiController {

    private final BasicChatService chatService;
    private final PromptTemplateService templateService;
    private final BookstoreChatbotService chatbotService;
    private final StreamingChatService streamingService;

    BookstoreAiController(
            BasicChatService chatService,
            PromptTemplateService templateService,
            BookstoreChatbotService chatbotService,
            StreamingChatService streamingService) {
        this.chatService = chatService;
        this.templateService = templateService;
        this.chatbotService = chatbotService;
        this.streamingService = streamingService;
    }

    // POST /api/ai/ask — simple question
    @PostMapping("/ask")
    public Map<String, String> ask(@RequestBody Map<String, String> body) {
        String answer = chatService.askWithSystemContext(body.get("question"));
        return Map.of("answer", answer);
    }

    // GET /api/ai/recommend?genre=fantasy&level=intermediate
    @GetMapping("/recommend")
    public Map<String, String> recommend(
            @RequestParam String genre,
            @RequestParam(defaultValue = "intermediate") String level) {
        String recommendations = templateService.getBookRecommendation(genre, level);
        return Map.of("recommendations", recommendations);
    }

    // POST /api/ai/chat — conversational endpoint (stateful per session)
    @PostMapping("/chat")
    public Map<String, String> chat(
            @RequestHeader(value = "X-Session-Id", defaultValue = "default") String sessionId,
            @RequestBody Map<String, String> body) {
        String reply = chatbotService.chat(sessionId, body.get("message"));
        return Map.of("reply", reply);
    }

    // DELETE /api/ai/chat — clear conversation history
    @DeleteMapping("/chat")
    public void clearChat(
            @RequestHeader(value = "X-Session-Id", defaultValue = "default") String sessionId) {
        chatbotService.clearSession(sessionId);
    }

    // GET /api/ai/stream/summary?title=CleanCode — streaming response via SSE
    // text/event-stream content type enables Server-Sent Events in the browser
    @GetMapping(value = "/stream/summary", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamSummary(@RequestParam String title) {
        return streamingService.streamBookSummary(title);
    }
}
