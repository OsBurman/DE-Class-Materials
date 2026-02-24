#!/usr/bin/env bash
# =============================================================
# AWS Services & Deployment — CLI Reference
# Day 40 — Week 8 — Bookstore Platform on AWS
# Part 2: S3, Beanstalk, RDS, ECS, ECR, EKS, Lambda,
#          SNS, SQS, CloudWatch, IAM, DynamoDB, Fargate,
#          Full-Stack Deployment
# =============================================================
# PREREQUISITE: AWS CLI configured (see Part 1 script)
#   aws configure  →  set region to us-east-1
# =============================================================

export AWS_DEFAULT_REGION=us-east-1
export AWS_PAGER=""
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "=== Account: $ACCOUNT_ID | Region: $AWS_DEFAULT_REGION ==="

# ─────────────────────────────────────────────────────────────
# SECTION 1 — IAM (Identity and Access Management)
# Best practice: set up IAM FIRST, before anything else
# ─────────────────────────────────────────────────────────────

# ── 1a. IAM USERS ──
# Create a dedicated IAM user for the bookstore application (not root account)
aws iam create-user --user-name bookstore-app-user

# Attach policies to the user
# Option A: Managed policy (easier, less precise)
aws iam attach-user-policy \
    --user-name bookstore-app-user \
    --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

# Option B: Inline custom policy (more precise — principle of least privilege)
# This policy only allows S3 access to the bookstore-specific bucket
aws iam put-user-policy \
    --user-name bookstore-app-user \
    --policy-name bookstore-s3-policy \
    --policy-document '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
                "Resource": "arn:aws:s3:::bookstore-assets-prod/*"
            },
            {
                "Effect": "Allow",
                "Action": "s3:ListBucket",
                "Resource": "arn:aws:s3:::bookstore-assets-prod"
            }
        ]
    }'

# Create access keys for this user (for programmatic access)
aws iam create-access-key --user-name bookstore-app-user

# ── 1b. IAM ROLES (for EC2/ECS/Lambda — preferred over access keys) ──
# Create a role that EC2 instances can assume
aws iam create-role \
    --role-name bookstore-ec2-role \
    --assume-role-policy-document '{
        "Version": "2012-10-17",
        "Statement": [{
            "Effect": "Allow",
            "Principal": {"Service": "ec2.amazonaws.com"},
            "Action": "sts:AssumeRole"
        }]
    }'

# Attach policies to the role
# S3 access for reading book images
aws iam attach-role-policy \
    --role-name bookstore-ec2-role \
    --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess

# CloudWatch Logs access for application logs
aws iam attach-role-policy \
    --role-name bookstore-ec2-role \
    --policy-arn arn:aws:iam::aws:policy/CloudWatchLogsFullAccess

# SSM access (Systems Manager Session Manager — SSH without key pairs!)
aws iam attach-role-policy \
    --role-name bookstore-ec2-role \
    --policy-arn arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore

# Create an instance profile (required to attach a role to EC2)
aws iam create-instance-profile --instance-profile-name bookstore-ec2-profile
aws iam add-role-to-instance-profile \
    --instance-profile-name bookstore-ec2-profile \
    --role-name bookstore-ec2-role

echo "IAM role and instance profile created for EC2."

# ── 1c. IAM GROUPS ──
# Create groups to manage permissions for dev team members
aws iam create-group --group-name BookstoreDevelopers

aws iam attach-group-policy \
    --group-name BookstoreDevelopers \
    --policy-arn arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess

aws iam attach-group-policy \
    --group-name BookstoreDevelopers \
    --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess

# Add a user to the group
aws iam add-user-to-group \
    --user-name bookstore-app-user \
    --group-name BookstoreDevelopers

# List all IAM users
aws iam list-users \
    --query 'Users[*].{User:UserName,Created:CreateDate,ARN:Arn}' \
    --output table

# ── 1d. COMMON IAM MISTAKES (comments for classroom discussion) ──
# ❌ WRONG: Using root account for day-to-day operations
#    Root account has god-mode access and cannot be restricted. Use IAM users.
# ❌ WRONG: Hardcoding access keys in source code
#    Keys go into environment variables or AWS Secrets Manager.
# ❌ WRONG: Giving AdministratorAccess to everything
#    Use least-privilege — only the permissions actually needed.
# ✅ RIGHT: Use IAM Roles for EC2/ECS/Lambda (no keys to rotate/leak)
# ✅ RIGHT: Enable MFA on all IAM users, especially root
# ✅ RIGHT: Rotate access keys regularly (every 90 days)

# ─────────────────────────────────────────────────────────────
# SECTION 2 — AMAZON S3 (Simple Storage Service)
# ─────────────────────────────────────────────────────────────

# S3 bucket names must be globally unique across ALL AWS accounts
BUCKET_NAME="bookstore-assets-${ACCOUNT_ID}-$(date +%Y%m%d)"
echo "Creating bucket: $BUCKET_NAME"

# Create an S3 bucket
# Note: us-east-1 does NOT use --create-bucket-configuration (it's the default region)
aws s3api create-bucket \
    --bucket "$BUCKET_NAME" \
    --region us-east-1

# For regions OTHER than us-east-1:
# aws s3api create-bucket \
#     --bucket "$BUCKET_NAME" \
#     --region eu-west-1 \
#     --create-bucket-configuration LocationConstraint=eu-west-1

# Tag the bucket
aws s3api put-bucket-tagging \
    --bucket "$BUCKET_NAME" \
    --tagging 'TagSet=[{Key=Project,Value=bookstore},{Key=Environment,Value=dev}]'

# Enable versioning (keeps history of all object versions)
aws s3api put-bucket-versioning \
    --bucket "$BUCKET_NAME" \
    --versioning-configuration Status=Enabled
echo "Versioning enabled on $BUCKET_NAME"

# Enable server-side encryption (AES-256 by default)
aws s3api put-bucket-encryption \
    --bucket "$BUCKET_NAME" \
    --server-side-encryption-configuration '{
        "Rules": [{
            "ApplyServerSideEncryptionByDefault": {
                "SSEAlgorithm": "AES256"
            },
            "BucketKeyEnabled": true
        }]
    }'
echo "Encryption enabled on $BUCKET_NAME"

# ── 2a. UPLOAD AND DOWNLOAD OBJECTS ──
# Create a sample book cover image placeholder
echo "Bookstore Book Cover" > /tmp/sample-cover.txt

# Upload a single file
aws s3 cp /tmp/sample-cover.txt "s3://${BUCKET_NAME}/covers/book-001/cover.jpg"

# Upload a directory (sync — only uploads changed files)
mkdir -p /tmp/book-assets/covers
echo "Sample book data" > /tmp/book-assets/covers/book-001.txt
echo "Sample book data" > /tmp/book-assets/covers/book-002.txt
aws s3 sync /tmp/book-assets/ "s3://${BUCKET_NAME}/" \
    --exclude "*.tmp" \
    --include "*.txt"
echo "Assets synced to S3."

# List bucket contents
aws s3 ls "s3://${BUCKET_NAME}/" --recursive --human-readable

# Download a file
aws s3 cp "s3://${BUCKET_NAME}/covers/book-001/cover.jpg" /tmp/downloaded-cover.txt
echo "Downloaded: $(cat /tmp/downloaded-cover.txt)"

