package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// TODO 4: Add @SpringBootTest and @Import(MockAiTestConfiguration.class)
class FunctionCallingTest {

    // TODO 4: Inject ChatModel
    ChatModel chatModel;

    @Test
    void functionCall_returnsWeatherData() {
        // TODO 4a: Build ChatOptions that enable the "currentWeather" function.
        //          Hint: OpenAiChatOptions.builder().function("currentWeather").build()
        //                or use the provider-agnostic ChatOptionsBuilder

        // TODO 4b: Build a Prompt with UserMessage "What is the weather in Paris?"
        //          and pass the options as the second argument to new Prompt(messages, options)

        // TODO 4c: Call chatModel.call(prompt) and assert the response content is not blank
    }
}
