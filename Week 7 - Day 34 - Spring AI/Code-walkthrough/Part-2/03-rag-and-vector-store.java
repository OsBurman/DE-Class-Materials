// =============================================================================
// Day 34 — Spring AI Part 2: RAG & Vector Store
// Bookstore Application
//
// Topics covered:
//   1. What RAG is and why it matters (reduces hallucinations)
//   2. ETL Pipeline: loading and chunking documents
//   3. VectorStore — storing and searching document embeddings
//   4. SimpleVectorStore for development (in-memory)
//   5. PgVectorStore for production (PostgreSQL + pgvector)
//   6. QuestionAnswerAdvisor — automatic RAG with ChatClient
//   7. Manual RAG flow: embed → search → augment → respond
// =============================================================================

package com.bookstore.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// =============================================================================
// SECTION 1: The Problem — LLMs Only Know What They Were Trained On
// =============================================================================
//
// The Challenge:
//   • GPT-4 was trained on public internet data up to a cutoff date
//   • It knows nothing about YOUR bookstore's catalog, YOUR customer data,
//     YOUR internal documents, or anything added after its training cutoff
//
//   User asks: "Do you have any books about Spring AI in stock?"
//   LLM without RAG: Makes something up (HALLUCINATION) or says "I don't know"
//
// RAG — Retrieval Augmented Generation solves this:
//   1. RETRIEVAL: Search your own documents for relevant context
//   2. AUGMENTATION: Inject that context into the prompt
//   3. GENERATION: The LLM answers using REAL data, not training memory
//
// RAG Flow:
//   User Question
//       ↓
//   Embed question into a vector (numbers representing meaning)
//       ↓
//   Search VectorStore for nearest matching document chunks
//       ↓
//   Add retrieved chunks to the prompt: "Based on this context: ..."
//       ↓
//   LLM generates a grounded, accurate answer

// =============================================================================
// SECTION 2: The ETL Pipeline — Loading Documents into the VectorStore
// =============================================================================
//
// Before answering queries, you need to LOAD your documents.
//
// ETL = Extract → Transform → Load
//
//   Extract:   Read raw documents (text files, PDFs, web pages)
//   Transform: Split into chunks + add metadata
//   Load:      Store embeddings in VectorStore

@Service
class BookstoreDocumentIngestionService {

    private final VectorStore vectorStore;

    BookstoreDocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    // ── 2a: Load from plain text (simplest case) ─────────────────────────────
    public void ingestBookCatalog(@Value("classpath:books-catalog.txt") Resource catalogFile) {
        // TextReader loads a file as a list of Document objects
        TextReader reader = new TextReader(catalogFile);
        reader.getCustomMetadata().put("source", "catalog");   // optional metadata
        reader.getCustomMetadata().put("type", "book-catalog");

        List<Document> documents = reader.get();   // read the full document

        // TokenTextSplitter breaks documents into smaller chunks
        // LLMs have context limits — 500–1000 token chunks work well for RAG
        TokenTextSplitter splitter = new TokenTextSplitter(
                500,    // default chunk size in tokens
                100,    // minimum chunk size
                5,      // minimum number of chars per chunk
                10_000, // maximum number of chars to process
                true    // keep separator
        );

        List<Document> chunks = splitter.apply(documents);  // chunk the documents

        System.out.println("Ingesting " + chunks.size() + " document chunks...");

        // VectorStore.add() embeds each chunk (calls EmbeddingModel internally)
        // and stores the vector + original text + metadata
        vectorStore.add(chunks);

        System.out.println("Ingestion complete.");
    }

    // ── 2b: Load programmatically created documents ──────────────────────────
    // You can also create Document objects directly (no file needed)
    public void ingestBookDescriptions() {
        List<Document> bookDocs = List.of(
                new Document(
                        "Clean Code by Robert C. Martin. ISBN: 978-0132350884. " +
                        "Genre: Programming. A handbook of agile software craftsmanship. " +
                        "Teaches how to write readable, maintainable code. " +
                        "Topics: naming, functions, comments, formatting, objects.",
                        Map.of("isbn", "978-0132350884", "genre", "programming", "author", "Robert C. Martin")
                ),
                new Document(
                        "The Pragmatic Programmer by David Thomas and Andrew Hunt. " +
                        "ISBN: 978-0135957059. Genre: Programming. " +
                        "Covers career development, coding philosophy, and best practices. " +
                        "Topics: DRY principle, orthogonality, tracer bullets, prototypes.",
                        Map.of("isbn", "978-0135957059", "genre", "programming", "author", "David Thomas")
                ),
                new Document(
                        "Designing Data-Intensive Applications by Martin Kleppmann. " +
                        "ISBN: 978-1449373320. Genre: Data Engineering. " +
                        "Deep dive into databases, streams, and distributed systems. " +
                        "Topics: replication, partitioning, transactions, batch/stream processing.",
                        Map.of("isbn", "978-1449373320", "genre", "data-engineering", "author", "Martin Kleppmann")
                )
        );

        vectorStore.add(bookDocs);
        System.out.println("Loaded " + bookDocs.size() + " book documents.");
    }
}

// =============================================================================
// SECTION 3: VectorStore Configuration
// =============================================================================

@Configuration
class VectorStoreConfig {

