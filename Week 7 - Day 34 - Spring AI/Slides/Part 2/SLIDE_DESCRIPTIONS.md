# Day 34 Part 2 — Spring AI: Function Calling, Structured Output, RAG, and Production Concerns
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 34 Part 2: Function Calling, Structured Output, RAG, and Production AI

**Subtitle:** Tools, structured responses, document retrieval, error handling, and observability

**Learning Objectives:**
- Implement function calling with `@Tool` so the AI can invoke Java methods
- Parse AI responses directly into Java objects with structured output converters
- Understand the RAG pipeline: document ingestion, vector storage, and retrieval
- Ingest documents using `TikaDocumentReader` and `TokenTextSplitter`
- Configure `SimpleVectorStore` (dev) and `PgVectorStore` (production)
- Wire `QuestionAnswerAdvisor` for automatic RAG on every chat request
- Configure AI model options in `application.yml` and manage API keys securely
- Handle `TransientAiException` and `NonTransientAiException` with retry and fallback
- Apply circuit breaker pattern to AI service calls with Resilience4j
- Monitor AI interactions with Micrometer metrics and `SimpleLoggerAdvisor`

---

### Slide 2 — Function Calling — What It Is and Why It Matters

**Title:** Function Calling — Giving the AI the Ability to Act

**Content:**

Without function calling, an AI model can only generate text. It cannot look up live data, query a database, send an email, or interact with any external system. Function calling closes this gap.

**How Function Calling Works:**

```
1. Developer registers available functions/tools with descriptions
2. User sends a message: "What's the current price of the Java book?"
3. AI analyzes the request and determines it needs the getBookPrice function
4. AI returns a structured JSON response: { "function": "getBookPrice", "args": {"isbn": "978-0..."} }
5. Spring AI intercepts this, calls your actual Java method with those arguments
6. Your Java method executes (queries database, calls external API, etc.)
7. Result is sent back to the AI as a tool result message
8. AI incorporates the result and generates the final text response for the user
```

**The AI–Tool Loop:**
```
User Input
    │
    ▼
AI Reasoning: "Do I need a tool?"
    │                │
    │ No             │ Yes
    ▼                ▼
Generate         Tool Call Request
Response         (function + args)
                     │
                     ▼
                Execute Java Method
                     │
                     ▼
                Return Result to AI
                     │
                     ▼
               Generate Response
               with Real Data
```

**Use Cases:**

| Use Case | Function |
|----------|---------|
| Live data | `getStockPrice(symbol)`, `getCurrentWeather(city)` |
| Database queries | `findBooksByAuthor(authorName)`, `checkInventory(isbn)` |
| External actions | `sendConfirmationEmail(orderId)`, `createTicket(description)` |
| Internal calculation | `calculateShippingCost(weight, destination)` |

**Key Insight:** The AI decides when to call a function. You describe what each function does. The AI figures out if and when the description matches the user's need. It will not call `sendEmail` when the user is just asking a general question.

---

### Slide 3 — Function Calling — Spring AI Implementation

**Title:** Implementing Tools with `@Tool` and `FunctionCallback`

**Content:**

**Method 1: `@Tool` Annotation (Recommended)**

Create a Spring `@Component` and annotate individual methods with `@Tool`:

```java
@Component
public class BookstoreTools {

    private final BookRepository bookRepository;
    private final InventoryService inventoryService;

    public BookstoreTools(BookRepository bookRepository,
                          InventoryService inventoryService) {
        this.bookRepository = bookRepository;
        this.inventoryService = inventoryService;
    }

    @Tool(description = "Find books by a given author name. Returns a list of books with title and ISBN.")
    public List<BookSummary> findBooksByAuthor(
            @ToolParam(description = "The full name of the author") String authorName) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorName)
            .stream()
            .map(b -> new BookSummary(b.getTitle(), b.getIsbn(), b.getPrice()))
            .collect(Collectors.toList());
    }

    @Tool(description = "Check the current inventory quantity for a book by its ISBN.")
    public int checkInventory(
            @ToolParam(description = "The ISBN-13 of the book") String isbn) {
        return inventoryService.getQuantityByIsbn(isbn);
    }

    @Tool(description = "Get the current price of a book by its ISBN.")
    public BigDecimal getBookPrice(
            @ToolParam(description = "The ISBN-13 of the book") String isbn) {
        return bookRepository.findByIsbn(isbn)
            .map(Book::getPrice)
            .orElseThrow(() -> new BookNotFoundException("ISBN not found: " + isbn));
    }
}
```

