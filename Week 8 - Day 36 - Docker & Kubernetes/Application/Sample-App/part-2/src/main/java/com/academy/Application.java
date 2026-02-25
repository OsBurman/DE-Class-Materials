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

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ─── Kubernetes Info Component ────────────────────────────────────────────────

@Component
class KubernetesInfo {

    @Value("${app.config.message:Hello from Kubernetes Demo!}")
    private String appMessage;

    @Value("${app.config.environment:local}")
    private String appEnvironment;

    @Value("${server.port:8080}")
    private String serverPort;

    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("message", appMessage);
        info.put("environment", appEnvironment);
        info.put("port", serverPort);
        info.put("javaVersion", System.getProperty("java.version"));

        try {
            info.put("hostname", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            info.put("hostname", "unknown");
        }

        // In Kubernetes, the Pod's hostname is the Pod name (from the HOSTNAME env var)
        String podName = System.getenv("HOSTNAME");
        info.put("podName", podName != null ? podName : "local");

        // POD_NAMESPACE and NODE_IP can be injected via Downward API in deployment.yaml
        String namespace = System.getenv("POD_NAMESPACE");
        info.put("namespace", namespace != null ? namespace : "unknown");

        String nodeIp = System.getenv("NODE_IP");
        info.put("nodeIp", nodeIp != null ? nodeIp : "local");

        return info;
    }
}

// ─── App Controller ───────────────────────────────────────────────────────────

@RestController
@RequiredArgsConstructor
class AppController {

    private final KubernetesInfo kubernetesInfo;

    @Value("${app.config.message:Hello from Kubernetes Demo!}")
    private String appMessage;

