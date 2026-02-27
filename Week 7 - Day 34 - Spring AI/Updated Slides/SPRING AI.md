# Spring AI – Full Lecture Script (≈ 60 Minutes)
### With Embedded Slide Notes

---

## PRE-CLASS SETUP
Make sure you have:
- A Spring Boot 3.x project with `spring-ai-openai-spring-boot-starter` on the classpath
- An OpenAI API key in your environment
- A running Postgres + pgvector instance (for the RAG demo)
- IntelliJ or VS Code open and ready

---

# SECTION 1 — SPRING AI OVERVIEW & ARCHITECTURE (≈ 8 min)

---

**[SLIDE 1 — Title Slide]**
> **Title:** Spring AI: Building Production-Ready AI Applications
> **Subtitle:** From Chat Models to RAG Systems
> **Include:** Spring logo, a simple diagram showing "Spring Boot App → AI Model"

---

**[SLIDE 2 — What Is Spring AI?]**
> **Title:** What Is Spring AI?
> **Bullets:**
> - Official Spring project for AI/ML integration
> - Vendor-neutral abstraction layer over LLMs
> - Follows familiar Spring idioms: dependency injection, auto-configuration, starters
> - Part of the broader Spring ecosystem (Boot, Data, Security)
> **Include:** GitHub repo URL: github.com/spring-projects/spring-ai

---

*Script:*

Good morning everyone. Today we're doing a deep dive into Spring AI — the official Spring framework project that lets you integrate large language models into your Spring Boot applications without vendor lock-in.

If you've ever used Spring Data, you know how it abstracts away whether you're talking to Postgres or MongoDB behind a common `Repository` interface. Spring AI does the same thing for AI models. Whether you're calling OpenAI, Azure OpenAI, Anthropic's Claude, or a local Ollama model, you write the same code. The framework handles the translation.

The key philosophy here is *portability*. You should be able to swap your AI provider with a configuration change, not a code rewrite. Keep that in mind throughout today's lesson — every design decision in Spring AI is built around that goal.

---

**[SLIDE 3 — Core Architecture]**
> **Title:** Spring AI Architecture Overview
> **Diagram:**
> ```
> Your Spring Boot App
>   ├── ChatModel         → OpenAI / Azure / Anthropic / Ollama
>   ├── EmbeddingModel    → text → vectors
>   ├── VectorStore       → store & retrieve embeddings
>   ├── PromptTemplate    → dynamic prompt construction
>   └── @AiService        → high-level declarative service layer
> ```
> **Bullets:**
> - Auto-configured via Spring Boot starters
> - All major components are interfaces — swap implementations freely

---

*Script:*

Let's look at the architecture. At the top level, your Spring Boot application works with a small set of core abstractions.

`ChatModel` is the most important one — it handles sending messages to an LLM and getting responses. `EmbeddingModel` converts text into numerical vectors, which is the foundation for semantic search. `VectorStore` persists and queries those embeddings. `PromptTemplate` helps you build dynamic prompts safely. And `@AiService` is a higher-level annotation that lets you declare AI-powered services declaratively, almost like `@FeignClient` but for AI.

All of these are interfaces backed by auto-configured beans. You add a starter dependency, set an API key, and Spring Boot wires everything up. Let's start walking through each one.

---

# SECTION 2 — CHATMODEL & EMBEDDINGMODEL INTERFACES (≈ 8 min)

---

**[SLIDE 4 — ChatModel Interface]**
> **Title:** ChatModel — The Core Abstraction
> **Code block:**
> ```java
> public interface ChatModel {
>     ChatResponse call(Prompt prompt);
>     default String call(String message) { ... }
>     Flux<ChatResponse> stream(Prompt prompt); // streaming
> }
> ```
> **Bullets:**
> - `Prompt` wraps one or more `Message` objects
> - `ChatResponse` contains `Generation` objects with text + metadata
> - Streaming via Project Reactor `Flux`

---

*Script:*

Here's the `ChatModel` interface. It is deliberately simple. You call it with a `Prompt`, you get back a `ChatResponse`. There's also a convenience overload that just takes a plain `String` for quick usage.

For production you'll almost always work with the full `Prompt` object, because it lets you pass system messages, user messages, conversation history, and model-specific options — all together.

The `stream` method returns a reactive `Flux<ChatResponse>`, which is how you implement token-by-token streaming in a Spring WebFlux or SSE endpoint. Very important for chatbot UIs where users expect to see text appear word by word.

