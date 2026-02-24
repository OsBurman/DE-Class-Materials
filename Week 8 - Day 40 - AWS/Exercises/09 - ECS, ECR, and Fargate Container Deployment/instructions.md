# Exercise 09: ECS, ECR, and Fargate — Container Deployment on AWS

## Objective

Push a Docker image to Amazon ECR, define an ECS task, and deploy a containerized service using Fargate (serverless containers — no EC2 to manage).

## Background

**Amazon ECR (Elastic Container Registry)** is a managed Docker image registry — like Docker Hub but private and integrated with IAM. **Amazon ECS (Elastic Container Service)** is AWS's container orchestration platform. **AWS Fargate** is the serverless compute engine for ECS — you define the task CPU/memory and Fargate runs it without you provisioning EC2 instances. Together they form the standard AWS-native way to run containers in production.

## Requirements

### Part 1 — Push an Image to ECR

Using the AWS CLI and Docker, complete the following steps:

a. **Create an ECR repository** named `order-service`

b. **Authenticate Docker** to your ECR registry using `aws ecr get-login-password`

c. **Build a Docker image** from the current directory:
   ```bash
   docker build -t order-service .
   ```

d. **Tag the image** with the full ECR URI format:
   `<account-id>.dkr.ecr.<region>.amazonaws.com/order-service:latest`

e. **Push the image** to ECR

f. **List images** in the repository to confirm the push succeeded

### Part 2 — ECS Cluster and Fargate Task

a. **Create an ECS cluster** named `app-cluster` using the FARGATE capacity provider

b. **Register a task definition** (`order-service-task`) with:
   - Launch type: FARGATE
   - CPU: 256 (.25 vCPU)
   - Memory: 512 MB
   - Container: `order-service`, port 8080, using the ECR image URI from Part 1
   - Log driver: `awslogs` → CloudWatch log group `/ecs/order-service`
   - Task execution role: `ecsTaskExecutionRole`

c. **Create an ECS service** that:
   - Runs 2 tasks (desired count)
   - Uses FARGATE launch type
   - Deploys into a VPC subnet (use your default VPC)
   - Assigns a public IP so the container is reachable

d. **List running tasks** and get the public IP of one task

### Part 3 — Reflection Questions

Answer the following:

1. What is the difference between an **ECS Task Definition** and an **ECS Service**?

2. **Fargate vs EC2 launch type** — what does Fargate abstract away? When would you choose EC2 launch type instead?

3. What is the **task execution role** vs the **task role** in ECS? Give an example of what each is used for.

4. Your ECS service is running 2 tasks. One task crashes (OOM). What does ECS do automatically? How does this compare to running Docker directly on EC2?

5. You want to zero-downtime deploy a new container version. What ECS deployment configuration enables this?

## Hints

- The ECR login command: `aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account-id>.dkr.ecr.<region>.amazonaws.com`
- Task definitions are JSON. Use `aws ecs register-task-definition --cli-input-json file://task-def.json`.
- Fargate tasks need `networkMode: awsvpc` and a subnet/security group at service creation.
- To get a task's public IP: `aws ecs describe-tasks` → `networkInterfaceId` → `aws ec2 describe-network-interfaces`.

## Expected Output

```
Part 1: CLI commands to create ECR repo, authenticate, build, tag, push, verify
Part 2: CLI commands + task definition JSON for cluster, task def, service, status
Part 3: 5 reflection answers
```
