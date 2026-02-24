# Day 34 — Spring AI | Walkthrough Script — Part 2
## Function Calling, Structured Output, RAG & Vector Store, Config/Error/Monitoring
**Estimated time:** 90 minutes  
**Code files:** `01-function-calling.java`, `02-structured-output.java`, `03-rag-and-vector-store.java`, `04-config-error-handling-monitoring.java`

---

## Pre-Class Checklist

- [ ] Spring Boot app compiles and starts
- [ ] `OPENAI_API_KEY` environment variable set
- [ ] Postman or REST client ready
- [ ] Both code files open side-by-side if possible

---

## Opening (2 min)

> "In Part 1 we learned the fundamentals: ChatModel, prompts, conversation history, streaming, and embeddings. You now know how to talk to an AI model from Java.
>
> Part 2 is where things get production-ready. We're going to tackle four real engineering challenges that every team hits when building AI-powered applications:
>
> One — how do you get the AI to return structured data your app can actually use, not just free text? Two — how do you give the AI the power to call functions in your own codebase? Three — how do you ground the AI's answers in your own data instead of its training memory? Four — how do you make it resilient, observable, and production-hardened?
>
> Let's start with function calling."

---

## Section 1 — Function Calling (20 min)
**File:** `01-function-calling.java`

---

### 1.1 The Concept (4 min)

> "Open `01-function-calling.java`. Start at the top comments in Section 1."

Point to Section 1 comments:

> "Here's the question: what does this AI response actually mean for your bookstore app?
>
> If a customer asks 'Is Clean Code in stock?' the LLM has no idea. It was trained on public internet data — it knows nothing about *your* inventory.
>
> Function calling solves this. You register Java methods with the AI as 'tools'. When the user asks something the AI can't answer from its training data, the model pauses, requests a function call with specific parameters, Spring runs the function against your real database, and the result is returned to the model. The model then answers using real data.
>
> The key insight: the AI decides *when* to call a function. You decide *what* the function does. Read the descriptions carefully — they're instructions to the AI about when to use each tool."

---

### 1.2 Function Definitions — The Request/Response Records (4 min)

Scroll to the records section:

> "We model each function's input and output as Java records. `BookInventoryRequest` takes an ISBN and returns `BookInventoryResponse` with `inStock` and `quantityAvailable`. Clean, typed, serializable.
>
> Spring AI uses these types to generate a JSON schema it sends to the model. The model fills in the parameters, Spring deserializes them into these records, calls your function, and serializes the result back."

---

### 1.3 FunctionCallbackWrapper — Registering Functions (5 min)

Scroll to `BookstoreFunctionConfig`:

> "This `@Configuration` class is where you register your functions as beans. Each `@Bean` returns a `FunctionCallback`.
>
> The `.withName()` is the function identifier — the AI calls it by this name. The `.withDescription()` is critical — this is what the AI reads to decide whether to invoke this function. Write it in plain English as if you're telling the AI when to use this tool. The `.withInputType()` tells Spring how to deserialize the model's JSON arguments.
>
> The lambda at the end is your actual business logic. In production this calls a repository or an external API."

---

### 1.4 ChatClient with Functions (5 min)

Scroll to `FunctionCallingService`:

> "In `callWithFunctions()` we chain `.functions()` onto the ChatClient. We're telling Spring AI: for this conversation, these three functions are available to the model.
>
> Notice the natural language query — 'Check if ISBN 978-0132350884 is in stock'. The model decides it needs to call `checkInventory`, populates the request with that ISBN, Spring calls your lambda, gets back a response, and the model weaves that into a natural sentence.
>
> You can register functions globally in the ChatClient builder via `.defaultFunctions()`, or per-request via `.functions()`. Per-request is better for security — only expose tools relevant to the current context."

> **Quick question:** "When the model wants to call a function, does it actually execute Java code? Or does it tell Spring AI which function to call and Spring runs it?"  
> *(Answer: Spring AI runs it — the model only outputs a structured request)*

---

### 1.5 Demo — Function Calling in Action (2 min)