Let me show you the simplest possible usage:

---

**[SLIDE 5 — ChatModel in Practice]**
> **Title:** Using ChatModel
> **Code block:**
> ```java
> @Service
> public class MyAiService {
>
>     private final ChatModel chatModel;
>
>     public MyAiService(ChatModel chatModel) {
>         this.chatModel = chatModel;
>     }
>
>     public String ask(String userQuestion) {
>         return chatModel.call(userQuestion);
>     }
>
>     public String askWithSystem(String userQuestion) {
>         Prompt prompt = new Prompt(List.of(
>             new SystemMessage("You are a helpful Java tutor."),
>             new UserMessage(userQuestion)
>         ));
>         return chatModel.call(prompt)
>                         .getResult()
>                         .getOutput()
>                         .getContent();
>     }
> }
> ```

---

*Script:*

This is idiomatic Spring. You inject `ChatModel` through the constructor — Spring Boot auto-configures the right implementation based on which starter you have on the classpath. If you have `spring-ai-openai-spring-boot-starter`, you get an `OpenAiChatModel`. Add `spring-ai-anthropic-spring-boot-starter` instead, and you get a Claude model. Your `MyAiService` code does not change at all.

Notice how we build a `Prompt` with both a `SystemMessage` and a `UserMessage`. The system message shapes the model's behavior for the entire conversation. This is how you give your AI a persona or restrict what it talks about.

---

**[SLIDE 6 — EmbeddingModel Interface]**
> **Title:** EmbeddingModel — Text to Vectors
> **Code block:**
> ```java
> public interface EmbeddingModel {
>     List<Double> embed(String text);
>     EmbeddingResponse call(EmbeddingRequest request);
> }
> ```
> **Bullets:**
> - Converts text into high-dimensional float vectors
> - Used for semantic similarity, search, clustering
> - Same vendor-neutral pattern as ChatModel
> - Common providers: OpenAI `text-embedding-ada-002`, Azure, Ollama

---

*Script:*

`EmbeddingModel` is simpler but equally important. You hand it a string of text, and it returns a list of floats — a vector that captures the semantic meaning of that text in a way a machine can compare.

Two sentences that mean the same thing will produce vectors that are close together in this high-dimensional space. Two sentences about completely different topics will be far apart. This is the mathematical foundation for everything we'll cover in the RAG section.

You won't use `EmbeddingModel` directly very often — usually `VectorStore` handles it for you — but you need to understand what it does under the hood.

---

# SECTION 3 — PROMPTTEMPLATE FOR DYNAMIC PROMPTS (≈ 5 min)

---

**[SLIDE 7 — PromptTemplate]**
> **Title:** PromptTemplate — Dynamic, Safe Prompt Construction
> **Code block:**
> ```java
> String template = """
>     You are an expert in {domain}.
>     Answer the following question concisely:
>     {question}
>     """;
>
> PromptTemplate promptTemplate = new PromptTemplate(template);
>
> Prompt prompt = promptTemplate.create(Map.of(
>     "domain", "Spring Boot",
>     "question", userInput
> ));
>
> String response = chatModel.call(prompt)
>                            .getResult()
>                            .getOutput()
>                            .getContent();
> ```
> **Bullets:**
> - Uses `{placeholder}` syntax
> - Store templates in `.st` files on the classpath
> - Prevents prompt injection via variable escaping
> - Load from classpath: `new PromptTemplate(resource)`

---

*Script:*

Hard-coding prompts as concatenated strings is a maintenance nightmare and a security risk — it opens you up to prompt injection attacks where a malicious user sneaks instructions into their input.

`PromptTemplate` solves this with a simple placeholder substitution model. You define the template with `{variable}` placeholders, then call `.create()` with a map of values. The framework handles the substitution safely.

Even better, you can store your templates in `.st` files on the classpath — like `src/main/resources/prompts/support.st` — and inject them as Spring `Resource` objects. This means your prompts are version-controlled, reviewable, and changeable without recompiling your application. This is a best practice you should follow from day one.

---

# SECTION 4 — MODEL INTEGRATIONS (≈ 6 min)

---

