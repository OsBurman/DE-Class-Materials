# Day 34 Part 1 — Spring AI: Overview, ChatModel, ChatClient, PromptTemplate, and Chatbots
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 34 Part 1: Spring AI — Building AI-Powered Applications in Spring Boot

**Subtitle:** ChatModel, ChatClient, PromptTemplate, model integrations, and conversational interfaces

**Learning Objectives:**
- Understand the Spring AI framework and its abstraction layer across AI providers
- Set up Spring AI in a Spring Boot project with the BOM and provider starters
- Use the `ChatModel` interface directly for low-level AI interactions
- Construct `Prompt` objects from `SystemMessage`, `UserMessage`, and `AssistantMessage`
- Use the `EmbeddingModel` interface to generate text embeddings
- Use the `ChatClient` high-level fluent API for clean, readable AI service code
- Write reusable `PromptTemplate` instances with variable substitution
- Configure multiple AI model providers (OpenAI, Azure, Anthropic, Ollama)
- Build an AI-powered chatbot with conversation memory in Spring Boot
- Stream AI responses using `Flux` and Server-Sent Events

---

### Slide 2 — Spring AI Overview and Architecture

**Title:** Spring AI — What It Is and Why It Exists

**Content:**

Spring AI is a Spring Framework project that provides a **portable, idiomatic API for integrating AI capabilities** into Spring Boot applications. Released as 1.0.0 GA in 2024, it follows the same design philosophy as the rest of Spring: abstraction over vendor APIs, dependency injection, auto-configuration, and familiar idioms.

**The Core Problem Spring AI Solves:**

Every major AI provider has its own SDK with different APIs, different method names, different configuration formats, and different error types. Without an abstraction layer:
```
OpenAI SDK:       openAiClient.createCompletion(CompletionRequest.builder()...)
Anthropic SDK:    client.messages().create(MessageCreateParams.builder()...)
Azure OpenAI:     client.getChatCompletions(deploymentId, chatCompletionsOptions)
Ollama REST:      POST http://localhost:11434/api/chat { "model": "llama3", ... }
```

Switching providers means rewriting all AI code. Spring AI provides a single interface that works with all of them.

**Spring AI Architecture Layers:**

```
┌────────────────────────────────────────────────────────────┐
│                    Your Application Code                    │
│            (ChatClient, ChatModel, VectorStore)             │
├────────────────────────────────────────────────────────────┤
│                Spring AI Abstraction Layer                  │
│   ChatModel │ EmbeddingModel │ ImageModel │ VectorStore     │
├────────────────────────────────────────────────────────────┤
│               Provider Auto-Configurations                  │
│   OpenAI │ Azure OpenAI │ Anthropic │ Ollama │ Bedrock      │
├────────────────────────────────────────────────────────────┤
│                   Provider SDKs / REST                      │
└────────────────────────────────────────────────────────────┘
```

**Core Abstractions:**

| Interface | Purpose |
|-----------|---------|
| `ChatModel` | Send prompts, receive text/structured responses |
| `EmbeddingModel` | Generate vector embeddings from text |
| `ImageModel` | Generate images (DALL-E, Stability AI) |
| `VectorStore` | Store and retrieve embedded documents by similarity |
| `ChatClient` | High-level fluent API wrapping `ChatModel` |

**Key Design Principles:**
- **POJO-based** — plain Java records/classes for requests and responses
- **Auto-configured** — one starter dependency = everything wired up
- **Portable** — swap providers by changing a dependency and YAML config
- **Advisor pattern** — cross-cutting concerns (logging, RAG, memory) as composable interceptors
- **Observability built-in** — Micrometer metrics and tracing out of the box

---

### Slide 3 — Getting Started — Dependencies and BOM

**Title:** Adding Spring AI to a Spring Boot Project

**Content:**

Spring AI uses a **BOM (Bill of Materials)** to manage consistent versions across its many modules. You import the BOM once, then add individual starters without specifying version numbers.

**pom.xml — BOM Import:**
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