> "Start the app. POST to `/api/ai/tools/query` with body `{ 'query': 'What books about design patterns do you have and how much do they cost?' }`.
>
> Watch the logs — you'll see multiple function invocations for one user message. The model orchestrates them. That's the real power: it can call multiple functions in the right order to answer a complex question."

---

## Section 2 — Structured Output (18 min)
**File:** `02-structured-output.java`

---

### 2.1 The Problem with Free Text (3 min)

> "Open `02-structured-output.java`. Read Section 1 comments.
>
> Without structured output, the AI returns a paragraph. You need to parse it — with regex, string splits, or manual parsing. That's fragile. The model might format it differently every time. One call it uses 'Title:' as a label, the next it uses '1.' as a bullet. Your parser breaks.
>
> Structured output tells the AI: respond in this exact JSON format matching this Java class. Spring AI generates the schema, injects it into the prompt as format instructions, and then deserializes the JSON string into your Java object automatically."

---

### 2.2 BeanOutputConverter (6 min)

Scroll to Section 3, `getStructuredRecommendation()`:

> "This is the core pattern. `BeanOutputConverter<BookRecommendation>` wraps your target class. Calling `.getFormat()` generates a JSON schema string — something like 'Respond with a JSON object with these fields: title (string), author (string), genre (string)...'
>
> We inject that schema string as a `{format}` placeholder in our PromptTemplate. The AI sees these instructions and responds with valid JSON.
>
> We get back a raw string. `converter.convert(jsonResponse)` parses it into a `BookRecommendation` record. That's it. You now have a typed Java object, not text."

Point to `BookRecommendationList`:

> "Nested types work too. `BookRecommendationList` contains a `List<BookRecommendation>`. The converter handles the nesting automatically. The AI produces a JSON object with an array inside and Spring maps it to the right fields."

---

### 2.3 ChatClient `.entity()` API (3 min)

Scroll to `analyzeBookReviews()`:

> "ChatClient has an even cleaner API for this: `.entity(BookReviewAnalysis.class)`. You skip the converter entirely — ChatClient handles the format instructions and deserialization internally.
>
> This is the preferred pattern when you're using ChatClient. Use `BeanOutputConverter` directly only when you need to work with `ChatModel` directly or need more control over the format instructions."

---

### 2.4 ListOutputConverter and MapOutputConverter (3 min)

Scroll to `getGenreList()` and `getAuthorFacts()`:

> "`ListOutputConverter` for `List<String>` — no wrapper record needed. `MapOutputConverter` for `Map<String, Object>` — good for flexible structures when you don't know the keys in advance, or when the data shape varies.
>
> Both follow the same pattern: get format instructions, inject into prompt, convert the response."

---

### 2.5 ParameterizedTypeReference (2 min)

Scroll to `getRecommendationListDirect()`:

> "One edge case: if you want `List<BookRecommendation>` directly without a wrapper record, you need a `ParameterizedTypeReference` because of Java's type erasure. `new org.springframework.core.ParameterizedTypeReference<>() {}` — that anonymous inner class captures the generic type at compile time. This is a pattern you'll also see with Spring's RestTemplate and WebClient."

> **Quick question:** "Why can't you just do `new BeanOutputConverter<>(List.class)` to get a list?"  
> *(Answer: type erasure — List.class loses the generic type info; ParameterizedTypeReference captures it)*

---

## Section 3 — RAG & Vector Store (25 min)
**File:** `03-rag-and-vector-store.java`

---

### 3.1 The Hallucination Problem (4 min)

> "Open `03-rag-and-vector-store.java`. Read Section 1 carefully — this is one of the most important concepts in practical AI engineering.
>
> Ask the class: 'If a customer asks what books we have about Spring AI, what will GPT-4 say?' Pause.
>
> It'll make something up, or say it doesn't know. That's because GPT-4 was trained on internet data from before Spring AI existed. It has zero knowledge of your bookstore's specific catalog.
>
> RAG — Retrieval Augmented Generation — is the production solution. Instead of relying on the model's training memory, you:
> 1. Convert your documents into vector embeddings and store them
> 2. When a user asks a question, find the most relevant document chunks
> 3. Inject those chunks into the prompt
> 4. The model answers using REAL data you provided
>
> The AI is now grounded in your data. Hallucinations drop dramatically."