**[SLIDE 8 — Supported AI Providers]**
> **Title:** Model Integrations
> **Table:**
> | Provider | Starter Dependency | Models |
> |---|---|---|
> | OpenAI | spring-ai-openai-spring-boot-starter | GPT-4o, GPT-4, GPT-3.5 |
> | Azure OpenAI | spring-ai-azure-openai-spring-boot-starter | GPT-4 on Azure |
> | Anthropic | spring-ai-anthropic-spring-boot-starter | Claude 3.5, Claude 3 |
> | Ollama (local) | spring-ai-ollama-spring-boot-starter | Llama 3, Mistral, etc. |
> | Google Vertex | spring-ai-vertex-ai-gemini-spring-boot-starter | Gemini |
> | AWS Bedrock | spring-ai-bedrock-* starters | Titan, Claude via Bedrock |
> | Mistral AI | spring-ai-mistral-ai-spring-boot-starter | Mistral, Mixtral |

---

*Script:*

Spring AI supports all the major providers through starter dependencies. You add the starter, set the API key in `application.properties`, and you're done. Let's look at what configuration looks like.

---

**[SLIDE 9 — Provider Configuration]**
> **Title:** Configuring AI Providers
> **Code block:**
> ```yaml
> # OpenAI
> spring:
>   ai:
>     openai:
>       api-key: ${OPENAI_API_KEY}
>       chat:
>         options:
>           model: gpt-4o
>           temperature: 0.7
>           max-tokens: 1000
>
> # Anthropic (Claude)
> spring:
>   ai:
>     anthropic:
>       api-key: ${ANTHROPIC_API_KEY}
>       chat:
>         options:
>           model: claude-3-5-sonnet-20241022
>           max-tokens: 2048
>
> # Ollama (local, no key needed)
> spring:
>   ai:
>     ollama:
>       base-url: http://localhost:11434
>       chat:
>         options:
>           model: llama3
> ```
> **IMPORTANT:** Never commit API keys. Always use environment variables or Spring Cloud Config / Vault.

---

*Script:*

Here's the configuration for three different providers. Notice the consistent pattern: `spring.ai.{provider}.api-key` and `spring.ai.{provider}.chat.options.*`. It is consistent across all providers.

A critical security note: never put API keys directly in your `application.properties` file that gets committed to source control. Use `${ENV_VAR}` references, or better yet, use Spring Cloud Config or a secrets manager like HashiCorp Vault. I'll reinforce this in the best practices section.

You can also override model options per request by passing `ChatOptions` to your `Prompt` object — useful when different features of your app need different temperatures or token limits.

---

# SECTION 5 — @AISERVICE ANNOTATION (≈ 5 min)

---

**[SLIDE 10 — @AiService]**
> **Title:** @AiService — Declarative AI Services
> **Code block:**
> ```java
> @AiService
> public interface CustomerSupportAi {
>
>     @SystemMessage("You are a polite customer support agent for AcmeCorp.")
>     String chat(@UserMessage String userMessage);
>
>     @SystemMessage("""
>         You are a sentiment analyzer.
>         Respond with only: POSITIVE, NEGATIVE, or NEUTRAL.
>         """)
>     String analyzeSentiment(@UserMessage String text);
> }
> ```
> **Usage:**
> ```java
> @Autowired
> CustomerSupportAi supportAi;
>
> String reply = supportAi.chat("I need help with my order.");
> ```
> **Bullets:**
> - Interface-based — similar to Spring Data repositories or Feign clients
> - `@SystemMessage` sets the AI persona
> - `@UserMessage` marks the user input parameter
> - Spring AI generates the implementation at runtime

---

*Script:*

`@AiService` is one of my favorite features in Spring AI because it follows a pattern Java developers already know well. Just like `@Repository` interfaces in Spring Data where you declare method signatures and Spring generates the implementation, `@AiService` lets you declare AI-powered methods on an interface and Spring AI generates the implementation at runtime.

You annotate an interface with `@AiService`, define methods with `@SystemMessage` to set the AI's behavior and `@UserMessage` on the parameter that carries user input, and that's it. You inject it like any other Spring bean.

This is incredibly powerful for keeping AI concerns cleanly separated from your business logic. Your service layer just calls `supportAi.chat(message)` — it doesn't know or care about HTTP calls, API keys, or prompt construction.

---

# SECTION 6 — BUILDING AI FEATURES IN SPRING BOOT (≈ 5 min)

---

