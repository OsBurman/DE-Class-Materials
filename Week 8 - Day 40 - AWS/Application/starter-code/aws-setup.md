# AWS Setup Guide — Day 40

## Prerequisites

- AWS account (free tier is sufficient for today)
- AWS CLI installed: `brew install awscli`
- Configure CLI: `aws configure`

---

## Part 1: S3 Bucket Setup

### 1. Create an S3 Bucket

```bash
# Replace YOUR-BUCKET-NAME with a globally unique name (e.g., academy-files-jsmith-2024)
aws s3 mb s3://YOUR-BUCKET-NAME --region us-east-1
```

### 2. Configure Bucket for the App

```bash
# Block all public access (files accessed via pre-signed URLs only)
aws s3api put-public-access-block \
    --bucket YOUR-BUCKET-NAME \
    --public-access-block-configuration "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
```

### 3. Set Environment Variable

```bash
export S3_BUCKET_NAME=YOUR-BUCKET-NAME
export AWS_REGION=us-east-1
```

---

## Part 2: IAM — Least Privilege Policy

### TODO: Fill in the policy document below

Create a file `s3-policy.json`:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "TODO",
            "Effect": "Allow",
            "Action": [
                "TODO: list the minimum S3 actions needed"
            ],
            "Resource": [
                "arn:aws:s3:::YOUR-BUCKET-NAME",
                "arn:aws:s3:::YOUR-BUCKET-NAME/*"
            ]
        }
    ]
}
```

```bash
# Create IAM policy
aws iam create-policy \
    --policy-name AcademyS3Policy \
    --policy-document file://s3-policy.json

# Create IAM user for local development
aws iam create-user --user-name academy-dev

# Attach policy to user
aws iam attach-user-policy \
    --user-name academy-dev \
    --policy-arn arn:aws:iam::YOUR-ACCOUNT-ID:policy/AcademyS3Policy

# Create access keys
aws iam create-access-key --user-name academy-dev
```

---

## Part 3: Elastic Beanstalk Deployment

### 1. Install EB CLI

```bash
pip install awsebcli
```

### 2. Initialize EB Application

```bash
# From project root
eb init aws-app --platform java-17 --region us-east-1
```

### 3. Create Environment

```bash
eb create academy-prod \
    --instance-type t3.micro \
    --single
```

### 4. Set Environment Variables

```bash
eb setenv \
    S3_BUCKET_NAME=YOUR-BUCKET-NAME \
    AWS_REGION=us-east-1
```

### 5. Deploy

```bash
# Build JAR first
mvn package -DskipTests

# Deploy
eb deploy
```

### 6. Open in Browser

```bash
eb open
```

---

## Part 4: Key Concepts

Fill in the answers:

1. **What is IAM?**

   TODO

2. **What is the Principle of Least Privilege?**

   TODO

3. **What is a pre-signed URL and when would you use it?**

   TODO

4. **What is the difference between S3 Standard and S3 Glacier?**

   TODO

5. **How does Elastic Beanstalk differ from deploying on an EC2 instance directly?**

   TODO
