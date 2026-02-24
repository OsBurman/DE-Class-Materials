// =============================================================================
// Day 34 — Spring AI Part 2: Configuration, Error Handling & Monitoring
// Bookstore Application
//
// Topics covered:
//   1. AI model configuration best practices
//   2. Catching and handling AiException
//   3. @Retryable with exponential backoff for rate limits
//   4. Fallback strategies (cached responses, default answers)
//   5. Resilience4j circuit breaker around AI calls
//   6. Monitoring with Micrometer and ObservationRegistry
//   7. Token usage logging from ChatResponse metadata
// =============================================================================

package com.bookstore.ai;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import io.micrometer.observation.ObservationRegistry;

import java.util.List;
import java.util.Map;

// =============================================================================
// SECTION 1: Configuration Best Practices
// =============================================================================
//
// Never hardcode API keys or model parameters. Use application.properties.
//
// ─── application.properties ──────────────────────────────────────────────────
//
//   # API keys from environment variables — NEVER commit keys to Git
//   spring.ai.openai.api-key=${OPENAI_API_KEY}
//
//   # Model selection
//   spring.ai.openai.chat.options.model=gpt-4o
//
//   # Temperature: 0.0 = deterministic, 1.0 = very creative
//   # Use LOW temperature for factual answers (catalog search, structured output)
//   # Use HIGHER temperature for creative tasks (book descriptions, story ideas)
//   spring.ai.openai.chat.options.temperature=0.3
//
//   # Max tokens limits cost and response length
//   # 1 token ≈ 0.75 words
//   spring.ai.openai.chat.options.max-tokens=800
//
//   # Timeout — prevent hanging requests (milliseconds)
//   spring.ai.openai.chat.options.timeout=30000
//
//   # Embedding model for RAG
//   spring.ai.openai.embedding.options.model=text-embedding-3-small
//
// ─── Per-request options override application.properties ─────────────────────
//
//   OpenAiChatOptions requestOptions = OpenAiChatOptions.builder()
//       .withModel("gpt-4o-mini")      // cheaper model for quick summaries
//       .withTemperature(0.7f)
//       .withMaxTokens(200)
//       .build();
//
//   chatModel.call(new Prompt("...", requestOptions));
//
// ─── Profile-based config ─────────────────────────────────────────────────────
//
//   # application-dev.properties
//   spring.ai.openai.chat.options.model=gpt-4o-mini    # cheap for dev
//
//   # application-prod.properties
//   spring.ai.openai.chat.options.model=gpt-4o         # full model in prod
//
// =============================================================================
// SECTION 2: @Configuration with ChatClient Defaults
// =============================================================================

@Configuration
class AiConfiguration {

    // Inject model name and temperature from application.properties
    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    @Value("${app.ai.temperature:0.3}")
    private float temperature;

    @Value("${app.ai.max-tokens:800}")
    private int maxTokens;

