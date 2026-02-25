package com.academy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ─────────────────────────────────────────────────────────────────────────────
// Advanced CI/CD Demo Application — Day 37 Part 2
//
// This single file demonstrates advanced CI/CD concepts:
//   1. Calculator     — business logic with full parameterized test coverage
//   2. DeploymentInfo — rich GitHub Actions environment variable capture
//   3. AppController  — endpoints exposing advanced CI/CD reference material
//   4. Application    — Spring Boot entry point
//
// ADVANCED CI/CD PATTERNS DEMONSTRATED:
//   - Matrix strategy: full-pipeline.yml tests on Java 17 AND Java 21
//   - Job outputs: Docker image tag passed from build-push → deploy-staging
//   - Environment protection: production requires manual approval
//   - Concurrency groups: prevent overlapping deployments
//   - Security scanning: Trivy + TruffleHog integrated into pipeline
// ─────────────────────────────────────────────────────────────────────────────


// ═════════════════════════════════════════════════════════════════════════════
// 1. CALCULATOR — Plain Java business logic
//    Covered by CalculatorTest with @ParameterizedTest and @Nested classes.
//    Matrix strategy in full-pipeline.yml verifies this runs on Java 17 + 21.
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
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    /**
     * Checks whether a number is prime.
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
// 2. DEPLOYMENT INFO — Advanced GitHub Actions environment variable capture
//    Captures the full set of GitHub Actions context variables.
//    In production, these allow you to answer:
//      "Which commit is deployed?"  → gitCommitSha
//      "Which workflow ran?"        → workflowName
//      "Who triggered this deploy?" → actor
//      "What branch is deployed?"   → gitRef
// ═════════════════════════════════════════════════════════════════════════════
@Component
class DeploymentInfo {

    @Value("${info.app.version}")
    private String version;

    @Value("${info.app.build}")
    private String buildNumber;

    /**
     * Returns a comprehensive map of deployment metadata.
     *
     * GitHub Actions sets these environment variables automatically:
     *   GITHUB_SHA       — Full 40-character SHA of the commit being built
     *   GITHUB_REF       — Full ref name: refs/heads/main, refs/tags/v1.0.0
     *   GITHUB_WORKFLOW  — Name of the workflow file that triggered this run
     *   GITHUB_RUN_ID    — Unique ID for this workflow run (links to Actions UI)
     *   GITHUB_ACTOR     — GitHub username of the person who triggered the run
     *
     * All default to "local" when running on a developer machine (not in CI).
     */
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();

        // From application.properties (set by Maven -D flags in the workflow)
        info.put("version",     version);
        info.put("buildNumber", buildNumber);

        // From GitHub Actions environment variables
        info.put("gitCommitSha",  getEnvOrDefault("GITHUB_SHA",      "local"));
        info.put("gitRef",        getEnvOrDefault("GITHUB_REF",      "local"));
        info.put("workflowName",  getEnvOrDefault("GITHUB_WORKFLOW",  "local"));
        info.put("runId",         getEnvOrDefault("GITHUB_RUN_ID",    "local"));
        info.put("actor",         getEnvOrDefault("GITHUB_ACTOR",     "local"));

        return info;
    }

    private String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}


// ═════════════════════════════════════════════════════════════════════════════
// 3. APP CONTROLLER — REST endpoints
// ═════════════════════════════════════════════════════════════════════════════
@RestController
class AppController {

    private final DeploymentInfo deploymentInfo;

    public AppController(DeploymentInfo deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }

