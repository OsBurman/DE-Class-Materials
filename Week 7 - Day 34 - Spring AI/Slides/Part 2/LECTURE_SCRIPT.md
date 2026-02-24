# Day 34 Part 2 — Spring AI: Function Calling, Structured Output, RAG, and Production Concerns
## Lecture Script

---

**[00:00–02:30] Opening — The Gap Between Simple Prompts and Real Applications**

Welcome back. Part 1 gave you the foundation: Spring AI's abstraction layer, `ChatModel`, `ChatClient`, `PromptTemplate`, conversation memory, streaming. You can now build a chatbot that talks to OpenAI or Anthropic from a Spring Boot controller.

Part 2 is where we bridge the gap between "chatbot that answers general questions" and "AI feature embedded in a real production system." There are three problems that remain unsolved from Part 1.

Problem one: the AI can only talk. It can't look anything up. It doesn't know the current inventory of your bookstore. It doesn't know the current weather. It can't send an email. You have to give it the ability to call your code. That's function calling.

Problem two: the AI returns unstructured text. If you ask it for a book recommendation, it returns a paragraph. If you want to store that recommendation in a database, you'd normally need to parse it manually. There's a better way. That's structured output.

Problem three: the AI doesn't know about your data. It was trained on the public internet. It doesn't know the contents of your internal documentation, your product catalog, your support tickets. RAG connects it to your specific data. And Spring AI makes the implementation much more manageable than building it from scratch.

Then we'll cover configuration best practices, error handling for production, fallback strategies, and monitoring. Let's go.

---

**[02:30–10:00] Slides 2–3 — Function Calling**

Function calling is one of the most powerful capabilities modern AI APIs provide, and it's worth understanding the mental model before we look at code.

Without function calling, an AI model is a very sophisticated text transformer. Input text in, output text out. It was trained on data up to a certain date. It doesn't know what's happened since then. It doesn't know your database. It can't call your REST services. It can only work with what you put in the prompt.

Function calling changes this by introducing tools. You register a set of functions with descriptions. When the AI is processing a request, it can choose to call one of those functions if it decides the function is necessary to answer the question. You execute the function. You return the result. The AI incorporates that result and generates a final answer.

Let me trace through a concrete example. A user asks your bookstore assistant: "Do you have Clean Code in stock?" Without function calling, the AI would say "I'm not sure, you should check the website." That's useless. With function calling: the AI receives the question, reasons that it needs inventory information, returns a structured message saying "call `checkInventory` with ISBN `978-0132350884`". Spring AI intercepts this, calls your actual `checkInventory` Java method, gets back the integer `3`. Spring AI sends this back to the AI as a tool result. The AI now generates: "Yes, we currently have 3 copies of Clean Code in stock." That's a real, useful answer.

The AI decides when to call a function based on your description. If the user just asks "What is object-oriented programming?", the AI correctly determines that no tool call is needed — it can answer from its training data. It only calls tools when it has determined the tool is necessary to answer the question at hand. Your job is to write good descriptions.

In Spring AI, the cleanest way to implement this is the `@Tool` annotation. Create a regular Spring `@Component`. Write your methods with `@Tool` annotations. The `description` attribute is what the AI reads to decide when to use this function — this is the most important part. Write it clearly: "Find books by a given author name. Returns a list of books with title and ISBN." That's descriptive enough that the AI understands both what it does and what it returns.

You can annotate individual parameters with `@ToolParam` with their own descriptions. For complex input types, you'd define a record or class that the AI fills in from the user's message.

Using tools is then a one-liner on the `ChatClient` call: `.tools(bookstoreTools)` — pass the Spring component directly. Spring AI uses reflection to discover all `@Tool`-annotated methods and makes them available to the model for that call.

The second approach — `FunctionCallback` — is more programmatic. You use it when you don't have a Spring component, or when you want to define a quick lambda. Use `FunctionCallback.builder()`, `.function("name", lambda)`, `.description("...")`, `.inputType(InputClass.class)`, `.build()`. Then pass it to `.toolCallbacks(List.of(callback))`. It's more verbose but gives you full control.

One important note on tool descriptions: be specific. "Do something with a book" is too vague. "Find books by a given author name. Returns a list with title, ISBN, and price for each matching book" — that's clear. The model uses your description to decide whether this is the right tool for a given user request. Bad descriptions lead to tools being called at wrong times or not at all.