**Provider Starters (add the one(s) you need):**
```xml
<dependencies>
  <!-- OpenAI (ChatGPT, GPT-4o) -->
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
  </dependency>

  <!-- Anthropic (Claude) -->
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
  </dependency>

  <!-- Ollama (local models: Llama, Mistral) -->
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
  </dependency>

  <!-- Azure OpenAI -->
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

**What Each Starter Gives You (auto-configured beans):**
- `ChatModel` — provider-specific implementation
- `ChatClient.Builder` — ready to use via `@Autowired`
- `EmbeddingModel` — for generating embeddings (most providers)
- `ChatOptions` subclass — provider-specific options with full IDE support

**Minimum Required Configuration (OpenAI example):**
```yaml
# application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}    # never hardcode — always use env var
      chat:
        options:
          model: gpt-4o
```

> **Note:** Spring Initializr at `start.spring.io` supports Spring AI starters directly — select "Spring AI" in the dependencies section.

---

### Slide 4 — ChatModel Interface — The Core Low-Level API

**Title:** `ChatModel` — Sending Prompts and Receiving Responses

**Content:**

`ChatModel` is the foundational interface. It has one main method with several overloads. All higher-level APIs (including `ChatClient`) delegate to it internally.

**The Interface:**
```java
public interface ChatModel {
    ChatResponse call(Prompt prompt);
    default String call(String message) { ... }   // convenience overload
}
```

**Simplest Possible Usage:**
```java
@Service
public class AiDemoService {

    private final ChatModel chatModel;

    public AiDemoService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String simpleQuestion(String question) {
        // convenience overload — wraps string in a UserMessage automatically
        return chatModel.call(question);
    }
}
```

**Accessing the Full Response:**
```java
public String callWithFullResponse(String question) {
    // Build Prompt from a single UserMessage
    Prompt prompt = new Prompt(new UserMessage(question));

    ChatResponse response = chatModel.call(prompt);

    // Navigate the response object tree
    Generation generation = response.getResult();                   // first choice
    AssistantMessage output = generation.getOutput();               // the AI message
    String text = output.getContent();                              // the text content

    // Metadata (token usage, finish reason)
    ChatGenerationMetadata metadata = generation.getMetadata();

    return text;
}
```

**Response Object Hierarchy:**
```
ChatResponse
  └── List<Generation>          (multiple choices if n > 1)
        └── Generation
              ├── AssistantMessage (the response text)
              └── ChatGenerationMetadata
                    ├── finishReason  ("STOP", "LENGTH", "TOOL_CALLS")
                    └── usage         (promptTokens, generationTokens, totalTokens)
```

**Token Usage (important for cost tracking):**
```java
Usage usage = response.getMetadata().getUsage();
System.out.println("Prompt tokens:     " + usage.getPromptTokens());
System.out.println("Generation tokens: " + usage.getGenerationTokens());
System.out.println("Total tokens:      " + usage.getTotalTokens());
```

---

### Slide 5 — Message Types and Prompt Construction

**Title:** Building Prompts with `SystemMessage`, `UserMessage`, and `AssistantMessage`

**Content:**

A `Prompt` wraps a list of `Message` objects. Spring AI maps these directly to the message roles that AI APIs expect.

**Message Types:**

| Class | Role | When to Use |
|-------|------|-------------|
| `SystemMessage` | `system` | Set persona, rules, constraints, output format instructions |
| `UserMessage` | `user` | The human's actual question or input |
| `AssistantMessage` | `assistant` | Previous AI response (for multi-turn conversation) |

**Constructing a Prompt with System + User Messages:**
```java
Message systemMessage = new SystemMessage(
    "You are a senior Java developer who explains concepts clearly " +
    "with concise code examples. Keep answers under 200 words."
);
Message userMessage = new UserMessage("Explain the Singleton design pattern.");

// Pass as a List
Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

ChatResponse response = chatModel.call(prompt);
String answer = response.getResult().getOutput().getContent();
```

**Multi-Turn Conversation (Manual History):**
```java
List<Message> history = new ArrayList<>();
history.add(new SystemMessage("You are a helpful assistant."));

