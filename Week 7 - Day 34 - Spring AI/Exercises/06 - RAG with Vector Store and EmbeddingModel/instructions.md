# Exercise 06 — RAG with Vector Store and EmbeddingModel

## Learning Objectives
- Understand Retrieval-Augmented Generation (RAG) and why it improves LLM accuracy
- Store documents in a `SimpleVectorStore` using an `EmbeddingModel`
- Perform semantic similarity search to retrieve relevant context
- Prepend retrieved context to a user question before calling the model

## Background

**What is RAG?**
RAG (Retrieval-Augmented Generation) grounds the model's response in real data by:
1. **Indexing** — converting your documents into vector embeddings and storing them
2. **Retrieval** — converting the user's question into a vector and finding the most similar documents
3. **Generation** — prepending the retrieved documents to the prompt so the model answers from them

**Spring AI RAG building blocks**
| Class | Role |
|---|---|
| `Document` | Wraps text content (and optional metadata) |
| `EmbeddingModel` | Converts text to a float vector |
| `SimpleVectorStore` | In-memory vector store; supports `add()` and `similaritySearch()` |
| `ChatModel` | Answers the final augmented prompt |

**Key API**
```java
// 1 — Build the store
SimpleVectorStore store = new SimpleVectorStore(embeddingModel);
store.add(List.of(new Document("Spring AI supports OpenAI and Ollama.")));

// 2 — Retrieve relevant docs
List<Document> relevant = store.similaritySearch("What models does Spring AI support?");

// 3 — Augment and call
String context = relevant.stream().map(Document::getText).collect(Collectors.joining("\n"));
String augmentedPrompt = "Context:\n" + context + "\n\nQuestion: " + userQuestion;
ChatResponse response = chatModel.call(new Prompt(new UserMessage(augmentedPrompt)));
```

## Task

### Step 1 — Populate the knowledge base

Open `starter-code/KnowledgeBase.java`.

**TODO 1:** In the `build(EmbeddingModel embeddingModel)` method, create a `SimpleVectorStore` and add **at least three** `Document` objects covering different Spring AI facts (e.g., supported providers, RAG capabilities, function calling).

**TODO 2:** Return the populated store.

### Step 2 — Build the RAG service

Open `starter-code/RagService.java`.

**TODO 3:** In `answer(ChatModel chatModel, SimpleVectorStore store, String question)`:
1. Call `store.similaritySearch(question)` to retrieve relevant documents
2. Concatenate their text with `\n` as separator
3. Build an augmented prompt string: `"Context:\n" + context + "\n\nQuestion: " + question`
4. Call `chatModel.call(new Prompt(new UserMessage(augmentedPrompt)))`
5. Return the content string from the response

### Step 3 — Write the tests

Open `starter-code/RagTest.java`.

**TODO 4:** Assert that `answer(...)` returns a non-null, non-blank string for a question about Spring AI.

**TODO 5:** Assert that calling `similaritySearch("RAG")` on the populated store returns at least one document.

## Expected Outcome

- `KnowledgeBase.build()` returns a `SimpleVectorStore` containing at least 3 documents
- `RagService.answer()` retrieves context and passes an augmented prompt to the model
- Both tests pass against the mocked model and embedding model

## Files

```
06 - RAG with Vector Store and EmbeddingModel/
├── instructions.md
├── starter-code/
│   ├── KnowledgeBase.java
│   ├── RagService.java
│   └── RagTest.java
└── solution/
    ├── KnowledgeBase.java
    ├── RagService.java
    └── RagTest.java
```
