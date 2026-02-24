# Maven vs Gradle Comparison and Coding Standards — Solution

---

## Part 1 — Maven vs Gradle Comparison Table

| Dimension | Maven | Gradle |
|---|---|---|
| Configuration language/format | XML (`pom.xml`) | Groovy DSL (`build.gradle`) or Kotlin DSL (`build.gradle.kts`) |
| Dependency declaration syntax | `<dependency><groupId>...</groupId><artifactId>...</artifactId><version>...</version></dependency>` | `implementation 'group:artifact:version'` |
| Build model | Fixed **lifecycle** of ordered phases (validate → compile → test → package → install → deploy) | Flexible **task graph** — tasks declare dependencies on other tasks; only needed tasks run |
| Default project structure | `src/main/java`, `src/main/resources`, `src/test/java`, `src/test/resources` | Identical to Maven by default (Gradle adopts the same convention) |
| Performance | Slower incremental builds; no build cache by default (added via plugins) | Incremental builds, build cache, parallel execution, and a persistent daemon out of the box — significantly faster on large projects |
| Flexibility (custom logic) | Verbose — requires writing or configuring plugins in XML; hard to express conditionals | Native Groovy/Kotlin scripting in `build.gradle` — any conditional logic, loops, or custom tasks are straightforward |
| IDE support | Excellent — IntelliJ IDEA, Eclipse, VS Code all support Maven natively | Excellent — IntelliJ IDEA and Android Studio have first-class Gradle support |
| Best suited for | Enterprise Java/Spring Boot projects, projects requiring strict reproducibility, teams new to build tools | Android development (the only supported tool), large multi-module builds, projects needing custom build logic |

---

## Part 2 — When to Choose Which

**Scenario 1:** You are starting a new Android mobile app. Which tool do you choose and why?

> **Gradle** — it is the only officially supported build tool for Android. The Android Gradle Plugin provides all the tooling needed for APK building, code shrinking (ProGuard/R8), flavors, and signing. Maven has no Android support.

**Scenario 2:** You are joining an existing enterprise Spring Boot project that already uses Maven. Should you migrate to Gradle?

> **No, not without strong justification.** The project already works, the team knows Maven, and the CI/CD pipeline is configured for it. Migration has real costs: team retraining, CI reconfiguration, resolving edge cases in the Gradle migration. The benefits (faster builds, more flexible configuration) are real but rarely worth the disruption on a stable project. Migrate only if build performance is a genuine bottleneck or the build logic needs significant customisation.

**Scenario 3:** Your build has complex, conditional logic. Which tool handles this more naturally?

> **Gradle** — because `build.gradle` is real Groovy (or Kotlin) code. You can write `if (System.getenv("DEPLOY_ENV") == "prod") { ... }` directly in the build script. In Maven, the equivalent requires a combination of profiles, plugin configurations, and the `exec-maven-plugin`, which is significantly more verbose and harder to read.

---

## Part 3 — Coding Standards Violations

| # | Violation Found | Correct Form |
|---|---|---|
| 1 | Class name `book_manager` uses `snake_case` | `BookManager` — Java class names use `UpperCamelCase` |
| 2 | Instance variable `BookTitle` uses `UpperCamelCase` | `bookTitle` — instance variables use `lowerCamelCase` |
| 3 | Constant `lateFeePerDay` uses `lowerCamelCase` | `LATE_FEE_PER_DAY` — `static final` constants use `SCREAMING_SNAKE_CASE` |
| 4 | Method name `PrintBookInfo` uses `UpperCamelCase` | `printBookInfo` — method names use `lowerCamelCase` |
| 5 | Local variable `f` is a single letter with no meaning | `lateFee` — descriptive names required (exception: loop counters `i`, `j`) |
| 6 | Missing Javadoc on the public class | Add `/** ... */` above the class declaration describing its purpose |
| 7 | Missing Javadoc on the public method `PrintBookInfo` | Add `/** ... */` above the method describing what it does and any side effects |
| 8 | Magic number `3` used in `overdueDays = 3` and `>= 3` | Extract to `private static final int OVERDUE_THRESHOLD_DAYS = 3;` |
