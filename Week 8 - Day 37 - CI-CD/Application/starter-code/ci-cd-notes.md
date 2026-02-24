# CI/CD Notes — Day 37

## Part 1: Core Concepts

Fill in the definitions:

| Term | Definition |
|------|-----------|
| Continuous Integration (CI) | TODO |
| Continuous Delivery (CD) | TODO |
| Continuous Deployment | TODO |
| Pipeline | TODO |
| Job | TODO |
| Step | TODO |
| Artifact | TODO |
| Secret | TODO |

---

## Part 2: GitHub Actions Workflow Structure

Label each part of this workflow:

```yaml
name: My Pipeline            # (1) TODO: what is this?

on:                           # (2) TODO: what does `on` define?
  push:
    branches: [main]

jobs:                         # (3) TODO: what is a job?
  build:
    runs-on: ubuntu-latest    # (4) TODO: what does runs-on mean?
    steps:
      - uses: actions/checkout@v4    # (5) TODO: what is `uses`?
      - run: mvn test -B             # (6) TODO: what is `run`?
```

---

## Part 3: Security Best Practices

1. Why should you never hardcode API keys in workflow files?

   TODO

2. How do GitHub Actions secrets work?

   TODO

3. What is the principle of least privilege and how does it apply to CI/CD?

   TODO

---

## Part 4: Reflection

1. What is the main benefit of running tests automatically on every push?

   TODO

2. What is the difference between `actions/upload-artifact` and pushing to Docker Hub?

   TODO

3. Describe a scenario where a deployment pipeline would roll back automatically.

   TODO
