# Day 37 – CI/CD & DevOps | Part 2
## File: 02-infrastructure-as-code.md
## Topic: Infrastructure as Code, Environment Management, and IaC Tools

---

## 1. What Is Infrastructure as Code (IaC)?

**Infrastructure as Code** means managing and provisioning infrastructure (servers, networks, databases, load balancers, DNS) using **configuration files** checked into version control — exactly the same way you manage application code.

### The Problem IaC Solves

| Old Way (ClickOps) | IaC Way |
|---|---|
| Click through AWS Console to create a server | Write a `.tf` file that describes the server |
| Write a runbook doc that goes stale | The code IS the runbook — always current |
| "It worked in staging but not prod" — different configs | Identical configs in code, applied to both |
| Can't reproduce the environment from scratch | `terraform apply` → environment recreated in minutes |
| Manual drift — someone SSHs in and tweaks a setting | Changes go through code review and version control |
| No audit trail | Every change is a git commit with author and message |

### IaC Benefits

- **Reproducibility** — spin up an identical environment any time
- **Version control** — every infrastructure change is a commit, reviewable and revertible
- **Automation** — environments provisioned by the CI/CD pipeline, not humans
- **Documentation** — the code describes exactly what exists
- **Consistency** — dev, staging, and production are defined the same way (with different variable values)

---

## 2. IaC Tools Landscape

| Tool | Provider | What it manages |
|---|---|---|
| **Terraform** | HashiCorp (now IBM) | Cloud-agnostic — AWS, GCP, Azure, K8s |
| **AWS CloudFormation** | AWS | AWS resources only (JSON/YAML) |
| **AWS CDK** | AWS | AWS resources using TypeScript/Python/Java |
| **Pulumi** | Pulumi | Cloud-agnostic, uses real programming languages |
| **Ansible** | Red Hat | Server configuration and application deployment |
| **Helm** | CNCF | Kubernetes manifest templating |
| **Kustomize** | Kubernetes SIG | K8s manifest customization (no templating) |

**Terraform** is the most widely used cloud-agnostic IaC tool. We'll use it as our reference.

---

## 3. Terraform Core Concepts

```
┌────────────────────────────────────────────────────┐
│              Terraform Workflow                     │
│                                                     │
│  Write .tf files                                    │
│       ↓                                             │
│  terraform init   → download provider plugins      │
│       ↓                                             │
│  terraform plan   → preview what will change       │
│       ↓                                             │
│  terraform apply  → create/update infrastructure   │
│       ↓                                             │
│  terraform destroy → tear everything down          │
└────────────────────────────────────────────────────┘
```

### Key Terraform File Types

```
bookstore-infra/
├── main.tf         → resource definitions
├── variables.tf    → input variable declarations
├── outputs.tf      → values to export after apply
├── terraform.tfvars → variable values (not committed — like .env)
├── providers.tf    → cloud provider configuration
└── backend.tf      → remote state configuration (S3, GCS, Terraform Cloud)
```

---

## 4. Terraform Example — Bookstore AWS Infrastructure

```hcl
# providers.tf
# ─────────────────────────────────────────────────────────────────────────────
# Tell Terraform to use the AWS provider and which region to deploy into.
terraform {
  required_version = ">= 1.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Remote state — store Terraform state in S3 so the whole team shares it
  backend "s3" {
    bucket = "bookstore-terraform-state"
    key    = "bookstore/terraform.tfstate"
    region = "us-east-1"
    # Enable state locking with DynamoDB (prevents concurrent applies)
    dynamodb_table = "bookstore-state-lock"
    encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region
}
```

```hcl
# variables.tf
# ─────────────────────────────────────────────────────────────────────────────
# Declare all inputs — values come from terraform.tfvars or CI/CD environment vars.
variable "aws_region" {
  description = "AWS region for all resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Deployment environment: dev, staging, or production"
  type        = string
  validation {
    condition     = contains(["dev", "staging", "production"], var.environment)
    error_message = "environment must be dev, staging, or production."
  }
}

variable "app_name" {
  description = "Application name used as resource prefix"
  type        = string
  default     = "bookstore"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.micro"
}

variable "db_password" {
  description = "RDS database password — never hardcode, pass via CI/CD secrets"
  type        = string
  sensitive   = true   # Terraform will not print this value in logs
}
```

```hcl
# main.tf
# ─────────────────────────────────────────────────────────────────────────────
# SECTION 1: VPC and Networking
# ─────────────────────────────────────────────────────────────────────────────
resource "aws_vpc" "bookstore_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true

  tags = {
    Name        = "${var.app_name}-vpc-${var.environment}"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_subnet" "public" {
  count             = 2
  vpc_id            = aws_vpc.bookstore_vpc.id
  cidr_block        = "10.0.${count.index}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name        = "${var.app_name}-public-${count.index}-${var.environment}"
    Environment = var.environment
  }
}

# ─────────────────────────────────────────────────────────────────────────────
# SECTION 2: RDS PostgreSQL (managed database)
# ─────────────────────────────────────────────────────────────────────────────
resource "aws_db_instance" "bookstore_db" {
  identifier        = "${var.app_name}-db-${var.environment}"
  engine            = "postgres"
  engine_version    = "15.4"
  instance_class    = var.environment == "production" ? "db.t3.medium" : "db.t3.micro"
  allocated_storage = var.environment == "production" ? 100 : 20

  db_name  = "bookstore_db"
  username = "admin"
  password = var.db_password   # Injected from CI/CD secret — never hardcoded

  # Only accessible from within the VPC — NOT publicly accessible
  publicly_accessible = false
  skip_final_snapshot = var.environment != "production"  # Keep snapshot in prod

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

# ─────────────────────────────────────────────────────────────────────────────
# SECTION 3: EKS Cluster (Kubernetes)
# ─────────────────────────────────────────────────────────────────────────────
resource "aws_eks_cluster" "bookstore_eks" {
  name     = "${var.app_name}-eks-${var.environment}"
  role_arn = aws_iam_role.eks_cluster_role.arn

  vpc_config {
    subnet_ids = aws_subnet.public[*].id
  }

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}
```

