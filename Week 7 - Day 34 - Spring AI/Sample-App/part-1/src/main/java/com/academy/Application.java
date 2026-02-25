package com.academy;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// ============================================================
//  DemoModeChecker
//  Reads the configured API key and tells the rest of the app
//  whether we are running against a real OpenAI key or in
//  offline "demo" mode (the default when no key is provided).
// ============================================================
@Component
class DemoModeChecker {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    /**
     * Returns true when no real OpenAI API key has been configured.
     * The application.properties file defaults the key to the literal
     * string "DEMO_MODE" via ${OPENAI_API_KEY:DEMO_MODE}, so any value
     * that is null, equals "DEMO_MODE", or does not start with "sk-"
     * is treated as demo mode.
     */
    public boolean isDemoMode() {
        return "DEMO_MODE".equals(apiKey) || apiKey == null || !apiKey.startsWith("sk-");
    }

    /**
     * Returns a human-readable note explaining how to enable live mode.
     */
    public String getDemoNote() {
        return "DEMO MODE active. Set OPENAI_API_KEY env var for live AI. "
             + "Example: export OPENAI_API_KEY=sk-xxx && mvn spring-boot:run";
    }
}

// ============================================================
//  ChatDemoController
//  Demonstrates the three most common Spring AI chat patterns:
//    1. Simple single-message prompt  → GET /api/ai/chat
//    2. System + User message combo   → GET /api/ai/code-review
//    3. Structured POST body input    → POST /api/ai/generate-tests
// ============================================================
@RestController
@RequestMapping("/api/ai")
class ChatDemoController {

    private final ChatClient chatClient;
    private final DemoModeChecker demoModeChecker;

    // Spring injects both beans automatically via constructor injection.
    ChatDemoController(ChatClient chatClient, DemoModeChecker demoModeChecker) {
        this.chatClient = chatClient;
        this.demoModeChecker = demoModeChecker;
    }

