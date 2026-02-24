package com.springai;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.List;

public class KnowledgeBase {

    public SimpleVectorStore build(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = new SimpleVectorStore(embeddingModel);

        store.add(List.of(
            new Document("Spring AI supports AI providers including OpenAI, Ollama, Azure OpenAI, and Anthropic."),
            new Document("Spring AI supports Retrieval-Augmented Generation (RAG) using SimpleVectorStore and EmbeddingModel."),
            new Document("Spring AI function calling allows language models to invoke registered Java functions at runtime."),
            new Document("Spring AI PromptTemplate renders named variables into prompt strings using the {variable} syntax."),
            new Document("Spring AI ChatClient provides a fluent builder API wrapping ChatModel with support for advisors and memory.")
        ));

        return store;
    }
}
