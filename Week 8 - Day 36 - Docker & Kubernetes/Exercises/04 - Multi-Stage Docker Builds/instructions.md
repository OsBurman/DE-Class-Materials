# Exercise 04 — Multi-Stage Docker Builds

## Learning Objectives
By the end of this exercise you will be able to:
- Explain why single-stage builds produce unnecessarily large images
- Write a two-stage Dockerfile: a **builder** stage and a **runtime** stage
- Use `COPY --from=<stage>` to transfer only the compiled artefact between stages
- Verify the size difference between a single-stage and a multi-stage image

---

## Background

A single-stage Spring Boot Dockerfile that installs Maven or the full JDK produces an image that carries hundreds of megabytes of build tooling that is never needed at runtime.  
**Multi-stage builds** solve this by using one stage to compile and a second, minimal stage to run the artefact:

```
Stage 1 — builder  (maven:3.9-eclipse-temurin-17 ~700 MB)
  └── mvn package  ──► target/app.jar

Stage 2 — runtime  (eclipse-temurin:17-jre-alpine ~80 MB)
  └── COPY --from=builder target/app.jar app.jar
  └── Final image ships only the JRE + app.jar
```

Only the **last stage** ends up in the final image.

---

## Requirements

### Requirement 1 — Builder Stage
Write a `FROM` instruction for the builder stage using the image `maven:3.9-eclipse-temurin-17`.  
Name the stage `builder` using the `AS` keyword.

Set the working directory to `/build`.

### Requirement 2 — Dependency Cache Layer
Copy only `pom.xml` first and run `mvn dependency:go-offline`.  
This creates a Docker layer that caches all Maven dependencies.  
When only source files change (not `pom.xml`), Docker re-uses this layer and skips the slow download step.

### Requirement 3 — Compile the Application
Copy the `src/` directory into `/build/src`.  
Run `mvn package -DskipTests` to produce the JAR in `target/`.

### Requirement 4 — Runtime Stage
Begin the second stage using `eclipse-temurin:17-jre-alpine` as the base image.  
Set the working directory to `/app`.

### Requirement 5 — Non-Root User (same pattern as Ex 02)
Create a system group `spring` and a system user `spring` (no home directory, no login shell).

### Requirement 6 — Copy Artefact from Builder Stage
Use `COPY --from=builder` to transfer the built JAR from `/build/target/*.jar` into the runtime stage as `app.jar`.  
Apply `--chown=spring:spring` so the non-root user owns the file.

### Requirement 7 — Finalise the Runtime Stage
- Switch to the `spring` user
- Expose port `8080`
- Set the `ENTRYPOINT` (exec form) to run the JAR with the entropy fix flag:  
  `-Djava.security.egd=file:/dev/./urandom`

---

## Bonus Challenge
After building the multi-stage image, compare its size against a single-stage image  
(built from a `FROM maven:3.9-eclipse-temurin-17` base with no second stage).  
Record the size difference in a comment at the bottom of your Dockerfile.

---

## Deliverable
Complete the `Dockerfile` in `starter-code/`.

---

## Hints
- Stage names in `FROM … AS <name>` are case-insensitive but lowercase is conventional
- `RUN mvn dependency:go-offline` before copying `src/` is the key caching trick
- `COPY --from=builder /build/target/*.jar app.jar` — the glob `*.jar` matches the single artefact
- `docker images` shows image sizes; compare `springapp-single:latest` vs `springapp:latest`