    // ----------------------------------------------------------
    // GET /api/ai/chat?message=Hello
    //
    // The simplest possible Spring AI call:
    //   chatClient.call(new Prompt(message))
    //
    // ChatClient.call() sends the prompt to OpenAI and returns a
    // ChatResponse.  We unwrap the text with:
    //   response.getResults().get(0).getOutput().getContent()
    // ----------------------------------------------------------
    @GetMapping("/chat")
    public Map<String, Object> chat(@RequestParam(defaultValue = "Hello, AI!") String message) {
        if (demoModeChecker.isDemoMode()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode", "DEMO");
            result.put("message", message);
            result.put("demoResponse",
                "[Simulated AI response to: " + message + "] "
                + "In demo mode, this shows what a real AI response would look like. "
                + "Set OPENAI_API_KEY for live responses.");
            result.put("howItWorks",
                "chatClient.call(new Prompt(message)).getResults().get(0).getOutput().getContent()");
            result.put("note", demoModeChecker.getDemoNote());
            return result;
        }

        try {
            ChatResponse response = chatClient.call(new Prompt(message));
            String aiResponse = response.getResults().get(0).getOutput().getContent();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode", "LIVE");
            result.put("message", message);
            result.put("aiResponse", aiResponse);
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "AI call failed: " + e.getMessage());
            error.put("tip", "Check that your OPENAI_API_KEY is valid.");
            return error;
        }
    }

    // ----------------------------------------------------------
    // GET /api/ai/code-review?code=YourCode
    //
    // Demonstrates using a SystemMessage to set AI behaviour plus
    // a UserMessage for the actual input.  Both are wrapped in a
    // Prompt that accepts a List<Message>.
    //
    // Pattern:
    //   List<Message> messages = List.of(
    //       new SystemMessage("You are a senior Java developer..."),
    //       new UserMessage("Review this code:\n" + code)
    //   );
    //   chatClient.call(new Prompt(messages));
    // ----------------------------------------------------------
    @GetMapping("/code-review")
    public Map<String, Object> codeReview(
            @RequestParam(defaultValue = "public int add(int a, int b) { return a + b; }") String code) {

        String systemText = "You are a senior Java developer. "
                          + "Review code for correctness, performance, security, and style. Be concise.";
        String userText   = "Review this code:\n" + code;

        if (demoModeChecker.isDemoMode()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode", "DEMO");
            result.put("promptStructure", Map.of(
                "systemMessage", systemText,
                "userMessage",   userText
            ));
            result.put("demoReview",
                "[Simulated Code Review]\n"
                + "✅ Overall structure looks reasonable\n"
                + "⚠️  Consider adding null checks\n"
                + "⚠️  Add error handling for edge cases\n"
                + "💡 Consider extracting magic numbers to constants\n"
                + "🔒 Validate input parameters");
            result.put("note", demoModeChecker.getDemoNote());
            return result;
        }

        try {
            Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemText),
                new UserMessage(userText)
            ));
            ChatResponse response = chatClient.call(prompt);
            String review = response.getResults().get(0).getOutput().getContent();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode", "LIVE");
            result.put("code", code);
            result.put("review", review);
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Code review failed: " + e.getMessage());
            return error;
        }
    }

    // ----------------------------------------------------------
    // POST /api/ai/generate-tests
    // Body (JSON): { "className": "Calculator", "methodCode": "..." }
    //
    // Demonstrates accepting structured input and asking the AI
    // to produce JUnit 5 test skeletons.
    // ----------------------------------------------------------
    @PostMapping("/generate-tests")
    public Map<String, Object> generateTests(@RequestBody Map<String, String> body) {
        String className  = body.getOrDefault("className",  "MyClass");
        String methodCode = body.getOrDefault("methodCode", "public int add(int a, int b) { return a + b; }");

        String prompt = "Generate JUnit 5 unit tests for the following Java method in class " + className + ".\n"
                      + "Include: happy path, edge cases, and boundary tests.\n"
                      + "Use @Test, @DisplayName, and Assertions from JUnit 5.\n\n"
                      + "Method:\n" + methodCode;

        if (demoModeChecker.isDemoMode()) {
            String demoTests =
                "import org.junit.jupiter.api.Test;\n"
                + "import org.junit.jupiter.api.DisplayName;\n"
                + "import static org.junit.jupiter.api.Assertions.*;\n\n"
                + "class " + className + "Test {\n\n"
                + "    private final " + className + " sut = new " + className + "();\n\n"
                + "    @Test\n"
                + "    @DisplayName(\"[DEMO] Happy path — returns expected result\")\n"
                + "    void happyPath() {\n"
                + "        // Arrange\n"
                + "        // Act\n"
                + "        // Assert — set OPENAI_API_KEY for AI-generated test bodies\n"
                + "        assertTrue(true, \"Replace with real assertion\");\n"
                + "    }\n\n"
                + "    @Test\n"
                + "    @DisplayName(\"[DEMO] Edge case — null or zero input\")\n"
                + "    void edgeCase() {\n"
                + "        // AI would generate meaningful edge-case tests here\n"
                + "        assertTrue(true, \"Replace with real assertion\");\n"
                + "    }\n"
                + "}\n";

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",       "DEMO");
            result.put("className",  className);
            result.put("methodCode", methodCode);
            result.put("demoTests",  demoTests);
            result.put("note",       demoModeChecker.getDemoNote());
            return result;
        }

        try {
            ChatResponse response = chatClient.call(new Prompt(prompt));
            String tests = response.getResults().get(0).getOutput().getContent();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",           "LIVE");
            result.put("className",      className);
            result.put("methodCode",     methodCode);
            result.put("generatedTests", tests);
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Test generation failed: " + e.getMessage());
            return error;
        }
    }
}

// ============================================================
//  PromptTemplateDemoController
//  Shows how PromptTemplate and multi-message Prompts work.
//    1. Variable substitution    → GET /api/ai/explain-concept
//    2. Multi-message prompt     → GET /api/ai/compare-approaches
// ============================================================
@RestController
@RequestMapping("/api/ai")
class PromptTemplateDemoController {