// Turn 1
history.add(new UserMessage("My name is Alice."));
ChatResponse r1 = chatModel.call(new Prompt(history));
history.add(r1.getResult().getOutput());       // add AssistantMessage to history

// Turn 2
history.add(new UserMessage("What is my name?"));
ChatResponse r2 = chatModel.call(new Prompt(history));
// The model knows the name from previous context
```

> **Important:** Language models are **stateless** — they have no memory between calls. You must pass the full conversation history every time if you want multi-turn behavior. Spring AI's `MessageChatMemoryAdvisor` automates this (covered in Slide 13).

**System Prompt Best Practices:**
- Define the AI's role and persona clearly
- Specify the format of responses (JSON, bullet points, code only)
- State what the model should NOT do
- Keep it focused — overly long system prompts dilute attention

---

### Slide 6 — EmbeddingModel Interface

**Title:** `EmbeddingModel` — Turning Text Into Vectors

**Content:**

`EmbeddingModel` generates **vector embeddings** — numeric representations of text that capture semantic meaning. Two texts that mean similar things will produce vectors that are close together in vector space.

**Day 33 covered embeddings conceptually.** Today we implement them in Spring Boot.

**The Interface:**
```java
public interface EmbeddingModel {
    float[] embed(String text);
    EmbeddingResponse embedForResponse(List<String> texts);
}
```

**Basic Usage:**
```java
@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    // Embed a single text, get float array
    public float[] embedText(String text) {
        return embeddingModel.embed(text);
    }

    // Embed multiple texts in one API call (more efficient)
    public List<float[]> embedBatch(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        return response.getResults().stream()
            .map(Embedding::getOutput)
            .collect(Collectors.toList());
    }
}
```

**What Are Embeddings Used For?**

| Use Case | How |
|----------|-----|
| **Semantic search** | Embed query + documents; find nearest vectors |
| **RAG (Part 2)** | Embed documents into VectorStore; retrieve relevant chunks at query time |
| **Duplicate detection** | High similarity score = likely duplicate content |
| **Classification** | Cluster vectors by topic/category |
| **Recommendation** | Similar embedding = similar content |

**OpenAI Embedding Configuration:**
```yaml
spring:
  ai:
    openai:
      embedding:
        options:
          model: text-embedding-3-small    # 1536 dimensions, fast, cost-effective
          # model: text-embedding-3-large  # 3072 dimensions, more accurate
```

**Dimension Note:** The embedding dimension must match what your vector store is configured for. OpenAI `text-embedding-3-small` produces 1536-dimensional vectors. Ollama `nomic-embed-text` produces 768-dimensional vectors. You pick one and configure everything consistently.

---

### Slide 7 — ChatClient — The High-Level Fluent API

**Title:** `ChatClient` — Fluent, Readable AI Code

**Content:**

`ChatClient` is the preferred high-level API introduced in Spring AI 0.8+. It wraps `ChatModel` behind a fluent builder interface that reads like a conversation, handles message construction automatically, and integrates with the Advisor pattern for cross-cutting concerns.

**Auto-Configuration and Injection:**

Spring AI auto-configures a `ChatClient.Builder` bean. You inject the builder and construct your `ChatClient` in a `@Bean` method or directly in your service:

```java
@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a helpful software engineering assistant.")
            .build();
    }
}
```

**Basic Usage:**
```java
@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String ask(String question) {
        return chatClient
            .prompt()
            .user(question)
            .call()
            .content();   // returns the response text directly as String
    }
}
```

**Overriding System Prompt Per-Request:**
```java
public String askInRole(String role, String question) {
    return chatClient
        .prompt()
        .system("You are a " + role + ". Be concise and technical.")
        .user(question)
        .call()
        .content();
}
```

**Accessing the Full ChatResponse:**
```java
public ChatResponse askForFullResponse(String question) {
    return chatClient
        .prompt()
        .user(question)
        .call()
        .chatResponse();   // returns ChatResponse with metadata, token usage, etc.
}
```

**The Fluent Chain:**
```
chatClient
  .prompt()                  // starts the prompt builder
    .system("...")            // sets/overrides system message
    .user("...")              // sets user message (most common)
    .messages(List<Message>)  // full control: pass your own message list
  .call()                    // executes — blocking
    .content()               // extracts String text
    .chatResponse()          // extracts full ChatResponse
    .entity(Book.class)      // extracts structured output (Part 2)
