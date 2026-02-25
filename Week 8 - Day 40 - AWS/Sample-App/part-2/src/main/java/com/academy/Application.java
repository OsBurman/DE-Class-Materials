package com.academy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.InetAddress;
import java.time.*;
import java.util.*;

// ─────────────────────────────────────────────
// DeploymentInfo — runtime environment details
// ─────────────────────────────────────────────
@Component
class DeploymentInfo {

    @Value("${app.version}")
    String version;

    @Value("${app.environment}")
    String environment;

    @Value("${aws.region}")
    String awsRegion;

    Map<String, Object> getInfo() {
        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {}

        String podName = System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "not-set";
        String ecsTaskId = System.getenv("ECS_CONTAINER_METADATA_URI") != null
                ? System.getenv("ECS_CONTAINER_METADATA_URI")
                : "not-ecs";
        // In a real EC2 deployment, you would call: http://169.254.169.254/latest/meta-data/instance-id
        String ec2InstanceId = "not-ec2";

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("version", version);
        info.put("environment", environment);
        info.put("awsRegion", awsRegion);
        info.put("hostname", hostname);
        info.put("podName", podName);
        info.put("ecsTaskId", ecsTaskId);
        info.put("ec2InstanceId", ec2InstanceId);
        info.put("startedAt", LocalDateTime.now().toString());
        return info;
    }
}

// ─────────────────────────────────────────────
// AwsBeanConfig — SQS and DynamoDB clients
// ─────────────────────────────────────────────
@Configuration
class AwsBeanConfig {

    @Bean
    SqsClient sqsClient(@Value("${aws.region}") String region) {
        try {
            return SqsClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    DynamoDbClient dynamoDbClient(@Value("${aws.region}") String region) {
        try {
            return DynamoDbClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}

// ─────────────────────────────────────────────
// SqsDemoController — SQS endpoints
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api/aws/sqs")
class SqsDemoController {

    @Autowired(required = false)
    private SqsClient sqsClient;

    @Value("${aws.sqs.queue-url:}")
    private String queueUrl;

    @GetMapping("/demo")
    public Map<String, Object> sqsDemo() {
        return Map.of(
                "service", "Amazon SQS (Simple Queue Service)",
                "whatItIs", "Managed message queue for decoupling distributed systems",
                "vsKafka", Map.of(
                        "sqs", List.of(
                                "Simple to use",
                                "Messages deleted after processing",
                                "No consumer groups",
                                "Max 14 day retention",
                                "Good for: simple async tasks, decoupling services"
                        ),
                        "kafka", List.of(
                                "Higher throughput",
                                "Message replay",
                                "Consumer groups",
                                "Long retention",
                                "Good for: event streaming, audit logs, microservices"
                        )
                ),
                "springIntegration", "spring-cloud-aws-messaging: @SqsListener('queue-name') for consumers",
                "queueTypes", Map.of(
                        "standard", "At-least-once delivery, best-effort ordering, high throughput",
                        "fifo", "Exactly-once delivery, strict ordering, lower throughput"
                ),
                "sdkExample", Map.of(
                        "sendMessage", "sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(url).messageBody(body).build())",
                        "receiveMessage", "sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(url).build())",
                        "deleteMessage", "sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(url).receiptHandle(handle).build())"
                )
        );
    }

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody(required = false) Map<String, String> body) {
        if (queueUrl == null || queueUrl.isEmpty() || sqsClient == null) {
            return Map.of(
                    "mode", "DEMO",
                    "message", "No SQS_QUEUE_URL configured. Set SQS_QUEUE_URL env var to send real messages.",
                    "wouldSend", body != null ? body : Map.of("message", "Hello from Spring Boot!")
            );
        }
        try {
            String messageBody = body != null
                    ? body.getOrDefault("message", "Hello from Spring Boot!")
                    : "Hello from Spring Boot!";
            SendMessageResponse response = sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build()
            );
            return Map.of("mode", "LIVE", "messageId", response.messageId(), "body", messageBody);
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage());
        }
    }

    @GetMapping("/receive")
    public Map<String, Object> receiveMessages() {
        if (queueUrl == null || queueUrl.isEmpty() || sqsClient == null) {
            return Map.of(
                    "mode", "DEMO",
                    "message", "No SQS_QUEUE_URL configured. Set SQS_QUEUE_URL env var to receive real messages.",
                    "demoMessages", List.of(
                            Map.of("messageId", "abc123", "body", "Process order #1001"),
                            Map.of("messageId", "def456", "body", "Send welcome email to user@example.com")
                    )
            );
        }
        try {
            ReceiveMessageResponse response = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(10)
                            .build()
            );
            List<Map<String, String>> messages = new ArrayList<>();
            for (Message msg : response.messages()) {
                messages.add(Map.of(
                        "messageId", msg.messageId(),
                        "body", msg.body(),
                        "receiptHandle", msg.receiptHandle()
                ));
            }
            return Map.of("mode", "LIVE", "messages", messages, "count", messages.size());
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage());
        }
    }
}