**Using Tools with ChatClient:**
```java
@Service
public class BookstoreAssistant {

    private final ChatClient chatClient;
    private final BookstoreTools tools;

    public BookstoreAssistant(ChatClient.Builder builder, BookstoreTools tools) {
        this.chatClient = builder
            .defaultSystem(
                "You are a helpful bookstore assistant. You can look up books, check " +
                "prices, and verify inventory. Always provide accurate information " +
                "by using the available tools."
            )
            .build();
        this.tools = tools;
    }

    public String assist(String userMessage) {
        return chatClient
            .prompt()
            .user(userMessage)
            .tools(tools)          // register the @Tool methods with this call
            .call()
            .content();
    }
}
```

**Method 2: `FunctionCallback` (Programmatic, No Spring Component Needed)**
```java
// Useful for lambdas, external service calls, simple functions
FunctionCallback weatherTool = FunctionCallback.builder()
    .function("getWeather",
        (String city) -> weatherApi.fetchCurrent(city))
    .description("Get the current weather conditions for a city")
    .inputType(String.class)
    .build();

String response = chatClient
    .prompt()
    .user("What's the weather in Tokyo?")
    .toolCallbacks(List.of(weatherTool))
    .call()
    .content();
```

**Tool Description Best Practices:**
- Write descriptions as if explaining to a human: "What does this function do?"
- Include what the inputs represent and what format they should be in
- Mention what is returned
- Be specific enough that the AI won't call the wrong function

---

### Slide 4 — Structured Output — Converters

**Title:** Structured Output — Parsing AI Responses Into Java Objects

**Content:**

AI models return text. If you want a Java object back — a `Book` record, a `List<Recommendation>`, a `Map<String, String>` — you need structured output converters.

