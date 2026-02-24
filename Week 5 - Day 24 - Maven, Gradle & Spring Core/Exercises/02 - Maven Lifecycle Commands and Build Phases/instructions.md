# Exercise 02: Maven Lifecycle Commands and Build Phases

## Objective
Execute Maven lifecycle commands to compile, test, package, and install a project, and observe what each phase produces.

## Background
Maven builds follow a fixed **lifecycle**: a sequence of phases executed in order. Running a later phase automatically runs all earlier phases first. Understanding which phase does what — and what artifact each phase produces — is essential for day-to-day development work.

The three built-in lifecycles are **default** (build), **clean** (remove previous output), and **site** (generate docs). This exercise focuses on the **default** lifecycle phases you will use every day.

## Requirements
1. Review the `pom.xml` in `starter-code/` — it is a working project based on Exercise 01.
2. In `starter-code/commands.md`, fill in the correct Maven command for each numbered task described below.
3. For each command, also fill in what directory or file is created/changed as a result.
4. Complete the **Lifecycle Phase Order** table by listing the six default phases in the correct execution order, earliest first.
5. Answer the three short-answer questions at the bottom of `commands.md`.

**Tasks to document (commands 1–8):**
1. Delete all previously compiled output (the `target/` directory)
2. Download dependencies declared in `pom.xml` and copy them to the local repository cache
3. Compile all source files in `src/main/java/`
4. Run all unit tests (classes in `src/test/java/`)
5. Package the compiled code into a `.jar` file
6. Install the `.jar` into the local Maven repository (`~/.m2/repository/`)
7. Compile only, but skip running any tests (`-DskipTests`)
8. Print the effective POM (the merged result of the project POM + all parent POMs + defaults)

## Hints
- Every Maven command starts with `mvn`. Phases are passed as arguments (e.g., `mvn compile`).
- Running `mvn package` automatically runs `validate → initialize → generate-sources → process-sources → generate-resources → process-resources → compile → process-classes → generate-test-sources → process-test-sources → generate-test-resources → process-test-resources → test-compile → process-test-classes → test → prepare-package → package` — you don't call each one separately.
- The `-D` flag sets a system property: `-DskipTests=true` or just `-DskipTests`.
- `dependency:resolve` is a plugin goal, not a lifecycle phase — goals use the `plugin:goal` syntax.

## Expected Output

After running the correct package command, the `target/` directory should look like:
```
target/
├── classes/             ← compiled .class files from src/main/java
├── test-classes/        ← compiled .class files from src/test/java
├── surefire-reports/    ← test result XML/HTML files
└── library-management-1.0.0-SNAPSHOT.jar  ← the packaged artifact
```

The completed `commands.md` table should look like:

```
| Task | Command | Output/Effect |
|---|---|---|
| 1. Clean previous build | mvn clean | Deletes the target/ directory |
| 2. Download dependencies | mvn dependency:resolve | Downloads JARs to ~/.m2/repository/ |
| 3. Compile sources | mvn compile | Creates target/classes/ |
| 4. Run tests | mvn test | Creates target/surefire-reports/ |
| 5. Package to JAR | mvn package | Creates target/library-management-1.0.0-SNAPSHOT.jar |
| 6. Install to local repo | mvn install | Copies JAR to ~/.m2/repository/com/library/... |
| 7. Compile, skip tests | mvn compile -DskipTests | Compiles only; no test execution |
| 8. Print effective POM | mvn help:effective-pom | Prints the full resolved POM to the console |
```