```hcl
# outputs.tf
# ─────────────────────────────────────────────────────────────────────────────
# Export useful values after apply — used by CI/CD pipeline
output "rds_endpoint" {
  description = "RDS database endpoint URL"
  value       = aws_db_instance.bookstore_db.endpoint
}

output "eks_cluster_name" {
  description = "EKS cluster name for kubectl configuration"
  value       = aws_eks_cluster.bookstore_eks.name
}

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.bookstore_vpc.id
}
```

---

## 5. Environment Management (Dev / Staging / Production)

A well-structured IaC project manages multiple environments through **variable files** and **workspaces** — same Terraform code, different values.

```
bookstore-infra/
├── environments/
│   ├── dev.tfvars          → small instances, low cost, no backups
│   ├── staging.tfvars      → production-like, cheaper instances
│   └── production.tfvars   → high availability, backups, larger instances
├── main.tf
├── variables.tf
└── outputs.tf
```

```hcl
# environments/dev.tfvars
environment   = "dev"
instance_type = "t3.micro"
aws_region    = "us-east-1"

# environments/staging.tfvars
environment   = "staging"
instance_type = "t3.small"
aws_region    = "us-east-1"

# environments/production.tfvars
environment   = "production"
instance_type = "t3.medium"
aws_region    = "us-east-1"
```

```bash
# Deploy to dev
terraform apply -var-file="environments/dev.tfvars"

# Deploy to staging
terraform apply -var-file="environments/staging.tfvars"

# Deploy to production (with explicit approval in CI/CD)
terraform apply -var-file="environments/production.tfvars"
```

### Environment Promotion Principles

```
Code change (feature branch)
        ↓
  PR → CI passes
        ↓
  Merge to develop
        ↓
  Auto-deploy to DEV environment
  (Terraform applies dev.tfvars, kubectl applies dev K8s manifests)
        ↓
  QA testing in DEV
        ↓
  Promote to STAGING
  (Terraform applies staging.tfvars — SAME code, different scale)
        ↓
  Stakeholder acceptance testing in STAGING
        ↓
  Manual approval → deploy to PRODUCTION
  (Terraform applies production.tfvars — SAME code, production scale)
```

---

## 6. Terraform in a GitHub Actions CI/CD Pipeline

```yaml
# .github/workflows/terraform.yml
name: Infrastructure Pipeline

on:
  push:
    branches: [main]
    paths: ['infrastructure/**']   # Only trigger if infra files changed
  pull_request:
    paths: ['infrastructure/**']

jobs:
  terraform:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: infrastructure/

    steps:
      - uses: actions/checkout@v4

      - name: Setup Terraform
        uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: 1.5.7

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id:     ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region:            us-east-1

      - name: Terraform Init
        run: terraform init

      - name: Terraform Format Check
        run: terraform fmt -check -recursive

      - name: Terraform Validate
        run: terraform validate

      # On PRs: plan only (preview changes, post as PR comment)
      - name: Terraform Plan
        if: github.event_name == 'pull_request'
        run: |
          terraform plan \
            -var-file="environments/staging.tfvars" \
            -var="db_password=${{ secrets.DB_PASSWORD }}" \
            -out=tfplan
        env:
          TF_VAR_environment: staging

      # On merge to main: apply the plan
      - name: Terraform Apply
        if: github.ref == 'refs/heads/main' && github.event_name == 'push'
        run: |
          terraform apply \
            -var-file="environments/staging.tfvars" \
            -var="db_password=${{ secrets.DB_PASSWORD }}" \
            -auto-approve
        env:
          TF_VAR_environment: staging
```

---

## 7. Kubernetes Manifest Environment Management

For Kubernetes deployments, **Kustomize** and **Helm** are the standard tools for managing environment-specific configs.

### Kustomize (No Templating — Patch-Based)

```
k8s/
├── base/                          → shared config (same for all envs)
│   ├── deployment.yaml
│   ├── service.yaml
│   └── kustomization.yaml
└── overlays/
    ├── dev/
    │   ├── kustomization.yaml     → reference base, apply dev patches
    │   └── replica-patch.yaml    → replicas: 1
    ├── staging/
    │   ├── kustomization.yaml
    │   └── replica-patch.yaml    → replicas: 2
    └── production/
        ├── kustomization.yaml
        └── replica-patch.yaml    → replicas: 5
```

```bash
# Deploy to dev
kubectl apply -k k8s/overlays/dev/

# Deploy to staging
kubectl apply -k k8s/overlays/staging/

# Deploy to production
kubectl apply -k k8s/overlays/production/
```

---

## 8. IaC Best Practices

| Practice | Why |
|---|---|
| **Store state remotely** (S3, GCS) | Team shares state; no local conflicts |
| **Lock state** (DynamoDB) | Prevent two engineers applying at the same time |
| **Never hardcode secrets** | Use `sensitive = true` + CI/CD secret injection |
| **Use modules** | DRY — reuse VPC, EKS, RDS patterns across projects |
| **Separate state by environment** | `dev/terraform.tfstate`, `staging/terraform.tfstate` — blast radius isolation |
| **Plan before apply** | Always review `terraform plan` output before `terraform apply` |
| **Tag every resource** | `Environment`, `ManagedBy: Terraform`, `Owner` — essential for cost tracking |
| **Use `prevent_destroy`** | Add lifecycle block on critical resources (RDS, EKS) to prevent accidental deletion |
