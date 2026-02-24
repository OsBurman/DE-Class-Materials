# Day 34 Review — Spring AI
## Quick Reference Guide

---

## 1. Spring AI Overview

Spring AI is a Spring project that provides a **portable abstraction layer** over major AI providers. Write code once against Spring AI interfaces; switch providers by changing a dependency and YAML.

**Core Interfaces:**

| Interface | Purpose |
|-----------|---------|
| `ChatModel` | Send prompts → receive text responses |
| `EmbeddingModel` | Text → float[] vector |
| `ImageModel` | Generate images |
| `VectorStore` | Store and search embedded documents |
| `ChatClient` | High-level fluent API wrapping `ChatModel` |

---

## 2. BOM and Starter Setup

```xml
<!-- dependencyManagement — version managed here -->
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-bom</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
  <scope>import</scope>
</dependency>

<!-- dependencies — no version needed -->
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

Each starter auto-configures: `ChatModel`, `ChatClient.Builder`, `EmbeddingModel`, provider-specific `ChatOptions`.

---

## 3. ChatModel — Low-Level API

```java
@Service
public class SimpleAiService {
    private final ChatModel chatModel;

    // String convenience overload
    public String ask(String question) {
        return chatModel.call(question);   // wraps in UserMessage automatically
    }

    // Full Prompt with metadata access
    public String askWithUsage(String question) {
        ChatResponse response = chatModel.call(new Prompt(new UserMessage(question)));
        Usage usage = response.getMetadata().getUsage();
        log.info("Tokens used: {}", usage.getTotalTokens());
        return response.getResult().getOutput().getContent();
    }
}
```

**Response Object Hierarchy:**
```
ChatResponse → List<Generation> → Generation
  ├── AssistantMessage  (getContent() → String)
  └── ChatGenerationMetadata (finishReason, usage)
```

---

## 4. Message Types

| Class | Role | Purpose |
|-------|------|---------|
| `SystemMessage` | `system` | Set persona, rules, output format |
| `UserMessage` | `user` | Human input |
| `AssistantMessage` | `assistant` | AI response (inject into history) |

```java
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a Java expert. Be concise."),
    new UserMessage("Explain virtual threads in one paragraph.")
));
```

---

## 5. EmbeddingModel

```java
// Single text → float[]
float[] vector = embeddingModel.embed("Hello world");

// Batch — more efficient (one API call)
EmbeddingResponse resp = embeddingModel.embedForResponse(List.of("text1", "text2"));
float[] v1 = resp.getResults().get(0).getOutput();
```

**Configuration (OpenAI):**
```yaml
spring.ai.openai.embedding.options.model: text-embedding-3-small  # 1536 dimensions
```

The dimension count must match your `VectorStore` configuration exactly.

---

## 6. ChatClient — Basic Usage

```java
// Create in @Configuration
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a helpful assistant.")
        .build();
}

// Use in @Service
String answer = chatClient
    .prompt()
    .user("What is Spring AI?")
    .call()
    .content();    // returns String

// Access full response (with token usage)
ChatResponse full = chatClient.prompt().user("...").call().chatResponse();
```

---

## 7. ChatClient — System Override and Message List

```java
// Override system prompt per call
chatClient.prompt()
    .system("You are a code reviewer.")
    .user(code)
    .call().content();

// Pass full message list (manual history)
chatClient.prompt()
    .messages(List.of(systemMsg, userMsg1, assistantMsg1, userMsg2))
    .call().content();
```

---

## 8. ChatClient — Streaming (Flux / SSE)

```java
// Returns Flux<String> — emits tokens as generated
Flux<String> stream = chatClient.prompt().user(question).stream().content();