---

**[10:00–17:00] Slides 4–5 — Structured Output**

Structured output solves the JSON-parsing headache that every AI integration developer runs into.

Here's the problem. You ask the AI: "Recommend a book about microservices. Give me the title, author, ISBN, and why I should read it." The model might respond: "I recommend 'Building Microservices' by Sam Newman (ISBN: 978-1491950357). This book is essential reading because it..." That's a perfectly good paragraph. But if you want to save this recommendation to a database, you need to parse it. What's the ISBN? What's the title? Where does the "why" end? It's brittle regex and string parsing.

`BeanOutputConverter` fixes this elegantly. You define a Java record that represents the structure you want. The converter generates a JSON Schema instruction from that record and appends it to the prompt automatically. The model sees the schema and produces a JSON response that matches it. The converter then parses that JSON into your record. You get a type-safe Java object back with no manual parsing.

Define the record: `BookRecommendation` with fields `title`, `author`, `isbn`, and `reasonToRead`. Instantiate `new BeanOutputConverter<>(BookRecommendation.class)`. Call `converter.getFormat()` — this returns a multi-line instruction string that includes the JSON schema. Build a `PromptTemplate` with a `{format}` placeholder. Substitute the format instruction. Call `chatModel.call(prompt)`. Call `converter.convert(responseText)`. You get a `BookRecommendation` record.

For lists, `ListOutputConverter` handles `List<String>`. For maps, `MapOutputConverter` handles `Map<String, Object>`.

But `ChatClient` makes this even cleaner with the `.entity()` API. Instead of managing a converter yourself, you just tell `ChatClient` what type you want at the end of the call chain. `chatClient.prompt().user("...").call().entity(BookRecommendation.class)`. That's it. Spring AI injects the format instruction internally, calls the model, parses the response, and returns your record. One method call.

For lists — `List<BookRecommendation>` — you use `entity(new ParameterizedTypeReference<List<BookRecommendation>>() {})`. The anonymous `ParameterizedTypeReference` is how Java handles generic type information at runtime — same pattern you've seen in Spring's `RestTemplate` or `WebClient`.

A few tips for reliable structured output. Use records, not classes with inheritance. Keep the structure flat — the AI handles simple records better than deeply nested ones. Use clear field names that read like natural language. If a field might be absent, mark it nullable. And test with multiple prompts — language models are probabilistic, and occasionally the structure slips. Add validation or a try/catch around the parsing.

---

**[17:00–22:00] Slide 6 — RAG Architecture Overview**

RAG — Retrieval-Augmented Generation. Day 33 covered what this is conceptually. Today we implement it.

The core insight of RAG: an AI model's training has a cutoff date and it doesn't know your private data. But if you put the relevant text directly into the prompt, the model can answer questions about it. The challenge is scale: your documentation might be thousands of pages. You can't put all of it in every prompt — that would be too slow, too expensive, and would overwhelm the model's context window.

RAG's solution is a two-phase system. The ingestion phase runs once — or periodically when documents change. You read your documents, split them into reasonable-sized chunks, generate an embedding (vector) for each chunk, and store those vectors in a vector database. The vector database is optimized for finding similar vectors quickly.

The query phase runs on every user request. When a user asks a question, you embed the question using the same model. You search the vector store for the chunks whose embeddings are most similar to the question's embedding. You retrieve the top few chunks. You inject those chunks as context into the prompt. Now the AI is answering based on your relevant documentation, not just its training data.

The beautiful thing about Spring AI's implementation is that all of this is hidden inside a single advisor. You configure it once. Every subsequent call automatically retrieves and injects context. Your service code doesn't change at all.

---

**[22:00–31:00] Slides 7–9 — Document Pipeline and Vector Stores**

Let me walk through the implementation pieces.

Document readers. Spring AI's `TikaDocumentReader` uses Apache Tika under the hood — the same library that powers search indexers and document processing tools. You give it a `Resource` pointing to a file — could be a PDF, a Word document, an HTML file, a plain text file, a PowerPoint presentation. It reads the content, strips the formatting, and returns a list of `Document` objects. Each `Document` has a `content` string and a `metadata` map.

