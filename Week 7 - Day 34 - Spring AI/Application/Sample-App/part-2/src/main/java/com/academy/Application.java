package com.academy;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

// =============================================================
// DemoModeChecker
// =============================================================

@Component
class DemoModeChecker {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public boolean isDemoMode() {
        return "DEMO_MODE".equals(apiKey) || apiKey == null || !apiKey.startsWith("sk-");
    }

    public String getDemoNote() {
        return "DEMO MODE: Set OPENAI_API_KEY env var for live AI. export OPENAI_API_KEY=sk-xxx && mvn spring-boot:run";
    }
}

// =============================================================
// KnowledgeDocument
// =============================================================

@Data
@AllArgsConstructor
class KnowledgeDocument {
    private String id;
    private String title;
    private String content;
    private String category;
}

// =============================================================
// KnowledgeBase
// =============================================================

@Component
class KnowledgeBase {

    private List<KnowledgeDocument> documents;

    @PostConstruct
    public void init() {
        documents = new ArrayList<>();
        documents.add(new KnowledgeDocument(
                "kb-1", "Course Registration Policy",
                "Students may register for up to 6 courses per semester. Registration opens 2 weeks before each semester. " +
                "Students with GPA below 2.0 need advisor approval. Late registration incurs a $50 fee.",
                "policy"));
        documents.add(new KnowledgeDocument(
                "kb-2", "Java Programming Course",
                "Java Programming (CS101) is a 3-credit introductory course. Prerequisites: none. Instructor: Prof. Smith. " +
                "Topics: variables, loops, OOP, collections, exception handling.",
                "course"));
        documents.add(new KnowledgeDocument(
                "kb-3", "Spring Boot Course",
                "Spring Boot (CS301) is a 3-credit advanced course. Prerequisites: CS101, CS201. " +
                "Topics: REST APIs, Spring Data JPA, Spring Security, Testing, Actuator.",
                "course"));
        documents.add(new KnowledgeDocument(
                "kb-4", "Financial Aid FAQ",
                "Financial aid applications are due March 1st. Merit scholarships require 3.5+ GPA. " +
                "Need-based aid requires FAFSA. Work-study programs are available for eligible students enrolled in 6+ credits.",
                "faq"));
        documents.add(new KnowledgeDocument(
                "kb-5", "Graduation Requirements",
                "Students need 120 credits to graduate. Minimum 2.0 GPA required. " +
                "Must complete 30 credits in major, 15 general education credits. Senior capstone project required in final semester.",
                "policy"));
        documents.add(new KnowledgeDocument(
                "kb-6", "Tuition and Fees",
                "Tuition is $500 per credit hour. Full-time enrollment is 12+ credits. " +
                "Payment plans available in 3 installments. A late payment fee of $50 applies after the due date. Textbooks are not included.",
                "faq"));
        documents.add(new KnowledgeDocument(
                "kb-7", "Database Design Course",
                "Database Design (CS201) is 3 credits. Topics: ER diagrams, normalization (1NF-3NF), SQL queries, NoSQL overview, " +
                "transactions, indexing. Prerequisites: CS101.",
                "course"));
        documents.add(new KnowledgeDocument(
                "kb-8", "Academic Integrity Policy",
                "All submitted work must be original. Plagiarism results in course failure and possible expulsion. " +
                "AI tools may be used for learning but assignment submissions must be the student's own work.",
                "policy"));
    }

    public List<KnowledgeDocument> search(String query) {
        String[] words = query.toLowerCase().split("\\s+");
        return documents.stream()
                .filter(doc -> {
                    String combined = (doc.getTitle() + " " + doc.getContent()).toLowerCase();
                    for (String word : words) {
                        if (word.length() > 3 && combined.contains(word)) return true;
                    }
                    return false;
                })
                .limit(3)
                .collect(Collectors.toList());
    }
}

// =============================================================
// RagController
// =============================================================

@RestController
@RequestMapping("/api/ai")
class RagController {

