// =============================================================================
// Day 34 — Spring AI Part 2: Function Calling
// Bookstore Application — Giving the AI tools to act in the real world
//
// Topics covered:
//   1. What is function calling (tool calling)?
//   2. Defining functions as Spring @Bean
//   3. Registering functions with ChatClient
//   4. Multi-function tool use
//   5. Function calling with ChatModel directly
// =============================================================================

package com.bookstore.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

// =============================================================================
// SECTION 1: What Is Function Calling?
// =============================================================================
//
// By default, AI models only generate TEXT. They can't:
//   - Look up a price from a database
//   - Check current inventory
//   - Place an order
//   - Get today's date (their training data has a cutoff)
//
// Function calling (also called "tool calling") bridges this gap.
// The AI can call Java functions YOU define, then use the results
// to formulate its response.
//
// Flow:
//   1. You describe available functions to the model (name + description + params)
//   2. User sends a message: "Is 'Clean Code' in stock?"
//   3. Model decides to call checkInventory("Clean Code")
//   4. Spring AI calls your Java function and gets the result
//   5. Model formulates a response using the real data: "Yes, 5 copies in stock"
//
// The model NEVER directly accesses your DB — it requests a function call,
// Spring runs it, and passes the result back to the model.

// =============================================================================
// SECTION 2: Defining Functions
// =============================================================================
// Functions are plain Java records/classes implementing java.util.function.Function

// Request/response records for each function
// Records are ideal — Spring AI uses Jackson to serialize/deserialize them

record BookInventoryRequest(String title) {}
record BookInventoryResponse(String title, int quantity, boolean inStock) {}

record BookPriceRequest(String title) {}
record BookPriceResponse(String title, double price, String currency) {}

record BookSearchRequest(String genre, int maxResults) {}
record BookSearchResponse(List<String> titles, int totalFound) {}

// =============================================================================
// SECTION 3: Registering Functions as Spring Beans
// =============================================================================

@Configuration
class BookstoreFunctionConfig {

    // Each function is registered as a @Bean with a unique name.
    // Spring AI discovers them automatically from the application context.

    // ── Function 1: Check book inventory ────────────────────────────────────
    @Bean("checkInventory")
    public FunctionCallback checkInventoryFunction() {
        return FunctionCallbackWrapper.builder(
                // The actual Java function (lambda or method reference)
                (BookInventoryRequest request) -> {
                    // In a real app, this would call a repository
                    // Here we simulate with hardcoded data
                    int quantity = simulateInventoryLookup(request.title());
                    return new BookInventoryResponse(
                            request.title(),
                            quantity,
                            quantity > 0
                    );
                })
                .withName("checkInventory")
                // CRITICAL: The description tells the AI WHEN and HOW to use this function.
                // Write it clearly — the model reads this to decide if it should call it.
                .withDescription(
                        "Check how many copies of a book are currently in stock. " +
                        "Use this when a customer asks about book availability or stock levels."
                )
                // The input type tells Spring AI how to deserialize the model's JSON arguments
                .withInputType(BookInventoryRequest.class)
                .build();
    }

    // ── Function 2: Get book price ───────────────────────────────────────────
    @Bean("getBookPrice")
    public FunctionCallback getBookPriceFunction() {
        return FunctionCallbackWrapper.builder(
                (BookPriceRequest request) -> {
                    double price = simulatePriceLookup(request.title());
                    return new BookPriceResponse(request.title(), price, "USD");
                })
                .withName("getBookPrice")
                .withDescription(
                        "Look up the current price of a book. " +
                        "Use this when a customer asks how much a book costs or for pricing information."
                )
                .withInputType(BookPriceRequest.class)
                .build();
    }