// ─────────────────────────────────────────────
// DynamoDbDemoController — DynamoDB endpoints
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api/aws/dynamodb")
class DynamoDbDemoController {

    @Autowired(required = false)
    private DynamoDbClient dynamoDbClient;

    @Value("${aws.dynamodb.table}")
    private String tableName;

    @GetMapping("/demo")
    public Map<String, Object> dynamoDemo() {
        return Map.of(
                "service", "Amazon DynamoDB",
                "whatItIs", "Serverless NoSQL key-value and document database. Single-digit millisecond performance at any scale.",
                "dataModel", Map.of(
                        "table", "Collection of items (like MongoDB collection)",
                        "item", "A single record (like MongoDB document)",
                        "primaryKey", Map.of(
                                "partitionKey", "Hash key — distributes data across partitions",
                                "sortKey", "Range key — sorts items within a partition (optional)"
                        ),
                        "attributes", "Flexible schema — items in same table can have different attributes"
                ),
                "vsRDS", Map.of(
                        "dynamo", Map.of(
                                "pros", List.of(
                                        "Serverless — no servers to manage",
                                        "Unlimited throughput with on-demand mode",
                                        "Built-in TTL for item expiration",
                                        "Global tables for multi-region"
                                ),
                                "cons", List.of(
                                        "No SQL joins",
                                        "Query limited by partition key",
                                        "Higher cost for complex queries"
                                )
                        ),
                        "rds", Map.of(
                                "pros", List.of(
                                        "Full SQL with joins",
                                        "Complex transactions",
                                        "Familiar for developers"
                                ),
                                "cons", List.of(
                                        "Must manage/scale servers",
                                        "Higher latency than DynamoDB"
                                )
                        )
                ),
                "sdkExample", Map.of(
                        "putItem", "dynamoDbClient.putItem(PutItemRequest.builder().tableName(table).item(Map.of(\"id\", AttributeValue.builder().s(id).build())).build())",
                        "getItem", "dynamoDbClient.getItem(GetItemRequest.builder().tableName(table).key(Map.of(\"id\", AttributeValue.builder().s(id).build())).build())"
                ),
                "springDataDynamoDB", "Use spring-data-dynamodb for repository pattern similar to JPA"
        );
    }

