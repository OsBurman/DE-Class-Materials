#!/bin/bash
# Exercise 09 — ECS, ECR & Fargate: SOLUTION

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
aws ecr create-repository --repository-name $REPO_NAME --region $REGION

# b. Authenticate Docker to ECR
aws ecr get-login-password --region $REGION \
  | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

# c. Build Docker image (Dockerfile in current directory)
docker build -t $REPO_NAME .

# d. Tag image with full ECR URI
docker tag $REPO_NAME:latest $ECR_URI:latest

# e. Push image to ECR
docker push $ECR_URI:latest

# f. List images in repository
aws ecr list-images --repository-name $REPO_NAME --region $REGION

# ============================================================
# PART 2 — ECS Cluster and Fargate Task
# ============================================================

# a. Create ECS cluster with FARGATE capacity provider
aws ecs create-cluster \
  --cluster-name $CLUSTER_NAME \
  --capacity-providers FARGATE FARGATE_SPOT \
  --default-capacity-provider-strategy capacityProvider=FARGATE,weight=1

# Create CloudWatch log group for container logs
aws logs create-log-group --log-group-name /ecs/order-service --region $REGION

# b. Write and register task definition
cat > task-def.json << EOF
{
  "family": "$TASK_FAMILY",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::$ACCOUNT_ID:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "order-service",
      "image": "$ECR_URI:latest",
      "portMappings": [{ "containerPort": 8080, "protocol": "tcp" }],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/order-service",
          "awslogs-region": "$REGION",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "essential": true,
      "environment": [
        { "name": "SPRING_PROFILES_ACTIVE", "value": "prod" }
      ]
    }
  ]
}
EOF

aws ecs register-task-definition --cli-input-json file://task-def.json

# c. Create ECS service (Fargate, 2 tasks, public IP)
SUBNET_ID=$(aws ec2 describe-subnets \
  --filters "Name=default-for-az,Values=true" \
  --query "Subnets[0].SubnetId" --output text)

SG_ID=$(aws ec2 describe-security-groups \
  --filters "Name=group-name,Values=default" \
  --query "SecurityGroups[0].GroupId" --output text)

aws ecs create-service \
  --cluster $CLUSTER_NAME \
  --service-name $SERVICE_NAME \
  --task-definition $TASK_FAMILY \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[$SUBNET_ID],securityGroups=[$SG_ID],assignPublicIp=ENABLED}" \
  --deployment-configuration "minimumHealthyPercent=50,maximumPercent=200"

aws ecs wait services-stable --cluster $CLUSTER_NAME --services $SERVICE_NAME

# d. List running tasks and get public IP
TASK_ARN=$(aws ecs list-tasks \
  --cluster $CLUSTER_NAME \
  --service-name $SERVICE_NAME \
  --query "taskArns[0]" --output text)

ENI_ID=$(aws ecs describe-tasks \
  --cluster $CLUSTER_NAME \
  --tasks $TASK_ARN \
  --query "tasks[0].attachments[0].details[?name=='networkInterfaceId'].value" \
  --output text)

PUBLIC_IP=$(aws ec2 describe-network-interfaces \
  --network-interface-ids $ENI_ID \
  --query "NetworkInterfaces[0].Association.PublicIp" \
  --output text)

echo "Task public IP: $PUBLIC_IP"
echo "Access service at: http://$PUBLIC_IP:8080"

# ============================================================
# REFLECTION ANSWERS
# ============================================================

# 1. Task Definition vs ECS Service:
#    Task Definition: a versioned JSON blueprint — image, CPU, memory, ports, env vars,
#    log config, IAM roles. Like a Dockerfile for the whole task runtime environment.
#    ECS Service: the runtime controller that keeps N copies of the task running.
#    Handles replacement of failed tasks, load balancer registration, and rolling deploys.

# 2. Fargate vs EC2 launch type:
#    Fargate abstracts: EC2 instance selection, AMI management, cluster capacity planning,
#    OS patching, SSH access to hosts. You only care about CPU/memory per task.
#    Choose EC2 launch type for: GPU workloads, specific instance types for cost at scale,
#    Spot instance batch jobs, host-level mounts, or custom kernel/networking requirements.

# 3. Task execution role vs task role:
#    Execution role: used by the Fargate AGENT to set up the task — pull image from ECR,
#    write logs to CloudWatch, fetch Secrets Manager values at startup.
#    Task role: used by your APPLICATION CODE inside the container to call AWS services —
#    read from S3, query DynamoDB, publish to SNS. Give only the permissions your app needs.

# 4. One task crashes (OOM):
#    ECS detects the stopped task. The ALB health check deregisters it.
#    ECS automatically launches a replacement task to maintain desired count = 2.
#    On a bare EC2 with Docker: the container stays stopped until you manually restart it.
#    Docker restart policies help but don't span hosts or integrate with load balancers.

# 5. Zero-downtime rolling deployment:
#    ECS rolling update with deployment configuration:
#      minimumHealthyPercent=50 → ECS can have 50% of tasks down during rollout
#      maximumPercent=200        → ECS can run up to 2× desired tasks temporarily
#    New tasks launch with the new image → health checks pass → old tasks stop.
#    Add deploymentCircuitBreaker=ENABLED to auto-rollback if new tasks fail health checks.