// SSE endpoint
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam String q) {
    return chatService.streamAnswer(q);
}
```

---

## 9. Built-in Advisors

| Advisor | Purpose | How to Add |
|---------|---------|-----------|
| `SimpleLoggerAdvisor` | Log all prompts + responses at DEBUG | `.defaultAdvisors(new SimpleLoggerAdvisor())` |
| `MessageChatMemoryAdvisor` | Inject conversation history automatically | `.defaultAdvisors(new MessageChatMemoryAdvisor(memory))` |
| `QuestionAnswerAdvisor` | RAG: retrieve docs, augment prompt | `.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))` |

Custom advisors: implement `CallAroundAdvisor` interface.

---

## 10. PromptTemplate — Variable Substitution

```java
// Inline template
PromptTemplate template = new PromptTemplate(
    "You are a {role}. Answer: {question}"
);
Prompt prompt = template.create(Map.of("role", "Java expert", "question", q));

// From classpath file
@Value("classpath:prompts/review.st")
private Resource reviewPrompt;

Prompt p = new PromptTemplate(reviewPrompt).create(Map.of("code", code));
```

> **Rule:** Never concatenate user input into prompts. Always use `PromptTemplate` with `{variables}`.

---

## 11. Model Provider Configuration

```yaml
# OpenAI
spring.ai.openai.api-key: ${OPENAI_API_KEY}
spring.ai.openai.chat.options.model: gpt-4o
spring.ai.openai.chat.options.temperature: 0.7

# Anthropic
spring.ai.anthropic.api-key: ${ANTHROPIC_API_KEY}
spring.ai.anthropic.chat.options.model: claude-3-5-sonnet-20241022

# Ollama (local — no API key)
spring.ai.ollama.base-url: http://localhost:11434
spring.ai.ollama.chat.options.model: llama3.2
```

**Provider Starters:**

| Provider | Starter Artifact Suffix |
|----------|------------------------|
| OpenAI | `spring-ai-openai-spring-boot-starter` |
| Anthropic | `spring-ai-anthropic-spring-boot-starter` |
| Azure OpenAI | `spring-ai-azure-openai-spring-boot-starter` |
| Ollama (local) | `spring-ai-ollama-spring-boot-starter` |
| Google Vertex AI | `spring-ai-vertex-ai-gemini-spring-boot-starter` |

---

## 12. Building AI Services — the @Service Pattern

```java
@Service
public class BookAiService {

    private final ChatClient chatClient;

    public BookAiService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("You are a literary expert. Be concise.")
            .build();
    }

    public String summarize(String title, String author) {
        return chatClient.prompt()
            .user(u -> u.text("Summarize '{title}' by {author} in 3 sentences.")
                        .param("title", title).param("author", author))
            .call().content();
    }
}
```

> Spring AI does not have `@AiService`. This `@Service` + `ChatClient` pattern is the Spring AI equivalent.

---

## 13. Conversation Memory

```java
// Config
@Bean ChatMemory chatMemory() { return new InMemoryChatMemory(); }

// Service — advisor handles history automatically
ChatClient client = builder
    .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
    .build();

