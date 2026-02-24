#!/usr/bin/env bash
# Exercise 04: S3 Buckets, Object Storage, and Static Website Hosting
# Prerequisites: AWS CLI configured (aws configure)
# Replace <YOUR-UNIQUE-BUCKET-NAME> with a globally unique name (e.g., bootcamp-s3-yourname-2024)

BUCKET_NAME="<YOUR-UNIQUE-BUCKET-NAME>"
REGION="us-east-1"

# ─────────────────────────────────────────────
# PART A — Create and Configure a Bucket
# ─────────────────────────────────────────────

# Step 1: Create an S3 bucket (bucket names must be globally unique across all AWS accounts)
# TODO: aws s3api create-bucket with --bucket $BUCKET_NAME and --region $REGION
#       Note: for us-east-1, do NOT use --create-bucket-configuration (it's the default)
#       For all other regions, add: --create-bucket-configuration LocationConstraint=$REGION

# Step 2: Enable versioning on the bucket (protects against accidental deletion/overwrites)
# TODO: aws s3api put-bucket-versioning --bucket $BUCKET_NAME
#       with --versioning-configuration Status=Enabled

# Step 3: Block all public access (default — good security practice for private buckets)
# TODO: aws s3api put-public-access-block --bucket $BUCKET_NAME
#       with all four block settings set to true


# ─────────────────────────────────────────────
# PART B — Upload and Download Objects
# ─────────────────────────────────────────────

# Step 4: Create a local test file to upload
echo "Hello from S3!" > test-file.txt

# Step 5: Upload the file to the bucket under the key "uploads/test-file.txt"
# TODO: aws s3 cp test-file.txt s3://$BUCKET_NAME/uploads/test-file.txt

# Step 6: List all objects in the bucket
# TODO: aws s3 ls s3://$BUCKET_NAME --recursive

# Step 7: Download the file back to local disk as "downloaded-file.txt"
# TODO: aws s3 cp s3://$BUCKET_NAME/uploads/test-file.txt downloaded-file.txt

# Step 8: Generate a pre-signed URL (valid for 300 seconds) for the object
# (allows temporary public access without making the bucket public)
# TODO: aws s3 presign s3://$BUCKET_NAME/uploads/test-file.txt --expires-in 300


# ─────────────────────────────────────────────
# PART C — Static Website Hosting
# ─────────────────────────────────────────────

WEBSITE_BUCKET="<YOUR-UNIQUE-BUCKET-NAME>-website"

# Step 9: Create a second bucket for static website hosting
# TODO: aws s3api create-bucket for $WEBSITE_BUCKET

# Step 10: Disable the block public access setting on the website bucket
# (required before you can attach a bucket policy that allows public reads)
# TODO: aws s3api put-public-access-block --bucket $WEBSITE_BUCKET
#       with BlockPublicAcls=false, IgnorePublicAcls=false,
#            BlockPublicPolicy=false, RestrictPublicBuckets=false

# Step 11: Enable static website hosting on the bucket
# TODO: aws s3api put-bucket-website --bucket $WEBSITE_BUCKET
#       with --website-configuration:
#       '{"IndexDocument":{"Suffix":"index.html"},"ErrorDocument":{"Key":"error.html"}}'

# Step 12: Attach a bucket policy that allows public read of all objects
# TODO: aws s3api put-bucket-policy --bucket $WEBSITE_BUCKET
#       The policy JSON should allow s3:GetObject from Principal: "*" on Resource: arn:aws:s3:::$WEBSITE_BUCKET/*

# Step 13: Create and upload a minimal index.html
cat > index.html << 'EOF'
<!DOCTYPE html>
<html>
<head><title>My S3 Website</title></head>
<body><h1>Hello from S3 Static Website!</h1></body>
</html>
EOF
# TODO: aws s3 cp index.html s3://$WEBSITE_BUCKET/index.html

# Step 14: Get the website endpoint URL (format: http://<bucket>.s3-website-<region>.amazonaws.com)
# TODO: Print the website URL using the bucket name and region variables
echo "Website URL: http://$WEBSITE_BUCKET.s3-website-$REGION.amazonaws.com"


# ─────────────────────────────────────────────
# PART D — Reflection Questions
# ─────────────────────────────────────────────

# Q1: S3 is described as "11 nines of durability" (99.999999999%). How does AWS achieve this?
# A1: TODO

# Q2: What is the difference between S3 Standard, S3 Standard-IA, and S3 Glacier?
#     When would you use each?
# A2: TODO

# Q3: An S3 bucket has versioning enabled. You delete a file. Is it permanently gone?
#     How do you recover it?
# A3: TODO

# Q4: Why can't you host a dynamic Spring Boot API on S3 (static website hosting)?
# A4: TODO