    @GetMapping("/items-demo")
    public Map<String, Object> listItems() {
        if (dynamoDbClient == null) {
            return Map.of(
                    "mode", "DEMO",
                    "table", tableName,
                    "mockItems", List.of(
                            Map.of("id", "EVT-001", "type", "student_enrollment", "student", "Alice Johnson",
                                    "course", "Spring Boot", "timestamp", "2024-01-15T10:30:00"),
                            Map.of("id", "EVT-002", "type", "assignment_submitted", "student", "Bob Smith",
                                    "course", "AWS", "timestamp", "2024-01-16T14:22:00"),
                            Map.of("id", "EVT-003", "type", "grade_posted", "student", "Carol White",
                                    "course", "Docker", "grade", "A", "timestamp", "2024-01-17T09:00:00")
                    ),
                    "note", "Set AWS credentials and create DynamoDB table '" + tableName + "' to see real data"
            );
        }
        try {
            ScanResponse response = dynamoDbClient.scan(
                    ScanRequest.builder().tableName(tableName).limit(20).build()
            );
            List<Map<String, String>> items = new ArrayList<>();
            for (Map<String, AttributeValue> item : response.items()) {
                Map<String, String> simplified = new LinkedHashMap<>();
                item.forEach((k, v) -> simplified.put(k, v.s() != null ? v.s() : v.toString()));
                items.add(simplified);
            }
            return Map.of("mode", "LIVE", "table", tableName, "items", items, "count", items.size());
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage(), "table", tableName);
        }
    }

    @PostMapping("/item")
    public Map<String, Object> putItem(@RequestBody(required = false) Map<String, String> body) {
        if (dynamoDbClient == null) {
            return Map.of(
                    "mode", "DEMO",
                    "message", "No DynamoDB connection. Set AWS credentials to write real items.",
                    "wouldWrite", body != null ? body : Map.of("id", "EVT-" + System.currentTimeMillis(), "type", "demo_event")
            );
        }
        try {
            String id = body != null
                    ? body.getOrDefault("id", "EVT-" + System.currentTimeMillis())
                    : "EVT-" + System.currentTimeMillis();
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(id).build());
            item.put("timestamp", AttributeValue.builder().s(Instant.now().toString()).build());
            if (body != null) {
                body.forEach((k, v) -> {
                    if (!k.equals("id")) item.put(k, AttributeValue.builder().s(v).build());
                });
            }
            dynamoDbClient.putItem(PutItemRequest.builder().tableName(tableName).item(item).build());
            return Map.of("mode", "LIVE", "message", "Item written successfully", "id", id, "table", tableName);
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage());
        }
    }
}

// ─────────────────────────────────────────────
// AwsDeploymentReferenceController — deployment reference
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class AwsDeploymentReferenceController {

    @Autowired
    private DeploymentInfo deploymentInfo;

    @GetMapping("/deployment-info")
    public Map<String, Object> getDeploymentInfo() {
        return deploymentInfo.getInfo();
    }

    @GetMapping("/aws-deployment-reference")
    public Map<String, Object> awsDeploymentReference() {
        Map<String, Object> healthChecks = Map.of(
                "why", "AWS load balancers and ECS check health endpoints to know if your app is ready",
                "endpoints", Map.of(
                        "health", "/actuator/health — overall health",
                        "liveness", "/actuator/health/liveness — is app running? ECS restarts if failing",
                        "readiness", "/actuator/health/readiness — is app ready for traffic? ALB removes from rotation if failing"
                ),
                "springBootConfig", "management.health.livenessstate.enabled=true and management.health.readinessstate.enabled=true"
        );

        Map<String, Object> environmentConfiguration = Map.of(
                "principle", "12-Factor App: config in environment variables, not code",
                "springBoot", "Use ${ENV_VAR:default} in application.properties to read env vars with defaults",
                "awsParameterStore", "spring-cloud-aws-parameter-store: centralize config in SSM Parameter Store",
                "awsSecretsManager", "Store DB passwords, API keys. spring-cloud-aws-secrets-manager auto-injects secrets."
        );

        Map<String, Object> containerization = Map.of(
                "dockerfile", "Multi-stage builds: small runtime image (~100MB vs 500MB with JDK)",
                "jvmFlags", "-XX:+UseContainerSupport (respects container memory limits) -XX:MaxRAMPercentage=75.0",
                "springProfiles", "SPRING_PROFILES_ACTIVE=prod env var activates production profile"
        );

        Map<String, Object> ecsDeployment = Map.of(
                "steps", List.of(
                        "1. Push image to ECR: aws ecr get-login-password | docker login ...",
                        "2. Create Task Definition: container image, CPU, memory, env vars, health check",
                        "3. Create Service: desired count, load balancer, subnet",
                        "4. Update Service to deploy new image version"
                ),
                "environmentVariables", "Inject via Task Definition (plaintext) or Secrets Manager (sensitive)",
                "autoScaling", "ECS Service Auto Scaling based on CPU, memory, or custom CloudWatch metric"
        );

        Map<String, Object> elasticBeanstalk = Map.of(
                "whatItManages", "EC2 instances, load balancer, auto-scaling, CloudWatch monitoring, deployment",
                "procfile", "Procfile: web: java -jar app.jar --server.port=5000 (EB expects port 5000)",
                "envVars", "Set in EB console > Configuration > Software > Environment properties",
                "deployment", "eb deploy uploads new version, performs rolling or blue-green deploy"
        );

        Map<String, Object> monitoring = Map.of(
                "cloudWatch", "Automatically receives EC2/ECS metrics. Spring Boot Actuator metrics → CloudWatch with micrometer-registry-cloudwatch",
                "logs", "ECS: configure log driver awslogs in Task Definition. Logs go to CloudWatch Logs.",
                "alarms", "CloudWatch Alarms on CPU > 80%, error rate spike, p99 latency threshold"
        );

        return Map.of(
                "title", "AWS Deployment Patterns for Spring Boot",
                "healthChecks", healthChecks,
                "environmentConfiguration", environmentConfiguration,
                "containerization", containerization,
                "ecsDeployment", ecsDeployment,
                "elasticBeanstalk", elasticBeanstalk,
                "monitoring", monitoring
        );
    }
}

