# ============================================================
# Exercise 04 — Multi-Stage Docker Build — SOLUTION
# ============================================================

# ------------------------------------------------------------
# Stage 1: builder
# Use the full Maven + JDK image only for compilation.
# Naming the stage "builder" lets the runtime stage reference
# it with COPY --from=builder.
# ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy the POM first so Docker can cache the dependency layer.
# If pom.xml doesn't change, "mvn dependency:go-offline" is skipped
# on subsequent builds — a significant time saver.
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy source and compile.
COPY src ./src
RUN mvn package -DskipTests

# ------------------------------------------------------------
# Stage 2: runtime
# Only this stage makes it into the final image.
# eclipse-temurin:17-jre-alpine is ~80 MB vs ~700 MB for the builder.
# ------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user and group for security.
# -S  = system account    -H  = no home dir    -s /bin/false = no login shell
RUN addgroup -S spring && adduser -S -G spring -H -s /bin/false spring

# Copy ONLY the JAR from the builder stage — nothing else.
# --chown ensures the non-root user owns the file at copy time.
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

# Drop root privileges before starting the process.
USER spring

# Document the port the application listens on.
EXPOSE 8080

# Exec form prevents a shell wrapper process (PID 1 = java directly).
# The entropy flag prevents slow startup in containerised environments.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

# ------------------------------------------------------------
# Bonus — size comparison (example):
#   springapp-single:latest   ~720 MB  (Maven + JDK + app.jar)
#   springapp:latest          ~105 MB  (JRE-Alpine + app.jar)
#   Saving: ~615 MB (~85% reduction)
# ------------------------------------------------------------