Draw on whiteboard or describe the flow:
```
User question → embed → vector search → relevant chunks
                                              ↓
                                    augmented prompt → LLM → answer
```

---

### 3.2 ETL Pipeline — Ingesting Documents (7 min)

Scroll to `BookstoreDocumentIngestionService`:

> "Before you can search, you need to load your documents. This is ETL — Extract, Transform, Load — applied to AI.
>
> In `ingestBookCatalog()`: `TextReader` loads a text file as `Document` objects — that's the Extract step. We attach metadata like `source` and `type` to each document. Metadata lets you filter searches later.
>
> `TokenTextSplitter` is the Transform step. LLMs have context limits. A 500-token chunk is about 375 words. If your book catalog is 50,000 words, you split it into ~133 chunks. The splitter tries to break at sentence boundaries so chunks make semantic sense.
>
> Then `vectorStore.add(chunks)` — the Load step. This calls the EmbeddingModel for each chunk, converts the text to a numeric vector (an array of 1536 floats for OpenAI), and stores both the vector and original text. Now you can search semantically.
>
> In `ingestBookDescriptions()` we create Documents programmatically. This is what you'd use when loading from a database — pull your product catalog rows, create a Document per book, add metadata like genre and author, bulk insert."

---

### 3.3 VectorStore Configuration — Dev vs Prod (4 min)

Scroll to `VectorStoreConfig`:

> "Two implementations.
>
> `SimpleVectorStore` — in-memory. Nothing to install, no external service. Data disappears on restart. Perfect for demos, unit tests, or prototyping. This is what we're using today.
>
> `PgVectorStore` — PostgreSQL with the `pgvector` extension. You run `CREATE EXTENSION vector` once on your Postgres instance. Spring AI creates the table automatically with `initialize-schema=true`. Stores millions of vectors durably. This is what you ship to production.
>
> The beautiful part: your application code doesn't change. You inject `VectorStore`, and Spring Boot swaps the implementation based on which starter dependency is on the classpath and what's configured in `application.properties`."

---

### 3.4 Manual RAG Flow (5 min)

Scroll to `ManualRagService.answerWithContext()`:

> "Let's walk through each step of the RAG pipeline.
>
> Step 1 — Retrieval. `vectorStore.similaritySearch()` takes a `SearchRequest`. The query string gets embedded by the same EmbeddingModel that was used during ingestion — this produces a query vector. Spring AI computes cosine similarity between your query vector and all stored document vectors, returning the `topK` most similar chunks. The `withSimilarityThreshold(0.7)` means 'only return results that are at least 70% similar' — this prevents returning garbage results when the user asks something completely off-topic.
>
> Step 2 — Augmentation. We join the retrieved chunks into a context string with separator lines between them.
>
> Step 3 — Generation. We inject the context into a PromptTemplate. Notice the instruction: 'Use ONLY the information in the context below'. This is critical — it prevents the model from mixing retrieved data with training data hallucinations.
>
> If nothing relevant is found, we return a graceful message immediately without calling the LLM."

---

### 3.5 QuestionAnswerAdvisor — Automatic RAG (4 min)

Scroll to `AutoRagService`:

> "Manual RAG requires you to write retrieval, augmentation, and generation code yourself. Spring AI's advisor pattern automates this.
>
> `QuestionAnswerAdvisor` is injected into the `ChatClient.Builder` as a default advisor. Every call to `.prompt().user(...).call()` automatically triggers the retrieval + augmentation pipeline behind the scenes. Your calling code looks identical to a normal ChatClient call.
>
> This is the preferred pattern in production. It separates the AI call logic from the RAG plumbing. You can swap vector stores, change topK, or add metadata filters without touching your service layer."

> **Quick question:** "If you have 1 million book documents in PgVectorStore, how does similarity search stay fast?"  
> *(Answer: pgvector builds an HNSW or IVFFlat index on the vector column — approximate nearest neighbor search is sub-second even at millions of vectors)*

---

## Section 4 — Config, Error Handling & Monitoring (20 min)
**File:** `04-config-error-handling-monitoring.java`

---

### 4.1 Configuration Best Practices (4 min)