String response = client.prompt()
    .user(message)
    .advisors(a -> a.param(
        AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
    .call().content();
```

**Memory Implementations:**

| Implementation | Storage | Use Case |
|----------------|---------|---------|
| `InMemoryChatMemory` | JVM heap | Dev / single-instance |
| Custom `JdbcChatMemory` | Database | Production, persistent |
| Custom `RedisChatMemory` | Redis | High-traffic, distributed |

---

## 14. ChatOptions — Temperature and Token Control

```java
OpenAiChatOptions options = OpenAiChatOptions.builder()
    .withModel("gpt-4o-mini")
    .withTemperature(0.2f)
    .withMaxTokens(200)
    .build();

chatClient.prompt().user(q).options(options).call().content();
```

**Temperature Reference:**

| Range | Behavior | Use Case |
|-------|----------|---------|
| 0.0–0.3 | Deterministic, factual | Code gen, data extraction, Q&A |
| 0.4–0.7 | Balanced | Chat, summaries, explanations |
| 0.8–1.2 | Creative | Brainstorming, creative writing |

---

## 15. Function Calling — @Tool

```java
@Component
public class BookstoreTools {

    @Tool(description = "Find books by an author name. Returns title, ISBN, price.")
    public List<BookSummary> findBooksByAuthor(
            @ToolParam(description = "Full name of the author") String name) {
        return bookRepo.findByAuthor(name).stream()
            .map(BookSummary::from).collect(Collectors.toList());
    }
}

// Usage in service
chatClient.prompt()
    .user(userMessage)
    .tools(bookstoreTools)    // pass the @Component
    .call().content();
```

**Flow:** User message → AI reasons → tool call request → Spring AI calls your method → result returned to AI → final response.

---

## 16. Function Calling — FunctionCallback (Programmatic)

```java
FunctionCallback weather = FunctionCallback.builder()
    .function("getWeather", (String city) -> weatherApi.getCurrent(city))
    .description("Get current weather for a city")
    .inputType(String.class)
    .build();

chatClient.prompt().user(q)
    .toolCallbacks(List.of(weather))
    .call().content();
```

---

## 17. Structured Output — BeanOutputConverter

```java
public record BookRecommendation(String title, String author, String isbn, String reason) {}

BeanOutputConverter<BookRecommendation> converter =
    new BeanOutputConverter<>(BookRecommendation.class);

Prompt prompt = new PromptTemplate("Recommend a {topic} book.\n{format}")
    .create(Map.of("topic", "microservices", "format", converter.getFormat()));

BookRecommendation book = converter.convert(
    chatModel.call(prompt).getResult().getOutput().getContent()
);
```

---

## 18. Structured Output — ChatClient Entity API

```java
// Single object
BookRecommendation book = chatClient.prompt()
    .user("Recommend a book about distributed systems")
    .call()
    .entity(BookRecommendation.class);

// List
List<BookRecommendation> books = chatClient.prompt()
    .user("Recommend 5 books about clean code")
    .call()
    .entity(new ParameterizedTypeReference<List<BookRecommendation>>() {});
```

---

## 19. RAG — Two-Phase Overview

```
INGESTION:   Documents → Read → Split → Embed → VectorStore.add()
QUERY:       Question → Embed → VectorStore.search() → Augment Prompt → AI → Answer
```

Spring AI automates the query phase with `QuestionAnswerAdvisor`.

---

## 20. Document Readers and Splitters

```java
// Read PDF, Word, HTML, plain text
List<Document> docs = new TikaDocumentReader(new ClassPathResource("guide.pdf")).get();

// Read JSON — specify path to text field
List<Document> json = new JsonReader(resource, "description").get();

// Add metadata
docs.forEach(d -> d.getMetadata().put("source", "guide.pdf"));

// Split into chunks
TokenTextSplitter splitter = new TokenTextSplitter();   // defaults: ~800 tokens/chunk
List<Document> chunks = splitter.apply(docs);

// Store (embed + save)
vectorStore.add(chunks);
```

---

## 21. SimpleVectorStore — Development

```java
@Bean
@Profile("dev")
VectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
}
```

- In-memory, no external dependency
- Optional file persistence: `store.save(new File("vs.json"))`
- Linear scan — not suitable for thousands of documents

---

## 22. PgVectorStore — Production

```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>
```

```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536          # must match embedding model
        initialize-schema: true
```

Docker: `image: pgvector/pgvector:pg16`

---

## 23. Manual Similarity Search

```java
List<Document> results = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("What is Spring AI?")
        .topK(5)
        .similarityThreshold(0.70)
        .build()
);
```

---

## 24. QuestionAnswerAdvisor — Auto RAG

```java
@Bean
ChatClient ragClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
        .defaultSystem("Answer based on provided context. Say 'I don't know' if insufficient.")
        .defaultAdvisors(
            new QuestionAnswerAdvisor(vectorStore,
                SearchRequest.builder().topK(4).similarityThreshold(0.65).build())
        )
        .build();
}

// Usage — identical to regular ChatClient
String answer = ragClient.prompt().user(question).call().content();
```

**Tuning:**
- Increase `topK` when broad context is needed
- Lower `similarityThreshold` when retrieval is too strict
- Raise `similarityThreshold` when unrelated content is injected

---

## 25. Error Handling — Exception Hierarchy

| Exception | Cause | Action |
|-----------|-------|--------|
| `NonTransientAiException` | Bad API key, invalid request, policy violation | Log and fail fast; don't retry |
| `TransientAiException` | Rate limit (429), timeout, 503 | Retry with backoff |

---

## 26. Spring AI Built-In Retry

```yaml
spring:
  ai:
    retry:
      max-attempts: 3
      on-http-codes: 429,503
      exclude-on-http-codes: 401
      backoff:
        initial-interval: 1000
        multiplier: 2
        max-interval: 30000
