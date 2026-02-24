package com.springai;

public class ArchitectureExplainer {

    public static void main(String[] args) {
        printArchitecture();
    }

    // Prints a human-readable overview of Spring AI's layered architecture
    static void printArchitecture() {
        System.out.println("=== Spring AI Architecture Overview ===");
        System.out.println();

        // Layer 1 — the two core model interfaces that all providers implement
        System.out.println("Layer 1: Model Abstraction");
        System.out.println("  ChatModel      - Send messages, receive text/structured responses");
        System.out.println("  EmbeddingModel - Convert text to numeric vectors for similarity search");
        System.out.println();

        // Layer 2 — how you build the input to a model
        System.out.println("Layer 2: Prompt Building");
        System.out.println("  PromptTemplate - Build dynamic prompts with {variable} placeholders");
        System.out.println("  Message types  - SystemMessage, UserMessage, AssistantMessage");
        System.out.println();

        // Layer 3 — data ingestion and retrieval for RAG workflows
        System.out.println("Layer 3: Data & Retrieval");
        System.out.println("  VectorStore    - Store and search document embeddings (RAG)");
        System.out.println("  DocumentReader - Load documents from files, URLs, PDFs");
        System.out.println();

        // Layer 4 — concrete provider implementations
        System.out.println("Layer 4: Model Providers");
        System.out.println("  OpenAI, Azure OpenAI, Anthropic, Ollama, Mistral, and more");
        System.out.println("  All implement ChatModel / EmbeddingModel interfaces");
        System.out.println();

        // Layer 5 — how Spring AI plugs into the Spring Boot ecosystem
        System.out.println("Layer 5: Spring Integration");
        System.out.println("  Auto-configuration via application.properties");
        System.out.println("  @AiService for interface-driven AI services");
    }
}
