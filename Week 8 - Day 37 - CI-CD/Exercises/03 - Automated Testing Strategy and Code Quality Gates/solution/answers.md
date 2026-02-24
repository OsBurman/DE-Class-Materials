# Exercise 03 — Automated Testing Strategy and Code Quality Gates — SOLUTION

---

## Requirement 1 — The Test Pyramid

```
            /\
           /E2E\       ← End-to-End / UI tests  (fewest, slowest, widest scope)
          /------\
         /  Integ \    ← Integration tests  (moderate number, moderate speed)
        /----------\
       /    Unit    \  ← Unit tests  (most numerous, fastest, narrowest scope)
      /______________\
```

| Layer | Approx. % of suite | Speed | Scope | Example in a Spring Boot app |
|---|---|---|---|---|
| Unit | ~70% | Fast (milliseconds) | Single class or method in isolation | Testing `OrderService.calculateTotal()` with mocked repository |
| Integration | ~20% | Medium (seconds) | Multiple components working together, often with a real DB | `@SpringBootTest` test that hits a real H2 database via `OrderRepository` |
| End-to-End | ~10% | Slow (tens of seconds) | Full stack from HTTP request to DB and back | Selenium or REST-Assured test that calls `POST /orders` and verifies the response and DB state |

---

## Requirement 2 — Test Types in the Pipeline

| Test Type | Pipeline Stage | Blocks Pipeline? | Command / Tool |
|---|---|---|---|
| Unit tests | Test | ✅ Yes — failure = no deploy | `mvn test` (Surefire plugin) |
| Integration tests | Integration Test | ✅ Yes — failure = no deploy | `mvn verify` (Failsafe plugin, classes ending `IT`) |
| Smoke tests | Post-Deploy (Staging) | ✅ Yes — failure = rollback staging | `curl /actuator/health` or Newman/Postman collection |
| Contract tests | Test (alongside unit) | ✅ Yes | Spring Cloud Contract or Pact |
| Performance / load tests | Scheduled nightly | ⚠️ Warning only (optional block) | k6, Gatling, JMeter |

*Performance tests are run on a schedule rather than on every commit to avoid slowing the main feedback loop.*

---

## Requirement 3 — Code Quality Gate Rules

| Rule | Metric | Threshold | Why It Matters |
|---|---|---|---|
| 1 | New Code Coverage | ≥ 80% line coverage | Ensures new features ship with meaningful tests; prevents coverage from eroding over time |
| 2 | New Bugs | 0 blocker / critical bugs | SonarQube detects null-pointer risks, resource leaks, and logic errors; zero tolerance keeps the codebase clean |
| 3 | New Security Vulnerabilities | 0 | Blocks code with known security hotspots (SQL injection, hardcoded credentials) before they reach production |
| 4 | New Code Duplications | ≤ 3% duplicated lines | High duplication signals copy-paste code that is expensive to maintain and indicates missing abstractions |

---

## Requirement 4 — Static Analysis (Checkstyle)

1. **Maximum line length of 120 characters:**
   - Module: `LineLength`
   - Property: `max = 120`

2. **No wildcard imports:**
   - Module: `AvoidStarImport`
   - Property: *(no extra properties needed — the module itself enforces the rule)*

3. **Public methods must have Javadoc:**
   - Module: `JavadocMethod`
   - Key properties: `scope = public`, `allowMissingParamTags = false`, `allowMissingReturnTag = false`

---

## Requirement 5 — JaCoCo Coverage Reporting

### 5a: Generate the coverage report
Add the JaCoCo Maven plugin to `pom.xml` and run:
```
mvn verify
```
The plugin binds to the `prepare-agent` (before tests) and `report` (after tests) lifecycle phases automatically. The HTML report is written to `target/site/jacoco/index.html`.

### 5b: Fail the build if line coverage < 70%
Add a `<execution>` block with the `check` goal to the JaCoCo plugin configuration in `pom.xml`:

```xml
<execution>
  <id>jacoco-check</id>
  <goals><goal>check</goal></goals>
  <configuration>
    <rules>
      <rule>
        <element>BUNDLE</element>
        <limits>
          <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.70</minimum>
          </limit>
        </limits>
      </rule>
    </rules>
  </configuration>
</execution>
```
Running `mvn verify` will now fail with a build error if line coverage is below 70%.

### 5c: Upload the report for CI/CD dashboard visibility
In GitHub Actions, use the `actions/upload-artifact` action to save `target/site/jacoco/` as a build artefact:
```yaml
- name: Upload coverage report
  uses: actions/upload-artifact@v4
  with:
    name: jacoco-report
    path: target/site/jacoco/
```
Alternatively, integrate with a SonarQube server using `mvn sonar:sonar` — SonarQube displays coverage trends on its dashboard across every build.
