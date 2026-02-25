package com.academy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
// CI/CD Demo Application — Day 37 Part 1
//
// This single file contains all classes for the CI/CD demonstration:
//   1. Calculator     — plain Java business logic (testable with JUnit)
//   2. BuildInfo      — reads runtime metadata injected by the CI pipeline
//   3. AppController  — REST endpoints for exploring CI/CD concepts
//   4. Application    — Spring Boot entry point
//
// KEY CI/CD CONCEPT: Environment variables injected by GitHub Actions
//   The CI pipeline sets APP_VERSION, BUILD_NUMBER, APP_ENV, GITHUB_SHA, etc.
//   Spring reads them via @Value("${...}") from application.properties,
//   which maps ${APP_VERSION:1.0.0-local} → environment variable APP_VERSION
//   with a fallback of "1.0.0-local" when running locally.
// ─────────────────────────────────────────────────────────────────────────────


// ═════════════════════════════════════════════════════════════════════════════
// 1. CALCULATOR — Plain Java business logic
//    No Spring annotations. Pure Java = easy to unit test in CI.
//    JUnit tests in AppTest.java exercise every method below.
// ═════════════════════════════════════════════════════════════════════════════
class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divides two numbers.
     * @throws IllegalArgumentException if divisor is zero.
     *         The CI pipeline verifies this exception is thrown via testDivideByZero().
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    /**
     * Checks whether a number is prime.
     * Demonstrates that even simple algorithms deserve test coverage in CI.
     */
    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// 2. BUILD INFO — CI/CD pipeline metadata component
//    Values are injected at runtime from environment variables set by GitHub Actions.
//    When running locally, the defaults in application.properties are used.
//
//    GitHub Actions sets these automatically:
//      GITHUB_SHA        — the full commit SHA that triggered the workflow
//      GITHUB_RUN_NUMBER — increments with each workflow run (build number)
// ═════════════════════════════════════════════════════════════════════════════
@Component
class BuildInfo {

    // @Value reads from application.properties:
    //   app.version=${APP_VERSION:1.0.0-local}
    //   In CI: APP_VERSION is set in the workflow (e.g., mvn package -DAPP_VERSION=${{ github.sha }})
    //   Locally: falls back to "1.0.0-local"
    @Value("${app.version}")
    private String version;

    @Value("${app.build.number}")
    private String buildNumber;

    @Value("${app.environment}")
    private String environment;

    /**
     * Returns a map of all CI/CD metadata for this running instance.
     * Useful for debugging: "which version is deployed in staging right now?"
     */
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("version", version);
        info.put("buildNumber", buildNumber);
        info.put("environment", environment);

        // GitHub Actions sets GITHUB_SHA to the commit SHA that triggered the workflow.
        // In production, this lets you trace EXACTLY which commit is deployed.
        info.put("githubSha", System.getenv("GITHUB_SHA") != null
                ? System.getenv("GITHUB_SHA") : "local");

        // GITHUB_RUN_NUMBER increments with each workflow run.
        // Useful for correlating a deploy with a specific workflow execution.
        info.put("githubRunNumber", System.getenv("GITHUB_RUN_NUMBER") != null
                ? System.getenv("GITHUB_RUN_NUMBER") : "N/A");

        // startTime helps identify when this instance was last restarted.
        info.put("startTime", LocalDateTime.now().toString());

        return info;
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// 3. APP CONTROLLER — REST endpoints
//    Exposes CI/CD build info and an educational reference guide.
// ═════════════════════════════════════════════════════════════════════════════
@RestController
class AppController {

    private final BuildInfo buildInfo;

