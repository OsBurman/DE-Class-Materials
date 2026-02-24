#!/bin/bash
# Exercise 09 — ECS, ECR & Fargate: Starter Script
# Fill in all TODO sections.

set -e

REGION="us-east-1"
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
REPO_NAME="order-service"
ECR_URI="$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$REPO_NAME"
CLUSTER_NAME="app-cluster"
TASK_FAMILY="order-service-task"
SERVICE_NAME="order-service-svc"

# ============================================================
# PART 1 — Push Image to ECR
# ============================================================

# a. Create ECR repository
# TODO

# b. Authenticate Docker to ECR
# TODO

# c. Build Docker image
# TODO

# d. Tag image with ECR URI
# TODO

# e. Push image to ECR
# TODO

# f. List images in repository
# TODO

# ============================================================
# PART 2 — ECS Cluster and Fargate Task
# ============================================================

# a. Create ECS cluster with FARGATE capacity provider
# TODO

# b. Write task definition JSON and register it
cat > task-def.json << 'EOF'
{
  "family": "TODO",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "TODO",
  "memory": "TODO",
  "executionRoleArn": "arn:aws:iam::ACCOUNT_ID:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "TODO",
      "image": "ECR_URI:latest",
      "portMappings": [{ "containerPort": 8080, "protocol": "tcp" }],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "TODO",
          "awslogs-region": "TODO",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "essential": true
    }
  ]
}
EOF

# TODO: Register the task definition
# aws ecs register-task-definition --cli-input-json file://task-def.json

# c. Create ECS service (Fargate, 2 tasks, public IP)
SUBNET_ID=$(aws ec2 describe-subnets \
  --filters "Name=default-for-az,Values=true" \
  --query "Subnets[0].SubnetId" --output text)

SG_ID=$(aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=default" \
  --query "SecurityGroups[0].GroupId" --output text)

# TODO: Create ECS service
# aws ecs create-service ...

# d. List tasks and get public IP
# TODO
