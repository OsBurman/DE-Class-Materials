#!/usr/bin/env bash
# Exercise 04: S3 Buckets, Object Storage, and Static Website Hosting — SOLUTION

BUCKET_NAME="bootcamp-s3-yourname-2024"   # Replace with your unique name
REGION="us-east-1"

# ─────────────────────────────────────────────
# PART A — Create and Configure a Bucket
# ─────────────────────────────────────────────

# Create the bucket (us-east-1 is the default; no LocationConstraint needed)
aws s3api create-bucket \
  --bucket "$BUCKET_NAME" \
  --region "$REGION"

# Enable versioning — protects against accidental overwrites/deletes
aws s3api put-bucket-versioning \
  --bucket "$BUCKET_NAME" \
  --versioning-configuration Status=Enabled

# Block all public access — best practice for private buckets
aws s3api put-public-access-block \
  --bucket "$BUCKET_NAME" \
  --public-access-block-configuration \
    BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true


# ─────────────────────────────────────────────
# PART B — Upload and Download Objects
# ─────────────────────────────────────────────

echo "Hello from S3!" > test-file.txt

# Upload using the high-level 'aws s3 cp' command
aws s3 cp test-file.txt s3://"$BUCKET_NAME"/uploads/test-file.txt

# List all objects in the bucket recursively
aws s3 ls s3://"$BUCKET_NAME" --recursive

# Download back to local disk
aws s3 cp s3://"$BUCKET_NAME"/uploads/test-file.txt downloaded-file.txt

# Generate a pre-signed URL valid for 300 seconds
# Anyone with this URL can GET the object for 5 minutes — no AWS credentials needed
aws s3 presign s3://"$BUCKET_NAME"/uploads/test-file.txt --expires-in 300


# ─────────────────────────────────────────────
# PART C — Static Website Hosting
# ─────────────────────────────────────────────

WEBSITE_BUCKET="${BUCKET_NAME}-website"

# Create the website bucket
aws s3api create-bucket \
  --bucket "$WEBSITE_BUCKET" \
  --region "$REGION"

# Disable block public access so we can attach a public-read bucket policy
aws s3api put-public-access-block \
  --bucket "$WEBSITE_BUCKET" \
  --public-access-block-configuration \
    BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false

# Enable static website hosting
aws s3api put-bucket-website \
  --bucket "$WEBSITE_BUCKET" \
  --website-configuration '{"IndexDocument":{"Suffix":"index.html"},"ErrorDocument":{"Key":"error.html"}}'

# Attach a bucket policy allowing public read of all objects
# Without this policy, requests to the website endpoint return 403 Forbidden
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

# Create minimal index.html
cat > index.html << 'EOF'
<!DOCTYPE html>
<html>
<head><title>My S3 Website</title></head>
<body><h1>Hello from S3 Static Website!</h1></body>
</html>
EOF

aws s3 cp index.html s3://"$WEBSITE_BUCKET"/index.html

# Print the website endpoint URL
echo "Website URL: http://${WEBSITE_BUCKET}.s3-website-${REGION}.amazonaws.com"


# ─────────────────────────────────────────────
# PART D — Reflection Answers
# ─────────────────────────────────────────────

# Q1: 11 nines of durability — how?
# A1: S3 automatically replicates every object across a minimum of THREE Availability Zones
#     within a Region (for S3 Standard). Data is spread across multiple physical storage
#     devices using erasure coding. AWS also performs continuous data integrity checks
#     (checksum verification) and automatically repairs any corruption. The combination of
#     AZ-level geographic distribution, erasure coding, and proactive integrity monitoring
#     achieves 99.999999999% (11 nines) annual durability — effectively meaning you'd
#     expect to lose one object per 100 billion stored per year.

# Q2: S3 Standard vs Standard-IA vs Glacier
# A2:
#   S3 Standard: High durability + high availability (99.99%) + low latency.
#     Use for: frequently accessed data (web assets, active datasets, uploads).
#
#   S3 Standard-IA (Infrequent Access): Same durability; lower storage cost;
#     higher per-request cost + minimum 30-day storage charge.
#     Use for: backups, disaster recovery files accessed once a month or less.
#
#   S3 Glacier / Glacier Deep Archive: Lowest cost; retrieval takes minutes to hours (Glacier)
#     or up to 12 hours (Deep Archive). Minimum 90-day / 180-day storage.
#     Use for: long-term archival, compliance data, log retention for 7+ years.
#     Rule of thumb: choose based on access frequency + retrieval speed requirements.

# Q3: Versioning enabled; you delete a file — is it gone?
# A3: No. When versioning is enabled, "deleting" an object places a DELETE MARKER on the
#     latest version — the object appears deleted to normal LIST and GET requests, but all
#     previous versions are still stored. To recover:
#     1. aws s3api list-object-versions --bucket $BUCKET --prefix uploads/test-file.txt
#        → find the version ID before the delete marker
#     2. aws s3api delete-object --bucket $BUCKET --key uploads/test-file.txt \
#          --version-id <DELETE_MARKER_VERSION_ID>
#        → removing the delete marker restores the object to its previous state.
#     Or: copy a specific version back to the latest position with aws s3api copy-object.

# Q4: Why can't you host a Spring Boot API on S3?
# A4: S3 static website hosting serves only STATIC FILES (HTML, CSS, JS, images) directly
#     from object storage. It cannot EXECUTE code. A Spring Boot application is a Java
#     process that:
#       - Listens on a TCP port (e.g., 8080)
#       - Runs JVM bytecode in response to HTTP requests
#       - Connects to databases and other services
#     S3 has no JVM, no runtime, no process execution capability, and no ability to handle
#     dynamic request logic. You need a compute service (EC2, ECS, Elastic Beanstalk, or
#     Lambda) to run a Spring Boot app. S3 is appropriate only for the compiled static
#     frontend assets (React build output), not the backend.