    private final ChatClient chatClient;
    private final DemoModeChecker demoModeChecker;

    PromptTemplateDemoController(ChatClient chatClient, DemoModeChecker demoModeChecker) {
        this.chatClient = chatClient;
        this.demoModeChecker = demoModeChecker;
    }

    // ----------------------------------------------------------
    // GET /api/ai/explain-concept?concept=recursion&audience=beginner
    //
    // PromptTemplate replaces {placeholders} in a template string
    // with values from a Map — similar to String.format() but
    // designed for AI prompts.
    //
    // Pattern:
    //   PromptTemplate pt = new PromptTemplate(templateText);
    //   Prompt prompt = pt.create(Map.of("concept", concept, "audience", audience));
    //   chatClient.call(prompt);
    // ----------------------------------------------------------
    @GetMapping("/explain-concept")
    public Map<String, Object> explainConcept(
            @RequestParam(defaultValue = "recursion")  String concept,
            @RequestParam(defaultValue = "beginner")   String audience) {

        String templateText =
            "Explain {concept} to a {audience} developer. "
            + "Use: 1) Simple definition  2) One real-world analogy  3) A short Java code example.";

        if (demoModeChecker.isDemoMode()) {
            // Show the student exactly what would be sent to the AI
            String filled = templateText
                .replace("{concept}",  concept)
                .replace("{audience}", audience);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",         "DEMO");
            result.put("concept",      concept);
            result.put("audience",     audience);
            result.put("templateText", templateText);
            result.put("filledTemplate", filled);
            result.put("demoResponse",
                "[Simulated explanation of " + concept + " for " + audience + " developers]");
            result.put("howItWorks",
                "PromptTemplate substitutes {variables} at runtime - like String.format() but for AI prompts");
            result.put("note", demoModeChecker.getDemoNote());
            return result;
        }

        try {
            PromptTemplate pt     = new PromptTemplate(templateText);
            Prompt         prompt = pt.create(Map.of("concept", concept, "audience", audience));
            ChatResponse   resp   = chatClient.call(prompt);
            String         text   = resp.getResults().get(0).getOutput().getContent();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",         "LIVE");
            result.put("concept",      concept);
            result.put("audience",     audience);
            result.put("templateText", templateText);
            result.put("explanation",  text);
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Explain-concept failed: " + e.getMessage());
            return error;
        }
    }

    // ----------------------------------------------------------
    // GET /api/ai/compare-approaches?topic=singleton
    //
    // Multi-message prompt: combines a SystemMessage (persona) and
    // a UserMessage (request) into a single Prompt.
    // ----------------------------------------------------------
    @GetMapping("/compare-approaches")
    public Map<String, Object> compareApproaches(
            @RequestParam(defaultValue = "singleton") String topic) {

        String systemText = "You are a software architect. Be concise and practical.";
        String userText   = "Compare the different ways to implement " + topic
                          + " in Java. Show pros/cons table.";

        if (demoModeChecker.isDemoMode()) {
            Map<String, Object> messagesToBeSent = new LinkedHashMap<>();
            messagesToBeSent.put("message_1_type",    "SystemMessage");
            messagesToBeSent.put("message_1_content", systemText);
            messagesToBeSent.put("message_2_type",    "UserMessage");
            messagesToBeSent.put("message_2_content", userText);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",              "DEMO");
            result.put("topic",             topic);
            result.put("promptStructure",   messagesToBeSent);
            result.put("howItWorks",
                "List.of(new SystemMessage(...), new UserMessage(...)) is passed to "
                + "new Prompt(messages). The system message sets the AI persona; "
                + "the user message is the actual request.");
            result.put("demoComparison",
                "[Simulated comparison of " + topic + " implementations]\n"
                + "Set OPENAI_API_KEY to receive a real pros/cons table from the AI.");
            result.put("note", demoModeChecker.getDemoNote());
            return result;
        }

        try {
            Prompt       prompt = new Prompt(List.of(
                new SystemMessage(systemText),
                new UserMessage(userText)
            ));
            ChatResponse resp  = chatClient.call(prompt);
            String       text  = resp.getResults().get(0).getOutput().getContent();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mode",       "LIVE");
            result.put("topic",      topic);
            result.put("comparison", text);
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "Compare-approaches failed: " + e.getMessage());
            return error;
        }
    }
}

