# Exercise 01: Spring AI Architecture and ChatModel Overview

## Objective
Set up a Spring Boot project with Spring AI, wire a mocked `ChatModel` bean, and call it programmatically to understand the core request/response flow.

## Background
Spring AI is a Spring-ecosystem framework that provides a unified abstraction over many AI model providers (OpenAI, Azure OpenAI, Anthropic, Ollama, etc.). The central interface is `ChatModel`, which accepts a `Prompt` (containing one or more `Message` objects) and returns a `ChatResponse`. In this exercise you will use Spring AI's built-in mock infrastructure so no real API key is required.

## Requirements

1. In `pom.xml`, add the Spring AI BOM and the `spring-ai-test` starter dependency so `MockAiTestConfiguration` is available.

2. In `SpringAiOverviewTest.java`, annotate the test class with `@SpringBootTest` and import `MockAiTestConfiguration` to provide a mocked `ChatModel`.

3. Inject the `ChatModel` bean and write a test `basicChatCall_returnsResponse` that:
   - Creates a `UserMessage` with the text `"What is Spring AI?"`
   - Wraps it in a `Prompt`
   - Calls `chatModel.call(prompt)`
   - Asserts that the returned `ChatResponse` is not null
   - Asserts that `response.getResult().getOutput().getContent()` is not blank

4. Write a second test `chatModel_supportsMultipleMessages` that builds a `Prompt` from a `List` of two messages:
   - A `SystemMessage`: `"You are a helpful Java tutor."`
   - A `UserMessage`: `"Explain dependency injection in one sentence."`
   - Calls `chatModel.call(prompt)` and asserts the response content is not blank.

5. In `ArchitectureExplainer.java`, implement `printArchitecture()` which prints a plain-text overview of Spring AI's architecture covering: ChatModel, EmbeddingModel, PromptTemplate, VectorStore, and supported model providers. Call it from `main`.

## Hints
- `MockAiTestConfiguration` is available in `spring-ai-test`; it registers a mock `ChatModel` that returns a canned response — no API key needed.
- `new Prompt(new UserMessage("..."))` is the simplest way to build a single-message prompt.
- `ChatResponse.getResult()` returns an `AssistantMessage` generation — chain `.getOutput().getContent()` to get the string.
- For the multi-message constructor use `new Prompt(List.of(systemMsg, userMsg))`.

## Expected Output

Tests pass. `ArchitectureExplainer.main()` prints:
```
=== Spring AI Architecture Overview ===

Layer 1: Model Abstraction
  ChatModel      - Send messages, receive text/structured responses
  EmbeddingModel - Convert text to numeric vectors for similarity search

Layer 2: Prompt Building
  PromptTemplate - Build dynamic prompts with {variable} placeholders
  Message types  - SystemMessage, UserMessage, AssistantMessage

Layer 3: Data & Retrieval
  VectorStore    - Store and search document embeddings (RAG)
  DocumentReader - Load documents from files, URLs, PDFs

Layer 4: Model Providers
  OpenAI, Azure OpenAI, Anthropic, Ollama, Mistral, and more
  All implement ChatModel / EmbeddingModel interfaces

Layer 5: Spring Integration
  Auto-configuration via application.properties
  @AiService for interface-driven AI services
```