**The Problem Without Structured Output:**
```java
String json = chatModel.call("Give me a book recommendation as JSON");
// json might be:
// "Here is a recommendation:\n```json\n{\"title\":\"Clean Code\",...}\n```"
// Or:
// {"title": "Clean Code", "author": "Robert Martin", "isbn": "978-..."}
// You have to manually strip markdown, parse JSON, handle variations
```

**`BeanOutputConverter` — Parse Into a Java Record/Class:**

```java
// Define your target type as a record
public record BookRecommendation(
    String title,
    String author,
    String isbn,
    String reason          // why this book was recommended
) {}

// Use BeanOutputConverter
BeanOutputConverter<BookRecommendation> converter =
    new BeanOutputConverter<>(BookRecommendation.class);

// .getFormat() returns a JSON Schema instruction string that gets appended to the prompt
// This tells the AI exactly what JSON structure to produce
String formatInstruction = converter.getFormat();

PromptTemplate template = new PromptTemplate(
    "Recommend a book about {topic}.\n\n{format}"
);
Prompt prompt = template.create(Map.of(
    "topic", "clean code practices",
    "format", formatInstruction
));

ChatResponse response = chatModel.call(prompt);
BookRecommendation book = converter.convert(
    response.getResult().getOutput().getContent()
);
// book is now a fully populated BookRecommendation record
```

**`ListOutputConverter` — Parse Into a `List<String>`:**
```java
ListOutputConverter converter = new ListOutputConverter(new DefaultConversionService());

Prompt prompt = new PromptTemplate(
    "List 5 popular Java frameworks. {format}"
).create(Map.of("format", converter.getFormat()));

ChatResponse response = chatModel.call(prompt);
List<String> frameworks = converter.convert(
    response.getResult().getOutput().getContent()
);
// ["Spring Boot", "Quarkus", "Micronaut", "Helidon", "Jakarta EE"]
```

**`MapOutputConverter` — Parse Into `Map<String, Object>`:**
```java
MapOutputConverter converter = new MapOutputConverter();

Prompt prompt = new PromptTemplate(
    "Give me 3 key Java 21 features with one-line descriptions. {format}"
).create(Map.of("format", converter.getFormat()));

Map<String, Object> features = converter.convert(
    chatModel.call(prompt).getResult().getOutput().getContent()
);
// {"Virtual Threads": "...", "Pattern Matching": "...", "Sequenced Collections": "..."}
```

---

### Slide 5 — Structured Output — ChatClient Entity API

**Title:** `ChatClient.entity()` — The Simplest Path to Structured Output

**Content:**

While `BeanOutputConverter` works with `ChatModel` directly, `ChatClient` provides an even cleaner API: the `.entity()` call at the end of the fluent chain. It handles the format injection and parsing internally.

**`.entity(Class<T>)` — Single Object:**
```java
public record BookRecommendation(
    String title,
    String author,
    String isbn,
    String reasonToRead
) {}

// ChatClient handles format injection and parsing — no converter code needed
BookRecommendation book = chatClient
    .prompt()
    .user("Recommend a book about microservices architecture")
    .call()
    .entity(BookRecommendation.class);     // ← specify the target type

System.out.println(book.title());         // "Building Microservices"
System.out.println(book.author());        // "Sam Newman"
```

**`.entity(ParameterizedTypeReference<T>)` — List or Generic Types:**
```java
// For List<T> or other generic types, use ParameterizedTypeReference
List<BookRecommendation> books = chatClient
    .prompt()
    .user("Recommend 5 books about distributed systems")
    .call()
    .entity(new ParameterizedTypeReference<List<BookRecommendation>>() {});

books.forEach(b -> System.out.println(b.title() + " by " + b.author()));
```

**Combining with PromptTemplate:**
```java
@Value("classpath:prompts/recommend.st")
private Resource recommendTemplate;

public List<BookRecommendation> recommendBooks(String topic, int count) {
    PromptTemplate template = new PromptTemplate(recommendTemplate);
    Prompt prompt = template.create(Map.of("topic", topic, "count", count));

    return chatClient
        .prompt(prompt)
        .call()
        .entity(new ParameterizedTypeReference<List<BookRecommendation>>() {});
}
```

**Tips for Reliable Structured Output:**
- Use Java `record` types — they have well-defined fields with no inheritance ambiguity
- Keep the record shallow — avoid deeply nested structures when possible
- If a field can be absent, use `Optional` or annotate with `@Nullable`
- Use clear, descriptive field names — the AI uses them as hints for what to populate
- Test with multiple prompts — LLMs sometimes still hallucinate structure; add validation

---

### Slide 6 — RAG Architecture — The Spring AI Approach

**Title:** RAG with Spring AI — From Concept to Implementation Plan

**Content:**

**Quick Recap from Day 33:** RAG (Retrieval-Augmented Generation) solves the problem that AI models have a knowledge cutoff date and don't know about your specific data. RAG connects the AI to a knowledge base so it can answer questions about your documents, your database records, your internal documentation.

**The Two Phases of RAG:**

```
INGESTION PHASE (runs at startup or via admin endpoint — one time or periodic)
─────────────────────────────────────────────────────────────────────────────
Documents/Text
     │
     ▼
DocumentReader         ← reads PDF, Word, HTML, JSON, plain text
     │
     ▼
TokenTextSplitter      ← splits into overlapping chunks (~500–1000 tokens each)
     │
     ▼
EmbeddingModel         ← converts each chunk to a vector (float[])
     │
     ▼
VectorStore.add()      ← stores chunks + vectors + metadata in database


QUERY PHASE (runs on every user request)
─────────────────────────────────────────────────────────────────────────────
User Question
     │
     ▼
EmbeddingModel         ← embed the question → float[]
     │
     ▼
VectorStore.search()   ← find top K most similar chunks
     │
     ▼
QuestionAnswerAdvisor  ← inject retrieved chunks into prompt
     │
     ▼
ChatModel              ← generate answer using question + context chunks
     │
     ▼
Answer to User
```

**Spring AI Components Involved:**

| Phase | Component | Responsibility |
|-------|-----------|----------------|
| Ingestion | `TikaDocumentReader` | Read PDF, Word, HTML files |
| Ingestion | `JsonReader` | Read structured JSON data |
| Ingestion | `TokenTextSplitter` | Split into appropriately-sized chunks |
| Ingestion | `EmbeddingModel` | Vectorize each chunk |
| Both | `VectorStore` | Store (ingestion) and retrieve (query) |
| Query | `QuestionAnswerAdvisor` | Full retrieval + augmentation automatically |

---

### Slide 7 — Document Readers and Text Splitters

**Title:** Ingestion Pipeline — Reading and Chunking Documents

**Content:**

**Document Readers:**

Spring AI provides several `DocumentReader` implementations. All return `List<Document>` where each `Document` has a content string and a metadata map.

```java
// Read a PDF, Word doc, PowerPoint, HTML, plain text — Apache Tika handles formats
Resource resource = new ClassPathResource("docs/spring-ai-guide.pdf");
List<Document> docs = new TikaDocumentReader(resource).get();

// Read JSON — specify the JSON pointer to the text field
Resource jsonResource = new ClassPathResource("data/books.json");
List<Document> jsonDocs = new JsonReader(
    jsonResource,
    "description"    // JSON Pointer to the text field to embed
).get();

// Read plain text file
List<Document> textDocs = new TextReader(new ClassPathResource("faq.txt")).get();
```

**Adding Metadata During Ingestion:**
```java
// Metadata is stored alongside the embedding — useful for filtering later
List<Document> docs = new TikaDocumentReader(resource).get();
docs.forEach(doc -> {
    doc.getMetadata().put("source", "spring-ai-guide.pdf");
    doc.getMetadata().put("category", "documentation");
    doc.getMetadata().put("version", "1.0.0");
});
```

**Text Splitting — Why It Matters:**

Embedding models have a maximum input length (typically 512 to 8192 tokens). A 50-page PDF is far too long to embed as a single document. You must split it into chunks before embedding.

`TokenTextSplitter` splits by token count with configurable overlap:

```java
TokenTextSplitter splitter = new TokenTextSplitter(
    800,     // defaultChunkSize:   target tokens per chunk
    350,     // minChunkSizeChars:  minimum characters before splitting
    5,       // minChunkLengthToEmbed: skip chunks shorter than this
    10000,   // maxNumChunks:        cap total chunks per document
    true     // keepSeparator:       preserve sentence boundaries
);

List<Document> chunks = splitter.apply(docs);
System.out.println("Split " + docs.size() + " docs into " + chunks.size() + " chunks");
```

**Chunk Size Guidance:**

| Chunk Size | Good For | Trade-off |
|------------|---------|-----------|
| 200–400 tokens | Precise retrieval, short factual content | May miss context across chunks |
| 500–800 tokens | Balanced — most use cases | Good default |
| 1000–2000 tokens | Long-form context, narratives | Fewer, more expensive embeddings |

> **Overlap:** `TokenTextSplitter` adds overlap between consecutive chunks so that information at a boundary is not lost. If a sentence spans two chunks, both chunks contain it. Typical overlap: 10–20% of chunk size.

---

### Slide 8 — VectorStore — SimpleVectorStore for Development

**Title:** `SimpleVectorStore` — Getting RAG Running in Development

**Content:**

For development, testing, and prototyping, Spring AI provides `SimpleVectorStore` — an in-memory vector store with no external dependencies.

**Setup:**
```java
@Configuration
public class VectorStoreConfig {

    // SimpleVectorStore is an in-memory implementation — no external database required
    @Bean
    @Profile("dev")   // only use this in development
    VectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
```

**Ingestion with SimpleVectorStore:**
```java
@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @EventListener(ApplicationReadyEvent.class)   // run after startup
    public void ingestDocuments() {
        Resource[] resources = {
            new ClassPathResource("docs/spring-ai-faq.txt"),
            new ClassPathResource("docs/api-guide.pdf"),
        };

        TokenTextSplitter splitter = new TokenTextSplitter();

        for (Resource resource : resources) {
            List<Document> docs = new TikaDocumentReader(resource).get();
            List<Document> chunks = splitter.apply(docs);
            vectorStore.add(chunks);
        }

        System.out.println("Document ingestion complete.");
    }
}
```

**Persistence (Optional) — Save/Load from File:**
```java
// Save in-memory vector store to disk (JSON format)
SimpleVectorStore store = (SimpleVectorStore) vectorStore;
store.save(new File("vectorstore.json"));

// Load at startup instead of re-ingesting
store.load(new File("vectorstore.json"));
```

**Limitations of SimpleVectorStore:**

| Aspect | Limitation |
|--------|-----------|
| Storage | In-memory — lost on restart (unless saved to file) |
| Scaling | Single JVM — can't be shared across instances |
| Performance | Linear scan — slow for thousands of documents |
| Production | Not suitable for high-traffic or large document sets |

> **Rule:** `SimpleVectorStore` is for development and demos. Use `PgVectorStore` or another production vector store when you go beyond a few hundred documents or need persistence.

---

### Slide 9 — VectorStore — PgVector for Production

**Title:** `PgVectorStore` — Production-Grade Vector Storage with PostgreSQL

**Content:**

`PgVectorStore` uses the `pgvector` extension for PostgreSQL — it adds native vector similarity search (`<=>` cosine distance, `<#>` inner product, `<->` L2 distance) directly into Postgres. No separate vector database needed — your existing PostgreSQL instance handles it.

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>
```

**PostgreSQL with pgvector — Docker Compose for Development:**
```yaml
# docker-compose.yml
services:
  postgres:
    image: pgvector/pgvector:pg16      # official image with pgvector pre-installed
    environment:
      POSTGRES_DB: aidb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
```

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/aidb
    username: postgres
    password: ${POSTGRES_PASSWORD}
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW                  # Hierarchical Navigable Small World — fast ANN search
        distance-type: COSINE_DISTANCE    # best for text similarity
        dimensions: 1536                  # MUST match your embedding model's dimension
        initialize-schema: true           # auto-create vector_store table on startup
```

**The `dimensions` Must Match:**

| Embedding Model | Dimensions |
|----------------|-----------|
| OpenAI `text-embedding-3-small` | 1536 |
| OpenAI `text-embedding-3-large` | 3072 |
| OpenAI `text-embedding-ada-002` | 1536 |
| Ollama `nomic-embed-text` | 768 |
| Ollama `mxbai-embed-large` | 1024 |

If this doesn't match, Spring AI throws an exception at startup.

**No Code Changes:** The `PgVectorStore` bean is auto-configured. Your `DocumentIngestionService` and `QuestionAnswerAdvisor` code from the previous slide works without any changes — they reference `VectorStore` (the interface), not a specific implementation. This is the portability Spring AI provides.

**Similarity Search Directly (Without Advisor):**
```java
List<Document> results = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("How does Spring AI handle authentication?")
        .topK(5)                           // return top 5 most similar chunks
        .similarityThreshold(0.70)         // exclude results below 70% similarity
        .build()
);

results.forEach(doc -> {
    System.out.println("Score: " + doc.getScore());
    System.out.println("Content: " + doc.getContent().substring(0, 200));
    System.out.println("Source: " + doc.getMetadata().get("source"));
});
```

---

### Slide 10 — QuestionAnswerAdvisor — RAG in Action

**Title:** `QuestionAnswerAdvisor` — Wiring RAG into Every Chat Request

**Content:**

`QuestionAnswerAdvisor` is a Spring AI built-in advisor that automates the entire query-phase of RAG. Add it to your `ChatClient` and every request automatically retrieves relevant context from the vector store before calling the AI.

**What the Advisor Does Internally:**
```
1. Intercepts the ChatClient .call()
2. Takes the user's message text
3. Runs vectorStore.similaritySearch(userMessage, topK=4)
4. Formats retrieved documents as context:
     "Use the following context to answer the question:
      [Document 1 content...]
      [Document 2 content...]
      [Document 3 content...]
      [Document 4 content...]"
5. Prepends this context block to the prompt
6. Sends the augmented prompt to ChatModel
7. Returns the response (which now incorporates retrieved knowledge)
```

**Setup:**
```java
@Configuration
public class RagConfig {

    @Bean
    ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
        return builder
            .defaultSystem(
                "You are a knowledgeable assistant. Answer questions based on the " +
                "provided context. If the context does not contain enough information " +
                "to answer confidently, say so clearly rather than guessing."
            )
            .defaultAdvisors(
                new QuestionAnswerAdvisor(
                    vectorStore,
                    SearchRequest.builder()
                        .topK(4)                      // retrieve 4 most relevant chunks
                        .similarityThreshold(0.65)    // minimum similarity score
                        .build()
                )
            )
            .build();
    }
}
```

**Usage — Identical to Regular ChatClient:**
```java
@Service
public class KnowledgeBaseService {

    private final ChatClient ragChatClient;

    public KnowledgeBaseService(@Qualifier("ragChatClient") ChatClient chatClient) {
        this.ragChatClient = chatClient;
    }

    public String ask(String question) {
        // The advisor handles all RAG retrieval transparently
        return ragChatClient
            .prompt()
            .user(question)
            .call()
            .content();
    }
}
```

**Combining RAG with Conversation Memory:**
```java
@Bean
ChatClient ragWithMemoryChatClient(ChatClient.Builder builder,
                                    VectorStore vectorStore,
                                    ChatMemory memory) {
    return builder
        .defaultAdvisors(
            new MessageChatMemoryAdvisor(memory),         // handles conversation history
            new QuestionAnswerAdvisor(vectorStore)        // handles document retrieval
        )
        .build();
}
// The request now has: system prompt + conversation history + retrieved context + user question
```

**When to Tune the TopK and Threshold:**
- Increase `topK` (5–10) when questions may require broad context from multiple documents
- Lower `similarityThreshold` (0.5–0.6) when retrieval is too strict and missing relevant chunks
- Raise `similarityThreshold` (0.8+) when irrelevant context is polluting the prompt

---

### Slide 11 — AI Model Configuration Best Practices

**Title:** Configuration, Profiles, and API Key Management

**Content:**

**application.yml — Full Configuration Structure:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}               # NEVER hardcode — always env var
      chat:
        options:
          model: gpt-4o
          temperature: 0.7
          max-tokens: 2000
          top-p: 1.0
          frequency-penalty: 0.0
      embedding:
        options:
          model: text-embedding-3-small
    vectorstore:
      pgvector:
        dimensions: 1536
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        initialize-schema: true
    retry:
      max-attempts: 3
      backoff:
        initial-interval: 1000
        multiplier: 2
        max-interval: 30000