    // ── GET / ─────────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Kubernetes Demo</title>
                  <style>
                    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; background: #f9f9f9; }
                    h1   { color: #326ce5; }
                    h2   { color: #384d54; margin-top: 32px; }
                    code { background: #eef; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
                    .card { background: #fff; border: 1px solid #ddd; border-radius: 8px; padding: 16px; margin: 10px 0; }
                    a    { color: #326ce5; }
                  </style>
                </head>
                <body>
                  <h1>☸️ Kubernetes Demo Application</h1>
                  <p>A Spring Boot app demonstrating Kubernetes orchestration concepts for developer bootcamp students.</p>

                  <h2>Key Kubernetes Concepts</h2>
                  <div class="card">
                    <strong>Pod</strong> — The smallest deployable unit. One or more containers sharing network and storage.
                    Never create Pods directly — use a Deployment.
                  </div>
                  <div class="card">
                    <strong>Deployment</strong> — Manages Pods declaratively. Handles rolling updates and rollbacks.
                    Kubernetes continuously reconciles actual state toward the desired state.
                  </div>
                  <div class="card">
                    <strong>Service</strong> — A stable network endpoint for a set of Pods. Provides load balancing.
                    Pods come and go; the Service IP stays the same.
                  </div>
                  <div class="card">
                    <strong>ConfigMap</strong> — Stores non-sensitive configuration data injected as environment variables.
                    Change config without rebuilding your image.
                  </div>
                  <div class="card">
                    <strong>HPA</strong> — HorizontalPodAutoscaler scales replicas up/down based on CPU or memory metrics.
                  </div>

                  <h2>API Endpoints</h2>
                  <ul>
                    <li><a href="/api/info">/api/info</a> — Pod &amp; cluster info</li>
                    <li><a href="/api/message">/api/message</a> — Configured message (from ConfigMap)</li>
                    <li><a href="/api/kubernetes-reference">/api/kubernetes-reference</a> — Full Kubernetes reference guide</li>
                    <li><a href="/actuator/health/liveness">/actuator/health/liveness</a> — Liveness probe endpoint</li>
                    <li><a href="/actuator/health/readiness">/actuator/health/readiness</a> — Readiness probe endpoint</li>
                  </ul>
                </body>
                </html>
                """;
    }

    // ── GET /api/info ─────────────────────────────────────────────────────────

    @GetMapping("/api/info")
    public Map<String, Object> info() {
        return kubernetesInfo.getInfo();
    }

    // ── GET /api/message ──────────────────────────────────────────────────────

    @GetMapping("/api/message")
    public Map<String, String> message() {
        return Map.of("message", appMessage);
    }

    // ── GET /api/kubernetes-reference ─────────────────────────────────────────

    @GetMapping("/api/kubernetes-reference")
    public Map<String, Object> kubernetesReference() {
        Map<String, Object> ref = new LinkedHashMap<>();

        ref.put("title", "Kubernetes Reference Guide");

        ref.put("coreObjects", List.of(
                Map.of(
                        "name", "Pod",
                        "description", "Smallest deployable unit. One or more containers sharing network/storage.",
                        "note", "Don't create Pods directly — use Deployments"
                ),
                Map.of(
                        "name", "Deployment",
                        "description", "Manages Pods declaratively. Handles rolling updates and rollbacks.",
                        "keyFields", List.of("replicas", "selector", "template (pod spec)", "strategy (RollingUpdate/Recreate)")
                ),
                Map.of(
                        "name", "Service",
                        "description", "Stable network endpoint for a set of Pods",
                        "types", List.of(
                                "ClusterIP (cluster-internal only)",
                                "NodePort (accessible on node port)",
                                "LoadBalancer (cloud load balancer)"
                        )
                ),
                Map.of(
                        "name", "ConfigMap",
                        "description", "Store non-sensitive configuration data as key-value pairs",
                        "usage", "Mount as env vars or files in pods"
                ),
                Map.of(
                        "name", "Secret",
                        "description", "Store sensitive data (passwords, tokens) — base64 encoded",
                        "warning", "Not encrypted by default, use sealed-secrets or external secrets for production"
                ),
                Map.of(
                        "name", "Namespace",
                        "description", "Virtual cluster for resource isolation",
                        "useCases", List.of("dev/staging/prod environments", "Team separation")
                ),
                Map.of(
                        "name", "HorizontalPodAutoscaler",
                        "description", "Automatically scale pods based on CPU/memory metrics"
                ),
                Map.of(
                        "name", "Ingress",
                        "description", "HTTP/HTTPS routing rules from outside the cluster to services"
                )
        ));

        ref.put("essentialKubectl", Map.of(
                "apply",    "kubectl apply -f deployment.yaml (create/update resources)",
                "get",      "kubectl get pods | kubectl get services | kubectl get all",
                "describe", "kubectl describe pod <pod-name> (detailed info)",
                "logs",     "kubectl logs <pod-name> | kubectl logs -f <pod-name> (follow)",
                "exec",     "kubectl exec -it <pod-name> -- sh (shell in pod)",
                "delete",   "kubectl delete -f deployment.yaml | kubectl delete pod <pod-name>",
                "scale",    "kubectl scale deployment kubernetes-demo --replicas=3",
                "rollout",  "kubectl rollout status deployment/kubernetes-demo | kubectl rollout undo deployment/kubernetes-demo"
        ));

        ref.put("localDevelopment", Map.of(
                "minikube", List.of(
                        "minikube start",
                        "eval $(minikube docker-env)  # use minikube's Docker daemon",
                        "docker build -t kubernetes-demo:latest .",
                        "kubectl apply -f k8s/",
                        "minikube service kubernetes-demo-service (open in browser)"
                ),
                "kindCluster", "kind create cluster --name academy"
        ));

        ref.put("healthProbes", Map.of(
                "liveness",  "Is the container alive? Restart if failing. Path: /actuator/health/liveness",
                "readiness", "Is the container ready to receive traffic? Remove from service if failing. Path: /actuator/health/readiness",
                "startup",   "Is the app still starting up? Give it time before liveness kicks in."
        ));

        ref.put("rollingUpdate", Map.of(
                "strategy",    "maxSurge: 1 (temporarily have 1 extra pod), maxUnavailable: 0 (never reduce below desired count)",
                "zeroDowntime", "Old pods stay until new pods are healthy — zero downtime deployments"
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
                ╔═══════════════════════════════════════════════════════════════════════╗
                ║                ☸️  Kubernetes Demo Application                       ║
                ╠═══════════════════════════════════════════════════════════════════════╣
                ║  Run locally:                                                        ║
                ║    mvn spring-boot:run                                               ║
                ║                                                                      ║
                ║  Build Docker image:                                                 ║
                ║    docker build -t kubernetes-demo:latest .                          ║
                ║                                                                      ║
                ║  Deploy to Kubernetes (minikube):                                    ║
                ║    minikube start                                                    ║
                ║    eval $(minikube docker-env)     # point to minikube Docker daemon ║
                ║    docker build -t kubernetes-demo:latest .                          ║
                ║    kubectl apply -f k8s/           # apply all manifests             ║
                ║    minikube service kubernetes-demo-service                          ║
                ║                                                                      ║
                ║  Useful commands:                                                    ║
                ║    kubectl get pods                                                  ║
                ║    kubectl get hpa kubernetes-demo-hpa --watch                      ║
                ║    kubectl rollout status deployment/kubernetes-demo                 ║
                ║                                                                      ║
                ║  API Endpoints:                                                      ║
                ║    GET /                          → Welcome page                     ║
                ║    GET /api/info                  → Pod & cluster info               ║
                ║    GET /api/message               → Configured message               ║
                ║    GET /api/kubernetes-reference  → Full K8s reference guide        ║
                ║    GET /actuator/health/liveness  → Liveness probe                  ║
                ║    GET /actuator/health/readiness → Readiness probe                 ║
                ╚═══════════════════════════════════════════════════════════════════════╝
                """);
    }
}