> "Open `04-config-error-handling-monitoring.java`. Read the comment block in Section 1 — the entire `application.properties` block.
>
> Rule one: API keys ALWAYS come from environment variables. Never hardcode them, never commit them to Git. `${OPENAI_API_KEY}` reads from the OS environment at startup. In production this comes from AWS Secrets Manager, Vault, or your CI/CD pipeline.
>
> Temperature is a dial between determinism and creativity. For your bookstore catalog search, use 0.1 to 0.3 — you want consistent, factual answers. For generating creative book descriptions or summaries, go up to 0.7. If you need maximum creativity for story ideas, go to 0.9.
>
> Max-tokens limits cost. At roughly $0.01 per 1,000 tokens for GPT-4o, an 800-token limit on a high-traffic endpoint makes a significant cost difference. Size it for the task — a yes/no answer needs 20 tokens, a book summary needs 300.
>
> Profile-based config lets you use `gpt-4o-mini` in dev (10x cheaper) and `gpt-4o` in prod. Same code, different config."

---

### 4.2 ChatClient Configuration Bean (2 min)

Scroll to `AiConfiguration`:

> "We inject model name, temperature, and max tokens from properties into the `@Configuration`. The `ChatClient.Builder` gets a `defaultSystem` prompt — this is the persona injected into every conversation — plus `defaultOptions` applying our model settings globally.
>
> One `@Bean ChatClient` with sensible defaults, and every service that injects it benefits automatically."

---

### 4.3 Error Handling — AiException Hierarchy (4 min)

Scroll to `RobustAiService.getRecommendationSafe()`:

> "Spring AI throws `AiException` for all provider errors. The exception hierarchy tells you what to do:
>
> `NonTransientAiException` — something is fundamentally wrong. Bad API key, model not found, content policy violation. Don't retry — log it and surface an error.
>
> Transient exceptions — network timeouts, connection resets. These should be retried.
>
> The `catch (Exception e)` at the bottom calls `fallbackService.getFallbackRecommendation()`. Always have a fallback. The AI being unavailable should never hard-crash your application."

---

### 4.4 @Retryable — Automatic Retry (4 min)

Scroll to `getRecommendationWithRetry()`:

> "Rate limit errors (HTTP 429) are the most common transient failure in production. OpenAI, Anthropic, and every other provider has rate limits. At high traffic, you'll hit them.
>
> `@Retryable` from Spring Retry is the cleanest solution. Add `@EnableRetry` to the configuration, annotate the method, specify which exceptions to retry, and configure the backoff.
>
> With `delay = 2000` and `multiplier = 2.0`, the retry schedule is: 2 seconds, 4 seconds, 8 seconds. This is exponential backoff — standard industry practice for rate-limited APIs.
>
> Spring AI also has built-in retry via `spring.ai.retry.*` properties. Use that for quick setup, `@Retryable` for method-level control."

---

### 4.5 Circuit Breaker (3 min)

Scroll to `getRecommendationCircuitBreaker()`:

> "Retry handles temporary blips. A circuit breaker handles sustained outages.
>
> If 50% of your last 10 AI calls failed, the circuit opens. For the next 30 seconds, every call goes directly to the fallback — we skip hitting the AI entirely. This protects your app from thread pool exhaustion and cascading failures.
>
> `@CircuitBreaker(name = 'ai-service', fallbackMethod = 'circuitBreakerFallback')` — one annotation. The fallback method receives the original arguments plus the exception.
>
> Pair this with `@Retryable` for a complete resilience strategy: retry transient errors, circuit-break sustained outages."

---

### 4.6 Monitoring — Token Usage and Observability (3 min)

Scroll to `AiMonitoringService`:

> "`callWithTokenLogging()` shows how to get token usage from `ChatResponse.getMetadata().getUsage()`. You get prompt tokens (what you sent), completion tokens (what the AI returned), and total. Log these to your metrics platform. Token usage is directly proportional to cost — if total tokens spike unexpectedly, something in your prompts changed.
>
> Spring AI auto-integrates with Micrometer's `ObservationRegistry`. Every ChatModel call automatically creates a span with the model name, token counts, and latency. Expose the Prometheus endpoint and you get AI call latency, error rates, and token usage as Grafana dashboards with zero extra code.
>
> `callWithAuditLogging()` is a pattern for compliance — log who asked what, when, and how long it took. In regulated industries you may need a full audit trail of every AI interaction."