```

**Environment-Specific Profiles:**
```
src/main/resources/
  application.yml              ← shared configuration
  application-dev.yml          ← development overrides
  application-prod.yml         ← production overrides
```

```yaml
# application-dev.yml — use local Ollama, no API costs
spring:
  ai:
    openai:
      api-key: disabled
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2
  profiles:
    active: dev   # set SPRING_PROFILES_ACTIVE=dev as env var
```

```yaml
# application-prod.yml — use GPT-4o, strict settings
spring:
  ai:
    openai:
      chat:
        options:
          model: gpt-4o
          max-tokens: 1500
          temperature: 0.5
```

**API Key Security:**

| Environment | API Key Storage |
|-------------|----------------|
| Development | `.env` file (in `.gitignore`) or OS environment variable |
| CI/CD | GitHub Actions / Jenkins Secrets |
| Production (AWS) | AWS Secrets Manager or Parameter Store |
| Production (K8s) | Kubernetes Secret → mounted as env var |

```bash
# Never commit this file — add .env to .gitignore
# .env
OPENAI_API_KEY=sk-proj-...
POSTGRES_PASSWORD=...
```

**Cost Management Best Practices:**
- Set `max-tokens` to the minimum required for each use case
- Use `gpt-4o-mini` for simple tasks (classification, short answers) — ~15× cheaper per token
- Use `gpt-4o` for complex reasoning, code generation, and detailed analysis
- Monitor token usage with Micrometer metrics (Slide 14)
- Consider request caching for identical or similar queries

---

### Slide 12 — Error Handling — AI Provider Errors

**Title:** Handling `TransientAiException` and `NonTransientAiException`

**Content:**

Spring AI wraps all provider-specific errors into its own exception hierarchy. This means you don't need to catch OpenAI-specific or Anthropic-specific exceptions — your error handling code is portable.

**Exception Hierarchy:**
```
AiException (base)
  ├── NonTransientAiException    ← don't retry; fix the root cause
  │     ├── Bad request / invalid parameter
  │     ├── Invalid API key (401)
  │     └── Content policy violation
  └── TransientAiException       ← safe to retry; temporary condition
        ├── Rate limit exceeded (429)
        ├── Service temporarily unavailable (503)
        └── Timeout
