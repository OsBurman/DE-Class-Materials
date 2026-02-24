# Spring AI — Overview & Architecture

## What Is Spring AI?

Spring AI is the Spring ecosystem's answer to the question: *"How do I integrate AI models into my Spring Boot application using familiar Spring patterns?"*

Instead of learning vendor-specific SDKs (OpenAI Python SDK, Anthropic SDK, etc.), you use Spring abstractions — interfaces, auto-configuration, and dependency injection — just like Spring Data abstracts databases or Spring Security abstracts auth.

---

## Architecture Overview

```
Your Spring Boot Application
         │
         ▼
  ┌─────────────────────────────────────────────────────────┐
  │                    Spring AI Core                        │
  │                                                         │
  │  ChatModel ──────────────────────────────────────────── │─── Portable interface
  │  EmbeddingModel ───────────────────────────────────────  │─── Portable interface
  │  VectorStore ──────────────────────────────────────────  │─── Portable interface
  │  PromptTemplate ───────────────────────────────────────  │─── Reusable templating
  │  OutputConverter ──────────────────────────────────────  │─── Structured output
  └─────────────────────────────────────────────────────────┘
         │
         ▼ (auto-configured implementation, swap by changing dependency)
  ┌──────────────────────────────────────────────────┐
  │           Model Provider Implementations          │
  │                                                  │
  │   OpenAiChatModel        → GPT-4o, GPT-4 Turbo  │
  │   AzureOpenAiChatModel   → OpenAI via Azure      │
  │   AnthropicChatModel     → Claude 3.x            │
  │   OllamaChatModel        → Local Llama, Mistral  │
  │   VertexAiChatModel      → Google Gemini         │
  │   BedrockChatModel       → AWS Bedrock           │
  └──────────────────────────────────────────────────┘
         │
         ▼ (HTTP/HTTPS)
  External AI Provider (or local Ollama server)
```

**Key principle:** Your application code talks to Spring AI interfaces (`ChatModel`, `EmbeddingModel`). You swap providers by changing a single Maven/Gradle dependency and one `application.properties` line. No code changes.

---

## Core Interfaces

### `ChatModel`

The primary interface for chat/completion models.

```java
public interface ChatModel extends Model<Prompt, ChatResponse> {
    // Core method — takes a Prompt, returns a ChatResponse
    ChatResponse call(Prompt prompt);

    // Convenience overload — takes a plain String
    default String call(String message);

    // Streaming — returns a Flux<ChatResponse> for token-by-token output
    Flux<ChatResponse> stream(Prompt prompt);
}
```

**What it does:** Sends a prompt (system + user messages) to an AI model and returns the generated text.

**Key implementations:**
| Implementation | Provider | Dependency |
|---|---|---|
| `OpenAiChatModel` | OpenAI (GPT-4o etc.) | `spring-ai-openai-spring-boot-starter` |
| `AzureOpenAiChatModel` | Azure OpenAI | `spring-ai-azure-openai-spring-boot-starter` |
| `AnthropicChatModel` | Anthropic (Claude) | `spring-ai-anthropic-spring-boot-starter` |
| `OllamaChatModel` | Ollama (local) | `spring-ai-ollama-spring-boot-starter` |
| `VertexAiChatModel` | Google Vertex AI | `spring-ai-vertex-ai-gemini-spring-boot-starter` |

---

### `EmbeddingModel`

Interface for turning text into numerical vectors (embeddings).

```java
public interface EmbeddingModel extends Model<EmbeddingRequest, EmbeddingResponse> {
    // Embed a list of texts → returns a list of float[] vectors
    EmbeddingResponse call(EmbeddingRequest request);

    // Convenience — embed a single string
    float[] embed(String text);

    // Convenience — embed a Document
    float[] embed(Document document);
}
```

**What it does:** Converts text into a dense vector of numbers. Semantically similar texts have numerically similar vectors. Used in:
- Semantic search ("find books similar to this description")
- RAG (Retrieval-Augmented Generation)
- Recommendation systems

**Key implementations:**
| Implementation | Provider |
|---|---|
| `OpenAiEmbeddingModel` | `text-embedding-3-small`, `text-embedding-3-large` |
| `OllamaEmbeddingModel` | Local models via Ollama |
| `AzureOpenAiEmbeddingModel` | Azure-hosted OpenAI embeddings |