```

> **ChatClient vs ChatModel:** Use `ChatClient` for most application code — it's cleaner and integrates with advisors. Use `ChatModel` directly when you need low-level control or are writing library code.

---

### Slide 8 — ChatClient — Advisors, Options, and Streaming

**Title:** `ChatClient` — Cross-Cutting Concerns with Advisors

**Content:**

**Advisors** are Spring AI's mechanism for intercepting and augmenting AI calls with cross-cutting behavior — logging, memory, RAG retrieval, and more. They compose cleanly without polluting business logic.

**Adding Advisors:**
```java
@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a helpful assistant.")
            .defaultAdvisors(
                new SimpleLoggerAdvisor()     // logs requests/responses at DEBUG level
            )
            .build();
    }
}
```

**Built-in Advisors:**

| Advisor | Purpose |
|---------|---------|
| `SimpleLoggerAdvisor` | Log every prompt and response at DEBUG level |
| `MessageChatMemoryAdvisor` | Automatically add conversation history to each request |
| `QuestionAnswerAdvisor` | Retrieve relevant documents from VectorStore and inject into prompt (RAG) |

**Per-Request Options Override:**
```java
// Override model options for a specific call
public String quickAnswer(String question) {
    return chatClient
        .prompt()
        .user(question)
        .options(OpenAiChatOptions.builder()
            .withModel("gpt-4o-mini")           // faster, cheaper for simple questions
            .withTemperature(0.3f)              // lower = more focused/factual
            .withMaxTokens(200)                 // short answers only
            .build())
        .call()
        .content();
}
```

**Streaming — Non-Blocking Responses:**

Instead of waiting for the full response, streaming receives tokens as they are generated. Ideal for chat UIs where you want text to appear progressively:

```java
public Flux<String> streamAnswer(String question) {
    return chatClient
        .prompt()
        .user(question)
        .stream()          // switch from .call() to .stream()
        .content();        // returns Flux<String> — emits tokens as they arrive
}
```

**Streaming from a REST Controller (Server-Sent Events):**
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamEndpoint(@RequestParam String question) {
    return chatService.streamAnswer(question);
}
```

Clients connect to this endpoint and receive a continuous stream of tokens over HTTP SSE, which most modern browsers and JavaScript frameworks support natively.

---

### Slide 9 — PromptTemplate — Dynamic and Reusable Prompts

**Title:** `PromptTemplate` — Parameterized Prompts with Variable Substitution

**Content:**

`PromptTemplate` solves the problem of building prompts with variable values embedded in them. Instead of string concatenation (fragile, hard to maintain), you write a template with named placeholders and fill them in at runtime.

**Basic Usage:**
```java
// Template with {variable} placeholders
PromptTemplate template = new PromptTemplate(
    "You are a {role}. A student asks: {question}. " +
    "Provide a clear, concise answer with a code example."
);

// Fill variables at call time
Prompt prompt = template.create(Map.of(
    "role", "senior Java developer",
    "question", "What is a functional interface?"
));

ChatResponse response = chatModel.call(prompt);
```

**Loading Templates from Classpath Files:**

For longer, complex prompts, keep them in separate `.st` (StringTemplate) files. This separates prompt engineering from Java code:

```
src/main/resources/prompts/
  ├── code-review.st
  ├── summarize.st
  └── answer-question.st
```

**resources/prompts/code-review.st:**
```
You are an expert Java code reviewer.

Review the following code for:
- Correctness and potential bugs
- Code quality and readability
- SOLID principle violations
- Performance concerns

Code to review:
{code}

Provide structured feedback with severity (high/medium/low) for each issue.
```