# Generate a presigned URL (temporary access — for private objects)
# Expires in 1 hour (3600 seconds)
aws s3 presign "s3://${BUCKET_NAME}/covers/book-001/cover.jpg" --expires-in 3600
# Output: a long URL that anyone can use for the next hour — no auth required

# Delete an object
# aws s3 rm "s3://${BUCKET_NAME}/covers/book-001/cover.jpg"

# ── 2b. BUCKET POLICIES AND PUBLIC ACCESS ──
# Block all public access (secure default — enable selectively if needed)
aws s3api put-public-access-block \
    --bucket "$BUCKET_NAME" \
    --public-access-block-configuration \
        "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
echo "Public access blocked on $BUCKET_NAME"

# ── 2c. CORS CONFIGURATION (for browser-direct uploads) ──
aws s3api put-bucket-cors \
    --bucket "$BUCKET_NAME" \
    --cors-configuration '{
        "CORSRules": [{
            "AllowedOrigins": ["https://bookstore.example.com"],
            "AllowedMethods": ["GET", "PUT", "POST"],
            "AllowedHeaders": ["*"],
            "MaxAgeSeconds": 3000
        }]
    }'
echo "CORS configured for bookstore frontend."

# ── 2d. LIFECYCLE POLICIES (automatically move/delete old objects) ──
aws s3api put-bucket-lifecycle-configuration \
    --bucket "$BUCKET_NAME" \
    --lifecycle-configuration '{
        "Rules": [
            {
                "ID": "archive-old-book-images",
                "Status": "Enabled",
                "Filter": {"Prefix": "covers/"},
                "Transitions": [
                    {
                        "Days": 90,
                        "StorageClass": "STANDARD_IA"
                    },
                    {
                        "Days": 365,
                        "StorageClass": "GLACIER"
                    }
                ]
            },
            {
                "ID": "delete-old-temp-files",
                "Status": "Enabled",
                "Filter": {"Prefix": "temp/"},
                "Expiration": {"Days": 7}
            }
        ]
    }'
echo "Lifecycle policy created."

# ─────────────────────────────────────────────────────────────
# SECTION 3 — STATIC WEBSITE HOSTING ON S3
# ─────────────────────────────────────────────────────────────

# Create a separate public bucket for the bookstore React frontend
WEBSITE_BUCKET="bookstore-frontend-${ACCOUNT_ID}"

aws s3api create-bucket \
    --bucket "$WEBSITE_BUCKET" \
    --region us-east-1

# IMPORTANT: For static website hosting, we need public access
# First, remove the public access block (required for website hosting)
aws s3api delete-public-access-block \
    --bucket "$WEBSITE_BUCKET"

# Enable static website hosting
aws s3api put-bucket-website \
    --bucket "$WEBSITE_BUCKET" \
    --website-configuration '{
        "IndexDocument": {"Suffix": "index.html"},
        "ErrorDocument": {"Key": "error.html"}
    }'

# Add a bucket policy to allow public read
aws s3api put-bucket-policy \
    --bucket "$WEBSITE_BUCKET" \
    --policy "{
        \"Version\": \"2012-10-17\",
        \"Statement\": [{
            \"Sid\": \"PublicReadGetObject\",
            \"Effect\": \"Allow\",
            \"Principal\": \"*\",
            \"Action\": \"s3:GetObject\",
            \"Resource\": \"arn:aws:s3:::${WEBSITE_BUCKET}/*\"
        }]
    }"

# Create sample index.html (in practice, run npm run build and sync dist/)
cat > /tmp/index.html << 'HTML'
<!DOCTYPE html>
<html>
<head><title>Bookstore — Find Your Next Adventure</title></head>
<body>
  <h1>Welcome to the Bookstore!</h1>
  <p>Browse thousands of books...</p>
  <script>
    // React app would be injected here after build
    fetch('https://api.bookstore.example.com/books')
      .then(r => r.json())
      .then(books => console.log('Loaded books:', books));
  </script>
</body>
</html>
HTML

cat > /tmp/error.html << 'HTML'
<!DOCTYPE html>
<html>
<head><title>Page Not Found — Bookstore</title></head>
<body>
  <h1>404 — Page not found</h1>
  <a href="/">Back to bookstore</a>
</body>
</html>
HTML

# Upload the static site
aws s3 cp /tmp/index.html "s3://${WEBSITE_BUCKET}/index.html" --content-type text/html
aws s3 cp /tmp/error.html "s3://${WEBSITE_BUCKET}/error.html" --content-type text/html

# Get the website URL
WEBSITE_URL="http://${WEBSITE_BUCKET}.s3-website-us-east-1.amazonaws.com"
echo "Website available at: $WEBSITE_URL"

# In production: use CloudFront in front of S3 for HTTPS + CDN caching
# aws cloudfront create-distribution --distribution-config file://cf-config.json

# ─────────────────────────────────────────────────────────────
# SECTION 4 — AWS ELASTIC BEANSTALK (PaaS Deployment)
# ─────────────────────────────────────────────────────────────

# Elastic Beanstalk is PaaS — just upload your Spring Boot JAR
# EB manages EC2, load balancing, auto scaling, health monitoring

# Install the EB CLI (if not already installed)
# pip install awsebcli

# Initialize EB in your project directory (run from Spring Boot project root)
# eb init bookstore-api \
#   --platform "Java 21 running on 64bit Amazon Linux 2023" \
#   --region us-east-1

# Create an environment (this launches EC2 + LB + ASG automatically)
# eb create bookstore-dev \
#   --instance-type t3.micro \
#   --min-instances 1 \
#   --max-instances 3 \
#   --scale 1

# Deploy an update (upload new JAR — EB handles rolling deployment)
# mvn clean package -DskipTests
# eb deploy

# ── Beanstalk environment configuration via CLI ──
# List all Beanstalk applications
aws elasticbeanstalk describe-applications \
    --query 'Applications[*].{Name:ApplicationName,Description:Description}' \
    --output table 2>/dev/null || echo "(No Beanstalk applications yet)"

# List all Beanstalk environments
aws elasticbeanstalk describe-environments \
    --query 'Environments[*].{Name:EnvironmentName,Status:Status,Health:Health,URL:CNAME}' \
    --output table 2>/dev/null || echo "(No Beanstalk environments yet)"

# ── Beanstalk .ebextensions — customize the platform ──
# Create .ebextensions/bookstore.config in your project:
cat << 'EBCONFIG'
# .ebextensions/bookstore.config
# This file customizes the Elastic Beanstalk environment

option_settings:
  # Set the server port Spring Boot listens on
  aws:elasticbeanstalk:application:environment:
    SERVER_PORT: "5000"
    SPRING_PROFILES_ACTIVE: "prod"
    DB_URL: "jdbc:postgresql://bookstore-db.xxxxx.rds.amazonaws.com:5432/bookstore"

  # Configure the load balancer health check
  aws:elasticbeanstalk:environment:process:default:
    HealthCheckPath: /actuator/health
    MatcherHTTPCode: 200

  # Set the instance type
  aws:autoscaling:launchconfiguration:
    InstanceType: t3.small

  # Configure auto scaling
  aws:autoscaling:asg:
    MinSize: 1
    MaxSize: 4
    Cooldown: 300

