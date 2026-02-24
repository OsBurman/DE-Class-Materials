# Exercise 06 — Infrastructure as Code and Environment Management

## Objective
Understand Infrastructure as Code (IaC) principles and implement environment-specific configuration management using GitHub Actions environments and workflow variables.

## Background
Manually provisioning servers leads to "snowflake" infrastructure — environments that drift apart over time and are impossible to reproduce. **Infrastructure as Code** treats infrastructure definitions like source code: versioned, reviewed, and applied automatically. Paired with IaC is **environment management**: the practice of having isolated dev, staging, and production environments that receive the same application but different configuration values.

---

## Requirements

### Requirement 1 — IaC Concepts
Answer the following questions:

1. What is the difference between **declarative** and **imperative** IaC? Give one tool example for each.
2. What is **idempotency** in the context of IaC, and why is it important for CI/CD?
3. Name three categories of things IaC can manage (e.g., virtual machines, …)
4. What is **configuration drift** and how does IaC prevent it?

### Requirement 2 — Environment Definitions
For a Spring Boot application, describe the typical differences between three environments:

| Property | dev | staging | production |
|---|---|---|---|
| Database | | | |
| Log level | | | |
| Replicas / instances | | | |
| External integrations (email, payments) | | | |
| Who can deploy to it? | | | |
| How often is it deployed to? | | | |

### Requirement 3 — GitHub Actions Environments
GitHub Actions has a first-class **Environments** feature that allows you to:
- Define environment-specific secrets and variables
- Require manual approval before deploying to an environment
- Set deployment protection rules

Complete `env-workflow.yml`: a workflow that:
1. Builds and tests on every push to `main`
2. Deploys to the `staging` environment automatically after tests pass
3. Deploys to the `production` environment **only after a manual approval** in the GitHub UI
4. Uses an environment variable `APP_ENV` (set to `staging` or `production` in each job) to configure the deployment command

### Requirement 4 — Environment Variables Best Practices
For each type of configuration value, state whether it should be stored as a **GitHub Actions variable** (plain text), a **GitHub Actions secret** (encrypted), or **hardcoded in the workflow YAML**, and explain why:

| Value | Storage | Reason |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=production` | | |
| `DATABASE_URL=jdbc:postgresql://db:5432/prod` | | |
| `DATABASE_PASSWORD=s3cr3t!` | | |
| `JAVA_VERSION=17` | | |
| `DOCKER_HUB_TOKEN` | | |

---

## Hints
- Declarative IaC: Terraform, Kubernetes YAML, CloudFormation — you describe the desired end state
- Imperative IaC: Ansible, shell scripts — you describe the steps to get there
- GitHub Actions `environment:` key on a job links it to the Environment configuration in repo Settings
- Manual approval is configured in repo Settings → Environments → Required reviewers
- `${{ vars.VARIABLE_NAME }}` for plain-text variables; `${{ secrets.SECRET_NAME }}` for secrets

## Expected Output
- Completed `answers.md` (Requirements 1, 2, 4)
- Completed `env-workflow.yml` (Requirement 3)