**Java — Loading and Using the File Template:**
```java
@Value("classpath:prompts/code-review.st")
private Resource codeReviewPrompt;

public String reviewCode(String code) {
    PromptTemplate template = new PromptTemplate(codeReviewPrompt);
    Prompt prompt = template.create(Map.of("code", code));
    return chatModel.call(prompt)
        .getResult().getOutput().getContent();
}
```

**With ChatClient:**
```java
public String reviewCode(String code) {
    PromptTemplate template = new PromptTemplate(codeReviewPrompt);
    // ChatClient accepts Prompt objects directly
    return chatClient
        .prompt(template.create(Map.of("code", code)))
        .call()
        .content();
}
```

**Benefits of PromptTemplate:**
- Keeps prompts in version control as separate files
- Non-developers (prompt engineers, product managers) can edit prompts without touching Java code
- Easy to swap or A/B test different prompt variants
- Variables are explicit — no risk of accidentally interpolating sensitive data

> **Rule:** Never build prompts with Java string concatenation of user input. It's the AI equivalent of SQL injection. Use `PromptTemplate` with variables — Spring AI properly handles the separation.

---

### Slide 10 — Model Integrations — Supported Providers

**Title:** AI Provider Integrations — OpenAI, Azure, Anthropic, Ollama, and More

**Content:**

Spring AI supports a wide range of providers. The code you write (using `ChatModel`, `ChatClient`, `PromptTemplate`) stays identical — only the starter dependency and YAML configuration change.

**Provider Overview:**

| Provider | Starter Artifact | Models |
|----------|-----------------|--------|
| **OpenAI** | `spring-ai-openai-spring-boot-starter` | gpt-4o, gpt-4o-mini, o1 |
| **Azure OpenAI** | `spring-ai-azure-openai-spring-boot-starter` | gpt-4o via Azure deployment |
| **Anthropic** | `spring-ai-anthropic-spring-boot-starter` | claude-3-5-sonnet, claude-3-haiku |
| **Ollama** | `spring-ai-ollama-spring-boot-starter` | llama3, mistral, codellama (local) |
| **Google Vertex AI** | `spring-ai-vertex-ai-gemini-spring-boot-starter` | gemini-1.5-pro, gemini-flash |
| **Amazon Bedrock** | `spring-ai-bedrock-ai-spring-boot-starter` | Claude, Llama, Titan |
| **Mistral AI** | `spring-ai-mistralai-spring-boot-starter` | mistral-large, mixtral |

**Configuration Examples:**

```yaml
# OpenAI
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.7

# Anthropic
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-3-5-sonnet-20241022
          max-tokens: 2048

# Ollama (local — no API key needed)
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2

# Azure OpenAI
spring:
  ai:
    azure:
      openai:
        api-key: ${AZURE_OPENAI_API_KEY}
        endpoint: ${AZURE_OPENAI_ENDPOINT}
        chat:
          options:
            deployment-name: gpt-4o-deployment
```

**Multiple Providers in One Application:**

You can wire multiple providers simultaneously by qualifying beans:
```java
// Use OpenAI for complex reasoning
@Bean
@Qualifier("primary")
ChatClient primaryClient(@Qualifier("openAiChatModel") ChatModel model) {
    return ChatClient.builder(model).build();
}

// Use Ollama (local/free) for simple tasks or dev/test
@Bean
@Qualifier("local")
ChatClient localClient(@Qualifier("ollamaChatModel") ChatModel model) {
    return ChatClient.builder(model).build();
}
```

**Ollama for Development:**
Ollama runs AI models locally — no API costs, no network dependency, no data sent externally. Install with `brew install ollama` (macOS), run `ollama pull llama3.2`, start with `ollama serve`. Your Spring Boot app connects to `localhost:11434` as if it were a remote API.

---

### Slide 11 — Building AI Services in Spring Boot

**Title:** The `@Service` + `ChatClient` Pattern — Spring AI's AI Service Architecture

**Content:**

The standard pattern for building AI features in Spring Boot is to wrap `ChatClient` in a focused `@Service` class. This gives you a clean, testable, injectable service that encapsulates all AI-related logic.

