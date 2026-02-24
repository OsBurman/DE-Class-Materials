# Exercise 02: PromptTemplate for Dynamic Prompts

## Objective
Use Spring AI's `PromptTemplate` to build reusable, parameterised prompts and render them with different variable values.

## Background
Hardcoding prompt text in application code makes it brittle and hard to maintain. `PromptTemplate` lets you define a template string with `{variable}` placeholders and render it at runtime by passing a `Map<String, Object>` of values. This is essential for building AI features where the prompt changes based on user input, database data, or application state.

## Requirements

1. In `PromptTemplateService.java`, implement three methods:

   a. `buildGreetingPrompt(String name, String topic)` — creates a `PromptTemplate` from the string `"You are a helpful assistant. Greet {name} and explain {topic} in two sentences."`, renders it using a map of `{name, topic}`, and returns the rendered `Prompt`.

   b. `buildCodeReviewPrompt(String language, String code)` — creates a template from `"Review the following {language} code and list any issues:\n\n{code}"`, renders it, and returns the `Prompt`.

   c. `buildSummaryPrompt(String text, int maxWords)` — creates a template from `"Summarise the following text in no more than {maxWords} words:\n\n{text}"`, renders it, and returns the `Prompt`.

2. In `PromptTemplateTest.java`, write three `@SpringBootTest` tests (using `MockAiTestConfiguration`) that call each method, pass the resulting `Prompt` to `chatModel.call(prompt)`, and assert that the response content is not blank.

3. Additionally, in each test, assert on the rendered prompt text **before** passing to the model:
   - For greeting: assert the rendered text contains both the name and the topic
   - For code review: assert the rendered text contains the language name
   - For summary: assert the rendered text contains the `maxWords` value as a string

## Hints
- `PromptTemplate.create("...", Map.of("key", value))` is the factory method that both creates and renders in one step; or use `new PromptTemplate("...").create(Map.of(...))`.
- `Prompt.getContents()` returns the rendered prompt string for assertion.
- Variable names in the template must exactly match the keys in the map — case-sensitive.
- `PromptTemplate` is in the `org.springframework.ai.chat.prompt` package.

## Expected Output

All three tests pass. Rendered prompts contain the substituted values:

```
Greeting prompt rendered:
"You are a helpful assistant. Greet Alice and explain recursion in two sentences."

Code review prompt rendered:
"Review the following Java code and list any issues:\n\npublic int add(int a, int b) { return a + b; }"

Summary prompt rendered:
"Summarise the following text in no more than 50 words:\n\nSpring AI provides..."
```
