# Maven Lifecycle Commands — Solution

## Part 1 — Command Reference Table

| # | Task | Command | Output / Effect |
|---|---|---|---|
| 1 | Delete all previously compiled output | `mvn clean` | Deletes the entire `target/` directory |
| 2 | Download all declared dependencies | `mvn dependency:resolve` | Downloads JARs to `~/.m2/repository/`; prints resolved dependency tree |
| 3 | Compile all Java source files | `mvn compile` | Creates `target/classes/` with `.class` files |
| 4 | Run all unit tests | `mvn test` | Creates `target/test-classes/` and `target/surefire-reports/` with pass/fail XML |
| 5 | Package into a `.jar` file | `mvn package` | Creates `target/library-management-1.0.0-SNAPSHOT.jar` |
| 6 | Install to local Maven repository | `mvn install` | Copies JAR to `~/.m2/repository/com/library/library-management/1.0.0-SNAPSHOT/` |
| 7 | Compile, skip test execution | `mvn compile -DskipTests` | Compiles only; test compilation and execution are both skipped |
| 8 | Print the effective POM | `mvn help:effective-pom` | Prints the fully resolved POM (project + parent + defaults) to stdout |

---

## Part 2 — Lifecycle Phase Order

| Position | Phase Name |
|---|---|
| 1 | `validate` |
| 2 | `compile` |
| 3 | `test` |
| 4 | `package` |
| 5 | `install` |
| 6 | `deploy` |

---

## Part 3 — Short Answer Questions

**Q1.** If you run `mvn package`, which earlier phases are automatically executed?

> Maven executes every phase before `package` in the default lifecycle: `validate → initialize → generate-sources → process-sources → generate-resources → process-resources → compile → process-classes → generate-test-sources → process-test-sources → generate-test-resources → process-test-resources → test-compile → process-test-classes → test → prepare-package`. In practice, the phases you observe doing visible work are: validate → compile → test → package.

**Q2.** What is the difference between a Maven lifecycle phase and a plugin goal?

> A **lifecycle phase** (e.g., `compile`, `package`) is a named step in a fixed, ordered sequence. Maven executes all preceding phases first. A **plugin goal** (e.g., `dependency:resolve`, `help:effective-pom`) is a specific task provided by a plugin. Goals can be bound to lifecycle phases (e.g., `maven-compiler-plugin:compile` is bound to the `compile` phase), or invoked directly using `plugin:goal` syntax without entering the lifecycle sequence.

**Q3.** Where does Maven store downloaded dependencies, and why does this cache matter?

> Maven stores downloaded JARs in the **local repository** at `~/.m2/repository/` (by default). Each artifact is stored at a path derived from its GAV coordinates: `~/.m2/repository/groupId-path/artifactId/version/`. This cache matters because: (1) Maven only downloads each version once — subsequent builds reuse the cached copy, making builds faster; (2) teams sharing the same local cache (e.g., via a build server) avoid redundant downloads; (3) enterprise teams run a **repository manager** (Nexus, Artifactory) as a shared proxy so all developers share one cache and have access to internal artifacts.
