# Production-appropriate Dockerfile for a Spring Boot application

# 1. Use the Alpine-based JRE (not JDK) — significantly smaller image
FROM eclipse-temurin:17-jre-alpine

# 2. Set working directory — all subsequent paths are relative to /app
WORKDIR /app

# 3. Create a system group and user with no home dir and no login shell.
#    Running as non-root is a security best practice — if the JVM is compromised,
#    the attacker has limited OS privileges.
RUN addgroup -S spring && adduser -S -G spring -H -s /bin/false spring

# 4. Copy the JAR and set ownership in a single layer.
#    --chown avoids a separate RUN chown which would add another layer.
COPY --chown=spring:spring target/app.jar app.jar

# 5. Switch to the non-root user before the process starts
USER spring

# 6. Document the port the application listens on
EXPOSE 8080

# 7. Exec-form ENTRYPOINT (preferred over shell form — signals are passed directly to Java).
#    -Djava.security.egd speeds up Spring Boot startup on Linux by using /dev/urandom
#    instead of the blocking /dev/random for entropy.
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
