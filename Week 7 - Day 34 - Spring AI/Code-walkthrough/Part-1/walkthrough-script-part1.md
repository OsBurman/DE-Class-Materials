# Day 34 — Spring AI: Part 1
## Instructor Walkthrough Script

**Duration:** ~90 minutes  
**Format:** Live code walkthrough + slides  
**Files referenced:**
- `01-spring-ai-overview.md`
- `02-chatmodel-and-prompts.java`

---

## SECTION 1 — Opening & Context Setting (5 min)

**Say:**
> "Good morning. Today we're covering Spring AI — the Spring ecosystem's way of integrating Large Language Models into Java applications. This is one of the most exciting topics in the program because it's where everything you've learned — Spring Boot, dependency injection, REST APIs — meets the AI wave that's reshaping software development."

> "Think about what you'd need to do WITHOUT Spring AI. You'd call the OpenAI REST API directly, parse JSON responses, manage prompts as raw strings, handle retries manually. Spring AI wraps all of that in the same patterns you already know: interfaces, auto-configuration, dependency injection."

> "Quick question: what are some real features you'd want to add to our bookstore app using AI? …[Take responses — expect: book recommendations, summaries, search, chatbot, review generation]. All of those are possible with what we're covering today."

---

## SECTION 2 — Spring AI Overview & Architecture (15 min)

**Open `01-spring-ai-overview.md`**

**Say:**
> "Let's start with the big picture. Look at the architecture diagram."

**Point to the top layer:**
> "Your application code lives at the top. It talks to Spring AI's core interfaces — `ChatModel`, `EmbeddingModel`, `VectorStore`. These are just Java interfaces, like `JpaRepository` or `UserDetailsService`. Your code depends on the interface, not the implementation."

**Point to the provider implementations:**
> "The implementations are what actually call OpenAI, or Azure OpenAI, or Anthropic's Claude, or a local Ollama instance. You switch providers by changing ONE Maven dependency and ONE property. Your business code stays identical."

> "This is the same portability story Spring Data gives you for databases — switch from MySQL to PostgreSQL by changing a dependency. Spring AI gives you the same portability for AI models."

**Point to the interfaces section:**

> "Let's look at the three core interfaces. `ChatModel` is what you'll use most — it sends messages and gets text back. `call(String)` is the simple overload. `call(Prompt)` gives you full control. `stream(Prompt)` returns a reactive `Flux` for token-by-token streaming."

> "`EmbeddingModel` converts text to vectors — numbers that represent the semantic meaning of the text. We use this for search and RAG, which we'll cover in Part 2."

> "`VectorStore` is the database for those vectors. It stores documents with their embeddings and lets you do similarity searches: 'find me documents semantically similar to this query.'"

**Point to the configuration section:**
> "Switching providers is literally one line in pom.xml and two lines in application.properties. See the table — the dependency for OpenAI vs Anthropic vs Ollama. Ollama is particularly interesting — it runs models LOCALLY on your machine. No API key, no cost, no data leaving your network. Great for development."

> "⚠️ API keys — say this out loud to yourself: NEVER hardcode an API key in source code. Always use environment variables. An exposed API key in a GitHub repo can cost thousands of dollars before you even notice. Always: `${OPENAI_API_KEY}`."

**Point to @AiService:**
> "The last concept in this file is `@AiService` — the declarative pattern. Like Spring Data's `@Repository` where you define an interface and Spring generates the SQL, `@AiService` lets you define an interface with annotations and Spring generates the AI calls. We'll see code examples in the Java file."

---

## SECTION 3 — Basic ChatModel Usage (15 min)

**Open `02-chatmodel-and-prompts.java` — Section 1 (application.properties)**

**Say:**
> "Notice the `application.properties` at the top. This is all Spring AI needs to know — your API key (from an environment variable), which model to use, temperature, and max tokens. Temperature controls creativity: 0 = deterministic, 1 = very creative. For factual answers, use low temperature like 0.2. For creative writing, use 0.7-0.9."

**Scroll to Section 2 (BasicChatService)**

**Say:**
> "Now the first class: `BasicChatService`. Notice the constructor: `ChatModel chatModel`. Spring injects `OpenAiChatModel` here because that's the dependency you added to pom.xml. If you switch to Anthropic, Spring injects `AnthropicChatModel` instead. Your code doesn't change."

**Point to `askSimpleQuestion`:**
> "The simplest call: `chatModel.call(question)`. One line. It wraps your string in a `UserMessage`, sends it to the model, and returns the response text. No setup, no boilerplate."

**Point to `askWithSystemContext`:**
> "But real applications need more nuance. This is where `SystemMessage` comes in. A system message sets the AI's persona and constraints BEFORE the user's message. It's like briefing an employee before they take a call: 'You are BookBot, you only discuss books, you're friendly.'"

> "Notice the `Prompt` constructor takes a `List<Message>`. Order matters: system first, then user. The AI treats system messages as higher priority than user messages — it shapes HOW the AI responds."

> "After calling, we get a `ChatResponse`. `getResult().getOutput().getContent()` extracts the text. The response is rich — it also contains token usage stats, finish reason, and metadata."

**Point to `askWithCustomOptions`:**
> "For one-off requests where you need different settings — a lower temperature for a factual query, a higher max-tokens limit for a long summary — you can pass `OpenAiChatOptions` directly to the `Prompt`. This overrides the defaults from `application.properties` for just that one call."

---

## SECTION 4 — PromptTemplate (12 min)

**Scroll to Section 3 (PromptTemplateService)**

