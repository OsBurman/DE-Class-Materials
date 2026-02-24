# Exercise 06 — Infrastructure as Code and Environment Management
# Complete every TODO section below.

---

## Requirement 1 — IaC Concepts

### 1a: Declarative vs Imperative IaC
TODO — explain the difference and give one tool example for each.

### 1b: Idempotency
TODO — define idempotency in IaC and explain why it matters for CI/CD.

### 1c: Three categories IaC can manage
1. TODO
2. TODO
3. TODO

### 1d: Configuration drift
TODO — define configuration drift and explain how IaC prevents it.

---

## Requirement 2 — Environment Definitions

| Property | dev | staging | production |
|---|---|---|---|
| Database | TODO | TODO | TODO |
| Log level | TODO | TODO | TODO |
| Replicas / instances | TODO | TODO | TODO |
| External integrations | TODO | TODO | TODO |
| Who can deploy? | TODO | TODO | TODO |
| Deployment frequency | TODO | TODO | TODO |

---

## Requirement 4 — Environment Variables Best Practices

| Value | Storage | Reason |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=production` | TODO | TODO |
| `DATABASE_URL=jdbc:postgresql://db:5432/prod` | TODO | TODO |
| `DATABASE_PASSWORD=s3cr3t!` | TODO | TODO |
| `JAVA_VERSION=17` | TODO | TODO |
| `DOCKER_HUB_TOKEN` | TODO | TODO |
