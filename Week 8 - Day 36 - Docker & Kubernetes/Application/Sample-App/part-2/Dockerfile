# ─── Stage 1: Build (kubernetes-demo) ────────────────────────────────────────
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first for layer caching:
# If pom.xml hasn't changed, Docker reuses the cached dependency layer
# and skips the slow "mvn dependency:go-offline" step on subsequent builds.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code (changes more often than pom.xml)
COPY src ./src

# Build the JAR, skipping tests (tests run in CI pipeline, not in image build)
RUN mvn clean package -DskipTests

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
# Use a minimal JRE-only Alpine image — no Maven, no JDK, no build tools.
# Result: ~100MB image instead of ~500MB.
FROM eclipse-temurin:17-jre-alpine

# Add a non-root user for security best practice.
# Running as root inside a container is a security risk.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the built JAR from the build stage — nothing else.
COPY --from=build /app/target/*.jar app.jar

# Give ownership of the app directory to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Document the port the app listens on (does not actually publish it)
EXPOSE 8080

# Health check — Kubernetes liveness/readiness probes supplement this.
# --start-period gives the app 40s to start before health checks begin.
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# JVM flags for container awareness:
# -XX:+UseContainerSupport  — JVM respects cgroup memory/CPU limits (not host values)
# -XX:MaxRAMPercentage=75.0 — Use up to 75% of container memory for heap
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
