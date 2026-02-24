#!/usr/bin/env bash
# Exercise 05: IAM Users, Roles, and Policies — SOLUTION

# ─────────────────────────────────────────────
# PART 1 — Concepts
# ─────────────────────────────────────────────

# Q: IAM User vs IAM Role?
# A: An IAM User is a permanent identity with long-term credentials (password + access keys)
#    assigned to a specific person or application. It always has the same ARN and permissions.
#    An IAM Role is a temporary identity with NO permanent credentials — it issues short-lived
#    tokens via STS when assumed. Roles are assumed by AWS services (EC2, Lambda), other
#    accounts, or federated identities. The key rule: use Users for humans accessing the
#    console/CLI; use Roles for applications and AWS services.

# Q: Effect: Deny overrides Effect: Allow?
# A: Yes. In IAM, an explicit Deny ALWAYS overrides any Allow, regardless of where the Allow
#    comes from (group, inline, managed policy). The evaluation order is:
#    1. Is there an explicit Deny? → DENY immediately.
#    2. Is there an explicit Allow? → ALLOW.
#    3. Neither? → IMPLICIT DENY (default-deny).
#    This means a single Deny policy attached anywhere in the chain can revoke a permission
#    granted by a broader Allow policy.

# Q: AdministratorAccess for ECS deploy — why wrong?
# A: AdministratorAccess grants unrestricted access to ALL AWS services and ALL resources.
#    A developer deploying to ECS only needs:
#      - ecs:RegisterTaskDefinition, ecs:UpdateService, ecs:DescribeServices
#      - ecr:GetAuthorizationToken, ecr:BatchCheckLayerAvailability, ecr:PutImage
#      - iam:PassRole (to pass the ECS task execution role)
#    Granting AdministratorAccess violates the Principle of Least Privilege. If the
#    developer's credentials are compromised, the attacker gets full account access —
#    including the ability to create new admin users, delete all resources, or exfiltrate data.


# ─────────────────────────────────────────────
# PART 2 — IAM CLI Commands
# ─────────────────────────────────────────────

# Create IAM user
aws iam create-user --user-name bootcamp-dev

# Create group and add user
aws iam create-group --group-name developers
aws iam add-user-to-group --user-name bootcamp-dev --group-name developers

# Create customer managed policy
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

# Capture the Policy ARN from the response
POLICY_ARN=$(aws iam create-policy \
  --policy-name S3ReadOnly \
  --policy-document file://s3-readonly-policy.json \
  --query 'Policy.Arn' \
  --output text)

echo "Policy ARN: $POLICY_ARN"

# Attach policy to group
aws iam attach-group-policy \
  --group-name developers \
  --policy-arn "$POLICY_ARN"

# Create trust policy for EC2 to assume the role
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

# Create the role
aws iam create-role \
  --role-name EC2S3ReadRole \
  --assume-role-policy-document file://ec2-trust-policy.json

# Attach AWS managed read-only S3 policy to the role
aws iam attach-role-policy \
  --role-name EC2S3ReadRole \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess

# Create access key for bootcamp-dev (save output securely — shown only once)
aws iam create-access-key --user-name bootcamp-dev


# ─────────────────────────────────────────────
# PART 3 — Application Roles vs Users
# ─────────────────────────────────────────────

# Q3a: IAM Role vs access keys for EC2 app writing to S3
# A3a: Use an IAM ROLE attached to the EC2 instance (instance profile). Never hardcode keys.
#      Reason 1 — Security: Access keys are static; if leaked via git or logs, they are valid
#      until manually rotated. IAM role credentials are temporary (expire every ~1 hour) and
#      auto-rotated by the instance metadata service (IMDS).
#      Reason 2 — No credential management: you don't need to distribute, rotate, or revoke
#      access keys. The AWS SDK automatically fetches credentials from
#      http://169.254.169.254/latest/meta-data/iam/security-credentials/<role-name>.
#      Reason 3 — Auditability: CloudTrail logs show which role made each API call, tied to
#      the specific instance.

# Q3b: Lambda → CloudWatch + DynamoDB without a user
# A3b: Attach an IAM EXECUTION ROLE to the Lambda function with:
#        - logs:CreateLogGroup, logs:CreateLogStream, logs:PutLogEvents (CloudWatch Logs)
#        - dynamodb:GetItem, dynamodb:PutItem, dynamodb:Query (DynamoDB)
#      When Lambda invokes the function, it assumes this role via STS and receives temporary
#      credentials automatically. No user, no access keys needed. This is the standard
#      serverless security pattern.

# Q3c: STS and AssumeRole
# A3c: AWS Security Token Service (STS) issues TEMPORARY security credentials (Access Key ID,
#      Secret Access Key, Session Token) that expire after a configurable duration (15 min to
#      12 hours). sts:AssumeRole is the API call that exchanges a long-term identity (user or
#      role) for temporary credentials scoped to a specific role. This enables:
#        - Cross-account access (Account A assumes a role in Account B)
#        - Service-to-service delegation (Lambda assumes an ECS role)
#        - Federation (Active Directory user assumes an AWS role via SAML/OIDC)
#      The calling principal must be listed in the role's Trust Policy to be allowed to assume it.

# Q3d: Deny s3:DeleteObject (inline) + Allow s3:* (group). Can they delete?
# A3d: NO. The explicit Deny wins. IAM evaluation logic:
#      1. Check for explicit Deny → found in inline policy: Deny s3:DeleteObject
#      2. An explicit Deny ALWAYS overrides any Allow, regardless of source.
#      Even though the group policy grants Allow s3:* (which includes s3:DeleteObject),
#      the explicit Deny in the inline policy revokes it. The user can perform all other
#      S3 actions (GetObject, PutObject, ListBucket, etc.) but cannot delete objects.
