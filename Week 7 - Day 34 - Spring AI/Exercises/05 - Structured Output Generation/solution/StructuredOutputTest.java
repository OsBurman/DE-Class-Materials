package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = StructuredOutputService.class)
@Import(MockAiTestConfiguration.class)
class StructuredOutputTest {

    @Autowired
    private ChatModel chatModel;

    private final StructuredOutputService service = new StructuredOutputService();

    @Test
    void getBookSummary_returnsNonNullObject() {
        BookSummary summary = service.getBookSummary(chatModel, "The Great Gatsby");
        assertThat(summary).isNotNull();
    }

    @Test
    void getBookSummary_titleIsNotBlank() {
        BookSummary summary = service.getBookSummary(chatModel, "1984");
        assertThat(summary.title()).isNotBlank();
    }

    @Test
    void getBookSummary_authorIsNotBlank() {
        BookSummary summary = service.getBookSummary(chatModel, "Brave New World");
        assertThat(summary.author()).isNotBlank();
    }

    @Test
    void getBookSummary_genreIsNotBlank() {
        BookSummary summary = service.getBookSummary(chatModel, "Dune");
        assertThat(summary.genre()).isNotBlank();
    }
}