```

**Catching Specific Exceptions:**
```java
public String callWithHandling(String message) {
    try {
        return chatClient.prompt().user(message).call().content();

    } catch (NonTransientAiException e) {
        // Don't retry — log the error and fail fast
        log.error("Non-retryable AI error: {}", e.getMessage());
        throw new AiServiceException("AI service configuration error", e);

    } catch (TransientAiException e) {
        // Could retry — but Spring AI can do this automatically (next slide)
        log.warn("Transient AI error (will retry): {}", e.getMessage());
        throw e;
    }
}
```

**Spring AI Built-In Retry Configuration:**

Spring AI has built-in retry support for `TransientAiException`. Configure via YAML:

```yaml
spring:
  ai:
    retry:
      max-attempts: 3            # retry up to 3 times
      on-http-codes: 429,503     # which HTTP status codes trigger retry
      exclude-on-http-codes: 401 # these codes are NOT retried
      backoff:
        initial-interval: 1000   # wait 1 second before first retry
        multiplier: 2            # double the wait each time
        max-interval: 30000      # cap at 30 seconds
```

**What This Means in Practice:**
- Rate limit 429: Spring AI waits 1s, retries. If still 429, waits 2s, retries. If still 429, waits 4s, retries. After 3 attempts, throws `TransientAiException`.
- 401 Unauthorized: no retry — bad API key won't self-heal.
- 503: retried with backoff.

---

### Slide 13 — Fallback Strategies

**Title:** Fallback Strategies — Graceful Degradation for AI Services

**Content:**

Even with retry, the AI service can ultimately fail. Fallback strategies ensure your application degrades gracefully rather than crashing.

**Strategy 1: Simple try/catch with Fallback Response:**
```java
@Service
public class ResilientChatService {