container_commands:
  01_restart_nginx:
    command: "service nginx restart"
    leader_only: true
EBCONFIG

# ── Elastic Beanstalk vs EC2 (when to choose which) ──
cat << 'COMPARISON'
┌──────────────────────────────────────────────────────────────────┐
│  Elastic Beanstalk vs Raw EC2                                    │
├─────────────────────────┬────────────────────────────────────────┤
│  Elastic Beanstalk      │  Raw EC2 + ASG + ALB                  │
├─────────────────────────┼────────────────────────────────────────┤
│  PaaS — just upload JAR │  Full control over everything          │
│  Easy setup (minutes)   │  Complex setup (hours/days)            │
│  AWS manages OS patches │  You patch the OS                      │
│  Built-in LB + ASG      │  Configure LB + ASG manually           │
│  Limited customization  │  Unlimited customization               │
│  Good for: startups,    │  Good for: Netflix-scale, compliance,  │
│  MVPs, simple APIs      │  complex infra requirements            │
└─────────────────────────┴────────────────────────────────────────┘
COMPARISON

# ─────────────────────────────────────────────────────────────
# SECTION 5 — AMAZON RDS (Relational Database Service)
# ─────────────────────────────────────────────────────────────

# Create a DB Subnet Group (required — defines which subnets RDS can use)
# Get private subnet IDs (in production, DB should be in private subnets)
SUBNET_1=$(aws ec2 describe-subnets \
    --filters "Name=defaultForAz,Values=true" "Name=availabilityZone,Values=us-east-1a" \
    --query 'Subnets[0].SubnetId' --output text)
SUBNET_2=$(aws ec2 describe-subnets \
    --filters "Name=defaultForAz,Values=true" "Name=availabilityZone,Values=us-east-1b" \
    --query 'Subnets[0].SubnetId' --output text)

aws rds create-db-subnet-group \
    --db-subnet-group-name bookstore-db-subnet-group \
    --db-subnet-group-description "Bookstore DB subnet group" \
    --subnet-ids "$SUBNET_1" "$SUBNET_2"

# Create a Security Group for RDS (only allow traffic from app servers)
VPC_ID=$(aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" \
    --query 'Vpcs[0].VpcId' --output text)

RDS_SG=$(aws ec2 create-security-group \
    --group-name bookstore-rds-sg \
    --description "Bookstore RDS security group — PostgreSQL from app servers only" \
    --vpc-id "$VPC_ID" \
    --query 'GroupId' --output text)

# Allow PostgreSQL (5432) only from within the VPC (CIDR of default VPC)
aws ec2 authorize-security-group-ingress \
    --group-id "$RDS_SG" \
    --protocol tcp \
    --port 5432 \
    --cidr "172.31.0.0/16"

# Create the RDS PostgreSQL instance
aws rds create-db-instance \
    --db-instance-identifier bookstore-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --engine-version "16.1" \
    --master-username bookstore_admin \
    --master-user-password "BookstoreS3cret!" \
    --allocated-storage 20 \
    --max-allocated-storage 100 \
    --storage-type gp3 \
    --db-name bookstore \
    --vpc-security-group-ids "$RDS_SG" \
    --db-subnet-group-name bookstore-db-subnet-group \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00" \
    --preferred-maintenance-window "sun:04:00-sun:05:00" \
    --multi-az \
    --storage-encrypted \
    --tags "Key=Project,Value=bookstore" "Key=Environment,Value=dev"

echo "RDS instance creation started (takes ~5 minutes)..."

# Wait for the DB to be available
# aws rds wait db-instance-available --db-instance-identifier bookstore-db
# echo "RDS is available!"

# Get the RDS endpoint after it's created
aws rds describe-db-instances \
    --db-instance-identifier bookstore-db \
    --query 'DBInstances[0].{
        Identifier:DBInstanceIdentifier,
        Status:DBInstanceStatus,
        Engine:Engine,
        Version:EngineVersion,
        Class:DBInstanceClass,
        Endpoint:Endpoint.Address,
        Port:Endpoint.Port,
        MultiAZ:MultiAZ
    }' \
    --output table 2>/dev/null || echo "(RDS creating...)"

# Connect to RDS (from EC2 instance or local with SSH tunnel)
# psql -h bookstore-db.xxxxx.us-east-1.rds.amazonaws.com -U bookstore_admin -d bookstore

# Create a read replica (for read scaling)
# aws rds create-db-instance-read-replica \
#     --db-instance-identifier bookstore-db-read \
#     --source-db-instance-identifier bookstore-db \
#     --db-instance-class db.t3.micro

# ─────────────────────────────────────────────────────────────
# SECTION 6 — AWS ECR (Elastic Container Registry)
# ─────────────────────────────────────────────────────────────

# ECR is AWS's private Docker registry — like Docker Hub but in AWS

# Create a repository for the bookstore backend image
aws ecr create-repository \
    --repository-name bookstore/api \
    --image-scanning-configuration scanOnPush=true \
    --encryption-configuration encryptionType=AES256 \
    --tags "Key=Project,Value=bookstore"

# Create a repository for the React frontend
aws ecr create-repository \
    --repository-name bookstore/frontend \
    --image-scanning-configuration scanOnPush=true

# Get the registry URI
ECR_URI="${ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
echo "ECR URI: $ECR_URI"

# Authenticate Docker to ECR (token valid for 12 hours)
aws ecr get-login-password --region "$AWS_DEFAULT_REGION" | \
    docker login --username AWS --password-stdin "$ECR_URI"
echo "Docker authenticated to ECR."

# Build and push the bookstore API Docker image
# (Run from the root of your Spring Boot project — Dockerfile must exist)
cat << 'DOCKERFILE_EXAMPLE'
# Dockerfile for bookstore-api (example)
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY target/bookstore-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
DOCKERFILE_EXAMPLE

# Build the image
# mvn clean package -DskipTests
# docker build -t bookstore/api:latest .

# Tag the image for ECR
# docker tag bookstore/api:latest "${ECR_URI}/bookstore/api:latest"
# docker tag bookstore/api:latest "${ECR_URI}/bookstore/api:v1.0.0"

# Push to ECR
# docker push "${ECR_URI}/bookstore/api:latest"
# docker push "${ECR_URI}/bookstore/api:v1.0.0"

# List images in the repository
aws ecr list-images \
    --repository-name bookstore/api \
    --query 'imageIds[*].{Tag:imageTag,Digest:imageDigest}' \
    --output table 2>/dev/null || echo "(No images pushed yet)"

# Describe the repository
aws ecr describe-repositories \
    --repository-names bookstore/api \
    --output table 2>/dev/null

# Set lifecycle policy (auto-delete old images to save storage costs)
aws ecr put-lifecycle-policy \
    --repository-name bookstore/api \
    --lifecycle-policy-text '{
        "rules": [
            {
                "rulePriority": 1,
                "description": "Keep only the last 10 tagged images",
                "selection": {
                    "tagStatus": "tagged",
                    "tagPrefixList": ["v"],
                    "countType": "imageCountMoreThan",
                    "countNumber": 10
                },
                "action": {"type": "expire"}
            },
            {
                "rulePriority": 2,
                "description": "Delete untagged images after 7 days",
                "selection": {
                    "tagStatus": "untagged",
                    "countType": "sinceImagePushed",
                    "countUnit": "days",
                    "countNumber": 7
                },
                "action": {"type": "expire"}
            }
        ]
    }'
