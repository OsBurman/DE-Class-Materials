# Exercise 07 — Error Handling, Fallback Strategies, and Logging

## Learning Objectives
- Handle exceptions thrown by `ChatModel.call()` with a graceful fallback response
- Log prompt content, response content, and token usage using SLF4J
- Read token usage from `ChatResponse.getMetadata().getUsage()`
- Write a test that verifies the fallback path is triggered on error

## Background

In production, calls to an AI provider can fail due to network timeouts, rate limits, or invalid API keys. A resilient service should:
1. **Catch** the exception so the application does not crash
2. **Log** the error with enough context to debug it later
3. **Return a fallback response** so the user sees a sensible message

Spring AI's `ChatResponse` exposes token usage through its metadata:
```java
Usage usage = response.getMetadata().getUsage();
long promptTokens    = usage.getPromptTokens();
long completionTokens = usage.getGenerationTokens();
```

**SLF4J logging pattern**
```java
private static final Logger log = LoggerFactory.getLogger(ResilientChatService.class);

log.info("Prompt tokens used: {}", promptTokens);
log.warn("Model call failed: {}", e.getMessage());
```

## Task

### Step 1 — Build the resilient service

Open `starter-code/ResilientChatService.java`.

**TODO 1:** Inject `ChatModel` via constructor injection (field already provided).

**TODO 2:** In `chat(String userMessage)`, wrap the model call in a try/catch block.
- In the `try` block:
  1. Build a `Prompt` from the `userMessage`
  2. Call `chatModel.call(prompt)` and capture the `ChatResponse`
  3. Log the prompt token count at `INFO` level: `"Prompt tokens: {}"`
  4. Log the completion token count at `INFO` level: `"Completion tokens: {}"`
  5. Return the content string
- In the `catch (Exception e)` block:
  1. Log a `WARN`-level message: `"ChatModel call failed, using fallback: {}"`  with `e.getMessage()`
  2. Return the constant `FALLBACK_RESPONSE`

**TODO 3:** The `FALLBACK_RESPONSE` constant is already declared. Leave its value as-is or customise it.

### Step 2 — Write the tests

Open `starter-code/ResilientChatServiceTest.java`.

**TODO 4:** Write a test `chat_happyPath_returnsModelResponse()` that calls `chat("Hello")` and asserts the result is not blank.

**TODO 5:** Write a test `chat_whenModelThrows_returnsFallback()` that:
1. Creates a mock `ChatModel` that throws `RuntimeException("boom")` on any call
2. Creates a `ResilientChatService` with the throwing mock
3. Calls `service.chat("Hello")`
4. Asserts the result equals `ResilientChatService.FALLBACK_RESPONSE`

## Expected Outcome

- Happy-path call returns model content and logs token counts
- When the model throws, the fallback string is returned and a WARN log is emitted
- All tests pass

## Files

```
07 - Error Handling, Fallback Strategies, and Logging/
├── instructions.md
├── starter-code/
│   ├── ResilientChatService.java
│   └── ResilientChatServiceTest.java
└── solution/
    ├── ResilientChatService.java
    └── ResilientChatServiceTest.java
```
