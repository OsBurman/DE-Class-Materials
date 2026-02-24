# Exercise 11: Elastic Beanstalk — Application Deployment

## Objective

Deploy a Spring Boot application to AWS Elastic Beanstalk using the `eb` CLI. Understand how Beanstalk manages EC2, load balancers, and deployments as a PaaS layer on top of AWS infrastructure.

## Background

**AWS Elastic Beanstalk** is a PaaS that lets you deploy applications without managing infrastructure. You upload your code (JAR, WAR, ZIP, or Docker image), tell Beanstalk which platform to use (Java 21, Node.js, Python, Docker, etc.), and it provisions EC2 instances, Auto Scaling Groups, an Elastic Load Balancer, and CloudWatch alarms automatically. You retain full access to the underlying resources but don't have to configure them manually.

## Requirements

### Part 1 — Beanstalk Concepts

1. What is the relationship between an **Application**, an **Environment**, and an **Application Version** in Elastic Beanstalk?

2. Explain each deployment policy:
   - All at once | Rolling | Rolling with additional batch | Immutable | Blue/Green

3. When would you use **immutable** vs **rolling** deployment?

### Part 2 — Deploy a Spring Boot Application (CLI)

You have `order-service-1.0.jar`. Write the commands to:

a. **Initialize** the Beanstalk application (`order-service-app`, platform `Java 21`, region `us-east-1`)

b. **Create environment** `order-service-prod` — single instance, `t3.small`

c. **Deploy** the JAR

d. **Open** in browser

e. **Check health** and list events

f. **Update** env var `APP_VERSION=2.0` and re-deploy

g. **Terminate** the environment

### Part 3 — .ebextensions Config

Create `.ebextensions/env-config.config` that:
- Sets `JAVA_TOOL_OPTIONS=-Xmx512m`
- Configures health check path to `/actuator/health`

### Part 4 — Reflection Questions

1. What does Beanstalk automate that you'd configure manually with raw EC2?
2. When choose Beanstalk over ECS/Fargate or EKS?
3. What is `.elasticbeanstalk/config.yml`? Does it belong in version control?
4. Your app crashes every 6h (memory leak). How does Beanstalk handle it? How to investigate?

## Hints

- `eb init` flags: `--platform "java-21" --region us-east-1`
- `eb create --single` = single instance (no ELB)
- `.ebextensions/*.config` uses the same YAML syntax as CloudFormation option settings

## Expected Output

```
Part 1: 3 concept answers
Part 2: eb CLI commands
Part 3: .ebextensions/env-config.config YAML
Part 4: 4 reflection answers
```
