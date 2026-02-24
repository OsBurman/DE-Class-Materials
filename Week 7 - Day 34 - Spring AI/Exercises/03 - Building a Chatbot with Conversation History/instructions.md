# Exercise 03: Building a Chatbot with Conversation History

## Objective
Build a Spring Boot REST endpoint that maintains multi-turn conversation history using Spring AI's `MessageWindowChatMemory` so the chatbot remembers prior messages.

## Background
A single-turn chatbot forgets everything between requests. To build a real conversational assistant you need to store prior messages and send them with every new prompt. Spring AI provides `MessageWindowChatMemory`, which keeps the last N messages in memory per conversation ID, and the `ChatClient` fluent API to wire it all together.

## Requirements

1. In `ChatbotController.java`, create a `@RestController` with a `POST /chat` endpoint that accepts a JSON body `{ "conversationId": "...", "message": "..." }`.

2. In the controller constructor, build a `ChatClient` using `ChatClient.builder(chatModel)` and attach a `MessageWindowChatMemory` advisor:
   ```
   .defaultAdvisors(new MessageChatMemoryAdvisor(memory))
   ```
   where `memory = MessageWindowChatMemory.builder().maxMessages(10).build()`.

3. The `/chat` handler must:
   - Call the `ChatClient` fluent API, passing the user message and the conversation ID as the chat memory advisor's conversation key
   - Return the assistant's reply as a plain `String`

4. In `ChatbotControllerTest.java`, write two `@SpringBootTest(webEnvironment = RANDOM_PORT)` tests using `TestRestTemplate`:
   - `firstMessage_returnsReply` — sends one message and asserts the response body is not blank
   - `secondMessage_returnsReply` — sends two sequential messages with the same `conversationId` and asserts both responses are not blank (proving the endpoint handles multiple turns)

5. The conversation ID must be supplied by the caller — two requests with different IDs must not share history.

## Hints
- `MessageWindowChatMemory.builder().maxMessages(10).build()` creates an in-memory window that retains the last 10 messages.
- `ChatClient.builder(chatModel).defaultAdvisors(...).build()` creates a reusable `ChatClient` bean.
- The fluent call: `.prompt().user(message).advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)).call().content()` returns the reply string.
- `CHAT_MEMORY_CONVERSATION_ID_KEY` is a constant in `MessageChatMemoryAdvisor`.

## Expected Output

`POST /chat` with `{"conversationId":"s1","message":"Hi, my name is Bob."}`:
```json
"Hello Bob! How can I help you today?"
```

`POST /chat` with `{"conversationId":"s1","message":"What is my name?"}`:
```json
"Your name is Bob."
```

`POST /chat` with `{"conversationId":"s2","message":"What is my name?"}` (different ID):
```json
"I don't know your name yet. Could you tell me?"
```
