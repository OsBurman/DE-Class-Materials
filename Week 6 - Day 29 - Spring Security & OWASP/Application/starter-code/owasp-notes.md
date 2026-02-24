# OWASP Top 10 Notes — Day 29: Spring Security

## Instructions
For each OWASP Top 10 item below, fill in:
1. **What it is** — a brief description of the vulnerability
2. **Spring Security / mitigation** — how Spring Security helps, or what other control is needed

---

### A01: Broken Access Control
**What it is:**
> TODO: Your answer here

**Mitigation:**
> TODO: How @PreAuthorize, SecurityFilterChain, etc. help

---

### A02: Cryptographic Failures
**What it is:**
> TODO:

**Mitigation:**
> TODO: BCryptPasswordEncoder, HTTPS, avoiding weak algorithms

---

### A03: Injection
**What it is:**
> TODO: SQL injection, command injection, etc.

**Mitigation:**
> TODO: Parameterized queries, input sanitization (SanitizationUtil), @Valid

---

### A04: Insecure Design
**What it is:**
> TODO:

**Mitigation:**
> TODO:

---

### A05: Security Misconfiguration
**What it is:**
> TODO: Default credentials, exposed actuator endpoints, CORS wildcards

**Mitigation:**
> TODO: spring.security configuration, actuator exposure settings

---

### A06: Vulnerable and Outdated Components
**What it is:**
> TODO:

**Mitigation:**
> TODO: Keeping dependencies updated (mvn dependency:check, Dependabot)

---

### A07: Identification and Authentication Failures
**What it is:**
> TODO: Weak passwords, no brute-force protection, insecure session management

**Mitigation:**
> TODO: BCrypt, session management settings, JWT expiry

---

### A08: Software and Data Integrity Failures
**What it is:**
> TODO:

**Mitigation:**
> TODO:

---

### A09: Security Logging and Monitoring Failures
**What it is:**
> TODO:

**Mitigation:**
> TODO: SLF4J logging of auth events, Actuator metrics

---

### A10: Server-Side Request Forgery (SSRF)
**What it is:**
> TODO:

**Mitigation:**
> TODO:

---

## Reflection

**Which OWASP vulnerability do you think is most common in junior developers' code? Why?**
> TODO: Your answer here
