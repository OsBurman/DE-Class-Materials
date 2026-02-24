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
        // TODO: Call knowledgeBase.build(embeddingModel) to initialise the store
        // store = knowledgeBase.build(embeddingModel);
    }

    @Test
    void answer_returnsNonBlankResponse() {
        // TODO 4: Call ragService.answer(chatModel, store, "What providers does Spring AI support?")
        //         and assert the result is not blank
    }

    @Test
    void similaritySearch_returnsRelevantDocuments() {
        // TODO 5: Call store.similaritySearch("RAG")
        //         and assert the returned list has at least one document
    }
}