`JsonReader` handles structured JSON files. You specify a JSON Pointer — a path expression like `/description` — to tell it which field contains the text to embed. This is useful when you have a product catalog as JSON and you want to embed the product descriptions.

After reading, you add metadata to each document before ingesting. Source file name, category, version, timestamp. This metadata gets stored alongside the embedding in the vector store. Later when you retrieve documents, you can filter by metadata — only retrieve documentation tagged with the current product version, for example.

Then splitting. A raw PDF might be 80,000 tokens. OpenAI's embedding model can only handle 8,192 tokens at a time. More importantly, large chunks produce noisy embeddings that represent too many topics at once. You want focused chunks that each represent a specific topic.

`TokenTextSplitter` takes your documents and splits them by token count with configurable overlap. The key parameters: `defaultChunkSize` — how many tokens per chunk, typically 500–800. `minChunkSizeChars` — minimum characters before splitting, to avoid creating tiny meaningless fragments. `keepSeparator: true` — respect sentence boundaries so chunks don't end mid-sentence.

The overlap parameter is critical. If a chunk ends at a sentence boundary and the next chunk starts at the next sentence, any concept that spans those two sentences would be split. Overlap means each chunk contains a bit of the previous chunk's ending text, so nothing falls through the cracks at boundaries.

After splitting, call `vectorStore.add(chunks)` and Spring AI does the rest: calls `embeddingModel.embed()` for each chunk, stores the chunk content plus the vector plus the metadata.

For development: `SimpleVectorStore`. No external database. It's in-memory, built into Spring AI, configured with just a `@Bean` method. Perfect for getting RAG working on your laptop. If you call `save(new File("vectorstore.json"))`, it persists to disk so you don't have to re-ingest on every restart. Limitation: it's a linear scan — searching through 10,000 vectors one by one. Fine for hundreds of documents, slow for more.

For production: `PgVectorStore`. You need one thing: the `pgvector` extension for PostgreSQL. The simplest way: use the `pgvector/pgvector:pg16` Docker image, which has the extension pre-installed. In `application.yml`, set `spring.ai.vectorstore.pgvector.index-type: HNSW` — Hierarchical Navigable Small World, an approximate nearest-neighbor algorithm that's fast even for millions of vectors. Set `distance-type: COSINE_DISTANCE` — best for text similarity. Set `dimensions` to whatever your embedding model produces — 1536 for OpenAI `text-embedding-3-small`.

The `dimensions` value is a hard constraint. If you configure 1536 here but your embedding model produces 768-dimensional vectors, Spring AI throws an exception at startup. Check what your model produces and match it.

Here's the beautiful part: your ingestion code and your retrieval code don't reference `SimpleVectorStore` or `PgVectorStore`. They reference `VectorStore` — the interface. Switch from development to production by changing a profile and a dependency. Your service code is untouched.

---

**[31:00–37:00] Slide 10 — QuestionAnswerAdvisor**

`QuestionAnswerAdvisor` is where the RAG query phase is fully automated. Let me explain what it does internally and then show you how little code you need to write.

When you add `QuestionAnswerAdvisor` to your `ChatClient`, it intercepts every `.call()`. Before the request goes to the AI model, the advisor takes the user's message text, embeds it using your `EmbeddingModel`, runs a similarity search against your `VectorStore`, retrieves the top K matching document chunks, formats them into a context block, and prepends that context block to the prompt. The model receives your system prompt, the retrieved context, and the user's message all as one request. Then after the response comes back, the advisor passes it through unchanged.

The model's system prompt sets expectations: "You are a knowledgeable assistant. Answer questions based on the provided context. If the context does not contain enough information, say so clearly rather than guessing." That last sentence is important — without it, the model will hallucinate an answer from its training data even when the context is insufficient.

Setup: one `@Bean` method. Inject `ChatClient.Builder` and `VectorStore`. Call `.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().topK(4).similarityThreshold(0.65).build()))`. That's all the configuration. Now every call to this `ChatClient` runs RAG automatically.

Usage: identical to any other `ChatClient` usage. `ragChatClient.prompt().user(question).call().content()`. No indication in the calling code that RAG is happening. It's fully encapsulated in the advisor.

You can combine `QuestionAnswerAdvisor` with `MessageChatMemoryAdvisor` on the same `ChatClient`. Now every request carries both the conversation history and the retrieved context. The final prompt structure becomes: system prompt + conversation history + retrieved document chunks + new user message. This is a powerful combination for building a genuinely capable assistant that knows both your data and the conversation context.

