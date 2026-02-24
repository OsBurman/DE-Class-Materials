// =============================================================================
// Day 34 — Spring AI Part 2: Structured Output Generation
// Bookstore Application
//
// Topics covered:
//   1. The problem with free-text AI responses in applications
//   2. BeanOutputConverter — map AI output to Java POJOs
//   3. ListOutputConverter — get structured lists
//   4. MapOutputConverter — get key-value maps
//   5. Combining PromptTemplate with output converters
//   6. Schema enforcement and format instructions
// =============================================================================

package com.bookstore.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// =============================================================================
// SECTION 1: The Problem — AI Returns Free Text
// =============================================================================
//
// Without structured output, AI responses are free text:
//
//   "Here are some recommendations:
//    1. Clean Code by Robert C. Martin - A classic on writing maintainable code
//    2. The Pragmatic Programmer by Dave Thomas..."
//
// To use this in your app, you'd need to PARSE this string. That's fragile —
// the AI might format it differently each time, spell names wrong, add extra
// commentary, etc.
//
// Structured output asks the AI to respond in a specific format (JSON)
// that Spring AI automatically deserializes into your Java objects.

// =============================================================================
// SECTION 2: Java Records for Structured Output
// =============================================================================
// These are the data structures the AI will fill in.
// Use records — they're immutable, concise, and Jackson-serializable.

// A single book recommendation
record BookRecommendation(
        String title,
        String author,
        String genre,
        int publishedYear,
        String whyRecommended
) {}

// A list of recommendations with metadata
record BookRecommendationList(
        String queryGenre,
        int totalRecommendations,
        List<BookRecommendation> recommendations
) {}

// A book review analysis
record BookReviewAnalysis(
        String bookTitle,
        double averageRating,           // 0.0 - 5.0
        String overallSentiment,        // "positive", "negative", "mixed"
        List<String> mainThemes,
        String summary
) {}

// Author info
record AuthorProfile(
        String name,
        String nationality,
        int birthYear,
        List<String> notableWorks,
        String writingStyle
) {}

// =============================================================================
// SECTION 3: BeanOutputConverter — Map AI Response to a Java POJO
// =============================================================================

@Service
class StructuredOutputService {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    StructuredOutputService(ChatModel chatModel, ChatClient.Builder builder) {
        this.chatModel = chatModel;
        this.chatClient = builder.build();
    }

    // ── 3a: Using BeanOutputConverter with ChatModel ─────────────────────────
    public BookRecommendation getStructuredRecommendation(String genre) {
        // BeanOutputConverter takes the target Java class
        // It generates JSON schema instructions and deserializes the response
        BeanOutputConverter<BookRecommendation> converter =
                new BeanOutputConverter<>(BookRecommendation.class);

        // The converter generates format instructions:
        // "Respond with JSON matching this schema: { title: string, author: string, ... }"
        String formatInstructions = converter.getFormat();

        PromptTemplate template = new PromptTemplate("""
                Recommend one excellent book in the {genre} genre.
                
                {format}
                """);

        Prompt prompt = template.create(Map.of(
                "genre", genre,
                "format", formatInstructions  // inject schema instructions
        ));

        // Call the model — it returns JSON-formatted text
        String jsonResponse = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getContent();

        // Deserialize the JSON string into a BookRecommendation record
        return converter.convert(jsonResponse);
    }

    // ── 3b: Using BeanOutputConverter with a complex nested type ────────────
    public BookRecommendationList getRecommendationList(String genre, int count) {
        BeanOutputConverter<BookRecommendationList> converter =
                new BeanOutputConverter<>(BookRecommendationList.class);

        PromptTemplate template = new PromptTemplate("""
                You are a librarian recommending books to a customer.
                
                Recommend exactly {count} books in the {genre} genre.
                Include real books with accurate information.
                
                {format}
                """);

        Prompt prompt = template.create(Map.of(
                "count", String.valueOf(count),
                "genre", genre,
                "format", converter.getFormat()
        ));

        String jsonResponse = chatModel.call(prompt)
                .getResult().getOutput().getContent();

        // Nested objects (List<BookRecommendation>) are deserialized automatically
        return converter.convert(jsonResponse);
    }

