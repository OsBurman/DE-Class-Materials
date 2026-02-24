# Day 33 — AI & Developer Productivity
## Quick Reference Guide

---

## 1. The AI-Assisted Developer Workflow

AI tools amplify developer productivity — they do NOT replace understanding. Always review, test, and take ownership of AI-generated code.

```
Write prompt → Review suggestion → Edit/refine → Test → Commit
      ↑                                               |
      └──────── iterate if output needs work ─────────┘
```

---

## 2. GitHub Copilot — Core Features

| Feature | How to Access | Best For |
|---------|--------------|----------|
| **Inline suggestions** | Type code — accept with `Tab` | Boilerplate, repetitive patterns |
| **Copilot Chat** | `Ctrl+Shift+I` / sidebar panel | Explanations, debugging, refactoring |
| **Inline Chat** | `Ctrl+I` in editor | Edit specific code in context |
| **Explain** | Right-click → Copilot → Explain | Understand unfamiliar code |
| **Fix** | Right-click → Copilot → Fix | Quick bug fixes |
| **Generate Tests** | Right-click → Copilot → Generate Tests | Unit test scaffolding |
| **Terminal suggestion** | `Ctrl+I` in terminal | Shell commands |

---

## 3. Effective Prompting — The RACE Framework

| Letter | Stands For | Example |
|--------|-----------|---------|
| **R** | Role | "You are a senior Java developer..." |
| **A** | Action | "...write a REST endpoint that..." |
| **C** | Context | "...using Spring Boot 3 and PostgreSQL..." |
| **E** | Example/Expected | "...return a 201 response with the created entity as JSON." |

---

## 4. Prompt Engineering Best Practices

```
✅ Be specific about language, framework, and version
   "Write a Spring Boot 3 REST controller in Java 21..."

✅ Specify input/output format
   "Accept a JSON body with {name, email}; return {id, name, email, createdAt}"

✅ State constraints
   "Use constructor injection, not field injection"
   "Include error handling for duplicate email"
   "Do not use deprecated APIs"

✅ Provide context
   Paste in the interface, data model, or existing code you're extending

✅ Ask for explanation alongside code
   "Generate the code AND explain each key decision"

❌ Vague prompts produce generic code
   "Write a function that sorts things"  ← too vague

❌ Don't ask Copilot to make architectural decisions for you
   Review the output and verify it fits your design
```

---

## 5. Use Cases by Category

### Understanding Code
```
"Explain what this method does line by line"
"What does @Transactional actually do in this context?"
"Why is this causing a ConcurrentModificationException?"
```

### Writing New Code
```
"Generate a JPA entity for a Product with id, name, price, category"
"Write a Comparator that sorts employees by department then salary descending"
"Create a React component for a paginated data table with TypeScript"
```

### Refactoring
```
"Refactor this method to use the Strategy pattern"
"Convert this loop to a Stream pipeline"
"Extract this inline logic into a named method with a clear purpose"
```

### Debugging
```
"Here is my stack trace. Explain the root cause and suggest a fix: [paste trace]"
"This test is failing with [error]. What's wrong?"
"My SQL query returns duplicate rows — here's the schema and query: ..."
```

### Tests & Documentation
```
"Write JUnit 5 unit tests for this service class — include edge cases"
"Write Javadoc for this public method"
"Generate OpenAPI documentation for this controller"
```

---

## 6. AI in the SDLC

| Phase | AI Use Cases |
|-------|-------------|
| **Planning** | Estimate complexity, identify edge cases, draft acceptance criteria |
| **Design** | Suggest design patterns, review architectural decisions, generate diagrams (Mermaid) |
| **Development** | Code generation, autocompletion, boilerplate, algorithm implementation |
| **Testing** | Generate unit/integration tests, edge case identification, test data generation |
| **Code Review** | Explain unfamiliar code, spot security issues, suggest improvements |
| **Debugging** | Root cause analysis from stack traces, suggest fixes |
| **Documentation** | Generate Javadoc, README, API docs, commit messages |
| **Deployment** | Write Dockerfiles, CI/CD pipeline config, Kubernetes manifests |

---

## 7. Limitations & Trust Model

```
✅ AI is reliable for:
   - Boilerplate code (getters, setters, constructors, DTOs)
   - Common algorithm implementations (sorting, searching)
   - Translation (Java → Python, SQL → JPA)
   - Test scaffolding
   - Explaining well-known frameworks

⚠️ AI needs careful review for:
   - Business logic (may miss domain-specific rules)
   - Security-sensitive code (auth, encryption, input validation)
   - Complex edge cases
   - Performance-critical paths
   - Integration between services

❌ Do NOT trust without verification:
   - Exact API method signatures (may hallucinate)
   - Recent library versions (training data has cutoff)
   - Precise SQL query correctness for your specific schema
   - Legal/compliance-related logic
```

---

## 8. Copilot Chat Slash Commands

| Command | Purpose |
|---------|---------|
| `/explain` | Explain selected code |
| `/fix` | Suggest a fix for selected code or error |
| `/tests` | Generate tests for selected code |
| `/doc` | Generate documentation |
| `/simplify` | Simplify selected code |
| `/optimize` | Suggest performance improvements |
| `@workspace` | Include workspace context in question |
| `@terminal` | Ask about the last terminal command |
| `#file:path` | Reference a specific file in chat |
| `#selection` | Reference current selection |

---

## 9. Writing Better Code With AI — Tips

```
1. Write the signature first, let AI fill the body
   public List<User> findActiveUsersByDepartment(String dept) {
       // cursor here → Copilot suggests implementation
   }

2. Write a descriptive comment, accept the generated code
   // Parse the ISO-8601 date string and return milliseconds since epoch
   // Handle null input by returning -1

3. Use tests as a spec — write the test first, generate code to pass it

4. Iterate — if first suggestion is wrong, add more context and retry

5. Ask Copilot to explain its own output
   "Explain the tradeoffs of this approach vs [alternative]"
```

---

## 10. Prompt Templates

### Generate a class
```
Create a [Language] class named [ClassName] that:
- [Responsibility 1]
- [Responsibility 2]
Use [framework/pattern]. Include constructor injection.
Fields: [field list with types]. Implement [interface if any].
```

### Debug an error
```
I'm getting this error in [Language/Framework]:
[paste error/stack trace]

Here is the relevant code:
[paste code]

Context: [what the code is supposed to do]

What is the root cause and how do I fix it?
```

### Generate tests
```
Write [JUnit 5 / Jest / etc.] unit tests for the following [class/function]:
[paste code]

Cover: happy path, null/empty inputs, boundary values, and [specific edge case].
Mock: [dependencies to mock].
```

### Explain code
```
Explain what this code does, line by line.
Focus on: [specific part to understand].
Assume I know [language] but not [framework/library].
[paste code]
```

---

## 11. AI Security Considerations

```
⚠️ Never paste into AI chat:
   - Passwords, API keys, secrets
   - Personal Identifiable Information (PII) — user emails, names, SSNs
   - Proprietary business logic that must stay confidential
   - Database connection strings with credentials

✅ Safe to share:
   - Generic code structures without sensitive data
   - Sanitised stack traces (remove usernames, paths, IPs)
   - Public API usage questions

✅ Review AI-generated code for:
   - SQL injection vulnerabilities
   - Missing input validation
   - Hardcoded credentials
   - Insecure direct object references
   - Missing authentication/authorization checks
```