Tuning the advisor: `topK` — how many chunks to retrieve. 4 is a good default. Increase to 8–10 if questions might need broad context from multiple documents. `similarityThreshold` — minimum cosine similarity score (0 to 1) to include a result. 0.65 is reasonable. If your AI is giving answers about completely unrelated topics, raise the threshold. If it keeps saying "the context doesn't contain enough information" when you know it does, lower it.

---

**[37:00–43:00] Slide 11 — Configuration Best Practices**

Let me talk about configuration — specifically, the practices that distinguish a production application from a demo.

YAML structure. Set your OpenAI API key to `${OPENAI_API_KEY}` — the Spring `${...}` syntax means "read from environment variable." Never put the actual key in `application.yml`. That file goes into version control. API keys in version control is how you get compromised. Use environment variables, and if you need a local `.env` file to set them, add it to `.gitignore`.

Environment-specific profiles. `application.yml` is your shared base. `application-dev.yml` overrides for development. `application-prod.yml` overrides for production. In development, override the provider to Ollama — local, free, private, no API costs. Developers can run the full application offline. In production, use your production model with appropriate settings. Set `SPRING_PROFILES_ACTIVE=dev` as an environment variable on developer machines and in CI. Set it to `prod` on the server.

API key storage by environment. On developer machines: a `.env` file loaded by your IDE or the `spring-dotenv` dependency. In CI/CD: GitHub Actions Secrets or Jenkins credentials. In production on AWS: AWS Secrets Manager. On Kubernetes: Kubernetes Secrets mounted as environment variables. The pattern is the same in all cases — no secrets in source code or `application.yml`.

Cost management. You have two levers on cost: which model you use, and how many tokens you allow. `gpt-4o` is expensive — roughly $5 per million input tokens and $15 per million output tokens as of 2025. `gpt-4o-mini` is roughly $0.15 and $0.60 respectively — about 15–20x cheaper. For a simple classification or short-answer feature, `gpt-4o-mini` is more than capable. Save the expensive model for complex reasoning tasks. Set `maxTokens` to the minimum your feature needs. A customer service response doesn't need 4000 tokens. Set it to 500 and the model is forced to be concise, which is usually what you want anyway.

---

**[43:00–49:00] Slides 12–13 — Error Handling and Fallback**

Production AI services will fail. Rate limits, temporary outages, invalid requests. Your application needs to handle this gracefully.

Spring AI maps all provider errors to a consistent exception hierarchy. `AiException` is the base. `NonTransientAiException` means the error is permanent — a bad API key, an invalid parameter, a content policy violation. Don't retry. Log the error and fail fast or return a user-friendly message. `TransientAiException` means the error is temporary — a rate limit 429, a service unavailable 503, a network timeout. These are worth retrying.

Spring AI has built-in retry configured entirely in YAML. `spring.ai.retry.max-attempts: 3`. `backoff.initial-interval: 1000` — wait 1 second before the first retry. `multiplier: 2` — double the wait each time. `max-interval: 30000` — cap at 30 seconds. `on-http-codes: 429,503` — which codes trigger retry. `exclude-on-http-codes: 401` — don't retry authentication failures. This configuration alone handles the most common failure scenarios without any code.

For application-level fallbacks, there are three levels of sophistication. Level one: simple try/catch. Catch `AiException`, log it, return a graceful message: "I'm unable to process your request right now. Please try again in a few minutes." Minimal code, handles outages gracefully.

Level two: fallback to a different model. You have a primary `ChatClient` pointing to `gpt-4o` and a fallback `ChatClient` pointing to Ollama running locally. If the primary fails, call the fallback. The user gets an answer, possibly slightly lower quality, but the service stays up. This pattern is particularly valuable because Ollama runs locally — no network dependency, always available during development and in on-premise deployments.

Level three: Resilience4j circuit breaker. You've used Resilience4j in the microservices week. Apply `@CircuitBreaker(name = "aiService", fallbackMethod = "fallback")` to your AI service method. Configure the circuit: open after 50% failure rate over 5 calls, stay open for 30 seconds, then go half-open and test. Your `fallback` method receives the thrown exception and returns a safe response. The circuit breaker prevents your application from hammering a failing API — which is important because rate limits often compound when you keep retrying.

