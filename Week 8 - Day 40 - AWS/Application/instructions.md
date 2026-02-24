# Day 40 Application — AWS: Deploy to the Cloud

## Overview

Deploy the **Task Management API** to AWS using Elastic Beanstalk (or EC2), with an RDS PostgreSQL database, S3 for file storage, and CloudWatch for monitoring.

---

## Learning Goals

- Understand core AWS services (EC2, RDS, S3, IAM, CloudWatch)
- Deploy a Spring Boot app to Elastic Beanstalk
- Connect to AWS RDS PostgreSQL
- Store and retrieve files from S3 using the AWS SDK
- Set up CloudWatch log groups and alarms
- Use IAM roles (not hardcoded credentials)

---

## Prerequisites

- AWS account (free tier)
- AWS CLI installed and configured (`aws configure`)
- `awsebcli` installed (`pip install awsebcli`)
- Day 25 or Day 36 Task Management API

---

## Part 1 — Prepare the App

**Task 1 — `application-prod.yml`**  
Update to use environment variables for all secrets:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate.ddl-auto: update

aws:
  s3:
    bucket-name: ${AWS_S3_BUCKET}
  region: ${AWS_REGION:us-east-1}
```

**Task 2 — Add AWS SDK**  
Add to `pom.xml`:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
</dependency>
```

---

## Part 2 — S3 File Service

**Task 3 — `S3Service.java`**  
```java
@Service
public class S3Service {

    private final S3Client s3Client;
    @Value("${aws.s3.bucket-name}") private String bucketName;

    public String uploadFile(String key, byte[] content, String contentType) {
        // TODO: PutObjectRequest + s3Client.putObject()
        // TODO: return the S3 URL: https://{bucket}.s3.{region}.amazonaws.com/{key}
    }

    public byte[] downloadFile(String key) {
        // TODO: GetObjectRequest + s3Client.getObjectAsBytes()
    }

    public void deleteFile(String key) {
        // TODO: DeleteObjectRequest
    }
}
```

**Task 4 — `AttachmentController`**  
```
POST /api/tasks/{id}/attachments   — upload file to S3, save URL in task
GET  /api/tasks/{id}/attachments   — list attachment URLs
DELETE /api/tasks/{id}/attachments/{key} — delete from S3
```

---

## Part 3 — RDS Setup

**Task 5 — `aws-setup.md`**  
Document the steps (AWS Console or CLI):
1. Create RDS PostgreSQL 15 instance (Free Tier: db.t3.micro)
2. Set DB name, username, password
3. Configure security group: allow port 5432 from Elastic Beanstalk security group only
4. Note the endpoint URL

---

## Part 4 — Elastic Beanstalk Deploy

**Task 6 — `Procfile`**  
```
web: java -jar target/tasks-api-0.0.1-SNAPSHOT.jar
```

**Task 7 — `.ebextensions/environment.config`**  
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    SPRING_PROFILES_ACTIVE: prod
    # NOTE: Do NOT put secrets here — use Parameter Store or EB environment variables UI
```

**Task 8 — Deploy Commands** (document in `aws-setup.md`):
```bash
# TODO: eb init
# TODO: eb create tasks-api-staging --instance-type t3.micro
# TODO: eb setenv DATABASE_URL=jdbc:postgresql://... DATABASE_USERNAME=... DATABASE_PASSWORD=...
# TODO: eb deploy
# TODO: eb open (opens browser to deployed URL)
# TODO: curl https://your-env.elasticbeanstalk.com/actuator/health
```

---

## Part 5 — CloudWatch

**Task 9**  
In `aws-setup.md`, document:
1. How to view application logs in CloudWatch Logs
2. How to create an alarm: trigger when HTTP 5xx errors > 5 in 5 minutes
3. What CloudWatch metrics you would monitor for a production API

---

## Part 6 — IAM Best Practices

**Task 10 — `iam-notes.md`**  
- Never use root account credentials
- Create an IAM user with minimal permissions (AmazonS3FullAccess + AmazonRDSFullAccess)
- For EB: use IAM role attached to EC2 instance (no access keys in environment variables)
- Explain the difference between IAM User, IAM Role, and IAM Policy

---

## Submission Checklist

- [ ] App connects to RDS (not H2) in prod profile
- [ ] `S3Service` can upload, download, delete files
- [ ] Attachment endpoints work end-to-end
- [ ] `aws-setup.md` documents RDS + EB deploy steps
- [ ] App accessible via Elastic Beanstalk URL
- [ ] `iam-notes.md` explains IAM best practices
- [ ] CloudWatch alarm created for 5xx errors
