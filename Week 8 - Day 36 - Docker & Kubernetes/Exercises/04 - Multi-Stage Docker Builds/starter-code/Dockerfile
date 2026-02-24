# ============================================================
# Exercise 04 — Multi-Stage Docker Build Starter
# ============================================================
# Fill in every TODO to produce a two-stage Dockerfile that:
#   1. Compiles the Spring Boot app with Maven (builder stage)
#   2. Runs the app on a minimal JRE-only Alpine image (runtime stage)
# ============================================================

# ------------------------------------------------------------
# Stage 1: builder
# TODO 1: Set the base image to maven:3.9-eclipse-temurin-17
#         and name this stage "builder" using the AS keyword
# ------------------------------------------------------------
# FROM ... AS ...

WORKDIR /build

# TODO 2: Copy only pom.xml first to cache dependencies
# COPY ...

# TODO 2b: Pre-download all Maven dependencies
# RUN mvn dependency:go-offline

# TODO 3: Copy the source directory into /build/src
# COPY ...

# TODO 3b: Package the application (skip tests)
# RUN mvn package -DskipTests

# ------------------------------------------------------------
# Stage 2: runtime
# TODO 4: Set the base image to eclipse-temurin:17-jre-alpine
#         (no AS alias needed for the final stage)
# ------------------------------------------------------------
# FROM ...

WORKDIR /app

# TODO 5: Create a non-root system group and user named "spring"
# RUN addgroup ...

# TODO 6: Copy ONLY the compiled JAR from the builder stage.
#         Apply --chown=spring:spring so the non-root user owns it.
# COPY --from=builder ...

# TODO 7a: Switch to the non-root user
# USER ...

# TODO 7b: Expose the application port
# EXPOSE ...

# TODO 7c: Set the ENTRYPOINT (exec form) to run the JAR
#          Include -Djava.security.egd=file:/dev/./urandom
# ENTRYPOINT [...]