// ============================================================
//  SpringAiReferenceController
//  A self-contained reference guide returned as JSON.
//  Students can hit GET /api/spring-ai-reference to review the
//  key concepts without leaving the running application.
// ============================================================
@RestController
@RequestMapping("/api")
class SpringAiReferenceController {

    @GetMapping("/spring-ai-reference")
    public Map<String, Object> reference() {
        Map<String, Object> ref = new LinkedHashMap<>();

        ref.put("title",    "Spring AI Reference Guide");
        ref.put("version",  "Spring AI 0.8.1  |  Spring Boot 3.2.1");
        ref.put("overview",
            "Spring AI provides a consistent API abstraction over multiple AI providers "
            + "(OpenAI, Azure OpenAI, Anthropic, Hugging Face, Ollama, and more). "
            + "Switch providers by changing a dependency and a few properties — no code changes needed.");

        // ── Core abstractions ─────────────────────────────────────
        List<Map<String, String>> abstractions = new ArrayList<>();

        Map<String, String> chatClient = new LinkedHashMap<>();
        chatClient.put("name",        "ChatClient");
        chatClient.put("description", "Primary interface for chat completions");
        chatClient.put("usage",       "chatClient.call(new Prompt(message))");
        chatClient.put("note",        "Returns ChatResponse");
        abstractions.add(chatClient);

        Map<String, String> promptTemplate = new LinkedHashMap<>();
        promptTemplate.put("name",        "PromptTemplate");
        promptTemplate.put("description", "Variable substitution in prompts");
        promptTemplate.put("usage",       "new PromptTemplate(template).create(Map.of(\"key\", \"value\"))");
        abstractions.add(promptTemplate);

        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("name",        "SystemMessage");
        systemMessage.put("description", "Set AI persona / instructions");
        systemMessage.put("usage",       "new SystemMessage(\"You are a helpful assistant\")");
        abstractions.add(systemMessage);

        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("name",        "UserMessage");
        userMessage.put("description", "User input message");
        userMessage.put("usage",       "new UserMessage(input)");
        abstractions.add(userMessage);

        Map<String, String> chatResponse = new LinkedHashMap<>();
        chatResponse.put("name",        "ChatResponse");
        chatResponse.put("description", "Wrapper for the AI response");
        chatResponse.put("usage",       "response.getResults().get(0).getOutput().getContent()");
        abstractions.add(chatResponse);

        Map<String, String> embeddingClient = new LinkedHashMap<>();
        embeddingClient.put("name",        "EmbeddingClient");
        embeddingClient.put("description", "Convert text to vectors for semantic search");
        embeddingClient.put("usage",       "embeddingClient.embed(text)  →  List<Double>");
        abstractions.add(embeddingClient);

        Map<String, String> imageClient = new LinkedHashMap<>();
        imageClient.put("name",        "ImageClient");
        imageClient.put("description", "Generate images (DALL-E, Stability AI)");
        imageClient.put("usage",       "imageClient.call(new ImagePrompt(\"a sunrise over mountains\"))");
        abstractions.add(imageClient);

        ref.put("coreAbstractions", abstractions);

        // ── Configuration ─────────────────────────────────────────
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("openaiProperties", List.of(
            "spring.ai.openai.api-key=sk-...",
            "spring.ai.openai.chat.options.model=gpt-3.5-turbo",
            "spring.ai.openai.chat.options.temperature=0.7",
            "spring.ai.openai.chat.options.max-tokens=1000"
        ));
        config.put("switchingProviders",
            "Replace spring-ai-openai-spring-boot-starter with another provider starter "
            + "(e.g. spring-ai-anthropic-spring-boot-starter) and update properties. "
            + "ChatClient code stays the same.");
        config.put("availableProviders",
            List.of("OpenAI", "Azure OpenAI", "Anthropic Claude",
                    "Hugging Face", "Ollama (local)", "Mistral AI",
                    "Amazon Bedrock", "Google Vertex AI"));
        ref.put("configuration", config);

        // ── Prompt engineering examples ───────────────────────────
        Map<String, String> pe = new LinkedHashMap<>();
        pe.put("zeroShot",
            "new Prompt(\"What is the capital of France?\")");
        pe.put("fewShot",
            "new Prompt(\"Translate English to French.\\n"
            + "English: Hello → French: Bonjour\\n"
            + "English: Thank you → French: Merci\\n"
            + "English: Good morning → French:\")");
        pe.put("chainOfThought",
            "new Prompt(\"Solve step by step: If a train travels 60 mph for 2.5 hours, "
            + "how far does it travel? Think through each step.\")");
        pe.put("systemPrompt",
            "new Prompt(List.of(\\n"
            + "  new SystemMessage(\"You are a senior Java developer. "
            + "Be concise and use code examples.\"),\\n"
            + "  new UserMessage(\"Explain dependency injection\")))");
        ref.put("promptEngineering", pe);

        // ── Version notes ─────────────────────────────────────────
        Map<String, Object> versionNotes = new LinkedHashMap<>();
        versionNotes.put("springAi_0_8_x", Map.of(
            "chatClientInterface",   "org.springframework.ai.chat.ChatClient  (interface)",
            "callMethod",            "chatClient.call(Prompt)  →  ChatResponse",
            "messagePackage",        "org.springframework.ai.chat.messages",
            "promptTemplatePackage", "org.springframework.ai.chat.prompt"
        ));
        versionNotes.put("springAi_1_0_x", Map.of(
            "chatClientInterface",   "ChatClient refactored — fluent builder API added",
            "callMethod",            "chatClient.prompt().user(msg).call().content()  (fluent)",
            "breakingChanges",       "Package paths reorganised; ChatClient is now a class with builder",
            "migration",             "See https://docs.spring.io/spring-ai/reference/upgrade-notes.html"
        ));
        versionNotes.put("tip",
            "This sample app targets 0.8.1. If you upgrade to 1.x, update imports and use the fluent API.");
        ref.put("versionNotes", versionNotes);

        return ref;
    }
}