echo "ECR lifecycle policy applied."

# ─────────────────────────────────────────────────────────────
# SECTION 7 — AWS ECS (Elastic Container Service)
# ─────────────────────────────────────────────────────────────

# ECS is AWS's container orchestration service — a simpler alternative to Kubernetes
# It can run containers on EC2 instances (EC2 launch type) or
# serverlessly on Fargate (Fargate launch type — no EC2 management)

# ── 7a. ECS TASK DEFINITION ──
# A Task Definition is a blueprint for a container (like a docker-compose.yml service)
aws ecs register-task-definition \
    --family bookstore-api-task \
    --cpu 512 \
    --memory 1024 \
    --network-mode awsvpc \
    --requires-compatibilities FARGATE \
    --execution-role-arn "arn:aws:iam::${ACCOUNT_ID}:role/ecsTaskExecutionRole" \
    --container-definitions "[
        {
            \"name\": \"bookstore-api\",
            \"image\": \"${ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/bookstore/api:latest\",
            \"portMappings\": [{
                \"containerPort\": 8080,
                \"protocol\": \"tcp\"
            }],
            \"environment\": [
                {\"name\": \"SPRING_PROFILES_ACTIVE\", \"value\": \"prod\"},
                {\"name\": \"SERVER_PORT\", \"value\": \"8080\"}
            ],
            \"secrets\": [
                {
                    \"name\": \"DB_PASSWORD\",
                    \"valueFrom\": \"arn:aws:secretsmanager:us-east-1:${ACCOUNT_ID}:secret:bookstore/db-password\"
                }
            ],
            \"logConfiguration\": {
                \"logDriver\": \"awslogs\",
                \"options\": {
                    \"awslogs-group\": \"/ecs/bookstore-api\",
                    \"awslogs-region\": \"${AWS_DEFAULT_REGION}\",
                    \"awslogs-stream-prefix\": \"bookstore\"
                }
            },
            \"healthCheck\": {
                \"command\": [\"CMD-SHELL\", \"curl -f http://localhost:8080/actuator/health || exit 1\"],
                \"interval\": 30,
                \"timeout\": 5,
                \"retries\": 3,
                \"startPeriod\": 60
            }
        }
    ]"

echo "Task definition registered."

# ── 7b. ECS CLUSTER ──
# Create a Fargate-only cluster (no EC2 instances to manage!)
aws ecs create-cluster \
    --cluster-name bookstore-cluster \
    --capacity-providers FARGATE FARGATE_SPOT \
    --tags "key=Project,value=bookstore"

echo "ECS cluster created."

# ── 7c. ECS SERVICE ──
# A Service keeps N copies of the task running and replaces failed tasks
# (Requires a VPC, subnets, and security group — use from Part 1)
VPC_ID=$(aws ec2 describe-vpcs --filters "Name=isDefault,Values=true" \
    --query 'Vpcs[0].VpcId' --output text)
SUBNET_IDS=$(aws ec2 describe-subnets \
    --filters "Name=defaultForAz,Values=true" \
    --query 'Subnets[*].SubnetId' \
    --output text | tr '\t' ',')

