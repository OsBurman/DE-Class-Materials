package com.springai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.test.MockAiTestConfiguration;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {KnowledgeBase.class, RagService.class})
@Import(MockAiTestConfiguration.class)
class RagTest {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    private final KnowledgeBase knowledgeBase = new KnowledgeBase();
    private final RagService ragService = new RagService();
    private SimpleVectorStore store;

    @BeforeEach
    void setUp() {
        store = knowledgeBase.build(embeddingModel);
    }

    @Test
    void answer_returnsNonBlankResponse() {
        String response = ragService.answer(chatModel, store, "What providers does Spring AI support?");
        assertThat(response).isNotBlank();
    }

    @Test
    void similaritySearch_returnsRelevantDocuments() {
        List<Document> results = store.similaritySearch("RAG");
        assertThat(results).isNotEmpty();
    }
}
