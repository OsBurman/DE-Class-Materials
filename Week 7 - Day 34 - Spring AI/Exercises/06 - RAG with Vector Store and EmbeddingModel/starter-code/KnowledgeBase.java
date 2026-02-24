package com.springai;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.List;

/**
 * Builds and populates an in-memory vector store with Spring AI knowledge base documents.
 */
public class KnowledgeBase {

    /**
     * Creates a SimpleVectorStore, loads it with knowledge base documents, and returns it.
     *
     * @param embeddingModel the embedding model used to convert text to vectors
     * @return a populated SimpleVectorStore ready for similarity search
     */
    public SimpleVectorStore build(EmbeddingModel embeddingModel) {

        // TODO 1: Create a SimpleVectorStore:
        //         SimpleVectorStore store = new SimpleVectorStore(embeddingModel);
        //
        //         Then add at least THREE Document objects with Spring AI facts, e.g.:
        //           new Document("Spring AI supports providers including OpenAI, Ollama, and Azure OpenAI.")
        //           new Document("Spring AI supports Retrieval-Augmented Generation using SimpleVectorStore.")
        //           new Document("Spring AI function calling allows models to invoke registered Java functions.")
        //
        //         Use: store.add(List.of( doc1, doc2, doc3 ));

        // TODO 2: Return the populated store
        return null; // replace with your implementation
    }
}