# Create a security group for the ECS service
ECS_SG=$(aws ec2 create-security-group \
    --group-name bookstore-ecs-sg \
    --description "Bookstore ECS task security group" \
    --vpc-id "$VPC_ID" \
    --query 'GroupId' --output text 2>/dev/null || \
    aws ec2 describe-security-groups \
    --filters "Name=group-name,Values=bookstore-ecs-sg" \
    --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 authorize-security-group-ingress \
    --group-id "$ECS_SG" \
    --protocol tcp --port 8080 --cidr 0.0.0.0/0 2>/dev/null || true

aws ecs create-service \
    --cluster bookstore-cluster \
    --service-name bookstore-api-service \
    --task-definition bookstore-api-task \
    --desired-count 2 \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={
        subnets=[$(echo $SUBNET_IDS | tr ',' ' ' | awk '{print $1","$2}')],
        securityGroups=[$ECS_SG],
        assignPublicIp=ENABLED
    }" \
    --deployment-configuration "minimumHealthyPercent=50,maximumPercent=200"

echo "ECS service created."

# List services in the cluster
aws ecs list-services --cluster bookstore-cluster --output table

# View service status
aws ecs describe-services \
    --cluster bookstore-cluster \
    --services bookstore-api-service \
    --query 'services[0].{Status:status,Desired:desiredCount,Running:runningCount,Pending:pendingCount}' \
    --output table

# List running tasks
aws ecs list-tasks --cluster bookstore-cluster --output table

# Update service to deploy new image (rolling deployment)
aws ecs update-service \
    --cluster bookstore-cluster \
    --service bookstore-api-service \
    --force-new-deployment

echo "Rolling deployment triggered."

# ─────────────────────────────────────────────────────────────
# SECTION 8 — AWS EKS (Elastic Kubernetes Service)
# ─────────────────────────────────────────────────────────────

# EKS is AWS's managed Kubernetes service.
# AWS manages the control plane; you manage (or Fargate manages) the nodes.

# ── Install eksctl (EKS CLI — easiest way to create clusters) ──
# brew install eksctl   # macOS
# or: curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp

# ── Create an EKS cluster ──
# (This takes ~15 minutes — do as a demo or show students the output)
cat << 'EKSCONFIG'
# bookstore-cluster.yaml — EKS cluster configuration
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: bookstore-eks
  region: us-east-1
  version: "1.29"

managedNodeGroups:
  - name: bookstore-nodes
    instanceType: t3.medium
    desiredCapacity: 2
    minSize: 1
    maxSize: 5
    labels:
      app: bookstore
    tags:
      Project: bookstore
    iam:
      attachPolicyARNs:
        - arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy
        - arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly
        - arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy

addons:
  - name: aws-ebs-csi-driver
  - name: coredns
  - name: kube-proxy
  - name: vpc-cni

cloudWatch:
  clusterLogging:
    enable: [api, audit, authenticator, controllerManager, scheduler]
EKSCONFIG

# Create the cluster from config
# eksctl create cluster -f bookstore-cluster.yaml

# Configure kubectl to use the new cluster
# aws eks update-kubeconfig --region us-east-1 --name bookstore-eks

# After cluster is running — deploy bookstore app with kubectl
cat << 'K8S_MANIFEST'
# bookstore-deployment.yaml — Kubernetes deployment for EKS
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bookstore-api
  namespace: default
spec:
  replicas: 2
  selector:
    matchLabels:
      app: bookstore-api
  template:
    metadata:
      labels:
        app: bookstore-api
    spec:
      containers:
      - name: bookstore-api
        image: ACCOUNT.dkr.ecr.us-east-1.amazonaws.com/bookstore/api:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1024Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: bookstore-api-svc
spec:
  type: LoadBalancer   # Creates an AWS ALB automatically
  selector:
    app: bookstore-api
  ports:
  - port: 80
    targetPort: 8080
K8S_MANIFEST

# kubectl apply -f bookstore-deployment.yaml
# kubectl get pods
# kubectl get services   # Get the ALB DNS name

# EKS vs ECS — when to choose which:
cat << 'COMPARISON'
┌────────────────────────────────────────────────────────────────┐
│  ECS vs EKS                                                    │
├───────────────────────────┬────────────────────────────────────┤
│  ECS                      │  EKS                              │
├───────────────────────────┼────────────────────────────────────┤
│  AWS-native, simpler API  │  Kubernetes — industry standard    │
│  Less learning curve      │  More complex but more portable    │
│  Good for AWS-only shops  │  Good for multi-cloud / migration  │
│  Fargate support          │  Fargate support                   │
│  No Kubernetes expertise  │  Requires Kubernetes knowledge     │
│  required                 │  Huge ecosystem (Helm, Istio, etc) │
│  Simpler pricing          │  $0.10/hour for control plane      │
│  Good for: most apps      │  Good for: complex apps, portability│
└───────────────────────────┴────────────────────────────────────┘
COMPARISON

# ─────────────────────────────────────────────────────────────
# SECTION 9 — AWS FARGATE (Serverless Containers)
# ─────────────────────────────────────────────────────────────

# Fargate is a serverless compute engine for containers.
# You run containers WITHOUT managing EC2 instances.
# Used with BOTH ECS and EKS.

cat << 'FARGATE_NOTES'
┌──────────────────────────────────────────────────────────────┐
│  AWS Fargate — Serverless Containers                         │
│                                                              │
│  Traditional ECS/EC2:                                        │
│    You → Launch EC2 nodes → Install ECS agent → ECS places  │
│    containers on nodes → You patch OS, right-size instances │
│                                                              │
│  ECS + Fargate:                                              │
│    You → Define task CPU/memory → Fargate runs it           │
│    No EC2 to manage. No OS patching. No capacity planning.  │
│                                                              │
│  Pricing: Pay per vCPU/GB RAM per second of task runtime    │
│  vs EC2:  Pay per instance hour (even when idle)            │
│                                                              │
│  Fargate is MORE expensive per unit compute,                │
│  but often CHEAPER overall because you only pay when        │
│  tasks are running (great for sporadic workloads).          │
│                                                              │
│  Bookstore recommendation:                                  │
│    - Use FARGATE for the API service (predictable container) │
│    - Use FARGATE_SPOT for batch jobs (save 70%)             │
│    - Use EC2 launch type only if you need GPU instances     │
│      or very specific hardware                              │
└──────────────────────────────────────────────────────────────┘
FARGATE_NOTES

# Fargate task definition (already shown in ECS section above — launch type = FARGATE)
# No additional EC2 configuration needed — just set launch type to FARGATE

# ─────────────────────────────────────────────────────────────
# SECTION 10 — AWS LAMBDA (Serverless Functions)
# ─────────────────────────────────────────────────────────────

# Lambda runs code WITHOUT provisioning servers.
# You upload a function. AWS runs it when triggered. You pay per invocation.

# Create a Lambda function for bookstore (e.g., resize book cover images on upload)
mkdir -p /tmp/lambda-bookstore
cat > /tmp/lambda-bookstore/index.js << 'LAMBDA_CODE'
// Lambda function: Triggered when a book cover is uploaded to S3
// Resizes the image and saves a thumbnail back to S3

const AWS = require('@aws-sdk/client-s3');

exports.handler = async (event) => {
    const bucket = event.Records[0].s3.bucket.name;
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
    
    console.log(`Processing image: s3://${bucket}/${key}`);
    
    // In a real function, you would:
    // 1. Download the image from S3
    // 2. Resize it with Sharp or jimp
    // 3. Upload the thumbnail to S3
    
    const thumbnailKey = key.replace('covers/', 'thumbnails/');
    
    // Simulated processing
    console.log(`Thumbnail would be saved to: s3://${bucket}/${thumbnailKey}`);
    
    return {
        statusCode: 200,
        body: JSON.stringify({
            message: `Thumbnail created for ${key}`,
            thumbnail: thumbnailKey
        })
    };
};
LAMBDA_CODE

# Package the Lambda function
cd /tmp/lambda-bookstore && zip -r function.zip index.js 2>/dev/null
cd - > /dev/null

# Create IAM role for Lambda
aws iam create-role \
    --role-name bookstore-lambda-role \
    --assume-role-policy-document '{
        "Version": "2012-10-17",
        "Statement": [{
            "Effect": "Allow",
            "Principal": {"Service": "lambda.amazonaws.com"},
            "Action": "sts:AssumeRole"
        }]
    }' 2>/dev/null || echo "(Lambda role may already exist)"

aws iam attach-role-policy \
    --role-name bookstore-lambda-role \
    --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

aws iam attach-role-policy \
    --role-name bookstore-lambda-role \
    --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess

sleep 10  # Wait for role to propagate

# Create the Lambda function
aws lambda create-function \
    --function-name bookstore-image-resizer \
    --runtime nodejs20.x \
    --role "arn:aws:iam::${ACCOUNT_ID}:role/bookstore-lambda-role" \
    --handler index.handler \
    --zip-file fileb:///tmp/lambda-bookstore/function.zip \
    --timeout 30 \
    --memory-size 512 \
    --environment "Variables={THUMBNAIL_BUCKET=${BUCKET_NAME}}" \
    --tags "Project=bookstore,Function=image-resizer"

echo "Lambda function created."

# Test invoke the Lambda function
aws lambda invoke \
    --function-name bookstore-image-resizer \
    --payload '{"Records":[{"s3":{"bucket":{"name":"my-bucket"},"object":{"key":"covers/book-001/cover.jpg"}}}]}' \
    --cli-binary-format raw-in-base64-out \
    /tmp/lambda-output.json
cat /tmp/lambda-output.json

# Configure S3 to trigger Lambda on object upload
aws s3api put-bucket-notification-configuration \
    --bucket "$BUCKET_NAME" \
    --notification-configuration "{
        \"LambdaFunctionConfigurations\": [{
            \"LambdaFunctionArn\": \"arn:aws:lambda:${AWS_DEFAULT_REGION}:${ACCOUNT_ID}:function:bookstore-image-resizer\",
            \"Events\": [\"s3:ObjectCreated:*\"],
            \"Filter\": {
                \"Key\": {
                    \"FilterRules\": [{
                        \"Name\": \"prefix\",
                        \"Value\": \"covers/\"
                    }]
                }
            }
        }]
    }"

echo "S3 → Lambda trigger configured."

# Lambda concurrency and scaling
# Lambda automatically scales from 0 to thousands of concurrent invocations
# No servers to provision — AWS handles everything

# Update Lambda code (new version)
# aws lambda update-function-code \
#     --function-name bookstore-image-resizer \
#     --zip-file fileb:///tmp/lambda-bookstore/function.zip

# ─────────────────────────────────────────────────────────────
# SECTION 11 — AWS SNS and SQS (Messaging)
# ─────────────────────────────────────────────────────────────

# SNS = Simple Notification Service — pub/sub messaging (fan out)
# SQS = Simple Queue Service — message queue (point-to-point / buffering)

# ── 11a. SNS — Bookstore Order Notifications ──
# Create SNS topic for order events
ORDER_TOPIC_ARN=$(aws sns create-topic \
    --name bookstore-order-events \
    --query 'TopicArn' --output text)