**Say:**
> "Hardcoding prompts inside Java strings is messy and unmanageable. `PromptTemplate` is Spring AI's solution — the same concept as `JdbcTemplate` for SQL, but for AI prompts."

**Point to the template string:**
> "The template uses `{genre}` and `{level}` as placeholders — same syntax as Spring's `@Value` expressions. The template text is clean and readable. The business logic of filling it in is separate."

**Point to `template.create(Map.of(...))`:**
> "You call `create()` with a Map of placeholder values and get a fully filled `Prompt` object. Clean separation of concerns."

> "The comment about loading from a file is important. For production systems, put your prompt templates in `src/main/resources/prompts/`. Non-developers — product managers, UX writers — can then edit and tune prompts without touching Java code. That's a big deal in real companies."

**Point to `SystemPromptTemplate`:**
> "`SystemPromptTemplate` is the same idea but creates a system message. Here we have two templates: one for the system persona and one for the user question. We build both, combine them in a `Prompt`, and call the model."

**Ask the class:**
> "Why would you want your prompts in files rather than hardcoded strings? …[Take answers: editability without recompile, A/B testing prompts, non-devs can tune them, version control tracks prompt changes separately from code changes]."

---

## SECTION 5 — Chatbot & Conversation History (15 min)

**Scroll to Section 4 (BookstoreChatbotService)**

**Say:**
> "Now the most exciting thing to show in a demo: a real chatbot. The key insight is that LLMs are stateless — they have no memory of previous requests. You create the illusion of memory by including the conversation history in every message."

**Point to `conversationHistory` map:**
> "We store conversations in a Map: session ID maps to a list of messages. Each session is one user's ongoing conversation."

**Point to the `chat()` method:**
> "Every time the user sends a message, we: add their message to history, build a new `Prompt` with system + ALL previous messages + new message, call the model, store the AI's reply in history, return the reply."

**Whiteboard the message structure:**
```
Prompt for turn 3:
[SystemMessage: "You are BookBot..."]
[UserMessage: "What books do you recommend?"]       ← turn 1
[AssistantMessage: "I'd suggest Clean Code..."]    ← turn 1 reply
[UserMessage: "Tell me more about that first one"] ← turn 2
[AssistantMessage: "Clean Code by Robert C. Martin..."] ← turn 2 reply
[UserMessage: "Who is the author?"]                 ← turn 3 (current)
```

> "The AI sees the full history and can answer 'Who is the author?' correctly because it can see that turn 1 and 2 were about Clean Code."

**Point to the watch-out comment:**
> "Here's the trap: conversation history GROWS. If a session has 100 turns, you're sending 100 messages with every new call. Eventually you hit the model's context window limit — the maximum number of tokens it can process at once. Solution: keep only the last N turns. Common production practice is last 10-20 turns."

**Scroll to Section 5 (StreamingChatService)**

> "For long responses like book summaries, streaming is crucial for UX. Nobody wants to stare at a blank screen for 10 seconds. `chatModel.stream()` returns a `Flux<ChatResponse>`. We map each partial response to its text content. The controller returns this as `text/event-stream` — Server-Sent Events — and the browser shows tokens as they arrive."

---

## SECTION 6 — ChatClient Fluent API (8 min)

**Scroll to Section 8 (ChatClientService)**

**Say:**
> "Spring AI 1.0 introduced `ChatClient` — a fluent builder API that's now the preferred way to interact with models. Think of it as the `RestClient` or `WebClient` of AI calls."

**Point to the builder:**
> "You get a `ChatClient.Builder` injected, configure defaults — like a default system message — and build the client once. Now every call from this service has that system message automatically."

**Point to the fluent call:**
> "`chatClient.prompt().user(question).call().content()` — reads like English. Prompt, set user message, call the model, get the content. No `Prompt` object, no `List<Message>`, no `getResult().getOutput().getContent()` chain."

---

## SECTION 7 — REST Controller & Wrap-Up (10 min)

**Scroll to Section 9 (REST controller)**

**Say:**
> "The controller wires everything to HTTP. Notice the streaming endpoint: `produces = MediaType.TEXT_EVENT_STREAM_VALUE`. This tells Spring to send the response as Server-Sent Events — the browser receives tokens one by one as the Flux emits them."

> "The chatbot endpoint uses `X-Session-Id` as a header to identify the user's conversation. You'd typically use a JWT claim or a cookie for this in production."

**Recap:**
> "Part 1 covered the full breadth of Spring AI's client-side capabilities:"
> - "Spring AI architecture: portable interfaces over vendor SDKs"
> - "`ChatModel` — basic call, system messages, per-request options"
> - "`PromptTemplate` — parameterized prompts, file-based templates"
> - "Conversation history — the engine behind chatbots"
> - "Streaming with `stream()` and SSE"
> - "`EmbeddingModel` — vectors for semantic search"
> - "`ChatClient` — fluent API"

---

### 🎯 Quick-Check Questions

1. **"Why inject `ChatModel` instead of `OpenAiChatModel` directly?"**
   > *Portability — swap providers without changing application code.*

2. **"What does `temperature` control?"**
   > *Creativity/randomness of the output. 0 = deterministic, 1 = very creative.*

3. **"Why does a chatbot need to send conversation history with every request?"**
   > *LLMs are stateless — they have no memory between calls. History must be passed explicitly.*

4. **"What's the risk of storing unlimited conversation history?"**
   > *Token limit exceeded. Keep only the last N turns.*

5. **"What's the advantage of storing prompts in resource files instead of Java strings?"**
   > *Non-developers can edit them, no recompile needed, changes tracked separately.*

---

*End of Day 34 — Part 1 Script. Break before Part 2.*