    // ── 3c: Using ChatClient with entity() for structured output ────────────
    // ChatClient has a cleaner API for structured output via .entity()
    public BookReviewAnalysis analyzeBookReviews(String bookTitle, String reviews) {
        // .entity(Class) tells ChatClient to return a deserialized Java object
        return chatClient.prompt()
                .system("You are a literary analyst. Analyze reviews objectively.")
                .user("""
                        Analyze the following customer reviews for the book "%s"
                        and provide a structured analysis.
                        
                        Reviews:
                        %s
                        """.formatted(bookTitle, reviews))
                .call()
                .entity(BookReviewAnalysis.class);  // ← automatic structured output
    }

    // ── 3d: Get structured list of strings ──────────────────────────────────
    public List<String> getGenreList() {
        // ListOutputConverter handles List<String> — no need for a wrapper record
        ListOutputConverter converter = new ListOutputConverter(new DefaultConversionService());

        PromptTemplate template = new PromptTemplate("""
                List exactly 8 popular book genres.
                {format}
                """);

        Prompt prompt = template.create(Map.of("format", converter.getFormat()));
        String response = chatModel.call(prompt).getResult().getOutput().getContent();

        return converter.convert(response);
    }

    // ── 3e: Get structured key-value map ────────────────────────────────────
    public Map<String, Object> getAuthorFacts(String authorName) {
        // MapOutputConverter returns Map<String, Object>
        // Good for flexible structures without defining a record
        MapOutputConverter converter = new MapOutputConverter();

        PromptTemplate template = new PromptTemplate("""
                Provide factual information about the author {author}.
                Include: full name, birth year, nationality, famous works (as a list),
                and writing style description.
                {format}
                """);

        Prompt prompt = template.create(Map.of(
                "author", authorName,
                "format", converter.getFormat()
        ));

        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return converter.convert(response);
    }

    // ── 3f: Using TypeReference for generic types ───────────────────────────
    // For List<BookRecommendation> directly (without the wrapper record)
    public List<BookRecommendation> getRecommendationListDirect(String genre) {
        BeanOutputConverter<List<BookRecommendation>> converter =
                new BeanOutputConverter<>(
                        new org.springframework.core.ParameterizedTypeReference<>() {}
                );

        PromptTemplate template = new PromptTemplate("""
                Recommend 3 books in the {genre} genre.
                {format}
                """);

        Prompt prompt = template.create(Map.of(
                "genre", genre,
                "format", converter.getFormat()
        ));

        return converter.convert(
                chatModel.call(prompt).getResult().getOutput().getContent()
        );
    }
}

// =============================================================================
// SECTION 4: REST Controller for Structured Output
// =============================================================================

@RestController
@RequestMapping("/api/ai/structured")
class StructuredOutputController {

    private final StructuredOutputService service;

    StructuredOutputController(StructuredOutputService service) {
        this.service = service;
    }

    // GET /api/ai/structured/recommend?genre=mystery
    // Returns: { "title": "...", "author": "...", "genre": "...", ... }
    @GetMapping("/recommend")
    public BookRecommendation recommend(@RequestParam String genre) {
        return service.getStructuredRecommendation(genre);
    }

    // GET /api/ai/structured/recommend-list?genre=scifi&count=5
    // Returns a typed list of recommendations
    @GetMapping("/recommend-list")
    public BookRecommendationList recommendList(
            @RequestParam String genre,
            @RequestParam(defaultValue = "3") int count) {
        return service.getRecommendationList(genre, count);
    }

    // POST /api/ai/structured/analyze-reviews
    // Body: { "bookTitle": "Clean Code", "reviews": "Great book!..." }
    @PostMapping("/analyze-reviews")
    public BookReviewAnalysis analyzeReviews(@RequestBody Map<String, String> body) {
        return service.analyzeBookReviews(body.get("bookTitle"), body.get("reviews"));
    }

    // GET /api/ai/structured/author?name=Robert+C+Martin
    @GetMapping("/author")
    public Map<String, Object> authorFacts(@RequestParam String name) {
        return service.getAuthorFacts(name);
    }

    // GET /api/ai/structured/genres
    @GetMapping("/genres")
    public List<String> genres() {
        return service.getGenreList();
    }
}