echo "SNS Topic ARN: $ORDER_TOPIC_ARN"

# Create an email subscription (confirm via email)
aws sns subscribe \
    --topic-arn "$ORDER_TOPIC_ARN" \
    --protocol email \
    --notification-endpoint admin@bookstore.example.com

# Create an HTTPS subscription (webhook to a service)
aws sns subscribe \
    --topic-arn "$ORDER_TOPIC_ARN" \
    --protocol https \
    --notification-endpoint "https://api.bookstore.example.com/webhooks/orders"

# Publish a message to the topic (broadcasts to ALL subscribers)
aws sns publish \
    --topic-arn "$ORDER_TOPIC_ARN" \
    --subject "New Order Placed" \
    --message '{
        "orderId": "ORD-12345",
        "userId": "USR-789",
        "books": ["978-0-13-468599-1", "978-0-596-51774-8"],
        "total": 45.99,
        "timestamp": "2024-11-15T14:30:00Z"
    }' \
    --message-attributes '{
        "EventType": {
            "DataType": "String",
            "StringValue": "OrderPlaced"
        }
    }'
echo "Order event published to SNS."

# ── 11b. SQS — Queue for Order Processing ──
# Create a standard SQS queue for inventory service
INVENTORY_QUEUE_URL=$(aws sqs create-queue \
    --queue-name bookstore-inventory-queue \
    --attributes '{
        "DelaySeconds": "0",
        "MessageRetentionPeriod": "86400",
        "VisibilityTimeout": "30",
        "ReceiveMessageWaitTimeSeconds": "20"
    }' \
    --tags "Project=bookstore,Service=inventory" \
    --query 'QueueUrl' --output text)
echo "SQS Queue URL: $INVENTORY_QUEUE_URL"

# Create a Dead Letter Queue (for messages that fail processing)
DLQ_URL=$(aws sqs create-queue \
    --queue-name bookstore-inventory-dlq \
    --attributes '{
        "MessageRetentionPeriod": "1209600"
    }' \
    --query 'QueueUrl' --output text)

DLQ_ARN=$(aws sqs get-queue-attributes \
    --queue-url "$DLQ_URL" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text)

# Configure DLQ on the main queue (after 3 failed processing attempts)
aws sqs set-queue-attributes \
    --queue-url "$INVENTORY_QUEUE_URL" \
    --attributes "{
        \"RedrivePolicy\": \"{\\\"deadLetterTargetArn\\\":\\\"${DLQ_ARN}\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"
    }"
echo "DLQ configured."

# Subscribe SQS to SNS (fan out: order event → multiple queues)
SQS_ARN=$(aws sqs get-queue-attributes \
    --queue-url "$INVENTORY_QUEUE_URL" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text)

aws sns subscribe \
    --topic-arn "$ORDER_TOPIC_ARN" \
    --protocol sqs \
    --notification-endpoint "$SQS_ARN"

echo "SQS subscribed to SNS topic."

# Send a message directly to SQS
aws sqs send-message \
    --queue-url "$INVENTORY_QUEUE_URL" \
    --message-body '{
        "orderId": "ORD-12345",
        "books": [
            {"isbn": "978-0-13-468599-1", "quantity": 1},
            {"isbn": "978-0-596-51774-8", "quantity": 2}
        ]
    }' \
    --message-attributes '{
        "EventType": {
            "DataType": "String",
            "StringValue": "ReserveStock"
        }
    }'

# Receive and process messages (long polling = efficient, up to 20 seconds)
MESSAGE=$(aws sqs receive-message \
    --queue-url "$INVENTORY_QUEUE_URL" \
    --max-number-of-messages 1 \
    --wait-time-seconds 5 \
    --output json)
echo "Received: $MESSAGE"

# Delete a message after processing (acknowledge)
RECEIPT_HANDLE=$(echo "$MESSAGE" | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('Messages',[{}])[0].get('ReceiptHandle',''))" 2>/dev/null)
if [ -n "$RECEIPT_HANDLE" ]; then
    aws sqs delete-message \
        --queue-url "$INVENTORY_QUEUE_URL" \
        --receipt-handle "$RECEIPT_HANDLE"
    echo "Message acknowledged and deleted."
fi

# ─────────────────────────────────────────────────────────────
# SECTION 12 — AWS CLOUDWATCH (Monitoring and Logging)
# ─────────────────────────────────────────────────────────────

# CloudWatch provides metrics, logs, alarms, and dashboards for AWS

# ── 12a. LOG GROUPS ──
# Create a log group for the bookstore API
aws logs create-log-group \
    --log-group-name /bookstore/api \
    --tags "Project=bookstore"

# Set retention (logs auto-delete after 30 days — saves money)
aws logs put-retention-policy \
    --log-group-name /bookstore/api \
    --retention-in-days 30

echo "Log group created with 30-day retention."

# View logs (in practice, use aws logs tail for live streaming)
# aws logs tail /bookstore/api --follow

# Filter logs for errors
# aws logs filter-log-events \
#     --log-group-name /bookstore/api \
#     --filter-pattern "ERROR" \
#     --start-time $(($(date +%s) - 3600))000  # Last 1 hour

# ── 12b. CLOUDWATCH METRICS AND ALARMS ──
# Create an alarm: alert when 5xx errors exceed threshold
aws cloudwatch put-metric-alarm \
    --alarm-name bookstore-high-5xx-errors \
    --alarm-description "Alert when bookstore API 5xx errors are high" \
    --namespace AWS/ApplicationELB \
    --metric-name HTTPCode_Target_5XX_Count \
    --statistic Sum \
    --period 300 \
    --threshold 10 \
    --comparison-operator GreaterThanOrEqualToThreshold \
    --evaluation-periods 2 \
    --alarm-actions "$ORDER_TOPIC_ARN" \
    --ok-actions "$ORDER_TOPIC_ARN" \
    --treat-missing-data notBreaching

echo "CloudWatch alarm created for 5xx errors."

# Create an alarm for high CPU (works for EC2 instances)
INSTANCE_ID=$(aws ec2 describe-instances \
    --filters "Name=tag:Name,Values=bookstore-dev" "Name=instance-state-name,Values=running" \
    --query 'Reservations[0].Instances[0].InstanceId' \
    --output text 2>/dev/null || echo "i-placeholder")

aws cloudwatch put-metric-alarm \
    --alarm-name bookstore-high-cpu \
    --alarm-description "EC2 CPU exceeds 80% for 10 minutes" \
    --namespace AWS/EC2 \
    --metric-name CPUUtilization \
    --dimensions "Name=InstanceId,Value=${INSTANCE_ID}" \
    --statistic Average \
    --period 300 \
    --threshold 80 \
    --comparison-operator GreaterThanOrEqualToThreshold \
    --evaluation-periods 2 \
    --alarm-actions "$ORDER_TOPIC_ARN"

echo "CPU alarm created."

