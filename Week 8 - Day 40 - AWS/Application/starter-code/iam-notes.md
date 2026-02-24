# IAM — Identity & Access Management Notes

## Core IAM Concepts

| Concept | Definition | Example |
|---------|-----------|---------|
| **IAM User** | TODO | e.g., "developer-john" |
| **IAM Group** | TODO | e.g., "Developers" |
| **IAM Role** | TODO | e.g., EC2 instance role |
| **IAM Policy** | TODO | e.g., AmazonS3FullAccess |
| **Root Account** | TODO | First AWS login |

---

## Policy Statement Structure

Dissect this policy and label each field:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowReadOnlyS3",
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::my-bucket",
                "arn:aws:s3:::my-bucket/*"
            ]
        }
    ]
}
```

**Fill in:**

1. `Effect: "Allow"` means: ___________________________
2. The opposite of `"Allow"` is: ___________________________
3. `s3:GetObject` is a: ___________________________
4. `arn:aws:s3:::my-bucket/*` means: ___________________________
5. Why are there TWO Resource ARNs (bucket + bucket/*)? ___________________________

---

## Principle of Least Privilege

**Definition:** Give a user/service ONLY the permissions it needs — nothing more.

### Task: Write the Minimal Policy

Your Spring Boot app needs to:
- Upload files to S3
- Download/read files from S3
- List files in the bucket
- Delete files from S3

Write the minimal IAM policy below. Use only the actions you actually need:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "TODO",
            "Action": [
                "TODO: add only the required actions"
            ],
            "Resource": [
                "TODO: restrict to specific bucket only"
            ]
        }
    ]
}
```

**Relevant S3 Actions:**
- `s3:PutObject` — upload
- `s3:GetObject` — download
- `s3:DeleteObject` — delete
- `s3:ListBucket` — list objects
- `s3:GetBucketLocation` — get bucket region

---

## IAM Best Practices

Check off each one you understand:

- [ ] Never use root account credentials in code
- [ ] Never hardcode access keys in source code
- [ ] Use environment variables or IAM roles (not key files on servers)
- [ ] Rotate access keys every 90 days
- [ ] Use IAM roles for EC2/ECS/Lambda — no keys needed at all
- [ ] Enable MFA on root account
- [ ] Use separate IAM users for each developer

---

## Comparing Credential Methods

| Method | Where used | Pros | Cons |
|--------|-----------|------|------|
| Environment variables | Local dev / CI | Easy, no SDK config | Must manage rotation |
| `~/.aws/credentials` file | Local dev | Auto-picked up by SDK | Only for local, never ship |
| IAM Role on EC2/ECS | Production on AWS | No keys at all, auto-rotated | AWS-hosted only |
| Hardcoded in code | ❌ NEVER | TODO | TODO |

---

## Reflection Questions

1. A teammate pushes their AWS access keys to a public GitHub repo. What happens and what should they do immediately?

   TODO

2. You're deploying to Elastic Beanstalk. Should you set `AWS_ACCESS_KEY_ID` as an environment variable in EB, or use an IAM role attached to the EC2 instance? Why?

   TODO

3. What is the difference between authentication and authorization in the context of IAM?

   TODO
