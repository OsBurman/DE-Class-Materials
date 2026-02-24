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
        // TODO 3: Call service.getBookSummary(chatModel, "The Great Gatsby")
        //         and assert the result is not null
    }

    @Test
    void getBookSummary_titleIsNotBlank() {
        // TODO 4: Call service.getBookSummary(chatModel, "1984")
        //         and assert that summary.title() is not blank
    }

    @Test
    void getBookSummary_authorIsNotBlank() {
        // TODO 5: Call service.getBookSummary(chatModel, "Brave New World")
        //         and assert that summary.author() is not blank
    }

    @Test
    void getBookSummary_genreIsNotBlank() {
        // TODO 6: Call service.getBookSummary(chatModel, "Dune")
        //         and assert that summary.genre() is not blank
    }
}