**[SLIDE 11 — Spring Boot Integration]**
> **Title:** Wiring It All Together in Spring Boot
> **Code block:**
> ```java
> @RestController
> @RequestMapping("/api/ai")
> public class AiController {
>
>     private final ChatModel chatModel;
>
>     public AiController(ChatModel chatModel) {
>         this.chatModel = chatModel;
>     }
>
>     @PostMapping("/ask")
>     public ResponseEntity<String> ask(@RequestBody AskRequest request) {
>         String response = chatModel.call(request.question());
>         return ResponseEntity.ok(response);
>     }
>
>     @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
>     public Flux<String> stream(@RequestBody AskRequest request) {
>         return chatModel.stream(new Prompt(request.question()))
>                         .map(r -> r.getResult().getOutput().getContent());
>     }
> }
>
> public record AskRequest(String question) {}
> ```
> **Bullets:**
> - Standard `@RestController` — nothing AI-specific in the controller
> - SSE streaming via `Flux` and `TEXT_EVENT_STREAM_VALUE`
> - Keep AI logic in `@Service` classes, not controllers

---

*Script:*

Integrating Spring AI into a REST API is completely standard Spring MVC. The `/ask` endpoint does a blocking call and returns the full response. The `/stream` endpoint returns a Server-Sent Events stream so the client sees tokens as they arrive — this is what makes chatbot UIs feel responsive.

I want to emphasize: keep your controllers thin. Inject an `@AiService` or your own `@Service` class — don't put prompt logic directly in controllers. This makes your code testable, maintainable, and the AI provider replaceable.

---

# SECTION 7 — CHATBOTS AND CONVERSATIONAL INTERFACES (≈ 6 min)

---

**[SLIDE 12 — Conversation Memory]**
> **Title:** Building Conversational Interfaces
> **Bullets:**
> - LLMs are stateless — each call is completely independent
> - You must send the full conversation history with every request
> - Spring AI provides `MessageChatMemoryAdvisor` and `ChatMemory` abstractions
> - Implementations: InMemoryChatMemory, JDBC-backed (production), vector-store-backed
>
> **Code block:**
> ```java
> @Service
> public class ConversationalChatService {
>
>     private final ChatClient chatClient;
>
>     public ConversationalChatService(ChatClient.Builder builder) {
>         this.chatClient = builder
>             .defaultSystem("You are a helpful assistant.")
>             .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
>             .build();
>     }
>
>     public String chat(String conversationId, String userMessage) {
>         return chatClient.prompt()
>             .user(userMessage)
>             .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
>             .call()
>             .content();
>     }
> }
> ```

---

*Script:*

Here's a critical concept: LLMs have no memory. Each API call is completely stateless. If you want a chatbot that remembers what was said earlier in the conversation, *you* have to send that history with every request.

Spring AI introduces the `ChatClient` fluent API — distinct from `ChatModel` — and the concept of *Advisors*. Advisors are interceptors that can modify requests and responses. `MessageChatMemoryAdvisor` is a built-in advisor that automatically loads conversation history before sending a request and saves the response afterward.

You associate conversations with a unique `conversationId` — typically a UUID tied to the user's session. The memory store handles the rest. For production, use the JDBC-backed `ChatMemory` implementation so conversation memory survives application restarts.

---

**[SLIDE 13 — ChatClient vs ChatModel]**
> **Title:** ChatClient vs ChatModel — Which Do I Use?
> **Table:**
> | | ChatModel | ChatClient |
> |---|---|---|
> | Level | Low-level | High-level fluent API |
> | Memory | Manual | Built-in via Advisors |
> | RAG | Manual | Built-in via Advisors |
> | Best for | Fine-grained control | Applications, chatbots |

---

*Script:*

Quick clarification before we move on. `ChatModel` is the low-level interface for direct model calls — you manage everything yourself. `ChatClient` is a higher-level fluent builder that wraps `ChatModel` and adds advisors, default system prompts, and convenience methods.

For building real applications, you'll typically use `ChatClient`. For fine-grained control or when building your own abstractions on top of Spring AI, use `ChatModel` directly.

---

# SECTION 8 — FUNCTION CALLING (≈ 6 min)

---