# ── 12c. CUSTOM METRICS ──
# Push custom application metrics to CloudWatch
# (from your Spring Boot app — or directly via CLI for demo)
aws cloudwatch put-metric-data \
    --namespace "Bookstore/Application" \
    --metric-data '[
        {
            "MetricName": "OrdersPerMinute",
            "Value": 42,
            "Unit": "Count/Second",
            "Dimensions": [
                {"Name": "Environment", "Value": "prod"}
            ]
        },
        {
            "MetricName": "ActiveSessions",
            "Value": 156,
            "Unit": "Count",
            "Dimensions": [
                {"Name": "Environment", "Value": "prod"}
            ]
        }
    ]'
echo "Custom metrics published."

# ── 12d. CLOUDWATCH DASHBOARD ──
aws cloudwatch put-dashboard \
    --dashboard-name bookstore-overview \
    --dashboard-body '{
        "widgets": [
            {
                "type": "metric",
                "properties": {
                    "title": "Bookstore Orders Per Minute",
                    "metrics": [["Bookstore/Application", "OrdersPerMinute", "Environment", "prod"]],
                    "period": 60,
                    "stat": "Sum"
                }
            },
            {
                "type": "metric",
                "properties": {
                    "title": "EC2 CPU Utilization",
                    "metrics": [["AWS/EC2", "CPUUtilization", "InstanceId", "'${INSTANCE_ID}'"]],
                    "period": 300,
                    "stat": "Average"
                }
            }
        ]
    }'
echo "CloudWatch dashboard created."

# ─────────────────────────────────────────────────────────────
# SECTION 13 — AMAZON DYNAMODB (NoSQL Database)
# ─────────────────────────────────────────────────────────────

# DynamoDB is AWS's fully managed NoSQL key-value + document database
# Millisecond latency, unlimited scale, serverless — no DB instances to manage

# Create a DynamoDB table for bookstore shopping carts
# (session data — perfect for DynamoDB: high throughput, key-value, flexible schema)
aws dynamodb create-table \
    --table-name BookstoreShoppingCarts \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
        AttributeName=cartId,AttributeType=S \
    --key-schema \
        AttributeName=userId,KeyType=HASH \
        AttributeName=cartId,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST \
    --tags "Key=Project,Value=bookstore"

echo "DynamoDB table created."

# Wait for table to be active
aws dynamodb wait table-exists --table-name BookstoreShoppingCarts
echo "DynamoDB table is active!"

# Create a table for book view history (with TTL for auto-expiry)
aws dynamodb create-table \
    --table-name BookstoreViewHistory \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
        AttributeName=viewedAt,AttributeType=S \
    --key-schema \
        AttributeName=userId,KeyType=HASH \
        AttributeName=viewedAt,KeyType=RANGE \
    --billing-mode PAY_PER_REQUEST

# Enable TTL so view history expires after 90 days automatically
aws dynamodb update-time-to-live \
    --table-name BookstoreViewHistory \
    --time-to-live-specification "Enabled=true,AttributeName=expiresAt"

echo "View history table with TTL created."

# ── CRUD Operations ──
# Put an item (create/replace)
aws dynamodb put-item \
    --table-name BookstoreShoppingCarts \
    --item '{
        "userId": {"S": "user-123"},
        "cartId": {"S": "cart-456"},
        "items": {"L": [
            {"M": {
                "isbn": {"S": "978-0-13-468599-1"},
                "title": {"S": "Effective Java"},
                "price": {"N": "45.99"},
                "quantity": {"N": "1"}
            }}
        ]},
        "totalAmount": {"N": "45.99"},
        "createdAt": {"S": "2024-11-15T14:30:00Z"},
        "updatedAt": {"S": "2024-11-15T14:30:00Z"}
    }'
echo "Cart item created."

# Get an item by primary key (instant — O(1) lookup)
aws dynamodb get-item \
    --table-name BookstoreShoppingCarts \
    --key '{"userId": {"S": "user-123"}, "cartId": {"S": "cart-456"}}' \
    --output json

# Update an item (add a field)
aws dynamodb update-item \
    --table-name BookstoreShoppingCarts \
    --key '{"userId": {"S": "user-123"}, "cartId": {"S": "cart-456"}}' \
    --update-expression "SET totalAmount = :total, updatedAt = :now" \
    --expression-attribute-values '{
        ":total": {"N": "91.98"},
        ":now": {"S": "2024-11-15T15:00:00Z"}
    }'
echo "Cart item updated."

# Query items by partition key (get all carts for user-123)
aws dynamodb query \
    --table-name BookstoreShoppingCarts \
    --key-condition-expression "userId = :uid" \
    --expression-attribute-values '{":uid": {"S": "user-123"}}' \
    --output json

# Delete an item
# aws dynamodb delete-item \
#     --table-name BookstoreShoppingCarts \
#     --key '{"userId": {"S": "user-123"}, "cartId": {"S": "cart-456"}}'

# ─────────────────────────────────────────────────────────────
# SECTION 14 — DEPLOYING THE FULL-STACK BOOKSTORE TO AWS
# ─────────────────────────────────────────────────────────────

cat << 'ARCHITECTURE'
=============================================================
FULL-STACK BOOKSTORE ON AWS — ARCHITECTURE OVERVIEW
=============================================================

                           Internet
                               │
                    ┌──────────▼──────────┐
                    │  Route 53 (DNS)     │
                    │  bookstore.com      │
                    └──────────┬──────────┘
                               │
              ┌────────────────┴─────────────────┐
              │                                  │
   ┌──────────▼──────────┐           ┌───────────▼──────────┐
   │  CloudFront CDN      │           │  ALB                 │
   │  (frontend assets)   │           │  (API traffic)       │
   └──────────┬──────────┘           └───────────┬──────────┘
              │                                   │
   ┌──────────▼──────────┐           ┌────────────▼─────────┐
   │  S3 Static Website  │           │  ECS Fargate Service │
   │  React Build        │           │  bookstore-api       │
   │  (index.html, JS,   │           │  (2-4 containers)    │
   │   CSS, images)      │           └───────────┬──────────┘
   └─────────────────────┘                       │
                                    ┌─────────────┤
                                    │             │
                         ┌──────────▼───┐  ┌──────▼───────────┐
                         │  RDS         │  │  DynamoDB        │
                         │  PostgreSQL  │  │  Shopping Carts  │
                         │  (Multi-AZ)  │  │  Sessions        │
                         └──────────────┘  └──────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
          ┌─────────▼─────────┐         ┌──────────▼─────────┐
          │  SNS               │         │  S3                │
          │  Order Events      │         │  Book Images       │
          └─────────┬─────────┘         │  User Uploads      │
                    │                   └──────────┬─────────┘
          ┌─────────▼─────────┐                    │
          │  SQS              │         ┌──────────▼─────────┐
          │  Inventory Queue  │         │  Lambda            │
          │  Payment Queue    │         │  Image Resizer     │
          └───────────────────┘         └────────────────────┘

Monitoring & Security:
  CloudWatch  →  logs, metrics, alarms, dashboards
  IAM         →  roles and policies for all services
  Secrets Mgr →  DB passwords, API keys (never hardcode!)
=============================================================
ARCHITECTURE

# ── Step-by-step deployment guide ──
cat << 'DEPLOYMENT_STEPS'
=== FULL-STACK DEPLOYMENT CHECKLIST ===

