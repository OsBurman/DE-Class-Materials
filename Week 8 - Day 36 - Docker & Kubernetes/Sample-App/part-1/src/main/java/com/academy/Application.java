package com.academy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ─── Docker Info Component ────────────────────────────────────────────────────

@Component
class DockerInfo {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.environment:local}")
    private String appEnvironment;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("port", serverPort);
        info.put("environment", appEnvironment);
        info.put("version", appVersion);
        info.put("javaVersion", System.getProperty("java.version"));

        try {
            info.put("hostname", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            info.put("hostname", "unknown");
        }

        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemoryMB", Runtime.getRuntime().maxMemory() / (1024 * 1024));

        // /.dockerenv is created by Docker inside every container — reliable detection flag
        boolean inDocker = new File("/.dockerenv").exists();
        info.put("containerInfo", inDocker
                ? "Running inside a Docker container"
                : "Running locally (not in Docker)");

        return info;
    }
}

// ─── App Controller ───────────────────────────────────────────────────────────

@RestController
@RequiredArgsConstructor
class AppController {

    private final DockerInfo dockerInfo;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.environment:local}")
    private String appEnvironment;

    // ── GET / ─────────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Docker Demo</title>
                  <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; background: #f9f9f9; }
                    h1   { color: #0db7ed; }
                    h2   { color: #384d54; margin-top: 32px; }
                    code { background: #eef; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
                    .card { background: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 16px; margin: 10px 0; }
                    a    { color: #0db7ed; }
                  </style>
                </head>
                <body>
                  <h1>🐳 Docker Demo Application</h1>
                  <p>A Spring Boot app demonstrating Docker containerization concepts for developer bootcamp students.</p>

                  <h2>Key Docker Concepts</h2>
                  <div class="card">
                    <strong>Container</strong> — A lightweight, isolated process with its own filesystem, network, and process space.
                    Containers share the host OS kernel; they are faster and smaller than VMs.
                  </div>
                  <div class="card">
                    <strong>Image</strong> — A read-only template built from a <code>Dockerfile</code>.
                    Each instruction creates a cached layer, making rebuilds fast.
                  </div>
                  <div class="card">
                    <strong>Multi-Stage Build</strong> — Separates build tools (Maven, JDK) from the runtime image.
                    Result: a <code>~100MB</code> Alpine JRE image instead of a <code>~500MB</code> JDK/Maven image.
                  </div>
                  <div class="card">
                    <strong>Docker Compose</strong> — Define and run multi-container applications with a single
                    <code>docker compose up -d</code> command.
                  </div>

                  <h2>API Endpoints</h2>
                  <ul>
                    <li><a href="/api/info">/api/info</a> — App &amp; container info</li>
                    <li><a href="/api/health">/api/health</a> — Health check</li>
                    <li><a href="/api/environment">/api/environment</a> — Environment config</li>
                    <li><a href="/api/docker-reference">/api/docker-reference</a> — Full Docker reference guide</li>
                    <li><a href="/actuator/health">/actuator/health</a> — Spring Actuator health</li>
                  </ul>
                </body>
                </html>
                """;
    }

    // ── GET /api/info ─────────────────────────────────────────────────────────

    @GetMapping("/api/info")
    public Map<String, Object> info() {
        return dockerInfo.getInfo();
    }

    // ── GET /api/health ───────────────────────────────────────────────────────

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", "Application is healthy"
        );
    }

    // ── GET /api/environment ──────────────────────────────────────────────────

    @GetMapping("/api/environment")
    public Map<String, String> environment() {
        return Map.of(
                "PORT", serverPort,
                "APP_ENV", appEnvironment
        );
    }

    // ── GET /api/docker-reference ─────────────────────────────────────────────

    @GetMapping("/api/docker-reference")
    public Map<String, Object> dockerReference() {
        Map<String, Object> ref = new LinkedHashMap<>();

        ref.put("title", "Docker Reference Guide");

        ref.put("concepts", List.of(
                Map.of(
                        "name", "Container",
                        "description", "Lightweight, isolated process with its own filesystem, network, and process space",
                        "vsVM", "Containers share the host OS kernel; VMs have their own OS. Containers are faster and smaller."
                ),
                Map.of(
                        "name", "Image",
                        "description", "Read-only template used to create containers. Built from Dockerfile.",
                        "layers", "Each Dockerfile instruction creates a layer. Layers are cached and reused."
                ),
                Map.of(
                        "name", "Dockerfile",
                        "description", "Text file with instructions to build a Docker image"
                ),
                Map.of(
                        "name", "Registry",
                        "description", "Storage for Docker images. Docker Hub is the default public registry.",
                        "examples", List.of("Docker Hub", "AWS ECR", "Google Container Registry", "GitHub Container Registry")
                ),
                Map.of(
                        "name", "Volume",
                        "description", "Persistent storage that survives container restarts",
                        "types", List.of(
                                "Named volumes: docker volume create mydata",
                                "Bind mounts: map host directory into container"
                        )
                ),
                Map.of(
                        "name", "Network",
                        "description", "Communication between containers",
                        "types", List.of(
                                "bridge (default)",
                                "host (container uses host network)",
                                "overlay (swarm multi-host)"
                        )
                )
        ));

        ref.put("dockerfileInstructions", List.of(
                Map.of("instruction", "FROM",        "description", "Base image to build from",                         "example", "FROM eclipse-temurin:17-jre-alpine"),
                Map.of("instruction", "WORKDIR",     "description", "Set working directory",                            "example", "WORKDIR /app"),
                Map.of("instruction", "COPY",        "description", "Copy files from host to image",                    "example", "COPY target/app.jar app.jar"),
                Map.of("instruction", "RUN",         "description", "Execute command during build",                     "example", "RUN mvn dependency:go-offline"),
                Map.of("instruction", "EXPOSE",      "description", "Document which port the container listens on",     "example", "EXPOSE 8080"),
                Map.of("instruction", "ENV",         "description", "Set environment variable",                         "example", "ENV PORT=8080"),
                Map.of("instruction", "ENTRYPOINT",  "description", "Command to run when container starts",             "example", "ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]"),
                Map.of("instruction", "HEALTHCHECK", "description", "Command to check container health",                "example", "HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health")
        ));

        ref.put("essentialCommands", Map.of(
                "build",   "docker build -t myapp:latest .",
                "run",     "docker run -d -p 8080:8080 --name myapp myapp:latest",
                "list",    "docker ps (running) | docker ps -a (all)",
                "logs",    "docker logs myapp | docker logs -f myapp (follow)",
                "exec",    "docker exec -it myapp sh (open shell in container)",
                "stop",    "docker stop myapp",
                "remove",  "docker rm myapp | docker rmi myapp:latest (remove image)",
                "compose", "docker compose up -d | docker compose down | docker compose logs -f"
        ));

        ref.put("multistageBuilds", Map.of(
                "why", "Keeps production image small by not including build tools (Maven, SDK) in final image",
                "stages", List.of(
                        "Stage 1: Use JDK/Maven image to compile and build JAR",
                        "Stage 2: Use small JRE-only image, copy only the JAR"
                ),
                "sizeComparison", "Full JDK Maven image: ~500MB | Multi-stage alpine JRE: ~100MB"
        ));

        ref.put("bestPractices", List.of(
                "Use specific image tags (not :latest) for reproducibility",
                "Copy pom.xml before source code for better layer caching",
                "Use .dockerignore to exclude unnecessary files",
                "Run as non-root user in production",
                "Use HEALTHCHECK for container orchestration",
                "Use environment variables for configuration (not hardcoded values)",
                "Use XX:+UseContainerSupport for JVM container awareness"
        ));

        return ref;
    }
}

// ─── Application ──────────────────────────────────────────────────────────────

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner startupBanner() {
        return args -> System.out.println("""
                ╔══════════════════════════════════════════════════════════════════╗
                ║               🐳 Docker Demo Application                        ║
                ╠══════════════════════════════════════════════════════════════════╣
                ║  Run locally:                                                   ║
                ║    mvn spring-boot:run                                          ║
                ║                                                                 ║
                ║  Build & run with Docker:                                       ║
                ║    docker build -t docker-demo .                                ║
                ║    docker run -p 8080:8080 docker-demo                          ║
                ║                                                                 ║
                ║  Run with Docker Compose:                                       ║
                ║    docker compose up -d                                         ║
                ║                                                                 ║
                ║  API Endpoints:                                                 ║
                ║    GET /                       → Welcome page                   ║
                ║    GET /api/info               → App & container info           ║
                ║    GET /api/health             → Health check                   ║
                ║    GET /api/environment        → Environment config             ║
                ║    GET /api/docker-reference   → Full Docker reference guide    ║
                ║    GET /actuator/health        → Spring Actuator health         ║
                ╚══════════════════════════════════════════════════════════════════╝
                """);
    }
}