    public AppController(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    // ── GET / ────────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>CI/CD Demo</title>
                  <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
                    h1   { color: #2196F3; }
                    a    { color: #2196F3; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    ul   { line-height: 2; }
                    .badge { background: #4CAF50; color: white; padding: 2px 8px; border-radius: 4px; font-size: 0.8em; }
                  </style>
                </head>
                <body>
                  <h1>🚀 CI/CD Demo <span class="badge">Part 1</span></h1>
                  <p>This application is built, tested, and deployed automatically by GitHub Actions.</p>
                  <h2>Endpoints</h2>
                  <ul>
                    <li><a href="/api/build-info">/api/build-info</a> — CI/CD pipeline metadata for this build</li>
                    <li><a href="/api/health">/api/health</a> — Application health status</li>
                    <li><a href="/api/cicd-reference">/api/cicd-reference</a> — CI/CD concepts reference guide</li>
                    <li><a href="/actuator/health">/actuator/health</a> — Spring Boot Actuator health</li>
                  </ul>
                  <h2>Workflow Files</h2>
                  <ul>
                    <li><code>.github/workflows/ci.yml</code> — Build and test on every push</li>
                    <li><code>.github/workflows/cd.yml</code> — Build Docker image and deploy</li>
                  </ul>
                </body>
                </html>
                """;
    }

    // ── GET /api/build-info ──────────────────────────────────────────────────
    // Returns metadata injected by the CI/CD pipeline.
    // In staging/production, these values come from GitHub Actions environment variables.
    @GetMapping("/api/build-info")
    public Map<String, Object> buildInfo() {
        return buildInfo.getInfo();
    }

    // ── GET /api/health ──────────────────────────────────────────────────────
    // A lightweight health check endpoint.
    // The CD workflow's smoke test curls this endpoint after each deployment.
    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new LinkedHashMap<>();
        Map<String, Object> info = buildInfo.getInfo();
        health.put("status", "UP");
        health.put("version", info.get("version"));
        health.put("environment", info.get("environment"));
        return health;
    }

    // ── GET /api/cicd-reference ──────────────────────────────────────────────
    // Comprehensive CI/CD reference guide returned as structured JSON.
    @GetMapping("/api/cicd-reference")
    public Map<String, Object> cicdReference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "CI/CD Reference Guide");

        // ── Core Concepts ────────────────────────────────────────────────────
        Map<String, Object> concepts = new LinkedHashMap<>();

        Map<String, Object> ci = new LinkedHashMap<>();
        ci.put("name", "Continuous Integration");
        ci.put("description", "Automatically build and test code on every commit");
        ci.put("benefits", Arrays.asList(
                "Catch bugs early — before they reach production",
                "Ensure code always compiles and tests always pass",
                "Consistent code quality enforced by automation"
        ));
        concepts.put("CI", ci);

        Map<String, Object> cdDelivery = new LinkedHashMap<>();
        cdDelivery.put("name", "Continuous Delivery");
        cdDelivery.put("description",
                "Automatically prepare releases so they are always deployable. " +
                "Manual gate required for the final production deployment.");
        cdDelivery.put("keyPoint", "Every commit CAN be deployed, but a human decides WHEN");
        concepts.put("CD_delivery", cdDelivery);

        Map<String, Object> cdDeployment = new LinkedHashMap<>();
        cdDeployment.put("name", "Continuous Deployment");
        cdDeployment.put("description",
                "Automatically deploy to production on every commit that passes all tests. " +
                "No manual intervention required.");
        cdDeployment.put("keyPoint", "Every passing commit IS deployed automatically");
        concepts.put("CD_deployment", cdDeployment);

        ref.put("concepts", concepts);

        // ── GitHub Actions Components ────────────────────────────────────────
        Map<String, Object> components = new LinkedHashMap<>();
        components.put("workflow",  "YAML file in .github/workflows/ triggered by events");
        components.put("event",     "What triggers the workflow: push, pull_request, schedule, workflow_dispatch");
        components.put("job",       "Set of steps running on one runner. Jobs run in parallel by default.");
        components.put("step",      "Individual task in a job: uses an action or runs a shell command");
        components.put("action",    "Reusable unit from GitHub Marketplace: actions/checkout, actions/setup-java");
        components.put("runner",    "Server that runs the job: ubuntu-latest, windows-latest, macos-latest");
        components.put("secret",    "Encrypted variable stored in repo settings: ${{ secrets.MY_SECRET }}");
        components.put("artifact",  "Files produced by workflow, uploadable for download or other jobs");
        ref.put("githubActionsComponents", components);

        // ── CI Pipeline Steps ────────────────────────────────────────────────
        ref.put("ciPipeline", Arrays.asList(
                "1. Code push triggers workflow",
                "2. Checkout code (actions/checkout)",
                "3. Set up environment (Java, Node, Python, etc.)",
                "4. Install dependencies (mvn dependency:go-offline)",
                "5. Run tests (mvn test) — fail fast if any test fails",
                "6. Build artifact (mvn package → produces JAR)",
                "7. Run code quality / security checks (Checkstyle, OWASP)",
                "8. Upload artifacts and reports for download"
        ));

        // ── CD Pipeline Steps ────────────────────────────────────────────────
        ref.put("cdPipeline", Arrays.asList(
                "1. CI pipeline succeeds (CD only runs after CI passes)",
                "2. Build Docker image",
                "3. Push to container registry (ghcr.io, ECR, Docker Hub)",
                "4. Deploy to staging (automatic)",
                "5. Run smoke / integration tests against staging",
                "6. Manual approval gate (required reviewers for production)",
                "7. Deploy to production",
                "8. Monitor and alert"
        ));

        // ── Environments ─────────────────────────────────────────────────────
        Map<String, String> environments = new LinkedHashMap<>();
        environments.put("development", "Developer's local machine");
        environments.put("staging",     "Production-like environment for final testing before go-live");
        environments.put("production",  "Live environment serving real users");
        ref.put("environments", environments);

        // ── Secrets ──────────────────────────────────────────────────────────
        Map<String, Object> secrets = new LinkedHashMap<>();
        secrets.put("usage", "${{ secrets.SECRET_NAME }}");
        secrets.put("types", Arrays.asList(
                "Repository secrets — available to all workflows in the repo",
                "Environment secrets — only available when deploying to that environment",
                "Organization secrets — shared across multiple repositories"
        ));
        secrets.put("neverHardcode",
                "API keys, passwords, tokens must NEVER be in code — always use secrets");
        ref.put("secrets", secrets);

        return ref;
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// 4. APPLICATION — Spring Boot entry point
// ═════════════════════════════════════════════════════════════════════════════
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        printBanner();
        SpringApplication.run(Application.class, args);
    }

    private static void printBanner() {
        System.out.println("""
                ╔══════════════════════════════════════════════════════╗
                ║           🚀 CI/CD DEMO — DAY 37 PART 1             ║
                ║──────────────────────────────────────────────────────║
                ║  GitHub Actions Workflows:                           ║
                ║    .github/workflows/ci.yml  → Build & Test          ║
                ║    .github/workflows/cd.yml  → Build, Push & Deploy  ║
                ║──────────────────────────────────────────────────────║
                ║  Endpoints:                                          ║
                ║    GET /api/build-info     CI pipeline metadata      ║
                ║    GET /api/health         Health check              ║
                ║    GET /api/cicd-reference CI/CD concepts guide      ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }
}