**[SLIDE 14 — Function Calling Overview]**
> **Title:** Function Calling — Giving AI Access to Your Code
> **Bullets:**
> - LLMs can decide to invoke your Java functions based on user intent
> - You define the functions; the model decides when to use them
> - Enables: live data lookup, calculations, database queries, external API calls
> - Spring AI manages the entire function invocation loop automatically
> **Diagram:**
> ```
> User: "What's the weather in Austin?"
>     ↓
> ChatModel sees registered functions
>     ↓
> Model returns: "call getWeather({city: 'Austin'})"
>     ↓
> Spring AI invokes your Java bean
>     ↓
> Result sent back to model
>     ↓
> Model generates final natural language response
> ```

---

*Script:*

Function calling — also called tool use — is one of the most powerful features in modern LLMs. The model never directly executes code. Instead, it returns a structured request saying "please call this function with these arguments." Spring AI handles the invocation and sends the result back to the model.

This is how you build AI features that interact with live data: current prices, database records, user account information, external APIs. The model knows *what* to ask for; your code handles *how* to get it.

---

**[SLIDE 15 — Implementing Function Calling]**
> **Title:** Function Calling in Code
> **Code block:**
> ```java
> // Step 1: Define function as a Spring Bean
> @Bean
> @Description("Get the current weather for a given city")
> public Function<WeatherRequest, WeatherResponse> currentWeather() {
>     return request -> weatherService.getWeather(request.city());
> }
>
> public record WeatherRequest(String city) {}
> public record WeatherResponse(String city, double tempF, String conditions) {}
>
> // Step 2: Register it on your ChatClient call
> String response = chatClient.prompt()
>     .user("What's the weather in Austin and Dallas right now?")
>     .functions("currentWeather")   // bean name
>     .call()
>     .content();
> ```
> **Bullets:**
> - `@Description` is critical — it tells the model what the function does
> - Java records auto-generate JSON schema for function parameters
> - Model can call multiple functions in one conversation turn
> - Spring AI handles the full invocation loop transparently

---

*Script:*

Here's how it works in code. You define a `Function<Input, Output>` bean and annotate it with `@Description`. That description is what the model reads to decide when to call your function — write it clearly and precisely. Use Java records for your input and output types; Spring AI uses reflection to auto-generate the JSON schema that tells the model what parameters to pass.

Then you pass the function bean name to `.functions()` on your `ChatClient` call. Spring AI handles the entire invocation loop. If the user asks about weather in two cities, the model may call your function twice before generating its final response — Spring AI handles all of that automatically.

This is how you build AI agents that can actually *do* things in your system.

---

# SECTION 9 — STRUCTURED OUTPUT GENERATION (≈ 4 min)

---

**[SLIDE 16 — Structured Output]**
> **Title:** Structured Output — Getting Java Objects Back from AI
> **Code block:**
> ```java
> public record ProductReview(
>     String productName,
>     int rating,           // 1-5
>     String summary,
>     List<String> pros,
>     List<String> cons
> ) {}
>
> BeanOutputConverter<ProductReview> converter =
>     new BeanOutputConverter<>(ProductReview.class);
>
> String formatInstructions = converter.getFormat();
>
> Prompt prompt = new PromptTemplate("""
>     Analyze this review and extract structured data.
>     {format}
>     Review: {review}
>     """).create(Map.of("format", formatInstructions, "review", userReview));
>
> ProductReview result = converter.convert(
>     chatModel.call(prompt).getResult().getOutput().getContent()
> );
> ```
> **Also available:** `ListOutputConverter`, `MapOutputConverter`
> **Use cases:** entity extraction, classification, form population, data normalization

---

*Script:*

Often you don't want a conversational text response — you want structured data you can work with programmatically. Spring AI's output converters handle this cleanly.

`BeanOutputConverter` takes your Java class and generates natural language instructions telling the model to respond in the correct JSON format. It then parses the response back into your Java object. You get type-safe, structured output from a conversational AI call.

This is useful for extracting entities from text, categorizing user input, generating structured reports, or populating form fields from unstructured data. Any time you need AI to produce data rather than prose.

---

# SECTION 10 — RAG IMPLEMENTATION (≈ 8 min)

---

**[SLIDE 17 — What Is RAG?]**
> **Title:** Retrieval-Augmented Generation (RAG)
> **Diagram:**
> ```
> USER QUESTION
>     ↓
> [EmbeddingModel] → question vector
>     ↓
> [VectorStore.similaritySearch()] → top-K relevant document chunks
>     ↓
> [PromptTemplate] → context-stuffed prompt
>     ↓
> [ChatModel] → answer grounded in your documents
>     ↓
> ANSWER
> ```
> **Why RAG instead of fine-tuning?**
> - Models have a training cutoff — they don't know your private data
> - Fine-tuning is expensive, slow, and poor for frequently-changing data
> - RAG is real-time, low cost, and keeps your data private