PHASE 1 — Foundation (done once)
  □ Create IAM roles (EC2 role, ECS task role, Lambda role)
  □ Create VPC with public/private subnets (for prod — use default for dev)
  □ Create Security Groups (ALB, ECS tasks, RDS, ElastiCache)
  □ Create ECR repositories (bookstore/api, bookstore/frontend)
  □ Create RDS PostgreSQL instance (private subnet, multi-AZ for prod)
  □ Create DynamoDB tables (carts, sessions, view history)
  □ Create S3 buckets (assets, frontend, build artifacts)
  □ Create SNS topics and SQS queues
  □ Configure CloudWatch log groups and alarms

PHASE 2 — Build & Push (CI/CD pipeline — runs on every merge to main)
  □ Run tests: mvn clean verify
  □ Build Docker image: docker build -t bookstore/api .
  □ Push to ECR: docker push ${ECR_URI}/bookstore/api:${VERSION}
  □ Build React: npm run build
  □ Deploy frontend to S3: aws s3 sync dist/ s3://bookstore-frontend/

PHASE 3 — Deploy Backend
  □ Update ECS task definition with new image tag
  □ Update ECS service (triggers rolling deployment)
  □ Monitor deployment: aws ecs describe-services --cluster bookstore-cluster
  □ Check CloudWatch logs for errors
  □ Verify health checks pass: curl https://api.bookstore.com/actuator/health

PHASE 4 — Post-Deployment Verification
  □ Smoke test: place a test order, check all microservices respond
  □ Check CloudWatch dashboard — no spike in error rates
  □ Verify SNS/SQS message flow
  □ Monitor RDS metrics — connection count, CPU, read/write IOPS
  □ Check Lambda invocations — image resizer working
DEPLOYMENT_STEPS

# ── ECS Blue/Green Deployment (zero-downtime) ──
cat << 'BLUEGREEN'
=== BLUE/GREEN DEPLOYMENT WITH ECS ===

Blue/Green = run two identical environments (blue = current, green = new)
1. Deploy NEW version to "green" ECS service
2. Run smoke tests against green
3. Switch ALB traffic to green (instant cutover)
4. Keep blue running for 10 minutes (quick rollback if needed)
5. Terminate blue

AWS CodeDeploy supports blue/green for ECS automatically.
In practice: configure in appspec.yaml, CodePipeline handles the rest.

Benefits vs Rolling Deployment:
  - Zero downtime (traffic switches atomically)
  - Easy rollback (switch traffic back to blue in seconds)
  - New version tested before receiving live traffic
BLUEGREEN

# ─────────────────────────────────────────────────────────────
# SECTION 15 — CLEANUP
# ─────────────────────────────────────────────────────────────

cleanup_part2() {
    echo "=== Part 2 Cleanup ==="

    # Delete ECS service (scale to 0 first)
    aws ecs update-service --cluster bookstore-cluster \
        --service bookstore-api-service --desired-count 0 2>/dev/null || true
    sleep 10
    aws ecs delete-service --cluster bookstore-cluster \
        --service bookstore-api-service 2>/dev/null || true
    aws ecs delete-cluster --cluster bookstore-cluster 2>/dev/null || true

    # Delete Lambda function
    aws lambda delete-function --function-name bookstore-image-resizer 2>/dev/null || true

    # Delete SQS queues
    aws sqs delete-queue --queue-url "$INVENTORY_QUEUE_URL" 2>/dev/null || true
    aws sqs delete-queue --queue-url "$DLQ_URL" 2>/dev/null || true

    # Delete SNS topic
    aws sns delete-topic --topic-arn "$ORDER_TOPIC_ARN" 2>/dev/null || true

    # Delete S3 buckets (must empty first)
    aws s3 rm "s3://${BUCKET_NAME}/" --recursive 2>/dev/null || true
    aws s3api delete-bucket --bucket "$BUCKET_NAME" 2>/dev/null || true
    aws s3 rm "s3://${WEBSITE_BUCKET}/" --recursive 2>/dev/null || true
    aws s3api delete-bucket --bucket "$WEBSITE_BUCKET" 2>/dev/null || true

    # Delete DynamoDB tables
    aws dynamodb delete-table --table-name BookstoreShoppingCarts 2>/dev/null || true
    aws dynamodb delete-table --table-name BookstoreViewHistory 2>/dev/null || true

    # Delete CloudWatch resources
    aws cloudwatch delete-alarms \
        --alarm-names bookstore-high-5xx-errors bookstore-high-cpu 2>/dev/null || true
    aws cloudwatch delete-dashboards --dashboard-names bookstore-overview 2>/dev/null || true
    aws logs delete-log-group --log-group-name /bookstore/api 2>/dev/null || true

    # Delete ECR repositories
    aws ecr delete-repository --repository-name bookstore/api --force 2>/dev/null || true
    aws ecr delete-repository --repository-name bookstore/frontend --force 2>/dev/null || true

    # Delete RDS (takes a few minutes)
    aws rds delete-db-instance --db-instance-identifier bookstore-db \
        --skip-final-snapshot 2>/dev/null || true

    echo "=== Part 2 Cleanup complete! ==="
}

# Uncomment to run:
# cleanup_part2

# ─────────────────────────────────────────────────────────────
# QUICK REFERENCE CARD — AWS PART 2 SERVICES
# ─────────────────────────────────────────────────────────────

cat << 'REFERENCE'
=== AWS Part 2 — Services Quick Reference ===

IAM:
  aws iam create-user / create-role / create-group
  aws iam attach-user-policy / attach-role-policy
  aws iam create-instance-profile            # For EC2 roles

S3:
  aws s3api create-bucket                    # Create bucket
  aws s3 cp / sync                           # Upload/download
  aws s3api put-bucket-website               # Static hosting
  aws s3 presign                             # Temp URL

ELASTIC BEANSTALK:
  eb init / eb create / eb deploy            # EB CLI
  aws elasticbeanstalk describe-applications

RDS:
  aws rds create-db-instance                 # Create DB
  aws rds describe-db-instances              # Status/endpoint
  aws rds create-db-instance-read-replica    # Read replica

ECR:
  aws ecr create-repository                  # Create repo
  aws ecr get-login-password | docker login  # Authenticate
  docker build / tag / push                  # Push images

ECS:
  aws ecs create-cluster                     # Create cluster
  aws ecs register-task-definition           # Define container
  aws ecs create-service                     # Run containers
  aws ecs update-service --force-new-deployment # Redeploy

EKS:
  eksctl create cluster                      # Create K8s cluster
  aws eks update-kubeconfig                  # Configure kubectl
  kubectl apply -f deployment.yaml           # Deploy app

LAMBDA:
  aws lambda create-function                 # Create function
  aws lambda invoke                          # Test invoke
  aws lambda update-function-code            # Deploy new code

SNS/SQS:
  aws sns create-topic / publish / subscribe
  aws sqs create-queue / send-message / receive-message / delete-message

CLOUDWATCH:
  aws logs create-log-group / put-retention-policy
  aws cloudwatch put-metric-alarm            # Create alarm
  aws cloudwatch put-metric-data             # Custom metrics
  aws cloudwatch put-dashboard               # Dashboard

DYNAMODB:
  aws dynamodb create-table                  # Create table
  aws dynamodb put-item / get-item           # CRUD
  aws dynamodb query / scan                  # Read data
  aws dynamodb update-item / delete-item
REFERENCE