    // ── Function 3: Search books by genre ───────────────────────────────────
    @Bean("searchBooksByGenre")
    public FunctionCallback searchByGenreFunction() {
        return FunctionCallbackWrapper.builder(
                (BookSearchRequest request) -> {
                    List<String> titles = simulateGenreSearch(request.genre(), request.maxResults());
                    return new BookSearchResponse(titles, titles.size());
                })
                .withName("searchBooksByGenre")
                .withDescription(
                        "Search for books by genre and return a list of available titles. " +
                        "Use when a customer wants to browse books in a specific genre."
                )
                .withInputType(BookSearchRequest.class)
                .build();
    }

    // Simulated data access — replace with real service/repository calls
    private int simulateInventoryLookup(String title) {
        return title.toLowerCase().contains("clean code") ? 5 :
               title.toLowerCase().contains("pragmatic") ? 0 : 3;
    }

    private double simulatePriceLookup(String title) {
        return title.toLowerCase().contains("clean code") ? 39.99 : 29.99;
    }

    private List<String> simulateGenreSearch(String genre, int max) {
        List<String> techBooks = List.of(
                "Clean Code", "The Pragmatic Programmer",
                "Refactoring", "Design Patterns", "Domain-Driven Design"
        );
        List<String> fictionBooks = List.of(
                "The Hitchhiker's Guide", "Ender's Game",
                "The Martian", "Project Hail Mary"
        );
        List<String> results = genre.toLowerCase().contains("tech") ? techBooks : fictionBooks;
        return results.stream().limit(max).toList();
    }
}

// =============================================================================
// SECTION 4: Using Functions with ChatClient
// =============================================================================

@Service
class FunctionCallingService {

    private final ChatClient chatClient;

    FunctionCallingService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        You are BookBot, a helpful bookstore assistant.
                        You have access to real-time tools to check inventory,
                        prices, and search for books. Use these tools when needed.
                        Always be friendly and helpful.
                        """)
                .build();
    }

    // Register which functions the AI can call for this request
    // The AI decides autonomously whether and when to call them
    public String handleCustomerQuery(String customerMessage) {
        return chatClient.prompt()
                .user(customerMessage)
                // .functions() registers function bean names from the context
                // The AI can call ANY of these based on what the user asks
                .functions("checkInventory", "getBookPrice", "searchBooksByGenre")
                .call()
                .content();
    }

    // Example interactions this handles:
    //   "How many copies of Clean Code do you have?" → calls checkInventory
    //   "How much is The Pragmatic Programmer?"      → calls getBookPrice
    //   "What tech books do you carry?"              → calls searchBooksByGenre
    //   "Is Clean Code in stock and what does it cost?" → calls BOTH functions
    //   "Tell me about your store"                   → no function call needed

    // ── Function calling with explicit ChatModel (lower-level) ──────────────
    private final ChatModel chatModel;

    FunctionCallingService(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatClient = builder.build();
        this.chatModel = chatModel;
    }

    public String handleQueryWithChatModel(String userMessage) {
        // With ChatModel directly, specify functions in ChatOptions
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withFunctionCallbacks(List.of(
                        FunctionCallbackWrapper.builder(
                                (BookInventoryRequest req) -> {
                                    int qty = req.title().contains("Clean Code") ? 5 : 2;
                                    return new BookInventoryResponse(req.title(), qty, qty > 0);
                                })
                                .withName("checkInventory")
                                .withDescription("Check book inventory levels.")
                                .withInputType(BookInventoryRequest.class)
                                .build()
                ))
                .build();

        Prompt prompt = new Prompt(new UserMessage(userMessage), options);
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}

// =============================================================================
// SECTION 5: Function Calling REST Controller
// =============================================================================

@RestController
@RequestMapping("/api/ai/tools")
class FunctionCallingController {

    private final FunctionCallingService service;

    FunctionCallingController(FunctionCallingService service) {
        this.service = service;
    }

    // POST /api/ai/tools/query
    // Body: { "message": "How many copies of Clean Code do you have?" }
    @PostMapping("/query")
    public java.util.Map<String, String> query(@RequestBody java.util.Map<String, String> body) {
        String response = service.handleCustomerQuery(body.get("message"));
        return java.util.Map.of("response", response);
    }
}