---

## Wrap-Up (5 min)

> "Let's map what we built across Part 1 and Part 2 to the full Spring AI capability set."

Draw on board or summarize:

```
Spring AI Capabilities — Bookstore App Coverage
────────────────────────────────────────────────
Part 1:
  ✅ ChatModel — direct model calls
  ✅ PromptTemplate — parameterized prompts  
  ✅ Conversation history — multi-turn chatbot
  ✅ Streaming — Flux<String> SSE responses
  ✅ EmbeddingModel — semantic similarity
  ✅ ChatClient — fluent builder API

Part 2:
  ✅ Function calling — AI-driven tool invocation
  ✅ Structured output — Java POJO from AI text
  ✅ RAG — retrieval-augmented generation
  ✅ VectorStore — semantic document search
  ✅ Error handling — AiException, @Retryable
  ✅ Circuit breaker — Resilience4j
  ✅ Monitoring — Micrometer, token usage
```

---

## Interview Questions (go through these as a class)

1. **"What is the difference between ChatModel and ChatClient in Spring AI?"**  
   *(ChatModel is the low-level interface; ChatClient is the fluent builder on top of it with advisors, default options, and structured output support)*

2. **"How does Spring AI function calling work at the protocol level?"**  
   *(The model outputs a structured function call request in its response; Spring AI intercepts it, executes the registered Java function, returns the result to the model, and the model generates a final response)*

3. **"Why would you use BeanOutputConverter over just asking the AI to 'respond in JSON'?"**  
   *(BeanOutputConverter generates a precise JSON schema from your class and injects it as format instructions, giving consistent structure; free-form 'respond in JSON' prompts are unreliable)*

4. **"Explain the ETL pipeline in RAG."**  
   *(Extract: load documents with TextReader/TikaDocumentReader; Transform: chunk with TokenTextSplitter, add metadata; Load: embed with EmbeddingModel and store vectors in VectorStore)*

5. **"What is the difference between SimpleVectorStore and PgVectorStore?"**  
   *(SimpleVectorStore is in-memory with no persistence — dev/testing only; PgVectorStore uses PostgreSQL + pgvector extension for durable, indexed, production-scale vector storage)*

6. **"How does @Retryable differ from a circuit breaker?"**  
   *(@Retryable retries individual failed calls with backoff — handles transient errors; a circuit breaker stops calling the service entirely after a failure threshold — handles sustained outages)*

---

## Cheat Card — Spring AI Part 2

```
FUNCTION CALLING
  FunctionCallbackWrapper.builder()
    .withName("checkInventory")
    .withDescription("Use when customer asks about stock")
    .withInputType(BookInventoryRequest.class)
    .build()
  chatClient.prompt().functions("checkInventory").user(query).call().content()

STRUCTURED OUTPUT
  BeanOutputConverter<MyRecord> conv = new BeanOutputConverter<>(MyRecord.class);
  prompt.create(Map.of("format", conv.getFormat()))  // inject schema
  conv.convert(rawResponse)                          // parse JSON → Java
  chatClient.prompt().user(q).call().entity(MyRecord.class)  // ChatClient shortcut

RAG — INGEST
  new TextReader(resource)           // load documents
  new TokenTextSplitter(500, ...)    // chunk
  vectorStore.add(chunks)            // embed + store

RAG — QUERY
  vectorStore.similaritySearch(
    SearchRequest.query(q).withTopK(4).withSimilarityThreshold(0.7))
  // or: add QuestionAnswerAdvisor to ChatClient.Builder

ERROR HANDLING
  catch (NonTransientAiException e)  // bad key, policy violation
  @Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=2))
  @CircuitBreaker(name="ai-service", fallbackMethod="fallback")

MONITORING
  response.getMetadata().getUsage().getTotalTokens()
  ObservationRegistry  // auto-configured by Spring AI + Micrometer
  management.endpoints.web.exposure.include=prometheus
```
