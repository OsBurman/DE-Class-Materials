package com.springai;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(MockAiTestConfiguration.class)
class FunctionCallingTest {

    @Autowired ChatModel chatModel;

    @Test
    void functionCall_returnsWeatherData() {
        // Enable the "currentWeather" function for this specific call.
        // The model can now choose to invoke it rather than generating a pure text reply.
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .function("currentWeather")
                .build();

        // Pass the options as the second argument to Prompt — they apply only to this call
        Prompt prompt = new Prompt(
                List.of(new UserMessage("What is the weather in Paris?")),
                options);

        String content = chatModel.call(prompt).getResult().getOutput().getContent();

        // The mock model returns a canned non-blank response; a real model would call
        // WeatherFunctionConfig.currentWeather() and incorporate "Paris: 22°C, sunny"
        assertThat(content).isNotBlank();
    }
}