---

**[49:00–55:00] Slides 14–15 — Monitoring and Logging**

Once your AI service is in production, you need visibility. Two things: metrics for performance and cost, logging for audit trails and debugging.

Spring AI integrates with Micrometer automatically. The only requirement is `spring-boot-starter-actuator` on the classpath. No annotation, no configuration class. Just having the dependency enables the metrics.

The metrics Spring AI produces follow the OpenTelemetry semantic conventions for generative AI — `gen_ai.*` prefix. `gen_ai.client.operation.duration` is a Timer — it measures the full round-trip latency for each AI call. Configure `percentiles-histogram: true` and you get P50, P95, P99 latency values in your monitoring dashboard. `gen_ai.client.token.usage` tracks token consumption.

Every metric is tagged automatically with the provider, operation type, and model name. You can filter: show me token usage for `gpt-4o` only, or compare latency between `gpt-4o` and `gpt-4o-mini`. With Prometheus export and Grafana, you build dashboards showing: AI call latency over time, total tokens consumed per hour broken out by model, error rate, circuit breaker state.

With token counts and the provider's pricing page, you can calculate cost directly in your monitoring. Tokens in times input price plus tokens out times output price equals cost per request. Aggregate over the day. This is how you avoid surprise API bills.

Logging. During development, `SimpleLoggerAdvisor` is indispensable. Add it to your `ChatClient` builder. Set `org.springframework.ai` to `DEBUG` level in your YAML. Now every prompt your application sends and every response it receives is logged with the full message content. When the AI is doing something unexpected, you look at the log and you see exactly what it saw.

For production, turn off `SimpleLoggerAdvisor` or set the log level to `WARN` — full prompt/response logging is far too verbose for production traffic and will also log any sensitive user data that appears in prompts.

Instead, write a custom advisor — implement `CallAroundAdvisor`. In `aroundCall()`, capture the user's message (first 100 characters is usually enough for an audit), call `chain.nextAroundCall(request)` to execute the AI call, measure the duration, extract token usage from the response metadata, and write a structured log line. The log line should be machine-parsable: pipe-separated key-value pairs, or JSON. This log goes to your log aggregation system — ELK, Splunk, CloudWatch Logs — where you can search it, set alerts, and analyze patterns.

The audit log serves multiple purposes: you can answer "how many AI requests did we serve today?", "what is the distribution of token usage?", "did any prompt hit the context length limit?", and "which user's session generated the most tokens?" — all from log queries.

---

**[55:00–60:00] Slide 16 — Full Day Summary and Day 35 Preview**

Two hours of Spring AI. Let me give you the complete picture of what you can now build.

At the start of today, you knew how to call an AI API. By now, you can build a complete AI-powered feature in a Spring Boot application. You know the full stack: `ChatClient` with advisors for cross-cutting concerns. Function calling so the AI can query your database and call your services. Structured output so you get Java objects back, not string blobs. RAG so the AI can answer questions about your documents — read them, split them, embed them, store them, retrieve them with `QuestionAnswerAdvisor`. Configuration that is environment-aware, with API keys secured and model options tuned per use case. Error handling that retries transient failures and falls back gracefully on persistent ones. And monitoring through Micrometer metrics and structured audit logging.

The architecture of a production Spring AI feature looks like this. A REST controller delegates to a `@Service`. The service holds a `ChatClient` built with a system prompt, a memory advisor, a RAG advisor, and an audit logging advisor. The `ChatClient` is backed by a `ChatModel` auto-configured for your chosen provider. Alongside it runs an ingestion pipeline — scheduled or triggered — that reads new documents, splits them, embeds them, and stores them in `PgVectorStore`. Resilience4j wraps the service to open the circuit and call a fallback when the provider is down. Micrometer collects metrics for your Grafana dashboard.

That's a production AI feature. All of it within familiar Spring Boot idioms.

Tomorrow, Day 35, we shift completely — NoSQL and MongoDB. We'll look at why document databases exist alongside relational ones, how MongoDB's document model works, BSON, collections, CRUD in the Mongo shell, and Spring Data MongoDB for integrating it in a Spring Boot application. The mental model shift from rows/columns/joins to documents/collections/embedded arrays is significant, but once it clicks it opens up a new set of design options. See you then.
