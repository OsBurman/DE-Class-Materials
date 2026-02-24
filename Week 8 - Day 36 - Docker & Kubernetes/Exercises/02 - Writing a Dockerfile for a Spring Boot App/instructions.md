# Exercise 02: Writing a Dockerfile for a Spring Boot Application

## Objective
Write a production-appropriate `Dockerfile` that packages a Spring Boot application into a Docker image, applying best practices for layer caching and image size.

## Background
A `Dockerfile` is a text script of instructions Docker reads top-to-bottom to build an image. Each instruction (`FROM`, `WORKDIR`, `COPY`, `RUN`, `EXPOSE`, `ENTRYPOINT`) creates a new layer. Ordering instructions from least-changed to most-changed maximises the build cache — meaning `dependencies` should be copied and resolved before application source code.

## Requirements

A minimal Spring Boot app with a pre-built JAR at `target/app.jar` is provided. Write a `Dockerfile` that:

1. Uses `eclipse-temurin:17-jre-alpine` as the base image (small Alpine-based JRE — not JDK).
2. Sets a working directory of `/app` inside the container.
3. Creates a non-root user named `spring` with no home directory and no login shell (`addgroup` + `adduser` on Alpine).
4. Copies **only** `target/app.jar` into the container as `app.jar`.
5. Changes ownership of `app.jar` to the `spring` user.
6. Switches to the `spring` user (so the JVM does not run as root).
7. Exposes port `8080`.
8. Sets the entrypoint to run the jar with `java -jar app.jar`, adding the JVM flag `-Djava.security.egd=file:/dev/./urandom` to speed up startup.

Also write a `.dockerignore` file that excludes: `.git/`, `*.md`, `src/`, `.mvn/`, `mvnw`, and `mvnw.cmd`. This prevents unnecessary files from being sent to the Docker build context.

## Hints
- Alpine uses `addgroup -S <group>` and `adduser -S -G <group> -H -s /bin/false <user>` for system accounts.
- `COPY --chown=spring:spring target/app.jar app.jar` combines copy and ownership in one layer.
- `USER spring` must come **after** ownership is set, otherwise the user won't have read permission.
- `.dockerignore` syntax is the same as `.gitignore` — one pattern per line.

## Expected Output

```dockerfile
# When built with: docker build -t my-spring-app .
# The resulting image should:
# - Be based on eclipse-temurin:17-jre-alpine
# - Have a non-root "spring" user running the process
# - Expose port 8080
# - Start with: java -Djava.security.egd=file:/dev/./urandom -jar app.jar

# docker run --rm -p 8080:8080 my-spring-app
# Should start the Spring Boot application and show:
# Started Application in X.XXX seconds
```
