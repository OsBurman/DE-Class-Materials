# Exercise 06 — Infrastructure as Code and Environment Management — SOLUTION

---

## Requirement 1 — IaC Concepts

### 1a: Declarative vs Imperative IaC
**Declarative IaC** describes the *desired end state* of infrastructure, and a tool figures out the steps to reach it. You say "I want 3 servers with these properties" and the tool creates, updates, or deletes whatever is needed. Example tools: **Terraform**, **Kubernetes YAML manifests**, **AWS CloudFormation**.

**Imperative IaC** describes the *sequence of steps* to execute. You say "run this script to install Nginx, then open port 80, then start the service." Example tools: **Ansible playbooks**, **shell scripts**, **Chef recipes**.

Declarative is preferred for infrastructure that needs to be reproducible and auditable; imperative is useful for tasks that are inherently procedural (e.g., data migrations).

### 1b: Idempotency
An operation is **idempotent** if running it multiple times produces the same result as running it once. In IaC, this means applying a Terraform plan or a Kubernetes manifest 10 times leaves the infrastructure in exactly the same state as applying it once — no duplicate resources, no unintended side effects. Idempotency is critical for CI/CD because pipelines may re-run due to transient failures, and each re-run must not create additional resources or break the environment.

### 1c: Three categories IaC can manage
1. **Compute resources** — virtual machines, containers, serverless functions (EC2, ECS, Lambda)
2. **Networking** — VPCs, subnets, security groups, load balancers, DNS records
3. **Data and storage** — managed databases (RDS), object storage buckets (S3), secrets managers (AWS Secrets Manager, HashiCorp Vault)

### 1d: Configuration drift
**Configuration drift** occurs when the actual state of infrastructure diverges from its intended state over time — typically because someone made a manual change (a "hotfix" on a production server) that was never recorded. IaC prevents drift by making the YAML or HCL file the single source of truth: every change is committed to Git, reviewed, and applied via the pipeline. Scheduled `terraform plan` or `kubectl diff` runs can detect and alert on drift between the declared state and the live environment.

---

## Requirement 2 — Environment Definitions

| Property | dev | staging | production |
|---|---|---|---|
| Database | Local H2 or Docker PostgreSQL (dev machine) | Shared PostgreSQL on staging cluster (mirrors prod schema) | Managed RDS Multi-AZ PostgreSQL |
| Log level | `DEBUG` or `TRACE` — maximum verbosity | `INFO` | `WARN` or `ERROR` — minimal noise, retain for 30 days |
| Replicas / instances | 1 | 2 (enough to test load balancing) | 3+ with HPA auto-scaling |
| External integrations | Mocked / stubbed (WireMock, Mailtrap) | Sandbox APIs (Stripe test mode, SendGrid sandbox) | Live production APIs |
| Who can deploy? | Any developer (on their machine or on push to feature branch) | CI/CD pipeline automatically on push to `develop` | CI/CD pipeline + mandatory manual approval from a senior engineer |
| Deployment frequency | Many times per day (every commit) | Multiple times per day (after every PR merge) | Once per sprint or on-demand (after manual approval) |

---

## Requirement 4 — Environment Variables Best Practices

| Value | Storage | Reason |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=production` | GitHub Actions **variable** (`vars.`) | Non-sensitive; can be safely visible in logs and workflow YAML |
| `DATABASE_URL=jdbc:postgresql://db:5432/prod` | GitHub Actions **variable** (`vars.`) | The URL is not a secret by itself (the password is separate); storing as a plain variable makes debugging easier |
| `DATABASE_PASSWORD=s3cr3t!` | GitHub Actions **secret** (`secrets.`) | Credentials must never appear in logs, workflow output, or source control |
| `JAVA_VERSION=17` | **Hardcoded in workflow YAML** | This is a build tooling constant, not an environment difference; hardcoding makes the workflow self-documenting |
| `DOCKER_HUB_TOKEN` | GitHub Actions **secret** (`secrets.`) | Registry tokens are credentials — exposure allows anyone to push malicious images to your registry |