    private final ChatClient chatClient;

    public String askWithFallback(String question) {
        try {
            return chatClient.prompt().user(question).call().content();
        } catch (AiException e) {
            log.error("AI service unavailable: {}", e.getMessage());
            // Return a graceful fallback message
            return "I'm unable to process your request right now. " +
                   "Please try again in a few minutes or contact support.";
        }
    }
}
```

**Strategy 2: Fallback to a Different Model:**
```java
@Service
public class MultiModelChatService {

    private final ChatClient primaryClient;    // GPT-4o
    private final ChatClient fallbackClient;   // Ollama (local, always available)

    public MultiModelChatService(
            @Qualifier("primaryClient") ChatClient primaryClient,
            @Qualifier("localClient") ChatClient fallbackClient) {
        this.primaryClient = primaryClient;
        this.fallbackClient = fallbackClient;
    }

    public String ask(String question) {
        try {
            return primaryClient.prompt().user(question).call().content();
        } catch (AiException e) {
            log.warn("Primary AI model failed, falling back to local model: {}", e.getMessage());
            return fallbackClient.prompt().user(question).call().content();
        }
    }
}
```

**Strategy 3: Resilience4j Circuit Breaker:**
```java
@Service
public class CircuitBreakerChatService {

    private final ChatClient chatClient;
    private final CircuitBreakerRegistry registry;

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallback")
    public String ask(String question) {
        return chatClient.prompt().user(question).call().content();
    }