    @Bean
    public ChatClient bookstoreAiClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are an expert bookstore assistant for "The Page Turner" bookstore.
                        You help customers find books, answer questions about our catalog,
                        and provide reading recommendations. Keep responses concise and helpful.
                        Always recommend real books with accurate information.
                        """)
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .withModel(modelName)
                                .withTemperature(temperature)
                                .withMaxTokens(maxTokens)
                                .build()
                )
                .build();
    }
}

// =============================================================================
// SECTION 3: Error Handling — AiException and Rate Limits
// =============================================================================
//
// Spring AI wraps all provider errors in AiException (or subclasses):
//
//   AiException                    ← base exception
//   ├─ RateLimitException          ← 429 Too Many Requests
//   ├─ InvalidApiKeyException      ← 401 Unauthorized
//   ├─ ResourceAccessException     ← network timeout
//   └─ InvalidRequestException     ← bad prompt, model not found, etc.

@EnableRetry
@Service
class RobustAiService {

    private final ChatModel chatModel;
    private final ChatClient chatClient;
    private final AiFallbackService fallbackService;

    RobustAiService(ChatModel chatModel, ChatClient.Builder builder,
                    AiFallbackService fallbackService) {
        this.chatModel = chatModel;
        this.chatClient = builder.build();
        this.fallbackService = fallbackService;
    }

    // ── 3a: Basic try-catch on AiException ───────────────────────────────────
    public String getRecommendationSafe(String genre) {
        try {
            return chatClient.prompt()
                    .user("Recommend a " + genre + " book.")
                    .call()
                    .content();
        } catch (org.springframework.ai.retry.NonTransientAiException e) {
            // Non-retryable: bad API key, invalid request, content policy violation
            // Log and surface a user-friendly error
            System.err.println("Permanent AI error: " + e.getMessage());
            return "Unable to process your request. Please try again later.";
        } catch (Exception e) {
            // Transient: network issues, timeouts
            System.err.println("Transient AI error: " + e.getMessage());
            return fallbackService.getFallbackRecommendation(genre);
        }
    }

    // ── 3b: @Retryable — automatic retry with exponential backoff ────────────
    // This is perfect for rate limit errors (429):
    //   - First retry after 2 seconds
    //   - Second retry after 4 seconds
    //   - Third retry after 8 seconds
    //   - If all retries fail, the exception propagates
    @Retryable(
            retryFor = { org.springframework.web.client.ResourceAccessException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)  // 2s, 4s, 8s
    )
    public String getRecommendationWithRetry(String genre) {
        return chatClient.prompt()
                .user("Recommend a " + genre + " book with a brief description.")
                .call()
                .content();
    }

    // ── 3c: Circuit breaker with Resilience4j ────────────────────────────────
    // If the AI provider is consistently failing, the circuit "opens":
    //   - Closed state: requests flow through normally
    //   - Open state:   requests immediately go to fallback (AI is bypassed)
    //   - Half-open:    occasional test requests check if AI is back up
    //
    // application.properties:
    //   resilience4j.circuitbreaker.instances.ai-service.failure-rate-threshold=50
    //   resilience4j.circuitbreaker.instances.ai-service.wait-duration-in-open-state=30s
    //   resilience4j.circuitbreaker.instances.ai-service.sliding-window-size=10
    @CircuitBreaker(name = "ai-service", fallbackMethod = "circuitBreakerFallback")
    public String getRecommendationCircuitBreaker(String genre) {
        return chatClient.prompt()
                .user("Recommend a " + genre + " book.")
                .call()
                .content();
    }

    // Called automatically by Resilience4j when circuit is open or call fails
    public String circuitBreakerFallback(String genre, Throwable ex) {
        System.err.println("Circuit breaker triggered: " + ex.getMessage());
        return fallbackService.getFallbackRecommendation(genre);
    }
}

// =============================================================================
// SECTION 4: Fallback Service
// =============================================================================
//
// Always have a fallback strategy for AI failures:
//   - Return a cached response
//   - Return a pre-written default answer
//   - Return data from a traditional (non-AI) database query

@Service
class AiFallbackService {

    // Static fallbacks per genre — loaded from DB or config in real app
    private static final Map<String, String> FALLBACK_RECOMMENDATIONS = Map.of(
            "programming", "We recommend 'Clean Code' by Robert C. Martin — a timeless classic.",
            "fiction",     "We recommend 'The Great Gatsby' by F. Scott Fitzgerald.",
            "science",     "We recommend 'A Brief History of Time' by Stephen Hawking.",
            "fantasy",     "We recommend 'The Name of the Wind' by Patrick Rothfuss."
    );

    public String getFallbackRecommendation(String genre) {
        return FALLBACK_RECOMMENDATIONS.getOrDefault(
                genre.toLowerCase(),
                "Our AI assistant is temporarily unavailable. " +
                "Please visit our website for book recommendations."
        );
    }

    // Cache-backed fallback — real apps use Redis/Caffeine
    // Returns the last known good AI response for this query
    public String getCachedOrDefault(String cacheKey) {
        // In production: return cacheService.get(cacheKey).orElse(defaultResponse)
        return "Our recommendation service is temporarily unavailable. Please try again shortly.";
    }
}

// =============================================================================
// SECTION 5: Monitoring — Token Usage and Metrics
// =============================================================================

@Service
class AiMonitoringService {

    private final ChatModel chatModel;

    // ObservationRegistry is auto-configured by Spring AI + Micrometer
    // Spring AI automatically creates observations (spans/metrics) for every call
    private final ObservationRegistry observationRegistry;

    AiMonitoringService(ChatModel chatModel, ObservationRegistry observationRegistry) {
        this.chatModel = chatModel;
        this.observationRegistry = observationRegistry;
    }

    // ── 5a: Token usage logging ───────────────────────────────────────────────
    // ChatResponse includes metadata about the AI call (tokens used, model used)
    public String callWithTokenLogging(String question) {
        Prompt prompt = new Prompt("Answer concisely: " + question);

        // Use chatModel directly to get the full ChatResponse object
        ChatResponse response = chatModel.call(prompt);

        // Extract token usage metadata
        var usage = response.getMetadata().getUsage();
        if (usage != null) {
            System.out.printf(
                    "[AI Metrics] model=%s | prompt_tokens=%d | completion_tokens=%d | total_tokens=%d%n",
                    response.getMetadata().getModel(),
                    usage.getPromptTokens(),
                    usage.getGenerationTokens(),
                    usage.getTotalTokens()
            );
        }

        return response.getResult().getOutput().getContent();
    }

    // ── 5b: Manual observation / custom metric ────────────────────────────────
    // Create custom observations for business-level tracking
    // (e.g., track how often customers ask for recommendations vs. book info)
    public String callWithObservation(String category, String question) {
        return io.micrometer.observation.Observation
                .createNotStarted("bookstore.ai." + category, observationRegistry)
                .lowCardinalityKeyValue("category", category)
                .observe(() -> {
                    ChatResponse response = chatModel.call(new Prompt(question));
                    return response.getResult().getOutput().getContent();
                });
    }

    // ── 5c: Structured logging of AI interactions ─────────────────────────────
    // Log input + output for debugging and compliance auditing
    public String callWithAuditLogging(String userId, String question) {
        long startMs = System.currentTimeMillis();

        String answer = chatModel.call(question);

        long durationMs = System.currentTimeMillis() - startMs;

        // In production, send to your logging platform (Splunk, CloudWatch, ELK)
        System.out.printf(
                "[AI Audit] user=%s | question=\"%s\" | answer_length=%d | duration_ms=%d%n",
                userId,
                question.length() > 80 ? question.substring(0, 80) + "..." : question,
                answer.length(),
                durationMs
        );

        return answer;
    }
}

// =============================================================================
// SECTION 6: application.properties — Full Production Config Reference
// =============================================================================
//
// # ── Model ───────────────────────────────────────────────────────────────────
// spring.ai.openai.api-key=${OPENAI_API_KEY}
// spring.ai.openai.chat.options.model=gpt-4o
// spring.ai.openai.chat.options.temperature=0.3
// spring.ai.openai.chat.options.max-tokens=800
//
// # ── Retry (Spring AI built-in) ───────────────────────────────────────────
// spring.ai.retry.max-attempts=3
// spring.ai.retry.backoff.initial-interval=2000
// spring.ai.retry.backoff.multiplier=2
// spring.ai.retry.backoff.max-interval=30000
// spring.ai.retry.on-http-codes=429,500,503
//
// # ── Circuit Breaker (Resilience4j) ───────────────────────────────────────
// resilience4j.circuitbreaker.instances.ai-service.failure-rate-threshold=50
// resilience4j.circuitbreaker.instances.ai-service.wait-duration-in-open-state=30s
// resilience4j.circuitbreaker.instances.ai-service.sliding-window-size=10
//
// # ── Observability / Metrics ──────────────────────────────────────────────
// management.endpoints.web.exposure.include=health,metrics,prometheus
// management.metrics.export.prometheus.enabled=true
// spring.ai.chat.observations.include-prompt=true    # include prompt in traces
// spring.ai.chat.observations.include-completion=true
//
// # ── Vector Store (PgVector for prod) ─────────────────────────────────────
// spring.ai.vectorstore.pgvector.initialize-schema=true
// spring.ai.vectorstore.pgvector.dimensions=1536
// spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE

// =============================================================================
// SECTION 7: REST Controller — Resilient Endpoints
// =============================================================================

@RestController
@RequestMapping("/api/ai/resilient")
class ResilientAiController {

    private final RobustAiService robustService;
    private final AiMonitoringService monitoringService;

    ResilientAiController(RobustAiService robustService,
                          AiMonitoringService monitoringService) {
        this.robustService = robustService;
        this.monitoringService = monitoringService;
    }

    // GET /api/ai/resilient/recommend?genre=fantasy
    @GetMapping("/recommend")
    public String recommend(@RequestParam String genre) {
        return robustService.getRecommendationSafe(genre);
    }

    // GET /api/ai/resilient/recommend-retry?genre=programming
    @GetMapping("/recommend-retry")
    public String recommendWithRetry(@RequestParam String genre) {
        return robustService.getRecommendationWithRetry(genre);
    }

    // GET /api/ai/resilient/ask?question=...&userId=user123
    @GetMapping("/ask")
    public String askWithLogging(
            @RequestParam String question,
            @RequestParam(defaultValue = "anonymous") String userId) {
        return monitoringService.callWithAuditLogging(userId, question);
    }

    // GET /api/ai/resilient/token-usage?question=...
    @GetMapping("/token-usage")
    public String tokenUsage(@RequestParam String question) {
        return monitoringService.callWithTokenLogging(question);
    }
}
