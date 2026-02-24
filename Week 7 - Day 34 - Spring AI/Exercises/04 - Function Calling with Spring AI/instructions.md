# Exercise 04: Function Calling with Spring AI

## Objective
Register a Spring AI `FunctionCallback` bean and trigger it through a chat prompt so the model can call your Java code at runtime.

## Background
Function calling allows an AI model to invoke real application functions during a conversation. Instead of just generating text, the model can request a tool call (e.g., "get the current weather" or "look up a product price"), your code executes it, and the result is folded back into the response. Spring AI models this with `FunctionCallback` beans — you define the function's name, description, and input/output types, and Spring AI handles the protocol.

## Requirements

1. Create a `WeatherRequest` record with a single field `city: String`.
2. Create a `WeatherResponse` record with a single field `temperature: String`.
3. In `WeatherFunctionConfig.java`, define a `@Bean` of type `FunctionCallback` named `currentWeather` that:
   - Has name `"currentWeather"` and description `"Get the current weather for a given city"`
   - Accepts a `WeatherRequest` and returns a `WeatherResponse`
   - Simulates the response: return `new WeatherResponse(request.city() + ": 22°C, sunny")`
4. In `FunctionCallingTest.java`, write a `@SpringBootTest` test `functionCall_returnsWeatherData` that:
   - Builds a `ChatOptions` with the function name `"currentWeather"` enabled
   - Sends the user message `"What is the weather in Paris?"` using `chatModel.call(new Prompt(..., options))`
   - Asserts the response content is not blank

## Hints
- `FunctionCallback.builder().function("currentWeather", req -> ...).inputType(WeatherRequest.class).description("...").build()` is the builder pattern.
- To enable a function on a call, use `OpenAiChatOptions.builder().function("currentWeather").build()` or the provider-agnostic `ChatOptionsBuilder`.
- With `MockAiTestConfiguration`, the mock model will trigger the function and return a canned result — the test still passes.
- The `FunctionCallback` bean must be in the application context; declare it in a `@Configuration` class.

## Expected Output

Test passes. When called against a real model (e.g., OpenAI) the response would contain:
```
The current weather in Paris is 22°C and sunny.
```
With the mock model:
```
(non-blank mock response — exact text depends on mock configuration)
```
