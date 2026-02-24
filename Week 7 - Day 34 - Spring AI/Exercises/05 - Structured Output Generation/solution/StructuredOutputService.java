package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StructuredOutputService {

    public BookSummary getBookSummary(ChatModel chatModel, String bookTitle) {
        BeanOutputConverter<BookSummary> converter = new BeanOutputConverter<>(BookSummary.class);
        String formatInstructions = converter.getFormat();

        PromptTemplate template = new PromptTemplate(
                "Provide a structured summary for the book '{title}'. {format}");
        Prompt prompt = template.create(Map.of("title", bookTitle, "format", formatInstructions));

        String content = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        return converter.convert(content);
    }
}
