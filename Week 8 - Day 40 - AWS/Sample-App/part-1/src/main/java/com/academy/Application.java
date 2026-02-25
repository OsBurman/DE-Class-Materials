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

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.util.*;

// ─────────────────────────────────────────────
// AwsConfigChecker — checks if AWS is configured
// ─────────────────────────────────────────────
@Component
class AwsConfigChecker {

    @Value("${aws.region}")
    String region;

    @Value("${aws.s3.bucket}")
    String bucketName;

    boolean isConfigured() {
        String key = System.getenv("AWS_ACCESS_KEY_ID");
        return key != null && !key.isEmpty();
    }

    String getDemoNote() {
        return "DEMO MODE: Set AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, and AWS_REGION env vars for real AWS calls. " +
               "See https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html";
    }
}

// ─────────────────────────────────────────────
// AwsBeanConfig — creates AWS SDK clients
// ─────────────────────────────────────────────
@Configuration
class AwsBeanConfig {

    @Bean
    S3Client s3Client(@Value("${aws.region}") String region) {
        try {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    StsClient stsClient(@Value("${aws.region}") String region) {
        try {
            return StsClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}

// ─────────────────────────────────────────────
// S3DemoController — S3 endpoints
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api/aws/s3")
class S3DemoController {

    @Autowired(required = false)
    private S3Client s3Client;

    @Autowired
    private AwsConfigChecker configChecker;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @GetMapping("/buckets")
    public Map<String, Object> listBuckets() {
        if (!configChecker.isConfigured() || s3Client == null) {
            return Map.of(
                    "mode", "DEMO",
                    "demoData", List.of("academy-student-files", "academy-course-materials", "academy-backups"),
                    "note", configChecker.getDemoNote()
            );
        }
        try {
            List<Map<String, String>> buckets = new ArrayList<>();
            for (Bucket b : s3Client.listBuckets().buckets()) {
                buckets.add(Map.of(
                        "name", b.name(),
                        "creationDate", b.creationDate().toString()
                ));
            }
            return Map.of("mode", "LIVE", "buckets", buckets, "count", buckets.size());
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage(), "note", configChecker.getDemoNote());
        }
    }

    @GetMapping("/objects")
    public Map<String, Object> listObjects() {
        if (!configChecker.isConfigured() || s3Client == null) {
            return Map.of(
                    "mode", "DEMO",
                    "bucket", "demo-bucket",
                    "demoObjects", List.of(
                            Map.of("key", "courses/java-fundamentals.pdf", "size", "2.4 MB", "lastModified", "2024-01-15"),
                            Map.of("key", "students/spring-2024-roster.xlsx", "size", "156 KB"),
                            Map.of("key", "backups/db-backup-2024-01.sql.gz", "size", "45 MB")
                    ),
                    "note", configChecker.getDemoNote()
            );
        }
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(
                    ListObjectsV2Request.builder().bucket(bucketName).build()
            );
            List<Map<String, String>> objects = new ArrayList<>();
            for (S3Object obj : response.contents()) {
                objects.add(Map.of(
                        "key", obj.key(),
                        "size", obj.size() + " bytes",
                        "lastModified", obj.lastModified().toString()
                ));
            }
            return Map.of("mode", "LIVE", "bucket", bucketName, "objects", objects, "count", objects.size());
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage(), "note", configChecker.getDemoNote());
        }
    }

    @PostMapping("/upload-demo")
    public Map<String, Object> uploadDemo() {
        return Map.of(
                "mode", "DEMO",
                "uploadExample", Map.of(
                        "code", "s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromFile(file))",
                        "sdkOperation", "PutObjectRequest",
                        "presignedUrlExample", "s3Presigner.presignPutObject(request) generates a time-limited upload URL"
                )
        );
    }

    @GetMapping("/presigned-url-demo")
    public Map<String, Object> presignedUrlDemo() {
        return Map.of(
                "concept", "Presigned URL",
                "description", "A temporary URL granting time-limited access to an S3 object without needing AWS credentials",
                "useCases", List.of(
                        "Allow users to download files directly from S3 without going through your server",
                        "Allow uploads directly to S3 from browser",
                        "Share private files temporarily"
                ),
                "expirationNote", "Can set expiration from seconds to 7 days",
                "securityNote", "URL contains signature - expires after set time"
        );
    }
}

// ─────────────────────────────────────────────
// AwsIdentityController — STS identity endpoint
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api/aws")
class AwsIdentityController {