    // ── 3a: SimpleVectorStore — in-memory, great for development ─────────────
    // No external database needed. Data is lost on restart.
    // Perfect for demos, testing, or small datasets.
    @Bean
    public VectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    // ── 3b: PgVectorStore — PostgreSQL with pgvector extension ───────────────
    // Production-grade vector store. Persists data. Supports millions of vectors.
    //
    // Prerequisites:
    //   1. PostgreSQL database with pgvector extension installed:
    //        CREATE EXTENSION vector;
    //   2. Spring dependency: spring-ai-pgvector-store-spring-boot-starter
    //   3. application.properties:
    //        spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
    //        spring.datasource.username=postgres
    //        spring.datasource.password=secret
    //        spring.ai.vectorstore.pgvector.dimensions=1536
    //        spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
    //        spring.ai.vectorstore.pgvector.initialize-schema=true
    //
    // When configured, Spring Boot auto-creates the PgVectorStore bean.
    // You only need to inject VectorStore — Spring picks the right implementation.
    //
    // @Bean  ← comment out SimpleVectorStore above and use this in prod
    // public VectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
    //     return new PgVectorStore(jdbcTemplate, embeddingModel);
    // }
}

// =============================================================================
// SECTION 4: Manual RAG Flow
// =============================================================================

@Service
class ManualRagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    ManualRagService(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    // ── 4a: Full manual RAG pipeline ─────────────────────────────────────────
    public String answerWithContext(String userQuestion) {
        // Step 1: RETRIEVAL — search for relevant document chunks
        // SearchRequest.query() creates a similarity search
        // topK(4) returns the 4 most relevant chunks
        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.query(userQuestion)
                        .withTopK(4)                    // return top 4 matches
                        .withSimilarityThreshold(0.7)   // minimum similarity score (0-1)
        );

        if (relevantDocs.isEmpty()) {
            return "I don't have specific information about that in our catalog.";
        }

        // Step 2: AUGMENTATION — format retrieved docs into context string
        String context = relevantDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n---\n\n"));

        // Step 3: GENERATION — inject context into prompt
        PromptTemplate template = new PromptTemplate("""
                You are a helpful bookstore assistant.
                Use only the information in the context below to answer the question.
                If the answer is not in the context, say "I don't have that information."
                
                Context:
                {context}
                
                Question: {question}
                
                Answer:
                """);

        var prompt = template.create(Map.of(
                "context", context,
                "question", userQuestion
        ));

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    // ── 4b: RAG with metadata filtering ──────────────────────────────────────
    // Filter the vector search to only look in specific categories
    public String answerFromGenre(String question, String genre) {
        // FilterExpression allows SQL-like filtering on document metadata
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.query(question)
                        .withTopK(3)
                        .withFilterExpression("genre == '" + genre + "'")
        );

        String context = docs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        return chatModel.call("""
                Based only on this context about %s books:
                %s
                
                Answer: %s
                """.formatted(genre, context, question))
                .getResult().getOutput().getContent();
    }
}

// =============================================================================
// SECTION 5: Automatic RAG with QuestionAnswerAdvisor
// =============================================================================
//
// Spring AI includes advisors — components that automatically modify prompts.
// QuestionAnswerAdvisor does the retrieval + augmentation step for you.
// You just call ChatClient normally; the advisor handles the RAG pipeline.

@Service
class AutoRagService {

    private final ChatClient chatClient;

    AutoRagService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultAdvisors(
                        // ← Automatically retrieves context for every question
                        new QuestionAnswerAdvisor(
                                vectorStore,
                                SearchRequest.defaults()
                                        .withTopK(4)
                                        .withSimilarityThreshold(0.6)
                        )
                )
                .build();
    }

    // This looks like a normal ChatClient call —
    // QuestionAnswerAdvisor silently does the vector search and augments the prompt
    public String askBookstoreAssistant(String question) {
        return chatClient.prompt()
                .system("You are a knowledgeable bookstore assistant. Answer based on our catalog.")
                .user(question)
                .call()
                .content();
    }

    // Override advisor per-request if needed
    public String askWithCustomSearch(String question, int topK) {
        return chatClient.prompt()
                .user(question)
                .advisors(advisor -> advisor.param(
                        QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == 'book-catalog'")
                )
                .call()
                .content();
    }
}

// =============================================================================
// SECTION 6: REST Controller
// =============================================================================

@RestController
@RequestMapping("/api/ai/rag")
class RagController {

    private final ManualRagService manualRagService;
    private final AutoRagService autoRagService;
    private final BookstoreDocumentIngestionService ingestionService;

    RagController(ManualRagService manual, AutoRagService auto,
                  BookstoreDocumentIngestionService ingestion) {
        this.manualRagService = manual;
        this.autoRagService = auto;
        this.ingestionService = ingestion;
    }

    // POST /api/ai/rag/ingest — load book data into the vector store
    @PostMapping("/ingest")
    public String ingest() {
        ingestionService.ingestBookDescriptions();
        return "Documents ingested successfully.";
    }

    // GET /api/ai/rag/ask?question=What books do you have about Java?
    @GetMapping("/ask")
    public String ask(@RequestParam String question) {
        return manualRagService.answerWithContext(question);
    }

    // GET /api/ai/rag/ask-auto?question=Do you have sci-fi books?
    @GetMapping("/ask-auto")
    public String askAuto(@RequestParam String question) {
        return autoRagService.askBookstoreAssistant(question);
    }

    // GET /api/ai/rag/ask-genre?question=...&genre=programming
    @GetMapping("/ask-genre")
    public String askByGenre(@RequestParam String question, @RequestParam String genre) {
        return manualRagService.answerFromGenre(question, genre);
    }
}
