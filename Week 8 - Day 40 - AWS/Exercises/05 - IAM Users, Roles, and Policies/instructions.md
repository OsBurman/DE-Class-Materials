# Exercise 05: IAM Users, Roles, and Policies

## Objective

Configure AWS Identity and Access Management (IAM) users, groups, roles, and policies to enforce the principle of least privilege across AWS services.

## Background

IAM is the security control plane for every AWS account. Every API call you make to AWS — whether from the console, CLI, or application code — is authenticated (who are you?) and authorized (what are you allowed to do?) by IAM. Misconfigured IAM is the leading cause of cloud security breaches. Understanding **users**, **groups**, **roles**, and **policies** — and the critical distinction between them — is foundational for every AWS deployment.

## Requirements

### Part 1 — IAM Concepts

1. Define each IAM concept and identify the key differences:

   | Concept | Definition | Key characteristic |
   |---|---|---|
   | IAM User | | |
   | IAM Group | | |
   | IAM Role | | |
   | IAM Policy | | |
   | AWS Managed Policy | | |
   | Customer Managed Policy | | |
   | Inline Policy | | |

2. **Principal, Action, Resource.** IAM policy documents use these three elements. Explain each and fill in the blanks in this policy JSON:

   ```json
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Effect": "Allow",
       "Principal": "___",         // Who is allowed? (user, role, service, *)
       "Action": ["s3:GetObject"], // What action?
       "Resource": "___"           // On what resource (ARN)?
     }]
   }
   ```

   - What does `"Effect": "Deny"` do when combined with `"Effect": "Allow"` for the same action? Which takes precedence?

3. **Principle of Least Privilege.** A developer asks for `AdministratorAccess` IAM policy to deploy a Spring Boot app to ECS. Explain why this is wrong, and describe what permissions they actually need.

### Part 2 — Hands-On: AWS CLI IAM Commands

Using the AWS CLI, complete the following tasks in the starter file:

4. Create an IAM user named `bootcamp-dev`.
5. Create an IAM group named `developers` and add `bootcamp-dev` to it.
6. Create a **customer managed policy** named `S3ReadOnly` that allows `s3:GetObject` and `s3:ListBucket` on all resources.
7. Attach the `S3ReadOnly` policy to the `developers` group.
8. Create an IAM role named `EC2S3ReadRole` that:
   - Has EC2 as the **trusted entity** (trust policy allows EC2 to assume the role)
   - Has `AmazonS3ReadOnlyAccess` attached
9. Create an **access key** for `bootcamp-dev` (for CLI use).

### Part 3 — IAM Roles vs Users for Applications

10. Answer these questions:

    a. Your Spring Boot application running on EC2 needs to write to an S3 bucket. Should you create an IAM user with access keys and hardcode them in `application.yml`, or create an IAM role attached to the EC2 instance? Explain which is more secure and why.

    b. A Lambda function needs to write logs to CloudWatch and read from DynamoDB. How do you grant it these permissions without creating a user?

    c. What is the **AWS Security Token Service (STS)** and what does `sts:AssumeRole` do?

    d. An IAM user has `Deny s3:DeleteObject` attached via an inline policy AND `Allow s3:*` via a group policy. Can they delete S3 objects? Explain the evaluation logic.

## Hints

- IAM **Users** are for humans or applications that need long-term credentials. **Roles** are for temporary credentials assumed by AWS services, applications, or cross-account access.
- A **trust policy** on a role defines *who can assume the role* (the trusted principal). A **permission policy** defines *what the role can do*.
- IAM Deny always overrides Allow. The evaluation order: explicit Deny → explicit Allow → implicit Deny.
- EC2 instance profiles are the mechanism for attaching IAM roles to EC2 instances — the instance metadata service provides rotating temporary credentials to any process running on the instance.
- Never store IAM access keys in code, `.env` files checked into git, or `application.yml`. Use roles, secrets manager, or environment variables.

## Expected Output

```
Part 1: All tables filled in + policy explanation + least privilege answer

Part 2:
  aws iam create-user --user-name bootcamp-dev → User ARN: arn:aws:iam::ACCOUNT:user/bootcamp-dev
  aws iam create-group --group-name developers → Group created
  Policy S3ReadOnly created + attached to developers group
  Role EC2S3ReadRole created with trust policy for ec2.amazonaws.com

Part 3: Written answers for a–d
```