    @Autowired(required = false)
    private StsClient stsClient;

    @Autowired
    private AwsConfigChecker configChecker;

    @GetMapping("/identity")
    public Map<String, Object> getIdentity() {
        if (!configChecker.isConfigured() || stsClient == null) {
            return Map.of(
                    "mode", "DEMO",
                    "demoIdentity", Map.of(
                            "userId", "AIDACKCEVSQ6C2EXAMPLE",
                            "account", "123456789012",
                            "arn", "arn:aws:iam::123456789012:user/alice"
                    ),
                    "note", configChecker.getDemoNote()
            );
        }
        try {
            GetCallerIdentityResponse identity = stsClient.getCallerIdentity();
            return Map.of(
                    "mode", "LIVE",
                    "account", identity.account(),
                    "userId", identity.userId(),
                    "arn", identity.arn()
            );
        } catch (Exception e) {
            return Map.of("mode", "ERROR", "error", e.getMessage(), "note", configChecker.getDemoNote());
        }
    }
}

// ─────────────────────────────────────────────
// AwsReferenceController — AWS reference guide
// ─────────────────────────────────────────────
@RestController
@RequestMapping("/api")
class AwsReferenceController {

    @GetMapping("/aws-reference")
    public Map<String, Object> awsReference() {
        List<Map<String, Object>> coreServices = List.of(
                Map.of(
                        "service", "EC2 (Elastic Compute Cloud)",
                        "category", "Compute",
                        "description", "Virtual servers in the cloud. You manage OS, runtime, app.",
                        "analogyToLocal", "Like a VPS/dedicated server you SSH into",
                        "whenToUse", "Full control needed, legacy apps, custom OS requirements"
                ),
                Map.of(
                        "service", "ECS (Elastic Container Service)",
                        "category", "Compute",
                        "description", "Run Docker containers without managing servers (Fargate mode)",
                        "analogyToLocal", "Like Kubernetes but simpler, AWS-managed",
                        "whenToUse", "Containerized apps, microservices"
                ),
                Map.of(
                        "service", "Elastic Beanstalk",
                        "category", "Compute",
                        "description", "PaaS — upload your JAR, AWS handles EC2, load balancer, auto-scaling, deployment",
                        "analogyToLocal", "Like Heroku — easiest AWS option for Spring Boot",
                        "whenToUse", "Simplest way to deploy Spring Boot, rapid prototyping"
                ),
                Map.of(
                        "service", "Lambda",
                        "category", "Serverless",
                        "description", "Run code in response to events without managing servers. Pay per invocation.",
                        "analogyToLocal", "A single function that runs on demand",
                        "whenToUse", "Event handlers, data processing, scheduled jobs, microservices"
                ),
                Map.of(
                        "service", "S3 (Simple Storage Service)",
                        "category", "Storage",
                        "description", "Object storage. Store any file (documents, images, backups, static websites).",
                        "keyFeatures", List.of(
                                "Virtually unlimited storage",
                                "Versioning",
                                "Lifecycle policies",
                                "Presigned URLs for temporary access",
                                "Static website hosting"
                        ),
                        "whenToUse", "File storage for any application"
                ),
                Map.of(
                        "service", "RDS (Relational Database Service)",
                        "category", "Database",
                        "description", "Managed relational database. Supports MySQL, PostgreSQL, Oracle, SQL Server, Aurora.",
                        "benefits", List.of(
                                "Automated backups",
                                "Multi-AZ high availability",
                                "Read replicas for scaling",
                                "Automated patching"
                        ),
                        "springConfig", "spring.datasource.url=jdbc:postgresql://rds-endpoint.amazonaws.com:5432/mydb"
                ),
                Map.of(
                        "service", "DynamoDB",
                        "category", "Database",
                        "description", "Serverless NoSQL database. Single-digit millisecond latency at any scale.",
                        "whenToUse", "High-throughput apps, session storage, leaderboards, IoT"
                ),
                Map.of(
                        "service", "ElastiCache",
                        "category", "Database",
                        "description", "Managed Redis/Memcached. Caching layer for your application.",
                        "whenToUse", "Session caching, rate limiting, leaderboards, pub/sub"
                ),
                Map.of(
                        "service", "SQS (Simple Queue Service)",
                        "category", "Messaging",
                        "description", "Managed message queue. Decouple services with async messages.",
                        "comparedToKafka", "SQS: simpler, no consumer groups, messages deleted after consumption. Kafka: higher throughput, message replay, consumer groups."
                ),
                Map.of(
                        "service", "SNS (Simple Notification Service)",
                        "category", "Messaging",
                        "description", "Pub/Sub messaging. Fanout to multiple SQS queues, Lambda, HTTP endpoints, email."
                ),
                Map.of(
                        "service", "API Gateway",
                        "category", "Networking",
                        "description", "Managed API front door. Authentication, rate limiting, routing to Lambda/ECS."
                ),
                Map.of(
                        "service", "CloudWatch",
                        "category", "Monitoring",
                        "description", "Metrics, logs, alarms. Monitor your application health and set alerts."
                ),
                Map.of(
                        "service", "IAM (Identity and Access Management)",
                        "category", "Security",
                        "description", "Users, roles, policies. Control who can access what in AWS."
                )
        );

        Map<String, Object> deployingSpringBoot = Map.of(
                "elasticBeanstalk", Map.of(
                        "steps", List.of(
                                "1. Package: mvn package",
                                "2. Create EB application: eb init",
                                "3. Create environment: eb create",
                                "4. Deploy: eb deploy",
                                "5. Open: eb open"
                        ),
                        "envVars", "Set via EB console or eb setenv KEY=value",
                        "advantages", "Auto-provisions load balancer, auto-scaling, CloudWatch — zero server management"
                ),
                "ecs", Map.of(
                        "steps", List.of(
                                "1. Build Docker image",
                                "2. Push to ECR (Elastic Container Registry)",
                                "3. Create ECS Task Definition (container spec)",
                                "4. Create ECS Service (manages running tasks)",
                                "5. Add Application Load Balancer for traffic routing"
                        ),
                        "command", "aws ecr get-login-password | docker login --username AWS --password-stdin <account>.dkr.ecr.region.amazonaws.com"
                ),
                "ec2", Map.of(
                        "steps", List.of(
                                "1. Launch EC2 instance (Ubuntu, t2.micro for free tier)",
                                "2. SSH in: ssh -i key.pem ubuntu@<ip>",
                                "3. Install Java 17: sudo apt install openjdk-17-jre",
                                "4. Copy JAR: scp app.jar ubuntu@<ip>:~",
                                "5. Run: java -jar app.jar"
                        ),
                        "note", "Good for learning, but prefer ECS/Beanstalk for production"
                )
        );

        Map<String, Object> awsSdkJava = Map.of(
                "version", "AWS SDK for Java v2 (software.amazon.awssdk)",
                "credentials", "DefaultCredentialsProvider checks: env vars → ~/.aws/credentials → EC2 instance role",
                "pattern", "ServiceClient.builder().region(Region.US_EAST_1).credentialsProvider(DefaultCredentialsProvider.create()).build()",
                "s3Example", "s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.fromFile(file))"
        );

        List<String> iamBestPractices = List.of(
                "Principle of least privilege: grant minimum permissions needed",
                "Use IAM Roles (not access keys) for EC2/ECS workloads",
                "Rotate access keys regularly",
                "Never commit AWS credentials to Git",
                "Use environment variables or ~/.aws/credentials for local dev",
                "Use AWS Secrets Manager for database passwords"
        );

        return Map.of(
                "title", "AWS Reference Guide for Java Developers",
                "coreServices", coreServices,
                "deployingSpringBoot", deployingSpringBoot,
                "awsSdkJava", awsSdkJava,
                "iamBestPractices", iamBestPractices
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
    CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("==========================");
            System.out.println("AWS Overview Demo");
            System.out.println("==========================");
            System.out.println("Runs WITHOUT AWS credentials (DEMO MODE)");
            System.out.println("Set AWS env vars for real AWS calls:");
            System.out.println("  export AWS_ACCESS_KEY_ID=...");
            System.out.println("  export AWS_SECRET_ACCESS_KEY=...");
            System.out.println("  export AWS_REGION=us-east-1");
            System.out.println("  export S3_BUCKET_NAME=your-bucket");
            System.out.println();
            System.out.println("Endpoints:");
            System.out.println("  GET /api/aws/s3/buckets       - List S3 buckets");
            System.out.println("  GET /api/aws/s3/objects       - List bucket objects");
            System.out.println("  GET /api/aws/s3/presigned-url-demo - Presigned URL concept");
            System.out.println("  GET /api/aws/identity         - Check AWS identity (STS)");
            System.out.println("  GET /api/aws-reference        - Full AWS services guide");
            System.out.println("==========================");
        };
    }
}