    private final ChatClient chatClient;
    private final DemoModeChecker demoModeChecker;
    private final KnowledgeBase knowledgeBase;

    public RagController(ChatClient chatClient, DemoModeChecker demoModeChecker, KnowledgeBase knowledgeBase) {
        this.chatClient = chatClient;
        this.demoModeChecker = demoModeChecker;
        this.knowledgeBase = knowledgeBase;
    }

    @GetMapping("/advisor")
    public Map<String, Object> advisor(@RequestParam String question) {

        // Step 1 - RETRIEVE
        List<KnowledgeDocument> retrieved = knowledgeBase.search(question);

        // Step 2 - AUGMENT
        String context = retrieved.stream()
                .map(d -> "## " + d.getTitle() + "\n" + d.getContent())
                .collect(joining("\n\n"));
        String augmentedPrompt =
                "You are a helpful academic advisor. Answer ONLY based on the following context. " +
                "If the answer is not in the context, say 'I don't have information about that'.\n\n" +
                "CONTEXT:\n" + context + "\n\nSTUDENT QUESTION: " + question;

        // Step 3 - GENERATE
        boolean isDemoMode = demoModeChecker.isDemoMode();
        String answer;
        String step3;
        if (isDemoMode) {
            answer = "[DEMO ANSWER] Based on the retrieved context, I would answer your question about: '" + question +
                    "'. The system found " + retrieved.size() + " relevant documents. Set OPENAI_API_KEY to see real AI answer.";
            step3 = "DEMO: Would call chatClient.call(new Prompt(augmentedPrompt))";
        } else {
            ChatResponse response = chatClient.call(new Prompt(augmentedPrompt));
            answer = response.getResults().get(0).getOutput().getContent();
            step3 = "Called OpenAI GPT";
        }

        // Build response
        Map<String, String> ragSteps = new LinkedHashMap<>();
        ragSteps.put("step1_retrieve", "Found " + retrieved.size() + " relevant documents from knowledge base");
        ragSteps.put("step2_augment", "Built context-enriched prompt (" + augmentedPrompt.length() + " chars)");
        ragSteps.put("step3_generate", step3);

        List<Map<String, String>> retrievedDocs = retrieved.stream()
                .map(d -> {
                    Map<String, String> m = new LinkedHashMap<>();
                    m.put("id", d.getId());
                    m.put("title", d.getTitle());
                    m.put("category", d.getCategory());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, String> ragExplanation = new LinkedHashMap<>();
        ragExplanation.put("whyRAG", "RAG prevents hallucination by grounding AI responses in verified data");
        ragExplanation.put("vsFineTuning", "RAG is cheaper and easier to update than fine-tuning - just update your knowledge base");
        ragExplanation.put("productionStack", "Production RAG: VectorStore (Qdrant/Pinecone) + EmbeddingModel for semantic search");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ragSteps", ragSteps);
        result.put("retrievedDocuments", retrievedDocs);
        result.put("augmentedPrompt", augmentedPrompt);
        result.put("answer", answer);
        result.put("mode", isDemoMode ? "DEMO" : "LIVE");
        result.put("ragExplanation", ragExplanation);

        return result;
    }
}

// =============================================================
// StudentInfo record
// =============================================================

record StudentInfo(String name, String major, double gpa, int credits) {}

// =============================================================
// CourseClassification record
// =============================================================

record CourseClassification(
        String subject,
        String level,
        int estimatedCredits,
        List<String> prerequisites,
        List<String> tags) {}

// =============================================================
// StructuredOutputController
// =============================================================

@RestController
@RequestMapping("/api/ai")
class StructuredOutputController {

    private final ChatClient chatClient;
    private final DemoModeChecker demoModeChecker;

    public StructuredOutputController(ChatClient chatClient, DemoModeChecker demoModeChecker) {
        this.chatClient = chatClient;
        this.demoModeChecker = demoModeChecker;
    }

    @PostMapping("/extract-student")
    public Map<String, Object> extractStudent(@RequestBody Map<String, String> body) {
        String desc = body.get("description");
        BeanOutputConverter<StudentInfo> converter = new BeanOutputConverter<>(StudentInfo.class);
        String format = converter.getFormat();
        String promptText =
                "Extract student information from this description and respond with valid JSON matching the required format.\n\n" +
                "Description: " + desc + "\n\n" + format;

        Map<String, Object> result = new LinkedHashMap<>();
        if (demoModeChecker.isDemoMode()) {
            result.put("mode", "DEMO");
            result.put("description", desc);
            result.put("promptSentToAI", promptText);
            result.put("jsonSchemaInstructions", format);
            result.put("demoResult", new StudentInfo("Alice Smith", "Computer Science", 3.8, 90));
            result.put("howItWorks",
                    "BeanOutputConverter generates JSON schema from StudentInfo record, appends to prompt, parses response JSON back to StudentInfo object");
            result.put("note", demoModeChecker.getDemoNote());
        } else {
            ChatResponse response = chatClient.call(new Prompt(promptText));
            String responseText = response.getResults().get(0).getOutput().getContent();
            StudentInfo studentInfo = converter.convert(responseText);
            result.put("mode", "LIVE");
            result.put("description", desc);
            result.put("result", studentInfo);
        }
        return result;
    }

    @GetMapping("/classify-course")
    public Map<String, Object> classifyCourse(@RequestParam String description) {
        BeanOutputConverter<CourseClassification> converter = new BeanOutputConverter<>(CourseClassification.class);
        String format = converter.getFormat();
        String promptText =
                "Classify this course and respond with valid JSON.\n\nCourse description: " + description + "\n\n" + format;

        Map<String, Object> result = new LinkedHashMap<>();
        if (demoModeChecker.isDemoMode()) {
            result.put("mode", "DEMO");
            result.put("description", description);
            result.put("promptSentToAI", promptText);
            result.put("demoResult", new CourseClassification(
                    "Computer Science",
                    "Advanced",
                    3,
                    List.of("Networking basics", "Java or similar OOP"),
                    List.of("microservices", "cloud", "distributed systems")));
            result.put("howItWorks",
                    "BeanOutputConverter generates JSON schema from CourseClassification record, appends to prompt, parses response JSON back to CourseClassification object");
            result.put("note", demoModeChecker.getDemoNote());
        } else {
            ChatResponse response = chatClient.call(new Prompt(promptText));
            String responseText = response.getResults().get(0).getOutput().getContent();
            CourseClassification classification = converter.convert(responseText);
            result.put("mode", "LIVE");
            result.put("description", description);
            result.put("result", classification);
        }
        return result;
    }
}

// =============================================================
// SpringAiAdvancedReferenceController
// =============================================================

@RestController
@RequestMapping("/api")
class SpringAiAdvancedReferenceController {

    @GetMapping("/spring-ai-advanced-reference")
    public Map<String, Object> reference() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", "Spring AI Advanced Reference");

        // RAG
        Map<String, Object> rag = new LinkedHashMap<>();
        rag.put("name", "Retrieval-Augmented Generation (RAG)");
        rag.put("pattern", List.of(
                "1. RETRIEVE: Search knowledge base for relevant documents",
                "2. AUGMENT: Build context-enriched prompt with retrieved documents",
                "3. GENERATE: Call LLM with augmented prompt"));
        rag.put("benefits", List.of(
                "Prevents hallucination",
                "Uses up-to-date information",
                "No model retraining needed",
                "Cost-effective"));
        Map<String, String> springAiComponents = new LinkedHashMap<>();
        springAiComponents.put("VectorStore", "Interface for storing and searching document embeddings");
        springAiComponents.put("SimpleVectorStore", "In-memory vector store for development/testing");
        springAiComponents.put("PgVectorStore", "PostgreSQL-based vector store for production");
        springAiComponents.put("EmbeddingClient", "Converts text to vector embeddings");
        springAiComponents.put("Document", "Represents a document with content and metadata");
        springAiComponents.put("DocumentRetriever", "Retrieves relevant documents from vector store");
        rag.put("springAiComponents", springAiComponents);
        result.put("rag", rag);

        // Structured Output
        Map<String, Object> structuredOutput = new LinkedHashMap<>();
        structuredOutput.put("name", "Structured Output");
        structuredOutput.put("description", "Extract structured data from AI responses using Java records or classes");
        structuredOutput.put("howItWorks",
                "BeanOutputConverter generates JSON schema, appends to prompt, parses AI response to Java object");
        structuredOutput.put("beanOutputConverterUsage",
                "new BeanOutputConverter<>(MyClass.class) -> getFormat() -> append to prompt -> convert(aiResponse)");
        structuredOutput.put("supportedFormats",
                List.of("Records", "POJOs with getters", "Classes with Jackson annotations"));
        result.put("structuredOutput", structuredOutput);

        // Function Calling
        Map<String, Object> functionCalling = new LinkedHashMap<>();
        functionCalling.put("name", "Function Calling");
        functionCalling.put("description", "Allow AI to call your Java methods to fetch real-time data");
        functionCalling.put("howItWorks", List.of(
                "1. Define @Bean Function<Input, Output>",
                "2. Register with FunctionCallingOptions",
                "3. AI decides when to call the function",
                "4. Result is injected back into conversation"));
        functionCalling.put("example",
                "@Bean public Function<WeatherRequest, WeatherResponse> weatherFunction() { return req -> weatherService.getWeather(req.city()); }");
        result.put("functionCalling", functionCalling);

        // Streaming
        Map<String, Object> streaming = new LinkedHashMap<>();
        streaming.put("description", "Stream AI responses token-by-token using Flux<ChatResponse>");
        streaming.put("usage",
                "chatClient.stream(new Prompt(text)).flatMap(r -> Mono.justOrEmpty(r.getResults().get(0).getOutput().getContent()))");
        result.put("streaming", streaming);

        // Choosing Approach
        Map<String, Object> choosingApproach = new LinkedHashMap<>();
        choosingApproach.put("whenToUseRAG",
                "Dynamic knowledge base, frequently updated data, domain-specific Q&A, reduce hallucination");
        choosingApproach.put("whenToUseFineTuning",
                "Specific writing style, consistent format output, domain jargon, when RAG latency is too high");
        choosingApproach.put("whenToUsePrompting",
                "Simple tasks, general knowledge, quick prototyping, low-volume use cases");
        result.put("choosingApproach", choosingApproach);

        return result;
    }
}

// =============================================================
// Application
// =============================================================

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner banner(DemoModeChecker demoModeChecker) {
        return args -> {
            String mode = demoModeChecker.isDemoMode()
                    ? "DEMO  (no API key set)     "
                    : "LIVE  (OpenAI connected)   ";
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║        Spring AI Advanced — Day 34 Part 2                   ║");
            System.out.println("║        RAG & Structured Output                              ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  Mode : " + mode + "                   ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  RAG Endpoint:                                              ║");
            System.out.println("║    GET  /api/ai/advisor?question=What are graduation...     ║");
            System.out.println("║                                                             ║");
            System.out.println("║  Structured Output Endpoints:                               ║");
            System.out.println("║    POST /api/ai/extract-student                            ║");
            System.out.println("║         Body: {\"description\":\"Alice is a CS major...\"}     ║");
            System.out.println("║    GET  /api/ai/classify-course?description=...            ║");
            System.out.println("║                                                             ║");
            System.out.println("║  Reference:                                                 ║");
            System.out.println("║    GET  /api/spring-ai-advanced-reference                  ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            if (demoModeChecker.isDemoMode()) {
                System.out.println("  >> " + demoModeChecker.getDemoNote());
            }
            System.out.println();
        };
    }
}
