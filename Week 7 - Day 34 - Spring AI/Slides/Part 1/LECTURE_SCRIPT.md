# Day 34 Part 1 — Spring AI: Overview, ChatModel, ChatClient, PromptTemplate, and Chatbots
## Lecture Script

---

**[00:00–03:00] Opening — From AI Concepts to AI Code**

Good morning. Welcome to Day 34. Yesterday on Day 33 we covered AI and developer productivity from a conceptual and tooling perspective — how language models work, transformers, tokenization, embeddings as a concept, prompt engineering principles, RAG pipelines as an idea, and how tools like GitHub Copilot and MCP integration fit into your workflow.

Today and tomorrow we're going to go a level deeper. Today is Spring AI — how you actually integrate AI capabilities into a Spring Boot backend application. Not just using AI tools while you code, but building software systems that have AI as a functional component. Your Spring Boot service calling a language model. Your backend doing semantic search. Your application streaming AI responses to a frontend chat interface.

This is a skill that's moving fast and employers are actively looking for. AI integration is not a niche specialty anymore — it's becoming expected, just like knowing how to write a REST endpoint or configure a database. By the end of today you'll know how to set it up, how to use the API, how to build a chatbot with memory, and how to stream responses. Part 2 tomorrow adds function calling, structured output, RAG implementation with a real vector store, error handling, and monitoring.

Let's get started.

---

**[03:00–07:30] Slide 2 — Spring AI Overview and Architecture**

First: what is Spring AI, and why does it exist?

Every major AI provider — OpenAI, Anthropic, Google, Amazon, Azure — has their own SDK with their own Java library and their own completely different API. The OpenAI SDK has a method called `createCompletion` on a client object, takes a builder with all their specific parameters, returns their proprietary response type. The Anthropic SDK has completely different method names, different builder patterns, different response objects. Azure OpenAI needs a deployment name instead of a model name. Ollama — which runs models locally — uses a REST API directly.

If you write your application directly against one of these SDKs, you're locked in. You want to switch from OpenAI to Anthropic because the pricing changed? Rewrite all your AI code. You want to use OpenAI in production but Ollama locally for development so developers don't rack up API costs? You'd need two separate code paths.

Spring AI solves this with an abstraction layer. You write your application against Spring AI's interfaces — `ChatModel`, `EmbeddingModel`, `VectorStore` — and Spring AI handles the translation to whichever provider's API you've configured. Same Java code. Different starter dependency and different YAML configuration.

The architecture is four layers. At the top: your application code, which only ever references Spring AI interfaces. Below that: the Spring AI abstraction layer with those interfaces. Below that: provider-specific auto-configurations that implement those interfaces for each vendor. At the bottom: the actual provider SDKs and HTTP calls.

The design philosophy is pure Spring: dependency injection, auto-configuration, familiar idioms. If you know Spring Boot, you already know how to set this up. You add a starter, you configure a YAML property, you inject a `ChatModel` — it just works.

---

**[07:30–12:00] Slide 3 — Getting Started — Dependencies and BOM**

Let me show you exactly how to add Spring AI to a Spring Boot project.

Spring AI uses a BOM — a Bill of Materials — to manage consistent versions across its many modules. You'll have one BOM import in your `dependencyManagement` section, and then you add individual starters without specifying version numbers. The BOM guarantees that everything is compatible.

The BOM declaration goes in `dependencyManagement` inside your `pom.xml`. You set the `groupId` to `org.springframework.ai`, `artifactId` to `spring-ai-bom`, and as of today the version is `1.0.0`. Type `pom` and scope `import` — this is the standard Maven BOM pattern, same as how you'd import Spring Boot's own BOM.

Then in your regular `dependencies` section, you add the starter for whichever AI provider you want to use. For OpenAI: `spring-ai-openai-spring-boot-starter`. For Anthropic's Claude: `spring-ai-anthropic-spring-boot-starter`. For Ollama — which runs models locally on your machine: `spring-ai-ollama-spring-boot-starter`. No version numbers needed because the BOM manages them.

What does a starter give you? Everything auto-configured. You get a `ChatModel` bean wired up to that provider. You get a `ChatClient.Builder` bean ready to inject. You get an `EmbeddingModel` bean for most providers. You get provider-specific `ChatOptions` classes with full IDE autocomplete for all the parameters.