**Note on `@AiService`:** Some AI frameworks (notably LangChain4j) offer an `@AiService` annotation that generates AI service implementations from a plain Java interface — similar to how Spring Data generates repository implementations from interfaces. **Spring AI does not have this annotation.** Spring AI's equivalent is the explicit `@Service` + `ChatClient` pattern shown below. The `ChatClient` fluent API achieves the same goal with more explicit, debuggable code.

**Pattern: Focused AI Service:**
```java
@Service
public class BookReviewService {

    private final ChatClient chatClient;

    public BookReviewService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem(
                "You are a literary expert who provides thoughtful, concise book analysis. " +
                "Format all responses as structured JSON unless otherwise instructed."
            )
            .build();
    }

    public String summarize(String bookTitle, String authorName) {
        return chatClient.prompt()
            .user(u -> u.text("Summarize '{title}' by {author} in 3 sentences.")
                        .param("title", bookTitle)
                        .param("author", authorName))
            .call()
            .content();
    }

    public String recommendSimilar(String bookTitle) {
        return chatClient.prompt()
            .user("Recommend 3 books similar to '" + bookTitle + "'. " +
                  "For each: title, author, and one sentence why.")
            .call()
            .content();
    }
}
```

**Injecting via Constructor:**
```java
@RestController
@RequestMapping("/api/books")
public class BookAiController {

    private final BookReviewService bookReviewService;

    public BookAiController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @GetMapping("/{title}/summary")
    public ResponseEntity<String> getSummary(@PathVariable String title,
                                             @RequestParam String author) {
        return ResponseEntity.ok(bookReviewService.summarize(title, author));
    }
}
```

**Why One `ChatClient` Per Service (Not One Global):**
- Each service can have its own `defaultSystem` prompt tailored to that feature
- Different services can use different providers or ChatOptions
- Easier to test — mock `ChatClient` in unit tests per service

---

### Slide 12 — Building Chatbots — REST Endpoint Design

**Title:** Chatbot API Design — Request/Response and Session Management

**Content:**

A chatbot differs from a simple Q&A service because it maintains conversational context across multiple turns. The REST API design must accommodate sessions.

**Request and Response DTOs:**
```java
public record ChatRequest(
    String sessionId,    // uniquely identifies this conversation
    String message       // the user's latest message
) {}

public record ChatResponse(
    String sessionId,
    String response,
    int tokenCount
) {}
```

**Chatbot Service (with memory — covered in detail on Slide 13):**
```java
@Service
public class ChatbotService {

    private final ChatClient chatClient;

    public ChatbotService(ChatClient.Builder builder, ChatMemory memory) {
        this.chatClient = builder
            .defaultSystem(
                "You are a customer support assistant for a bookstore application. " +
                "Help users find books, check availability, and answer questions. " +
                "Be friendly, professional, and concise."
            )
            .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
            .build();
    }

    public String chat(String sessionId, String userMessage) {
        return chatClient
            .prompt()
            .user(userMessage)
            .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
            .call()
            .content();
    }
}
```

**REST Controller:**
```java
@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = chatbotService.chat(request.sessionId(), request.message());
        return ResponseEntity.ok(new ChatResponse(request.sessionId(), response, 0));
    }
}
```

**Session ID Options:**
- Generate client-side: `crypto.randomUUID()` in JavaScript
- Generate server-side: return a new UUID on first message if none provided
- Use authenticated user ID if the app has auth (maps one conversation per user)
- Use HTTP session ID (but be aware of statelessness in distributed deployments)

---

### Slide 13 — Conversation Memory — `MessageChatMemoryAdvisor`

**Title:** Multi-Turn Conversations with `MessageChatMemoryAdvisor`

**Content:**

`MessageChatMemoryAdvisor` is a built-in Spring AI advisor that automatically:
1. **Before** each request: loads conversation history for the given `sessionId` and prepends it to the messages
2. **After** each response: saves the new user message and AI response to memory

**Setup:**
```java
@Configuration
public class ChatMemoryConfig {

    // InMemoryChatMemory: stores history in application memory
    // Good for: development, single-instance apps, short-lived sessions
    @Bean
    ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
```