// ─────────────────────────────────────────────
// Application — Spring Boot entry point
// ─────────────────────────────────────────────
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner startupBanner(DeploymentInfo deploymentInfo) {
        return args -> {
            boolean awsConfigured = System.getenv("AWS_ACCESS_KEY_ID") != null
                    && !System.getenv("AWS_ACCESS_KEY_ID").isEmpty();
            System.out.println("==========================");
            System.out.println("AWS Deployment Patterns Demo");
            System.out.println("==========================");
            System.out.println("Mode: " + (awsConfigured ? "LIVE (AWS credentials detected)" : "DEMO (no AWS credentials)"));
            System.out.println("Environment: " + deploymentInfo.environment + " | Version: " + deploymentInfo.version);
            System.out.println();
            System.out.println("Set env vars for real AWS calls:");
            System.out.println("  export AWS_ACCESS_KEY_ID=...");
            System.out.println("  export AWS_SECRET_ACCESS_KEY=...");
            System.out.println("  export AWS_REGION=us-east-1");
            System.out.println("  export SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/...");
            System.out.println("  export DYNAMODB_TABLE=academy-events");
            System.out.println();
            System.out.println("Endpoints:");
            System.out.println("  GET  /api/aws/sqs/demo            - SQS concept guide");
            System.out.println("  POST /api/aws/sqs/send            - Send SQS message");
            System.out.println("  GET  /api/aws/sqs/receive         - Receive SQS messages");
            System.out.println("  GET  /api/aws/dynamodb/demo       - DynamoDB concept guide");
            System.out.println("  GET  /api/aws/dynamodb/items-demo - List DynamoDB items");
            System.out.println("  POST /api/aws/dynamodb/item       - Write DynamoDB item");
            System.out.println("  GET  /api/deployment-info         - Runtime environment info");
            System.out.println("  GET  /api/aws-deployment-reference - Full deployment guide");
            System.out.println("  GET  /actuator/health             - Health check");
            System.out.println("  GET  /actuator/health/liveness    - Liveness probe");
            System.out.println("  GET  /actuator/health/readiness   - Readiness probe");
            System.out.println("==========================");
        };
    }
}