    // Called automatically when circuit is open
    public String fallback(String question, Throwable t) {
        log.warn("Circuit open for AI service. Question: {}", question);
        return "AI service is temporarily unavailable. Please try again later.";
    }
}
```

```yaml
# application.yml — Resilience4j circuit breaker config
resilience4j:
  circuitbreaker:
    instances:
      aiService:
        failure-rate-threshold: 50         # open after 50% failure rate
        wait-duration-in-open-state: 30s   # wait 30s before half-open
        permitted-calls-in-half-open-state: 3
        minimum-number-of-calls: 5
```

**When to Use Each Strategy:**

| Strategy | Best For |
|----------|---------|
| Simple try/catch | Development, low-traffic apps |
| Multi-model fallback | Critical features needing high availability |
| Circuit breaker | High-traffic production, prevents cascade failures |

---

### Slide 14 — Monitoring AI Interactions — Micrometer

**Title:** Observability — Tracking Token Usage, Latency, and Cost

**Content:**

Spring AI auto-configures Micrometer metrics when `spring-boot-starter-actuator` is on the classpath. No extra code required.

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- Optional: Prometheus export -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Auto-Configured AI Metrics:**

| Metric Name | Type | What It Measures |
|-------------|------|-----------------|
| `gen_ai.client.operation.duration` | Timer | Full round-trip latency per AI call |
| `gen_ai.client.token.usage` | Distribution Summary | Token count per call (input + output) |

**application.yml — Expose Metrics:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  metrics:
    distribution:
      percentiles-histogram:
        gen_ai.client.operation.duration: true    # enables P50, P95, P99 latency
```

**Querying Metrics via Actuator:**
```
GET /actuator/metrics/gen_ai.client.operation.duration
GET /actuator/metrics/gen_ai.client.token.usage
```

**Metric Tags (for filtering by provider, model):**

Spring AI automatically adds tags to every metric:
- `gen_ai.system`: provider name (`openai`, `anthropic`, `ollama`)
- `gen_ai.operation.name`: `chat`, `embedding`
- `gen_ai.request.model`: the model name (`gpt-4o`, `claude-3-5-sonnet`)
- `gen_ai.usage.input_tokens`: input token count per request
- `gen_ai.usage.output_tokens`: output token count per request

**Custom Dashboard (Grafana / CloudWatch):**

With Prometheus export enabled, you can build dashboards showing:
- Average and P95 AI response latency by model
- Total tokens consumed per hour (input vs output)
- Cost estimation (input tokens × price + output tokens × price)
- Error rate by provider and model
- Circuit breaker state over time

**Programmatic Token Counting:**
```java
// If you want to count tokens per operation in your own logic
ChatResponse response = chatClient.prompt().user(question).call().chatResponse();
Usage usage = response.getMetadata().getUsage();
log.info("This call used {} tokens (prompt: {}, completion: {})",
    usage.getTotalTokens(), usage.getPromptTokens(), usage.getGenerationTokens());
```

---

### Slide 15 — Logging AI Interactions

**Title:** Logging — `SimpleLoggerAdvisor`, Audit Logging, and Cost Awareness

**Content:**

**`SimpleLoggerAdvisor` — Zero-Config Prompt/Response Logging:**
```java
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a helpful assistant.")
        .defaultAdvisors(
            new SimpleLoggerAdvisor()     // add this one line
        )
        .build();
}
```