**Wiring into ChatClient:**
```java
@Service
public class ChatbotService {

    private final ChatClient chatClient;
    private static final String CONVERSATION_ID_KEY =
        AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

    public ChatbotService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
            .defaultSystem("You are a helpful assistant.")
            .defaultAdvisors(
                new MessageChatMemoryAdvisor(chatMemory)
            )
            .build();
    }

    public String chat(String conversationId, String message) {
        return chatClient
            .prompt()
            .user(message)
            // Pass the conversationId so the advisor knows which history to load
            .advisors(a -> a.param(CONVERSATION_ID_KEY, conversationId))
            .call()
            .content();
    }
}
```

**How It Works Internally:**
```
Request arrives:
  1. Advisor loads messages for conversationId from ChatMemory
  2. Prepends [SystemMessage, ...history, UserMessage(new)] to prompt
  3. Sends full context to ChatModel

Response arrives:
  4. Advisor saves UserMessage and AssistantMessage to ChatMemory
  5. Returns response to caller
```

**Memory Strategies:**
| `ChatMemory` Impl | Storage | Use Case |
|-------------------|---------|---------|
| `InMemoryChatMemory` | JVM heap | Development, single-instance apps |
| Custom `JdbcChatMemory` | Database | Production, multi-instance, persistent sessions |
| Custom `RedisChatMemory` | Redis | High-traffic, distributed sessions |

> **Warning:** `InMemoryChatMemory` is lost on restart and grows unbounded. For production, implement `ChatMemory` backed by a database or implement a windowing strategy that keeps only the last N messages.

---

### Slide 14 — Streaming Responses — Flux and Server-Sent Events

**Title:** Streaming AI Responses for Responsive Chat UIs

**Content:**

Standard AI calls block until the model finishes generating the entire response — which can take 5–30 seconds for long answers. **Streaming** delivers tokens to the client as they are generated, making the UI feel instantaneous.

**The Streaming API:**
```java
@Service
public class StreamingChatService {

    private final ChatClient chatClient;

    public StreamingChatService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("You are a helpful assistant.")
            .build();
    }

    // Returns Flux<String> — each emission is a chunk of text (1–5 tokens)
    public Flux<String> streamAnswer(String question) {
        return chatClient
            .prompt()
            .user(question)
            .stream()           // .stream() instead of .call()
            .content();
    }

    // Access full streaming response (with metadata on completion)
    public Flux<ChatResponse> streamWithMetadata(String question) {
        return chatClient
            .prompt()
            .user(question)
            .stream()
            .chatResponse();    // emits ChatResponse per chunk; final emission has full metadata
    }
}
```

**REST Controller — Server-Sent Events Endpoint:**
```java
@RestController
@RequestMapping("/api/chat")
public class StreamController {

    private final StreamingChatService service;

    public StreamController(StreamingChatService service) {
        this.service = service;
    }

    // Client connects here, receives a stream of text/event-stream tokens
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String question) {
        return service.streamAnswer(question);
    }
}
```

**JavaScript Client (browser):**
```javascript
const eventSource = new EventSource(`/api/chat/stream?question=${encodeURIComponent(q)}`);
eventSource.onmessage = (event) => {
    document.getElementById("output").textContent += event.data;
};
eventSource.onerror = () => eventSource.close();
```

**Blocking vs Streaming — When to Use Each:**

| Use Case | API |
|----------|-----|
| Short answers, structured output, background jobs | `.call().content()` |
| Chat UIs where user sees text appear in real time | `.stream().content()` |
| REST APIs that need full response before sending | `.call()` |
| REST APIs using SSE for progressive display | `.stream()` |

---

### Slide 15 — ChatOptions — Per-Request Model Configuration

**Title:** `ChatOptions` — Controlling Temperature, Tokens, and Model Selection

**Content:**

`ChatOptions` lets you configure model parameters. There are two levels: **defaults** set on the `ChatClient` builder (apply to all calls), and **per-request** overrides applied to individual calls.

**Key Parameters:**