---

*Script:*

RAG — Retrieval-Augmented Generation — is the most important architectural pattern for building AI applications on top of your own data. The problem it solves: LLMs were trained on public internet data up to a cutoff date. They don't know your internal documentation, your product catalog, your support tickets.

Fine-tuning a model on your data is expensive, slow to update, and doesn't work well for data that changes frequently. RAG is the practical solution. You store your documents as embeddings in a vector database. When a user asks a question, you embed the question, find the most semantically similar document chunks, stuff them into the prompt as context, and ask the model to answer based on that context. The model grounds its answer in your documents rather than guessing.

---

**[SLIDE 18 — RAG: Ingestion Pipeline]**
> **Title:** RAG Step 1 — Document Ingestion
> **Code block:**
> ```java
> @Service
> public class DocumentIngestionService {
>
>     private final VectorStore vectorStore;
>     private final TokenTextSplitter textSplitter = new TokenTextSplitter();
>
>     public void ingest(MultipartFile file) throws IOException {
>         // 1. Read document (supports PDF, Word, HTML, Markdown, plain text)
>         TikaDocumentReader reader = new TikaDocumentReader(file.getResource());
>         List<Document> documents = reader.get();
>
>         // 2. Split into chunks (~512 tokens each)
>         List<Document> chunks = textSplitter.apply(documents);
>
>         // 3. Embed and store (EmbeddingModel called automatically)
>         vectorStore.add(chunks);
>     }
> }
> ```
> **Bullets:**
> - Run ingestion once (or on document update), not per query
> - `TikaDocumentReader` handles PDF, Word, HTML, and more
> - `TokenTextSplitter` is recommended over character-based splitting
> - VectorStore handles embedding automatically

---

*Script:*

RAG has two distinct phases. First is ingestion. You read your documents, split them into chunks — typically 512 to 1024 tokens — embed each chunk, and store the embeddings in a vector store. Spring AI provides `TikaDocumentReader` for PDFs and Office documents, and `TokenTextSplitter` for intelligent chunking that respects token boundaries. The `VectorStore.add()` method calls `EmbeddingModel` automatically and stores everything.

This ingestion step runs once when documents are added or updated. It's typically a background job, an admin endpoint, or a startup process. It is not repeated on every user query.

---

**[SLIDE 19 — RAG: Query Pipeline]**
> **Title:** RAG Step 2 — Query & Answer
> **Code block:**
> ```java
> @Service
> public class RagChatService {
>
>     private final ChatClient chatClient;
>
>     public RagChatService(ChatClient.Builder builder, VectorStore vectorStore) {
>         this.chatClient = builder
>             .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
>             .build();
>     }
>
>     public String askAboutDocuments(String question) {
>         return chatClient.prompt()
>             .user(question)
>             .call()
>             .content();
>     }
> }
> ```
> **QuestionAnswerAdvisor does all of this automatically:**
> 1. Embeds the user's question
> 2. Runs similarity search against the VectorStore (top-K chunks)
> 3. Injects retrieved chunks into the prompt as context
> 4. Sends the enriched prompt to ChatModel
> 5. Returns the grounded response

---

*Script:*

The query side is even simpler thanks to `QuestionAnswerAdvisor`. This built-in advisor wraps your entire RAG retrieval into a single line in your ChatClient setup. When you call `.call()`, the advisor intercepts the request, runs similarity search, injects the top matching chunks into the prompt context, and forwards the enriched prompt to the model.

Notice how clean the `askAboutDocuments` method is — four lines of code. All the complexity is encapsulated in the advisor. You can combine `QuestionAnswerAdvisor` with `MessageChatMemoryAdvisor` to get RAG plus conversation memory simultaneously. That is how you build a knowledge-base chatbot.

---

# SECTION 11 — VECTOR STORE INTEGRATION (≈ 3 min)

---

