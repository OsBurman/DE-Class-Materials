# TODO 1: Set the base image.
#         Use eclipse-temurin:17-jre-alpine (Alpine-based JRE — not JDK, keeps image small)
FROM ???

# TODO 2: Set the working directory inside the container to /app.
WORKDIR ???

# TODO 3: Create a system group named "spring" and a system user named "spring".
#         Alpine commands:
#           addgroup -S spring
#           adduser -S -G spring -H -s /bin/false spring
#         Use a single RUN instruction with && to keep layers minimal.
RUN ???

# TODO 4: Copy target/app.jar from the build context into the container as app.jar.
#         Set ownership to spring:spring in the same instruction using --chown.
#         COPY --chown=<user>:<group> <src> <dest>
COPY ???

# TODO 5: Switch to the non-root spring user so the JVM doesn't run as root.
USER ???

# TODO 6: Declare that the container listens on port 8080.
EXPOSE ???

# TODO 7: Set the ENTRYPOINT to run the jar.
#         Include the JVM flag: -Djava.security.egd=file:/dev/./urandom
#         Use exec form: ENTRYPOINT ["java", "...", "-jar", "app.jar"]
ENTRYPOINT [???]