The minimum configuration to make it work with OpenAI — add this to your `application.yml`: `spring.ai.openai.api-key` set to an environment variable. Never hardcode an API key. Use `${OPENAI_API_KEY}` and set the actual key as an environment variable or in your `.env` file, which should be in `.gitignore`. Below that, `spring.ai.openai.chat.options.model: gpt-4o`. That's it. Two YAML properties and Spring Boot wires up everything.

One more thing: if you're using Spring Initializr at `start.spring.io`, Spring AI is now listed directly in the dependencies section. Just search "Spring AI" and select the provider you want. It adds the BOM and starter for you automatically.

---

**[12:00–18:00] Slides 4–5 — ChatModel Interface and Message Types**

Now let's look at the actual API. Start with `ChatModel` — the core low-level interface.

`ChatModel` has one main method: `call` that takes a `Prompt` and returns a `ChatResponse`. It also has a convenience overload that takes a plain `String` and wraps it in a `UserMessage` for you.

Simplest possible usage. You inject `ChatModel` via constructor injection. You call `chatModel.call(question)` — that's a convenience overload that takes a string, wraps it in a `UserMessage`, calls the API, and returns the response text. One line. For quick Q&A this is perfectly fine.

When you need more control, you build a `Prompt` explicitly. `new Prompt(new UserMessage(question))` and call `chatModel.call(prompt)`. This returns a `ChatResponse` object. From there you navigate the tree: `response.getResult()` gives you the `Generation` — the first choice returned by the model. `generation.getOutput()` gives you an `AssistantMessage`. `assistantMessage.getContent()` gives you the text string you want.

The `ChatResponse` also contains metadata. `response.getMetadata().getUsage()` gives you a `Usage` object with `promptTokens` — how many tokens were in your request, `generationTokens` — how many the model generated in response, and `totalTokens` — the sum. This is important for cost tracking. OpenAI charges per token. A thousand tokens is roughly 750 words.

Now, message types. A `Prompt` wraps a list of `Message` objects. There are three main types: `SystemMessage`, `UserMessage`, and `AssistantMessage`.

`SystemMessage` is the system prompt — it sets the AI's role, persona, constraints, and behavior. It goes at the beginning of the conversation. Think of it as the instructions you give the AI before any user interaction starts.

`UserMessage` is the human's input — the actual question or request.

`AssistantMessage` is an AI response that you inject back into the conversation — used when you're manually managing conversation history.

To build a multi-message prompt, you pass a `List` of these messages to the `Prompt` constructor. `List.of(systemMessage, userMessage)`. The model sees all of them in order.

I want to emphasize one thing about conversation history that a lot of people get wrong. Language models are completely stateless. They have no memory between API calls. Every call starts fresh. If you want the model to remember something from a previous turn, you must include that previous turn in the current request. You do this by accumulating messages in a list — add the user message, get the response, add the response as an `AssistantMessage`, then on the next turn add the new user message, and pass the whole list again. Spring AI's `MessageChatMemoryAdvisor` automates this — we'll see it in Slide 13.

---

**[18:00–23:30] Slide 6 — EmbeddingModel Interface**

`EmbeddingModel` is the second core interface. Where `ChatModel` generates text responses, `EmbeddingModel` generates vector embeddings.

Day 33 covered what embeddings are conceptually — a vector is a list of numbers that represents the semantic meaning of a piece of text. Two texts that mean similar things produce vectors that are close together in mathematical space. This is the foundation of semantic search: instead of keyword matching, you're matching meaning.

The Spring AI `EmbeddingModel` interface has two key methods. `embed(String text)` takes a single text, returns a `float[]` — an array of floating point numbers. For `text-embedding-3-small` from OpenAI, that's 1536 numbers. For `nomic-embed-text` from Ollama, it's 768 numbers.

`embedForResponse(List<String> texts)` takes multiple texts, returns an `EmbeddingResponse` containing a list of `Embedding` objects. Call `getOutput()` on each to get its `float[]`. You want to use this batch version when you're embedding multiple things — one API call instead of N.

What do you use embeddings for in practice? A few things. Semantic search — embed a user's search query and all your documents, find the closest documents. RAG — embed documents and store them in a vector database during ingestion, then at query time embed the user's question and retrieve the closest documents. Duplicate detection — similar embedding score means likely duplicate content. Recommendations — books or articles with similar embeddings are similar in content.

For configuration, the embedding model is separate from the chat model. With OpenAI, set `spring.ai.openai.embedding.options.model` to `text-embedding-3-small` — which is 1536 dimensions, fast, cost-effective. There's also `text-embedding-3-large` at 3072 dimensions for higher accuracy. One critical constraint: the dimension count you configure here must exactly match the dimension count your vector store is configured for. We'll see this in Part 2.

