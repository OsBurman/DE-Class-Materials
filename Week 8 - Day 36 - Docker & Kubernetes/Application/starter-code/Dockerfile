# ============================================================
# Multi-stage Dockerfile — Day 36 Docker & Kubernetes
# ============================================================

# TODO Task 1: Complete this multi-stage Dockerfile

# --------------- Stage 1: Build ---------------
# TODO Task 1a: Use Maven + Java 17 image to build the fat JAR
# Hint: FROM maven:3.9.6-eclipse-temurin-17 AS builder

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# TODO Task 1b: Copy pom.xml first (layer caching benefit — why?)
COPY pom.xml .

# TODO Task 1c: Download dependencies as a separate layer
# RUN mvn dependency:go-offline -B

# TODO Task 1d: Copy source and build
COPY src ./src
RUN mvn package -DskipTests -B

# --------------- Stage 2: Runtime ---------------
# TODO Task 1e: Use a smaller JRE image for production
# Compare sizes: eclipse-temurin:17-jre vs eclipse-temurin:17-jdk

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# TODO Task 1f: Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# TODO Task 1g: Copy only the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# TODO Task 1h: Set ownership
RUN chown appuser:appgroup app.jar

USER appuser

# TODO Task 1i: Expose the application port
EXPOSE 8080

# TODO Task 1j: Set JVM options via environment variable
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# TODO Task 1k: Define the entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