```

No code required — auto-applied to all provider calls.

---

## 27. Fallback Strategies

```java
// Strategy 1: Simple fallback message
try {
    return chatClient.prompt().user(q).call().content();
} catch (AiException e) {
    return "Service temporarily unavailable. Please try again.";
}

// Strategy 2: Fallback to local Ollama model
try {
    return primaryClient.prompt().user(q).call().content();
} catch (AiException e) {
    return localClient.prompt().user(q).call().content();
}

// Strategy 3: Resilience4j circuit breaker
@CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
public String ask(String q) { return chatClient.prompt().user(q).call().content(); }

public String fallback(String q, Throwable t) {
    return "AI service temporarily unavailable.";
}
```

---

## 28. Monitoring — Micrometer Metrics

Requires: `spring-boot-starter-actuator` on classpath — no other configuration.

**Auto-Configured Metrics:**

| Metric | Type | Measures |
|--------|------|---------|
| `gen_ai.client.operation.duration` | Timer | End-to-end AI call latency |
| `gen_ai.client.token.usage` | Distribution Summary | Tokens per call |

**Automatic Tags:** `gen_ai.system`, `gen_ai.request.model`, `gen_ai.operation.name`

```yaml
management:
  endpoints.web.exposure.include: health, metrics, prometheus
  metrics.distribution.percentiles-histogram:
    gen_ai.client.operation.duration: true
```

---

## 29. Logging — SimpleLoggerAdvisor (Development)

```java
builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
```

```yaml
logging.level.org.springframework.ai: DEBUG
```

Logs full prompt + response at DEBUG. Disable in production.

---

## 30. Custom Audit Logging Advisor (Production)

```java
@Component
public class AuditLoggingAdvisor implements CallAroundAdvisor {

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        long start = System.currentTimeMillis();
        AdvisedResponse resp = chain.nextAroundCall(req);
        Usage usage = resp.response().getMetadata().getUsage();
        log.info("AI_AUDIT | tokens={} | latency={}ms | model='{}'",
            usage.getTotalTokens(),
            System.currentTimeMillis() - start,
            resp.response().getMetadata().getModel());
        return resp;
    }

    @Override public String getName() { return "AuditLoggingAdvisor"; }
    @Override public int getOrder() { return 0; }
}
```

---

## 31. Full Production Architecture

```
REST Controller
      │
@Service (ChatClient)
  ├── MessageChatMemoryAdvisor   → InMemoryChatMemory / JdbcChatMemory
  ├── QuestionAnswerAdvisor      → PgVectorStore ← Ingestion Pipeline
  ├── AuditLoggingAdvisor        → Micrometer / Logs
  └── ChatModel                  → OpenAI / Anthropic / Azure
        └── @CircuitBreaker (Resilience4j)
              └── fallback → Ollama (local)
```

---

## 32. API Key Security — Quick Reference

| Environment | Storage Method |
|-------------|---------------|
| Developer machine | `.env` file (in `.gitignore`) |
| CI/CD | GitHub Actions Secrets / Jenkins Credentials |
| AWS production | AWS Secrets Manager |
| Kubernetes | Kubernetes Secret → env var |

Always reference as `${ENV_VAR_NAME}` in `application.yml`. Never hardcode.

---

## 33. Cost Management — Model Selection Guide

| Task | Recommended Model | Why |
|------|------------------|-----|
| Complex reasoning, long code gen | `gpt-4o` | Highest capability |
| Simple chat, short answers, classification | `gpt-4o-mini` | ~15× cheaper |
| Development / offline / free | Ollama (`llama3.2`) | No cost, local |
| High-accuracy embeddings | `text-embedding-3-large` | 3072 dimensions |
| Balanced embeddings (most use cases) | `text-embedding-3-small` | 1536 dimensions, cost-effective |