**[SLIDE 20 — Vector Store Options]**
> **Title:** Vector Store Integration
> **Table:**
> | Store | Starter | Best for |
> |---|---|---|
> | pgvector (Postgres) | spring-ai-pgvector-store-spring-boot-starter | Existing Postgres users |
> | Redis | spring-ai-redis-store-spring-boot-starter | Low latency, existing Redis |
> | Chroma | spring-ai-chroma-store-spring-boot-starter | Dev / prototype |
> | Pinecone | spring-ai-pinecone-store-spring-boot-starter | Managed, large scale |
> | Weaviate | spring-ai-weaviate-store-spring-boot-starter | Hybrid search |
> | In-Memory | built-in | Testing only |
>
> **All implement the same `VectorStore` interface — swap with config, not code.**

---

*Script:*

Same vendor-neutral pattern as AI models — all vector stores implement the `VectorStore` interface. Switch from in-memory to pgvector to Pinecone by changing your starter dependency and configuration. Your application code is untouched.

For development: use in-memory. For production on an existing Postgres stack: pgvector is the easiest path because you're already running Postgres and understand its operational model. For high-scale managed solutions: Pinecone. Make your choice based on your infrastructure, not on Spring AI constraints.

---

# SECTION 12 — AI MODEL CONFIGURATION & BEST PRACTICES (≈ 3 min)

---

**[SLIDE 21 — Configuration Best Practices]**
> **Title:** Configuration & Best Practices
> **Bullets:**
> - Never hard-code API keys — use env vars, Vault, or Spring Cloud Config
> - Set `max-tokens` explicitly — prevent runaway costs from unexpectedly verbose responses
> - Configure read timeouts to 60 seconds or more — AI calls are slow
> - Use temperature intentionally: 0.0 = deterministic/factual, 1.0+ = creative
> - Log prompts and responses in dev, not in production (contains sensitive user data)
> - Cache embeddings — same text always produces same vector, cache aggressively
> - Keep models as Spring beans — always inject, never instantiate directly
> - Store prompt templates in `resources/prompts/` — version controlled and reviewable

---

*Script:*

A few critical best practices. First: never commit API keys. Ever. Always use environment variable references or a secrets manager.

Set `max-tokens` explicitly. Without a limit, a verbose model might generate a 10,000-token response to a question that needed 50 words, burning money and causing timeouts in your application.

Tune `temperature` to your use case. For factual Q&A and RAG, use 0.0 or 0.1 — you want the model to stick to the provided context, not improvise. For creative writing or brainstorming tools, go higher.

Cache your embeddings. Computing embeddings for the same text costs money and time. If you're embedding the same FAQ documents repeatedly, cache the result.

---

# SECTION 13 — ERROR HANDLING & FALLBACK STRATEGIES (≈ 4 min)

---

**[SLIDE 22 — Error Handling]**
> **Title:** Error Handling & Fallback Strategies
> **Code block:**
> ```java
> @Service
> public class ResilientAiService {
>
>     private final ChatModel primaryModel;
>     private final ChatModel fallbackModel;
>
>     public String askWithFallback(String question) {
>         try {
>             return primaryModel.call(question);
>         } catch (AiException e) {
>             log.warn("Primary model failed: {}. Activating fallback.", e.getMessage());
>             return fallbackModel.call(question);
>         }
>     }
> }
> ```
> **Common exception types:**
> - `AiException` — base class for all Spring AI exceptions
> - `RateLimitException` — quota exceeded, back off and retry
> - `ModelNotAvailableException` — provider outage
>
> **Resilience4j circuit breaker:**
> ```java
> @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackResponse")
> public String ask(String question) {
>     return chatModel.call(question);
> }
>
> public String fallbackResponse(String question, Throwable t) {
>     return "I'm temporarily unavailable. Please try again shortly.";
> }
> ```

---

*Script:*

AI APIs fail. Rate limits get hit. Providers have outages. You must design for this from the start.

Spring AI's exception hierarchy starts with `AiException`. Catch specific subclasses for specific handling. `RateLimitException` should trigger exponential backoff and retry logic. For provider outages, consider failing over to a secondary model — switching from GPT-4o to Claude, or from a cloud model to a local Ollama instance.

Resilience4j integrates naturally with Spring AI just like it does with any other Spring service. Wrap your AI calls with `@CircuitBreaker` so that if the model is consistently failing, the circuit opens and your fallback method returns a graceful message immediately rather than allowing every request to hang until timeout.

AI features should degrade gracefully. A broken AI feature should never bring down your entire application.

---

# SECTION 14 — MONITORING & LOGGING AI INTERACTIONS (≈ 4 min)