---

**[23:30–31:00] Slides 7–8 — ChatClient: The High-Level Fluent API**

`ChatClient` is the preferred API for most application code. Think of the relationship between `ChatModel` and `ChatClient` like the relationship between JDBC and Spring Data JPA. `ChatModel` is the low-level workhorse — it works, it's explicit, you have full control. `ChatClient` is the higher-level, more idiomatic, more readable interface built on top of it.

You get a `ChatClient.Builder` bean auto-configured by Spring AI. You inject it and either inject the fully-built `ChatClient` directly via a `@Bean` method, or build it inside your service constructor.

The typical setup: create a `@Configuration` class with a `@Bean` method. Inject `ChatClient.Builder`. Call `.defaultSystem("...")` to set the system prompt that applies to all calls through this client. Call `.build()`. Now inject this `ChatClient` wherever you need it.

The call syntax reads like a sentence. `chatClient.prompt()` — start a new prompt. `.user(question)` — set the user message. `.call()` — execute, blocking. `.content()` — extract the response text as a `String`. That's it. `chatClient.prompt().user(question).call().content()`.

You can override the system prompt for a specific call: `.system("You are an expert in X.")`. You can pass a full message list with `.messages(List<Message>)` for when you've built the messages yourself. And instead of `.content()` at the end, you can call `.chatResponse()` to get the full `ChatResponse` object with metadata and token counts.

Now, advisors. This is where `ChatClient` really shines. Advisors are Spring AI's composable interceptors. They wrap the AI call with additional behavior — before and after — without your service code knowing anything about it.

You add advisors on the builder with `.defaultAdvisors(...)`. `SimpleLoggerAdvisor` logs every request and response at DEBUG level — one line of code and you have complete visibility into what your application sends and receives from AI APIs. Invaluable for debugging.

`MessageChatMemoryAdvisor` handles conversation history automatically. `QuestionAnswerAdvisor` handles RAG — it retrieves relevant documents from a vector store and injects them into the prompt. We'll cover both in depth shortly.

You can also add per-request options with `.options(...)`. If you have a `ChatClient` configured with `gpt-4o` as the default, but for one specific call you want to use `gpt-4o-mini` because it's cheaper and the task is simple — you pass an `OpenAiChatOptions` object on that specific call. This overrides the default for that call only. Every other call still uses `gpt-4o`.

And streaming. Instead of `.call().content()`, you use `.stream().content()`. This returns a `Flux<String>` — a reactive stream that emits text chunks as the model generates them. Each emission is a small piece of text — a few tokens. The `Flux` completes when the model finishes generating.

To expose this to a frontend, create a Spring MVC `@GetMapping` that produces `MediaType.TEXT_EVENT_STREAM_VALUE` and returns the `Flux<String>` directly. Spring Boot and WebFlux know how to write a `Flux<String>` as a Server-Sent Events stream. The browser or JavaScript client connects with `EventSource`, receives tokens one at a time, and appends them to the UI. This is exactly how ChatGPT's interface works — the text appears progressively as it's generated.

---

**[31:00–37:00] Slides 9–10 — PromptTemplate and Model Integrations**

`PromptTemplate` handles dynamic prompts — prompts where you need to substitute runtime values into a template. The alternative is Java string concatenation, and I want to explain why that's a problem.

Imagine you're building a code review feature. The prompt is "Review this code for bugs: [user's code here]." If you use string concatenation, you do: `"Review this code for bugs: " + userCode`. This works, but it has problems. What if the user's code contains curly braces, or special characters that mess up the string? What if you need to prompt-engineer your way to a better review by tweaking the template? You'd change Java source code. And it's the AI equivalent of building SQL queries with string concatenation — it can let user input influence the prompt structure in unintended ways.

`PromptTemplate` fixes this. You define the template string with `{variable}` placeholders, then call `template.create(Map.of("variable", value))` to produce a `Prompt`. The values are substituted safely. The template structure is preserved regardless of what the values contain.

For short templates, define them inline in Java. For longer, complex prompts — a multi-paragraph code review prompt, a detailed system prompt for a specialized assistant — put them in classpath resource files with a `.st` extension, which stands for StringTemplate. Put these files in `src/main/resources/prompts/`. Inject them with `@Value("classpath:prompts/code-review.st") Resource codeReviewPrompt`. Construct `new PromptTemplate(codeReviewPrompt)`. Now your prompts are separate files — non-developers can edit them, you can version-control them independently, you can A/B test different prompt variants.