---

### `VectorStore`

Interface for storing and searching embeddings.

```java
public interface VectorStore {
    void add(List<Document> documents);       // store documents + their embeddings
    void delete(List<String> ids);             // remove documents
    List<Document> similaritySearch(String query);  // find semantically similar docs
    List<Document> similaritySearch(SearchRequest request);  // with filters/k/threshold
}
```

**Implementations:** `PgVectorStore` (PostgreSQL + pgvector), `ChromaVectorStore`, `PineconeVectorStore`, `WeaviateVectorStore`, `SimpleVectorStore` (in-memory, for dev/testing).

---

### `PromptTemplate`

Utility for building dynamic prompts from templates (like Spring's `JdbcTemplate` for SQL).

```java
// Template with placeholders
PromptTemplate template = new PromptTemplate("""
    You are a helpful bookstore assistant.
    Answer the following question about the book "{title}" by {author}:
    {question}
    """);

// Fill in the values
Prompt prompt = template.create(Map.of(
    "title", "Clean Code",
    "author", "Robert C. Martin",
    "question", "What are the main themes?"
));
```

---

## Model Integrations — Configuration

### Maven Dependencies

```xml
<!-- pom.xml — choose ONE chat model starter -->

<!-- OpenAI (GPT-4o, GPT-4 Turbo, GPT-3.5) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Azure OpenAI (same models, enterprise compliance) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Anthropic (Claude 3.5 Sonnet, Claude 3 Haiku) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
</dependency>

<!-- Ollama (local models — no API key needed, offline) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>
```

### `application.properties` — Provider Configuration

```properties
# ── OpenAI ───────────────────────────────────────────────────────────────────
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.chat.options.temperature=0.7
spring.ai.openai.chat.options.max-tokens=1000

# ── Azure OpenAI ─────────────────────────────────────────────────────────────
spring.ai.azure.openai.api-key=${AZURE_OPENAI_API_KEY}
spring.ai.azure.openai.endpoint=https://my-resource.openai.azure.com/
spring.ai.azure.openai.chat.options.deployment-name=gpt-4o
spring.ai.azure.openai.chat.options.temperature=0.7

# ── Anthropic ────────────────────────────────────────────────────────────────
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY}
spring.ai.anthropic.chat.options.model=claude-3-5-sonnet-20241022
spring.ai.anthropic.chat.options.temperature=0.7
spring.ai.anthropic.chat.options.max-tokens=1024

# ── Ollama (local — no key needed) ──────────────────────────────────────────
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.7
```

> ⚠️ **NEVER hardcode API keys in source code.** Always use environment variables (`${OPENAI_API_KEY}`) or Spring Vault. Hardcoded keys in git history are a security incident.

---

## @AiService — Declarative AI Services

`@AiService` is Spring AI's equivalent of `@FeignClient` or Spring Data repositories — you define an interface, and Spring AI generates the implementation.

```java
// Define the interface
@AiService
public interface BookstoreAiService {
    // Method name and parameters become the prompt automatically
    String recommendBooks(@UserMessage String genre);

    // Use @SystemMessage to set a system persona
    @SystemMessage("You are an expert librarian. Be concise and helpful.")
    String describeBook(@UserMessage String bookTitle);
}

// Spring creates the implementation bean automatically.
// Inject it like any other Spring service:
@RestController
public class BookController {
    @Autowired
    private BookstoreAiService aiService;
}
```

---

## ChatModel vs @AiService — When to Use Which

| | `ChatModel` | `@AiService` |
|---|---|---|
| Control | Full (build Prompt manually) | Declarative (annotations) |
| Streaming | Easy (`stream()` method) | Possible with `Flux<String>` return |
| Complex prompts | ✅ Natural | More verbose |
| Simple Q&A | More boilerplate | ✅ Clean |
| Function calling | Manual setup | Annotation-based |
| Testing | Mock the interface | Mock the interface |

> **Rule of thumb:** Start with `@AiService` for simple features. Use `ChatModel` directly when you need fine-grained control over prompt construction, streaming, or conversation history.
