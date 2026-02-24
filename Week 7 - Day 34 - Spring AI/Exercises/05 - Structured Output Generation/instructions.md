# Exercise 05 — Structured Output Generation

## Learning Objectives
- Use `BeanOutputConverter<T>` to coerce model responses into typed Java records
- Append format instructions to a prompt so the model returns valid JSON
- Parse the raw string response into a structured object using the converter

## Background

Spring AI's `BeanOutputConverter<T>` generates a JSON schema from a Java class (record or bean) and appends format instructions to your prompt, telling the model exactly what structure to return. You then call `converter.convert(content)` to deserialize the response into your target type.

**Key API**
```java
BeanOutputConverter<BookSummary> converter = new BeanOutputConverter<>(BookSummary.class);
String formatInstructions = converter.getFormat();   // JSON schema hint for the model

PromptTemplate template = new PromptTemplate("Summarize the book '{title}'. {format}");
Prompt prompt = template.create(Map.of("title", title, "format", formatInstructions));

ChatResponse response = chatModel.call(prompt);
String content = response.getResult().getOutput().getText();

BookSummary summary = converter.convert(content);   // deserialize → typed record
```

## Task

### Step 1 — Define the output record

Open `starter-code/BookSummary.java`.

**TODO 1:** Define a Java `record` named `BookSummary` in package `com.springai` with four fields:
- `String title`
- `String author`
- `String genre`
- `int yearPublished`

### Step 2 — Build the `StructuredOutputService`

Open `starter-code/StructuredOutputService.java`.

**TODO 2:** Complete the `getBookSummary(ChatModel chatModel, String bookTitle)` method:
1. Create a `BeanOutputConverter<BookSummary>` for `BookSummary.class`
2. Call `converter.getFormat()` to retrieve the JSON schema instructions
3. Create a `PromptTemplate` with the text:
   ```
   "Provide a structured summary for the book '{title}'. {format}"
   ```
4. Call `template.create(Map.of("title", bookTitle, "format", formatInstructions))` to render the prompt
5. Call `chatModel.call(prompt)` and extract the content string
6. Return `converter.convert(content)` to deserialize into a `BookSummary`

### Step 3 — Write the tests

Open `starter-code/StructuredOutputTest.java`.

**TODO 3:** Assert that the returned `BookSummary` object is not null.

**TODO 4:** Assert that `summary.title()` is not blank.

**TODO 5:** Assert that `summary.author()` is not blank.

**TODO 6:** Assert that `summary.genre()` is not blank.

## Expected Outcome

- `BookSummary` is a compact, well-typed record
- `StructuredOutputService.getBookSummary()` builds, calls, and converts the model response in ~6 lines
- All three test assertions pass against the mocked model

## Files

```
05 - Structured Output Generation/
├── instructions.md
├── starter-code/
│   ├── BookSummary.java
│   ├── StructuredOutputService.java
│   └── StructuredOutputTest.java
└── solution/
    ├── BookSummary.java
    ├── StructuredOutputService.java
    └── StructuredOutputTest.java
```
