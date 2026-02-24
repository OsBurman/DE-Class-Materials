package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StructuredOutputService {

    /**
     * Asks the model for a structured summary of the given book title.
     *
     * @param chatModel  the Spring AI ChatModel to call
     * @param bookTitle  the name of the book to summarise
     * @return a populated BookSummary record
     */
    public BookSummary getBookSummary(ChatModel chatModel, String bookTitle) {

        // TODO 2a: Create a BeanOutputConverter for BookSummary.class
        //          BeanOutputConverter<BookSummary> converter = new BeanOutputConverter<>(BookSummary.class);

        // TODO 2b: Retrieve the JSON schema format instructions
        //          String formatInstructions = converter.getFormat();

        // TODO 2c: Create a PromptTemplate:
        //          "Provide a structured summary for the book '{title}'. {format}"

        // TODO 2d: Render the prompt with the book title and format instructions
        //          Prompt prompt = template.create(Map.of("title", bookTitle, "format", formatInstructions));

        // TODO 2e: Call chatModel.call(prompt) and extract the content string

        // TODO 2f: Return converter.convert(content) to deserialize into a BookSummary

        return null; // replace with your implementation
    }
}
