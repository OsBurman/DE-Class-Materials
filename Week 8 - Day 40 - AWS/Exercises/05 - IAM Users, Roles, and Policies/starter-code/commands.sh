#!/usr/bin/env bash
# Exercise 05: IAM Users, Roles, and Policies — Starter
# Complete each TODO. Run commands one at a time.
# Prerequisites: AWS CLI configured with admin permissions.

# ─────────────────────────────────────────────
# PART 1 — Concepts (answer in comments)
# ─────────────────────────────────────────────

# Q: What is the difference between an IAM User and an IAM Role?
# A: TODO

# Q: What does "Effect: Deny" do when combined with "Effect: Allow" for the same action?
# A: TODO

# Q: Why is giving a developer AdministratorAccess wrong for deploying to ECS?
# A: TODO


# ─────────────────────────────────────────────
# PART 2 — IAM CLI Commands
# ─────────────────────────────────────────────

# Step 4: Create IAM user "bootcamp-dev"
# TODO: aws iam create-user --user-name bootcamp-dev

# Step 5a: Create IAM group "developers"
# TODO: aws iam create-group --group-name developers

# Step 5b: Add bootcamp-dev to the developers group
# TODO: aws iam add-user-to-group --user-name bootcamp-dev --group-name developers

# Step 6: Create a customer managed policy "S3ReadOnly"
# It should allow s3:GetObject and s3:ListBucket on all resources ("*")
# TODO: Create a local file s3-readonly-policy.json with the policy JSON
# TODO: aws iam create-policy --policy-name S3ReadOnly --policy-document file://s3-readonly-policy.json

cat > s3-readonly-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:ListBucket"
      ],
      "Resource": "*"
    }
  ]
}
EOF
# TODO: Run the aws iam create-policy command using s3-readonly-policy.json
# Save the returned Policy ARN to POLICY_ARN below
POLICY_ARN="<REPLACE_WITH_POLICY_ARN>"

# Step 7: Attach S3ReadOnly to the developers group
# TODO: aws iam attach-group-policy --group-name developers --policy-arn $POLICY_ARN

# Step 8: Create IAM role "EC2S3ReadRole" with EC2 as the trusted entity
# First create the trust policy document
cat > ec2-trust-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "Service": "ec2.amazonaws.com"
    },
    "Action": "sts:AssumeRole"
  }]
}
EOF

# TODO: aws iam create-role --role-name EC2S3ReadRole
#       --assume-role-policy-document file://ec2-trust-policy.json

# TODO: Attach AmazonS3ReadOnlyAccess managed policy to EC2S3ReadRole
#       (ARN: arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess)

# Step 9: Create an access key for bootcamp-dev
# TODO: aws iam create-access-key --user-name bootcamp-dev


# ─────────────────────────────────────────────
# PART 3 — Application Roles vs Users
# ─────────────────────────────────────────────

# Q3a: EC2 app needs to write to S3. Hardcode access keys or use an IAM role?
# A3a: TODO

# Q3b: Lambda needs CloudWatch + DynamoDB access. How without creating a user?
# A3b: TODO

# Q3c: What is STS? What does sts:AssumeRole do?
# A3c: TODO

# Q3d: User has Deny s3:DeleteObject (inline) AND Allow s3:* (group). Can they delete?
# A3d: TODO