Model integrations. Spring AI supports every major provider. OpenAI for GPT-4o and GPT-4o-mini. Azure OpenAI for enterprise deployments of the same models via Microsoft's infrastructure. Anthropic for the Claude family — Claude 3.5 Sonnet is particularly strong for coding tasks. Ollama for running models locally on your machine — Llama 3, Mistral, CodeLlama, completely free and private. Google Vertex AI for Gemini. Amazon Bedrock for hosted Claude, Llama, and Titan models via AWS.

The configuration pattern is identical for all of them. Different starter artifact, different YAML key prefix. OpenAI uses `spring.ai.openai`. Anthropic uses `spring.ai.anthropic`. Ollama uses `spring.ai.ollama`. All have the same structure: `api-key` (except Ollama which has `base-url`), `chat.options.model`, and so on.

Ollama is worth highlighting because of its value in development. You install it with one command on Mac, pull a model like Llama 3.2, and you have a local AI API running at `localhost:11434`. Your Spring Boot app connects to it exactly like a remote API, but nothing leaves your machine. No API costs. No data sent to external servers. Great for development and testing. You configure `spring.ai.ollama.base-url: http://localhost:11434` and you're done.

---

**[37:00–44:00] Slides 11–12 — Building AI Services and Chatbot Design**

Let me talk about how to structure AI features in your Spring Boot application.

The pattern is: a focused `@Service` class per AI feature, wrapping a `ChatClient`. The service encapsulates the system prompt, the prompt templates, any advisors, and the ChatOptions specific to that feature. Controllers inject the service and call plain methods. The AI implementation details don't leak out.

Here's a concrete example: a `BookReviewService`. Its constructor takes `ChatClient.Builder` and builds its own `ChatClient` with a system prompt tailored to book analysis. It has methods like `summarize(bookTitle, authorName)` and `recommendSimilar(bookTitle)`. Each method calls `chatClient.prompt().user(message).call().content()`. That's a clean Java API for your feature — callers don't know or care that it's powered by an AI model.

I mentioned earlier that Spring AI doesn't have an `@AiService` annotation. Some other Java AI frameworks — specifically LangChain4j — do have this annotation. LangChain4j lets you define an interface, annotate it with `@AiService`, and it generates an implementation that routes method calls to an AI model, similar to how Spring Data generates repository implementations from interfaces. It's an elegant pattern.

Spring AI's answer to this is the `ChatClient` builder. It's more explicit — you write the service class yourself — but it's also more debuggable and more flexible. When something goes wrong with a Spring AI service, you can step through the code. With a generated implementation, it's more opaque. Both approaches are valid. We're teaching the Spring AI way.

For chatbot endpoint design, there are two things to solve: the conversation turns and the session identification. Each HTTP request to a chatbot is stateless — the server needs to know which conversation a message belongs to. Use a `sessionId` field in your request DTO. Generate it client-side with `crypto.randomUUID()` in JavaScript, or have the server generate and return it on the first request.

Your `ChatRequest` record has a `sessionId` and a `message`. Your `ChatResponse` record echoes back the `sessionId` and contains the AI's `response`. The controller is thin — it deserializes the request, calls the service, returns the response. All the AI logic is in the service.

---

**[44:00–51:00] Slides 13–14 — Conversation Memory and Streaming**

Conversation memory — how to make your chatbot actually remember what was said.

`MessageChatMemoryAdvisor` is a built-in Spring AI advisor that handles the full memory cycle automatically. Before each request, it looks up the conversation history for a given `conversationId`, prepends those messages to the current request, then after getting the response, it saves the new user message and AI response back to storage.

The storage is a `ChatMemory` implementation. Spring AI provides `InMemoryChatMemory` — it stores all conversation history in the JVM heap. Perfect for development. In production you'd implement `ChatMemory` backed by a database table or Redis so that history survives restarts and works across multiple server instances.

The wiring is clean. You create a `ChatMemory` bean. You build your `ChatClient` with `defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))`. At call time, you pass the `conversationId` through the advisor param: `.advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))`. That's the key the advisor uses to look up and store history.