// ============================================================
//  Application  —  entry point
// ============================================================
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Prints a friendly startup banner to the console after the
     * Spring context is fully initialised.
     */
    @Bean
    CommandLineRunner startupBanner(DemoModeChecker demoModeChecker) {
        return args -> {
            String line = "=".repeat(62);
            System.out.println("\n" + line);
            System.out.println("  🤖  Spring AI Basics — Day 34 Sample App");
            System.out.println(line);
            System.out.println("  Server  : http://localhost:8080");
            System.out.println("  Mode    : " + (demoModeChecker.isDemoMode()
                                                 ? "⚠️  DEMO (no live AI calls)"
                                                 : "✅  LIVE (real OpenAI calls)"));
            System.out.println();
            System.out.println("  ── ChatDemoController ─────────────────────────────");
            System.out.println("  GET  /api/ai/chat?message=Hello");
            System.out.println("  GET  /api/ai/code-review?code=public+int+add(...)");
            System.out.println("  POST /api/ai/generate-tests");
            System.out.println("       Body: {\"className\":\"Calc\",\"methodCode\":\"...\"}");
            System.out.println();
            System.out.println("  ── PromptTemplateDemoController ───────────────────");
            System.out.println("  GET  /api/ai/explain-concept?concept=recursion&audience=beginner");
            System.out.println("  GET  /api/ai/compare-approaches?topic=singleton");
            System.out.println();
            System.out.println("  ── Reference ──────────────────────────────────────");
            System.out.println("  GET  /api/spring-ai-reference");
            System.out.println();

            if (demoModeChecker.isDemoMode()) {
                System.out.println("  " + demoModeChecker.getDemoNote());
            }
            System.out.println(line + "\n");
        };
    }
}