    // ── GET / ────────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Advanced CI/CD Demo</title>
                  <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
                    h1   { color: #9C27B0; }
                    a    { color: #9C27B0; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    ul   { line-height: 2; }
                    .badge { background: #FF5722; color: white; padding: 2px 8px; border-radius: 4px; font-size: 0.8em; }
                    code { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
                  </style>
                </head>
                <body>
                  <h1>🚀 Advanced CI/CD Demo <span class="badge">Part 2</span></h1>
                  <p>Demonstrates advanced GitHub Actions patterns: matrix strategy, job outputs,
                     environment protection rules, concurrency controls, and security scanning.</p>
                  <h2>Endpoints</h2>
                  <ul>
                    <li><a href="/api/deployment-info">/api/deployment-info</a> — Full GitHub Actions metadata</li>
                    <li><a href="/api/advanced-cicd-reference">/api/advanced-cicd-reference</a> — Advanced CI/CD reference</li>
                    <li><a href="/actuator/health">/actuator/health</a> — Spring Boot Actuator health</li>
                    <li><a href="/actuator/info">/actuator/info</a> — Spring Boot Actuator info (from application.properties)</li>
                  </ul>
                  <h2>Advanced Workflow</h2>
                  <ul>
                    <li><code>.github/workflows/full-pipeline.yml</code> — Full pipeline with matrix, security scan, environments</li>
                  </ul>
                  <h2>Pipeline Flow</h2>
                  <pre>test-matrix (Java 17) ─┐
                                         ├─→ build-push → deploy-staging → deploy-production
test-matrix (Java 21) ─┤                                                   (manual approval)
                         │
security-scan ──────────┘</pre>
                </body>
                </html>
                """;
    }

    // ── GET /api/deployment-info ─────────────────────────────────────────────
    @GetMapping("/api/deployment-info")
    public Map<String, Object> deploymentInfo() {
        return deploymentInfo.getInfo();
    }

    // ── GET /api/advanced-cicd-reference ────────────────────────────────────
    @GetMapping("/api/advanced-cicd-reference")
    public Map<String, Object> advancedCicdReference() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("title", "Advanced CI/CD Reference");

        // ── Matrix Strategy ──────────────────────────────────────────────────
        Map<String, Object> matrix = new LinkedHashMap<>();
        matrix.put("description",
                "Run the same job with multiple configurations simultaneously using 'strategy.matrix'");
        matrix.put("example",
                "Test on Java 17, Java 21, and Node 18 in parallel — all three must pass");
        matrix.put("benefits", Arrays.asList(
                "Ensures compatibility across multiple versions",
                "Parallel execution is faster than sequential",
                "fail-fast: false shows ALL failures, not just the first"
        ));
        matrix.put("syntax", Map.of(
                "definition",  "strategy: { matrix: { java-version: [17, 21] } }",
                "reference",   "${{ matrix.java-version }}",
                "failFast",    "fail-fast: false — run all matrix jobs even if one fails"
        ));
        ref.put("matrixStrategy", matrix);

        // ── Job Dependencies ─────────────────────────────────────────────────
        Map<String, Object> deps = new LinkedHashMap<>();
        deps.put("description",
                "'needs' keyword creates sequential job dependencies — downstream job only " +
                "runs after all listed jobs succeed");
        deps.put("example",
                "deploy needs build-and-test: deploy won't run if any test fails");
        deps.put("multipleNeeds",
                "needs: [test-matrix, security-scan] — wait for ALL listed jobs");
        deps.put("parallelDefault",
                "Jobs without 'needs' run in parallel automatically");
        ref.put("jobDependencies", deps);

        // ── GitHub Environments ──────────────────────────────────────────────
        Map<String, Object> envs = new LinkedHashMap<>();
        envs.put("description",
                "Named environments with protection rules and environment-specific secrets");
        envs.put("productionGating",
                "Add 'Required reviewers' in GitHub Settings > Environments > production. " +
                "The workflow pauses and sends approval notifications before the job runs.");
        envs.put("environmentSecrets",
                "Secrets scoped to an environment are only available to jobs targeting that environment. " +
                "Use this to keep production credentials separate from staging.");
        envs.put("deploymentHistory",
                "Every environment run is tracked in the repository's Deployments view");
        ref.put("environments", envs);

        // ── Job Outputs ──────────────────────────────────────────────────────
        Map<String, Object> outputs = new LinkedHashMap<>();
        outputs.put("description",
                "Pass data between jobs using the 'outputs' mechanism");
        outputs.put("definition",
                "jobs.my-job.outputs: { my-value: ${{ steps.my-step.outputs.some-output }} }");
        outputs.put("usage",
                "${{ needs.build-push.outputs.image-tag }} — reference in downstream job");
        outputs.put("example",
                "build-push outputs the Docker image tag → deploy-staging uses it to deploy the exact image");
        ref.put("outputs", outputs);

        // ── Caching ──────────────────────────────────────────────────────────
        Map<String, Object> caching = new LinkedHashMap<>();
        caching.put("description",
                "Cache dependencies between workflow runs to dramatically speed up builds");
        caching.put("mavenCache",
                "actions/setup-java with 'cache: maven' saves ~/.m2 — " +
                "subsequent runs skip downloading dependencies (saves 1-3 minutes)");
        caching.put("howItWorks",
                "GitHub stores the cache keyed by pom.xml hash. " +
                "Cache is restored at the start of the job and saved at the end.");
        ref.put("caching", caching);

        // ── Concurrency ──────────────────────────────────────────────────────
        Map<String, Object> concurrency = new LinkedHashMap<>();
        concurrency.put("description",
                "Prevent multiple deploys running simultaneously using 'concurrency' groups");
        concurrency.put("usage", Map.of(
                "group",              "concurrency: group: production",
                "cancelInProgress",   "cancel-in-progress: false — queue new runs, don't cancel active deploys",
                "forCI",              "cancel-in-progress: true — cancel old CI runs when new commit arrives"
        ));
        concurrency.put("whyItMatters",
                "Without concurrency control, two pushes in quick succession could deploy " +
                "in overlapping or wrong order, causing undefined application state.");
        ref.put("concurrency", concurrency);

        // ── Rollback Strategies ──────────────────────────────────────────────
        ref.put("rollbackStrategies", Arrays.asList(
                "Docker image tags: re-deploy the previous :sha tag instantly",
                "Git revert: git revert <commit> then push — triggers a new CI/CD pipeline",
                "Feature flags: disable the broken feature without any redeployment",
                "Blue-green: keep old environment running, switch load balancer back"
        ));

        // ── Deployment Strategies ────────────────────────────────────────────
        Map<String, String> deployStrategies = new LinkedHashMap<>();
        deployStrategies.put("blueGreen",
                "Run two identical environments (blue=active, green=new). " +
                "Switch traffic atomically for zero-downtime. Instant rollback: switch back.");
        deployStrategies.put("canary",
                "Route 5% of traffic to new version. Monitor error rate. " +
                "Gradually increase to 100% if healthy. Limit blast radius of bad deploys.");
        deployStrategies.put("rollingUpdate",
                "Replace old pods one by one (Kubernetes default). " +
                "Always some old and some new pods running during the rollout.");
        deployStrategies.put("recreate",
                "Stop ALL old instances, then start ALL new ones. " +
                "Simple but causes downtime — only acceptable for non-production environments.");
        ref.put("deploymentStrategies", deployStrategies);

        // ── Monitoring ───────────────────────────────────────────────────────
        Map<String, Object> monitoring = new LinkedHashMap<>();
        monitoring.put("metrics",
                "Expose /actuator/metrics; scrape with Prometheus; " +
                "visualize in Grafana. Alert on p99 latency and error rate.");
        monitoring.put("alerting",
                "Alert on error rate spike or latency increase within 15 min of a deploy. " +
                "Auto-rollback if error rate exceeds threshold.");
        monitoring.put("logging",
                "Use structured JSON logs (logstash-logback-encoder). " +
                "Centralize with ELK stack, Datadog, or AWS CloudWatch. " +
                "Include traceId for distributed tracing.");
        ref.put("monitoring", monitoring);

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
                ╔══════════════════════════════════════════════════════════════╗
                ║          🚀 ADVANCED CI/CD DEMO — DAY 37 PART 2             ║
                ║────────────────────────────────────────────────────────────  ║
                ║  Advanced Pipeline: .github/workflows/full-pipeline.yml      ║
                ║  Patterns: Matrix strategy, Job outputs, Env protection,     ║
                ║            Concurrency groups, Security scanning             ║
                ║────────────────────────────────────────────────────────────  ║
                ║  Endpoints:                                                  ║
                ║    GET /api/deployment-info          GitHub Actions metadata ║
                ║    GET /api/advanced-cicd-reference  Advanced patterns guide ║
                ║    GET /actuator/info                Spring Boot info        ║
                ╚══════════════════════════════════════════════════════════════╝
                """);
    }
}