Then set the log level:
```yaml
logging:
  level:
    org.springframework.ai: DEBUG    # enables SimpleLoggerAdvisor output
```

`SimpleLoggerAdvisor` logs the full `AdvisedRequest` (prompt, messages, options) and `ChatResponse` (content, metadata, token usage) at `DEBUG` level. Essential for development. Too verbose for production — set to `INFO` or `WARN` in production.

**Custom Logging Advisor — Audit Trail:**

For production, you often need structured audit logs: who asked what, when, how many tokens, what the response was. Build a custom advisor:

```java
@Component
public class AuditLoggingAdvisor implements CallAroundAdvisor {

    private static final Logger log = LoggerFactory.getLogger(AuditLoggingAdvisor.class);

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        String userMessage = request.userText();

        AdvisedResponse response = chain.nextAroundCall(request);   // call the AI

        long duration = System.currentTimeMillis() - startTime;
        Usage usage = response.response().getMetadata().getUsage();

        // Structured log — searchable in ELK, Splunk, CloudWatch
        log.info("AI_AUDIT | user='{}' | tokens={} | latency={}ms | model='{}'",
            userMessage.substring(0, Math.min(100, userMessage.length())),
            usage.getTotalTokens(),
            duration,
            response.response().getMetadata().getModel());

        return response;
    }

    @Override
    public String getName() { return "AuditLoggingAdvisor"; }

    @Override
    public int getOrder() { return 0; }   // run first in the advisor chain
}
```

**Logging Configuration for Production:**
```yaml
logging:
  level:
    org.springframework.ai: WARN          # suppress framework debug logs
    com.example.advisors.AuditLogging: INFO  # keep your audit logs
  pattern:
    console: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
```

**Cost Awareness Best Practices:**
- Log `totalTokens` per request in your audit advisor
- Aggregate token counts in your monitoring dashboard
- Set budget alerts: if daily token usage exceeds threshold → Slack alert via CloudWatch/PagerDuty
- Different features have different cost profiles: code generation (more tokens) vs simple classification (fewer tokens)

---

### Slide 16 — Part 2 Summary and Day 35 Preview

**Title:** Part 2 Summary — Spring AI in Production

**Content:**

**Full Day 34 Recap — What You Can Now Build:**

| Capability | What You Learned |
|-----------|-----------------|
| **AI Service** | `@Service` + `ChatClient.Builder` with system prompt and advisors |
| **Chat + Memory** | `MessageChatMemoryAdvisor` + `InMemoryChatMemory` + conversationId |
| **Function Calling** | `@Tool` on Spring component methods; AI decides when to invoke |
| **Structured Output** | `BeanOutputConverter` / `.entity(Book.class)` — AI response → Java record |
| **RAG Pipeline** | `TikaDocumentReader` → `TokenTextSplitter` → `VectorStore.add()` |
| **Vector Search** | `SimpleVectorStore` (dev) / `PgVectorStore` (prod) |
| **Auto RAG** | `QuestionAnswerAdvisor` wired into `ChatClient` |
| **Configuration** | Profiles, env vars, `ChatOptions` per request |
| **Error Handling** | `TransientAiException` retry, `NonTransientAiException` fail-fast |
| **Fallback** | try/catch fallback model; Resilience4j `@CircuitBreaker` |
| **Monitoring** | Micrometer `gen_ai.*` metrics; Prometheus/Grafana dashboard |
| **Logging** | `SimpleLoggerAdvisor` (dev) + custom `CallAroundAdvisor` (prod audit) |

**Production Spring AI Architecture:**
```
Browser/Client
      │
      ▼
REST Controller
      │
      ▼
AI Service (@Service)
  ├── ChatClient
  │     ├── MessageChatMemoryAdvisor  ← conversation history
  │     ├── QuestionAnswerAdvisor     ← RAG retrieval
  │     ├── AuditLoggingAdvisor       ← logging + metrics
  │     └── ChatModel (OpenAI/Anthropic)
  └── @CircuitBreaker (Resilience4j)
        └── Fallback → Ollama (local)

Ingestion Pipeline (async)
  └── DocumentReader → Splitter → EmbeddingModel → PgVectorStore
```

**Day 35 Preview — MongoDB:**
Tomorrow we move to a new category entirely: NoSQL databases. We'll start with MongoDB — what document-oriented databases are and why they exist alongside relational databases, the BSON document model, collections versus tables, CRUD operations in the Mongo shell, and Spring Data MongoDB integration. It's a fresh mental model: instead of rows and foreign keys, you'll be thinking in JSON-like documents with nested arrays and flexible schemas.