Think through what happens on the second message in a conversation. The advisor loads the history — `[SystemMessage, UserMessage("My name is Alice"), AssistantMessage("Nice to meet you, Alice!")]`. It prepends this to the current request. The prompt becomes the full history plus the new user message. The model sees the complete context and can refer back to what was said. The advisor then saves the new exchange. This is how statelessness becomes stateful from the user's perspective — you're re-sending the entire context on each call.

Streaming. When you have a chatbot or any AI feature that generates long responses, streaming makes the UX dramatically better. Instead of the user staring at a blank screen for eight seconds and then seeing the full answer appear all at once, they see text flowing in character by character — same latency, but perceived as much faster.

The API change is minimal. Replace `.call()` with `.stream()` and you get a `Flux<String>` instead of a `String`. Each emission from the `Flux` is a few tokens of text.

The REST endpoint uses `produces = MediaType.TEXT_EVENT_STREAM_VALUE` and returns the `Flux<String>` directly. Spring Boot uses WebFlux's SSE support to write each emission as an SSE event over the HTTP connection. The connection stays open until the `Flux` completes.

On the browser side, create an `EventSource` with the endpoint URL. Every SSE event received by `onmessage` is a chunk of text — append it to your output element. When the model finishes, the stream closes and `onerror` fires, which you use to close the `EventSource`. Total JavaScript: five lines.

---

**[51:00–57:00] Slide 15 — ChatOptions**

`ChatOptions` is how you control the model's behavior per request. Let me go through the key parameters you'll actually use.

`model` — which model to call. You might set `gpt-4o` as the default but override to `gpt-4o-mini` for tasks that don't need the full model. `gpt-4o-mini` is about 15x cheaper per token than `gpt-4o` for many tasks that don't need deep reasoning.

`temperature` — this is the most important one. It controls the randomness of the model's output. At 0.0, the model always picks the highest-probability next token. The output is deterministic and focused. At 1.0, it samples more broadly, producing more varied and creative output. For code generation, data extraction, factual Q&A, use 0.0 to 0.3. For chat, explanations, summaries, use 0.4 to 0.7. For creative writing, brainstorming, use 0.8 to 1.2.

`maxTokens` — the maximum number of tokens the model will generate in its response. This is a hard limit. If the model hits this limit mid-sentence, the response is cut off. Use it to control costs and response length. For a quick classification, set 100. For a detailed code review, set 2000.

`topP` — an alternative to temperature called nucleus sampling. Rather than amplifying or dampening all probabilities uniformly like temperature does, `topP` restricts the model to the smallest set of tokens whose cumulative probability exceeds P. Generally don't adjust both temperature and topP simultaneously — pick one.

`frequencyPenalty` and `presencePenalty` — these discourage repetition. If the model is echoing the same phrases or not covering new ground, increasing these can help.

There are two levels of configuration. You set defaults on the `ChatClient.Builder` with `.defaultOptions(...)` — these apply to every call through this client. You override per-call with `.options(...)` on the prompt chain — this only affects that one call.

The YAML approach also works for global defaults: `spring.ai.openai.chat.options.temperature: 0.7`. But per-request overrides in code give you the most flexibility — you can use different temperatures for different feature types within the same service.

---

**[57:00–60:00] Slide 16 — Summary and Part 2 Preview**

Part 1 is done. Let me give you the mental model for what we covered.

Spring AI is a portable abstraction layer. You code against `ChatModel` and `ChatClient`. You configure a provider via starter and YAML. Swap the provider, change the YAML. Everything else stays the same.

`ChatModel` is the low-level interface for direct control. `ChatClient` is the high-level fluent API that reads like a conversation and integrates with the advisor system. Use `ChatClient` for application code. `PromptTemplate` keeps your prompts maintainable and safe. Message types — `SystemMessage`, `UserMessage`, `AssistantMessage` — map directly to the AI roles. `EmbeddingModel` generates vectors for semantic use cases.

Build AI features as focused `@Service` classes. One service per feature. Each service owns its own `ChatClient`, system prompt, and ChatOptions. `MessageChatMemoryAdvisor` handles conversation history so your code doesn't have to. Streaming with `Flux` and SSE makes chat UIs responsive.

Part 2 is in an hour and it's where the really interesting engineering happens. Function calling — you'll write Java methods that the AI can decide to invoke, giving it the ability to look things up and take actions in your system. Structured output — you'll parse AI responses directly into Java records with zero manual JSON handling. RAG — you'll wire up a document store so the AI can answer questions about your own data. Then error handling, fallback strategies, monitoring with Micrometer, and logging. See you back here in an hour.