---

**[SLIDE 23 — Monitoring]**
> **Title:** Monitoring & Logging AI Interactions
> **Bullets:**
> - Spring AI integrates with Spring Boot Actuator automatically
> - Micrometer metrics auto-recorded:
>   - `spring.ai.chat.client.operation` — latency, token usage per call
>   - `spring.ai.embedding.model.operation` — embedding call latency
> - Export to Prometheus, Grafana, Datadog, CloudWatch — standard Micrometer exporters
> - OpenTelemetry distributed tracing via Spring's standard tracing support
>
> **application.yaml for metrics:**
> ```yaml
> management:
>   endpoints:
>     web:
>       exposure:
>         include: health, metrics, prometheus
>   metrics:
>     tags:
>       application: my-ai-app
> ```
>
> **What to monitor and alert on:**
> - Token consumption per endpoint (primary cost driver)
> - p95 / p99 latency
> - Error rate by model provider
> - Embedding cache hit rate
> - Abnormal token usage spikes

---

*Script:*

Monitoring AI interactions is different from monitoring regular services because the primary cost driver isn't CPU or memory — it's *tokens*. You need visibility into how many tokens your application consumes per request, per endpoint, and per model.

Spring AI automatically records Micrometer metrics for all model calls when Spring Boot Actuator is on the classpath. You get latency histograms and token usage counters out of the box. Export these to whatever monitoring stack you already use.

Set up token usage alerts. It is very easy for a runaway loop, an unexpectedly verbose prompt, or an unexpected traffic spike to multiply your AI costs by 100x overnight. Alert on abnormal token usage spikes before your bill does.

For logging: log prompts and responses in a compliant staging environment for debugging. In production, be careful — prompts often contain sensitive user data. Implement proper log redaction or use a structured logging approach that lets you toggle prompt logging per environment.

---

# SECTION 15 — WRAP-UP & Q&A (≈ 3 min)

---

**[SLIDE 24 — Summary]**
> **Title:** What We Covered Today
> **Bullets:**
> - Spring AI architecture: ChatModel, EmbeddingModel, VectorStore, ChatClient
> - PromptTemplate for safe dynamic prompts stored on classpath
> - Model integrations across all major providers via starters
> - @AiService for declarative, interface-based AI services
> - ChatClient with Advisors for conversation memory
> - Function calling for AI-driven tool use and live data access
> - Structured output with BeanOutputConverter
> - RAG: ingestion pipeline + QuestionAnswerAdvisor
> - Vector store options — all implementing VectorStore interface
> - Configuration best practices including API key security
> - Error handling with Resilience4j circuit breakers
> - Monitoring with Micrometer, Actuator, and token usage alerts

---

**[SLIDE 25 — The Three Things to Remember]**
> **Title:** Three Things to Take Away
> 1. **Everything is an interface** — ChatModel, EmbeddingModel, VectorStore are all swappable without changing application code
> 2. **Advisors are your power tools** — memory, RAG, logging, retry, all via composable Advisors on ChatClient
> 3. **Fail gracefully** — AI calls fail; circuit breakers and fallback strategies are not optional in production

---

**[SLIDE 26 — Resources & Next Steps]**
> **Title:** Resources
> - **Official Docs:** docs.spring.io/spring-ai/reference
> - **GitHub:** github.com/spring-projects/spring-ai
> - **Sample Apps:** github.com/spring-projects/spring-ai (sample-apps folder)
> - **Spring Initializr:** start.spring.io → search "Spring AI"
> - **Suggested exercise:** Build a chatbot with InMemoryChatMemory + one function call

---

*Script:*

Let's bring it all together. Spring AI gives you a consistent, Spring-idiomatic way to integrate AI capabilities into your applications. The vendor-neutral interfaces mean you're never locked in. The Advisor pattern gives you a clean extension point for cross-cutting concerns like memory and RAG. And since it's standard Spring, all of your existing knowledge about dependency injection, testing, configuration, and observability applies directly.

The most important thing to internalize: AI is just another service in your Spring application. It gets injected, it gets tested, it gets monitored, and it gets circuit-broken like everything else.

For your lab this week: go to start.spring.io, create a project with the OpenAI starter, and build a simple chatbot that uses conversation memory. Then extend it with one function call that looks up live data. By the time you've done that, today's content will be solid.

Any questions?

---