| Parameter | Type | Effect |
|-----------|------|--------|
| `model` | String | Which model to use (e.g., `gpt-4o`, `gpt-4o-mini`) |
| `temperature` | float 0.0–2.0 | 0.0 = deterministic/focused; 1.0+ = creative/varied |
| `maxTokens` | int | Max tokens in the response (cost control) |
| `topP` | float 0.0–1.0 | Nucleus sampling — alternative to temperature |
| `frequencyPenalty` | float -2.0–2.0 | Reduce repetition of common phrases |
| `presencePenalty` | float -2.0–2.0 | Encourage new topics |
| `stop` | List<String> | Stop generation when these sequences appear |

**Provider-Specific Options (OpenAI):**
```java
OpenAiChatOptions options = OpenAiChatOptions.builder()
    .withModel("gpt-4o")
    .withTemperature(0.7f)
    .withMaxTokens(1500)
    .withTopP(1.0f)
    .build();
```

**Setting Defaults on ChatClient Builder:**
```java
@Bean
ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a helpful assistant.")
        .defaultOptions(OpenAiChatOptions.builder()
            .withModel("gpt-4o")
            .withTemperature(0.7f)
            .withMaxTokens(1000)
            .build())
        .build();
}
```

**Per-Request Override:**
```java
// Override for a specific call — doesn't affect other calls
public String quickSummary(String text) {
    return chatClient
        .prompt()
        .user("Summarize this in one sentence: " + text)
        .options(OpenAiChatOptions.builder()
            .withModel("gpt-4o-mini")   // cheaper model for simple task
            .withTemperature(0.2f)      // more deterministic for summaries
            .withMaxTokens(100)         // force brevity
            .build())
        .call()
        .content();
}
```

**Temperature Quick Reference:**

| Temperature | Behavior | Good For |
|-------------|----------|---------|
| `0.0–0.3` | Deterministic, factual, consistent | Code generation, data extraction, Q&A |
| `0.4–0.7` | Balanced | General chat, summaries, explanations |
| `0.8–1.2` | Creative, varied | Brainstorming, creative writing |
| `1.3–2.0` | Unpredictable | Experimental use only |

---

### Slide 16 — Part 1 Summary

**Title:** Part 1 Summary — Spring AI Foundations

**Content:**

**What We Covered:**

| Topic | Key Takeaway |
|-------|-------------|
| Spring AI Architecture | Portable abstraction layer over all major AI providers |
| BOM + Starters | Import BOM once; add one provider starter; everything auto-configured |
| `ChatModel` | Low-level interface; `call(Prompt)` → `ChatResponse` → token usage |
| Message Types | `SystemMessage` (persona/rules), `UserMessage` (input), `AssistantMessage` (history) |
| `EmbeddingModel` | Text → float[] vector; foundation for semantic search and RAG |
| `ChatClient` | Fluent high-level API; `.prompt().user(...).call().content()` |
| Advisors | Composable interceptors: `SimpleLoggerAdvisor`, `MessageChatMemoryAdvisor` |
| Streaming | `.stream().content()` → `Flux<String>`; SSE endpoint for chat UIs |
| `PromptTemplate` | Variable substitution; load from classpath `.st` files |
| Model Integrations | Same code, different starter + YAML config |
| AI Service Pattern | `@Service` + `ChatClient` = testable, injectable AI feature |
| Conversation Memory | `InMemoryChatMemory` + `MessageChatMemoryAdvisor` + `sessionId` |
| `ChatOptions` | Temperature, maxTokens, model — set as default or per-call override |

**Architecture Pattern Recap:**
```
Controller  →  @Service (ChatClient)  →  ChatModel  →  Provider API
               ↑ PromptTemplate           ↑ Advisors
               ↑ ChatOptions             ↑ MessageChatMemory
```

**Part 2 Preview:**
- **Function calling** — let the AI invoke your Java methods
- **Structured output** — parse AI responses directly into Java objects
- **RAG** — connect AI to your own documents and data
- **VectorStore** — pgvector and SimpleVectorStore setup
- **Error handling and fallback** strategies
- **Monitoring** AI interactions with Micrometer and logging
