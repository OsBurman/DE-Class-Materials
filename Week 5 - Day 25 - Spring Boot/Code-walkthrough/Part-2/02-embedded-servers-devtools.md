# Embedded Servers, DevTools & Packaging Spring Boot Applications

## Overview

Spring Boot's embedded server model eliminates the need to deploy WAR files to an external application server. This file covers switching between embedded servers, using DevTools to accelerate development, and the mechanics of building and running production-ready Spring Boot applications.

---

## 1. Embedded Servers

### How It Works

When you include `spring-boot-starter-web`, Spring Boot embeds **Tomcat** by default. The starter pulls in `spring-boot-starter-tomcat`, which includes the Tomcat libraries directly in your classpath. Spring Boot's `TomcatServletWebServerFactory` auto-configuration picks it up and starts it inside your JVM process.

```
java -jar bookstore.jar
  └── JVM
       └── Spring Boot App
            └── Embedded Tomcat ← lives inside the JAR
                 └── DispatcherServlet → your controllers
```

No `catalina.sh`, no WAR deployment, no separate server process.

---

### Default: Embedded Tomcat

```xml
<!-- pom.xml — this is what spring-boot-starter-web gives you by default -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- Transitively includes spring-boot-starter-tomcat -->
</dependency>
```

---

### Switching to Jetty

To replace Tomcat with Jetty, **exclude Tomcat** from the web starter and **add the Jetty starter**:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <!-- Step 1: Remove Tomcat -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Step 2: Add Jetty -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

Spring Boot's auto-configuration detects Jetty on the classpath and uses `JettyServletWebServerFactory` instead of Tomcat. No code change required.

---

### Switching to Undertow

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

---

### Embedded Server Comparison

| Feature              | Tomcat (Default)              | Jetty                         | Undertow                       |
|----------------------|-------------------------------|-------------------------------|--------------------------------|
| **Maturity**         | Most mature, widely used      | Lightweight, good for APIs    | Very high performance          |
| **Memory footprint** | Medium                        | Lower                         | Lowest                         |
| **HTTP/2 support**   | ✅ (requires ALPN)            | ✅                            | ✅                             |
| **WebSocket**        | ✅                            | ✅                            | ✅                             |
| **Best for**         | General-purpose apps          | Microservices, embeddable     | High-throughput, reactive apps |
| **Spring Boot auto-config** | `TomcatServletWebServerFactory` | `JettyServletWebServerFactory` | `UndertowServletWebServerFactory` |

---

### Embedded Server Configuration (application.properties)

```properties
# ─── Server Port & Context Path ──────────────────────────────────────────────
server.port=8080
server.servlet.context-path=/api

# ─── Tomcat Thread Pool ───────────────────────────────────────────────────────
server.tomcat.threads.min-spare=10          # Minimum threads kept alive
server.tomcat.threads.max=200               # Maximum worker threads (default 200)
server.tomcat.accept-count=100              # Queue depth when all threads busy
server.tomcat.connection-timeout=20000      # ms before idle connection dropped
server.tomcat.max-http-form-post-size=2MB   # Max form POST body size

# ─── Compression ─────────────────────────────────────────────────────────────
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,application/json
server.compression.min-response-size=1024   # Only compress responses >= 1KB

# ─── HTTP/2 ──────────────────────────────────────────────────────────────────
server.http2.enabled=true                   # Requires SSL in most containers

# ─── SSL / HTTPS ──────────────────────────────────────────────────────────────
# server.port=8443
# server.ssl.key-store=classpath:keystore.p12
# server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
# server.ssl.key-store-type=PKCS12
# server.ssl.key-alias=bookstore

# ─── Graceful Shutdown ────────────────────────────────────────────────────────
server.shutdown=graceful                    # Finish in-flight requests before stopping
spring.lifecycle.timeout-per-shutdown-phase=30s   # Max wait per phase
```

---

## 2. Spring Boot DevTools

DevTools is a development-time tool that dramatically speeds up the inner loop: change code → see result in the browser. It is automatically **excluded** from production fat JARs.

### Adding DevTools

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>        <!-- Only active during 'mvn spring-boot:run' or IDE run -->
    <optional>true</optional>     <!-- Signals: do NOT include in downstream JARs -->
</dependency>
```

> **Why `optional=true`?**  
> When you build a fat JAR with `mvn package`, Spring Boot's plugin detects the `optional=true` flag and **excludes DevTools** from the production artifact. DevTools should never run in production.

---

### Feature 1: Automatic Restart

DevTools monitors the classpath for changes. When a `.class` file changes (after you save and compile), it triggers a **fast restart** using a pre-warmed application context:

```
Normal restart:   ████████████████████  ~10 seconds (full JVM restart)
DevTools restart: ████████              ~2 seconds  (reuses JVM, reloads app classloader)
```

**How it works internally:** DevTools uses two classloaders:
- **Base classloader** — loads third-party JARs (unchanged between restarts)
- **Restart classloader** — loads your classes (discarded and rebuilt on change)

Only the restart classloader is thrown away, saving the time to reload all dependencies.

**Excluding paths from triggering restart:**

```properties
# application.properties (dev profile)
# Changes to these paths do NOT trigger a restart
spring.devtools.restart.exclude=static/**,public/**,templates/**,META-INF/resources/**
```

---

### Feature 2: LiveReload

DevTools includes an embedded **LiveReload server** (port 35729). When server-side resources change (templates, static files), it signals the browser to automatically refresh.

**Setup:**
1. DevTools starts the LiveReload server automatically
2. Install the [LiveReload browser extension](http://livereload.com/extensions/) and enable it
3. Save a Thymeleaf template or static HTML → browser refreshes automatically

**Disable LiveReload if not needed:**

```properties
spring.devtools.livereload.enabled=false
```

---

### Feature 3: H2 Console

When using the H2 in-memory database in development, Spring Boot provides a web-based SQL console:

```properties
# application-dev.properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console        # Access at http://localhost:8080/h2-console
spring.h2.console.settings.web-allow-others=false  # Security: local only

# H2 datasource to connect with
spring.datasource.url=jdbc:h2:mem:bookstoredb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

**Connecting in the H2 console:**
- **JDBC URL:** `jdbc:h2:mem:bookstoredb`
- **Username:** `sa`
- **Password:** *(leave blank)*

> DevTools also **disables the H2 console security** by default when Spring Security is on the classpath, so the console is accessible during development.

---

### Feature 4: Property Defaults

DevTools overrides certain properties with developer-friendly defaults:

| Property | DevTools Default | Reason |
|----------|-----------------|--------|
| `spring.thymeleaf.cache` | `false` | Templates reload on every request |
| `spring.freemarker.cache` | `false` | Same for FreeMarker |
| `spring.web.resources.cache.period` | `0` | Static resources not cached |
| `spring.mvc.log-request-details` | `true` | See full request headers in logs |
| `logging.level.web` | `DEBUG` | See request mapping logs |

These only apply when DevTools is active. They are **not set in your `application.properties`** — DevTools applies them automatically.

---

### Feature 5: Remote DevTools (Advanced)

For remote development (e.g., app running in Docker or a remote VM), DevTools supports remote restart over HTTP:

```properties
# application.properties on the remote server
spring.devtools.remote.secret=my-secret-token   # Required for security
```

Then run the remote client locally from your IDE (or Maven) pointing at the remote URL. Changes pushed from your local workspace trigger a restart on the remote server. (Rarely used in practice — mostly for special dev environments.)

---

### DevTools Feature Summary

| Feature          | What It Does                                          | Configuration Key                              |
|------------------|------------------------------------------------------|------------------------------------------------|
| Auto Restart     | Fast restart when classpath changes                  | `spring.devtools.restart.enabled=true` (default) |
| Exclude Paths    | Don't restart for static file changes                | `spring.devtools.restart.exclude`              |
| LiveReload       | Browser auto-refresh on resource changes             | `spring.devtools.livereload.enabled=true`      |
| H2 Console       | Web SQL console for H2 in-memory DB                  | `spring.h2.console.enabled=true`               |
| Property Defaults| Cache disabled, request logging on                   | Automatic when DevTools is on classpath         |
| Remote Restart   | Restart app on remote server                         | `spring.devtools.remote.secret`                |

---

## 3. Building Spring Boot Applications

### The Fat JAR

A Spring Boot "fat JAR" (also called an **über-JAR** or **executable JAR**) contains:
- Your compiled classes
- All dependency JARs
- An embedded server
- A launcher that bootstraps the whole thing

This means your entire application ships as **one self-contained file**.

```bash
# Build the fat JAR
mvn clean package

# Output
target/bookstore-0.0.1-SNAPSHOT.jar       # ← Fat JAR (~30-80 MB)
target/bookstore-0.0.1-SNAPSHOT.jar.original  # ← Thin JAR (your code only, ~50 KB)
```

---

### Fat JAR Internal Structure

```
bookstore-0.0.1-SNAPSHOT.jar
├── META-INF/
│   └── MANIFEST.MF                   ← Entry point declaration
├── BOOT-INF/
│   ├── classes/                       ← YOUR compiled classes + resources
│   │   ├── com/bookstore/...
│   │   ├── application.properties
│   │   └── application-dev.properties
│   └── lib/                           ← ALL dependency JARs
│       ├── spring-boot-3.2.0.jar
│       ├── spring-webmvc-6.1.0.jar
│       ├── tomcat-embed-core-10.1.0.jar
│       └── ... (dozens more)
└── org/springframework/boot/loader/   ← Spring Boot Loader (extracts & runs)
    ├── JarLauncher.class
    ├── LaunchedURLClassLoader.class
    └── ...
```

**`MANIFEST.MF` contents:**

```
Manifest-Version: 1.0
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.bookstore.BookstoreApplication
Spring-Boot-Version: 3.2.0
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
```

- `Main-Class` is always `JarLauncher` — this is the Spring Boot class that knows how to read the nested JAR structure
- `Start-Class` is YOUR `@SpringBootApplication` class
- `JarLauncher` sets up the classloader, finds `Start-Class`, and calls `main()`

---

### Spring Boot Maven Plugin Configuration

```xml
<!-- pom.xml -->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <!-- Exclude DevTools from the fat JAR -->
                <excludeDevtools>true</excludeDevtools>

                <!-- If you have multiple main classes, specify which to use -->
                <!-- <mainClass>com.bookstore.BookstoreApplication</mainClass> -->

                <!-- Layers configuration for optimized Docker builds -->
                <layers>
                    <enabled>true</enabled>
                </layers>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <!-- Repackages the thin JAR into a fat JAR after 'package' phase -->
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

### Useful Maven Build Commands

```bash
# Standard build — clean, compile, test, package
mvn clean package

# Skip tests (useful in CI when tests are run separately)
mvn clean package -DskipTests

# Skip test compilation entirely (faster, but riskier)
mvn clean package -Dmaven.test.skip=true

# Build with a specific profile activated
mvn clean package -Pprod

# Show detailed dependency tree (useful for debugging version conflicts)
mvn dependency:tree

# Check what auto-configuration is active (set debug=true in properties)
mvn spring-boot:run -Ddebug=true
```

---

### Building a Docker Image with Buildpacks

Spring Boot 2.3+ can build an OCI-compliant Docker image without a `Dockerfile`:

```bash
# Requires Docker to be running
mvn spring-boot:build-image

# Equivalent with custom image name
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=bookstore:latest
```

The plugin uses **Cloud Native Buildpacks** (by Paketo) to produce a layered, optimized image. No Dockerfile needed.

---

### JAR vs WAR Packaging

| Decision           | JAR (Recommended)                    | WAR                                         |
|--------------------|--------------------------------------|---------------------------------------------|
| **Deployment**     | `java -jar` or container             | External app server (Tomcat, WildFly, etc.) |
| **Embedded server**| ✅ Included                          | ❌ Provided by app server                   |
| **Portability**    | ✅ Self-contained                    | ⚠️ Server-specific config required          |
| **Cloud-native**   | ✅ Perfect for containers            | ⚠️ Less common in modern deployments        |
| **Use case**       | Microservices, new greenfield apps   | Legacy enterprise app server requirement    |

**Switching to WAR packaging:**

```xml
<!-- pom.xml -->
<packaging>war</packaging>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>   <!-- Provided by external server; exclude from WAR -->
</dependency>
```

```java
// BookstoreApplication.java — extend SpringBootServletInitializer for WAR
@SpringBootApplication
public class BookstoreApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BookstoreApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
```

---

## 4. Running Spring Boot Applications

### Method 1: `java -jar` (Production & Testing)

The standard way to run a Spring Boot fat JAR:

```bash
# Basic run
java -jar target/bookstore-0.0.1-SNAPSHOT.jar

# With a different port
java -jar target/bookstore-0.0.1-SNAPSHOT.jar --server.port=9090

# With an active profile
java -jar target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Multiple profile activations
java -jar target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod,metrics

# With environment variables (12-Factor App style)
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/bookstore \
SPRING_PROFILES_ACTIVE=prod \
java -jar target/bookstore-0.0.1-SNAPSHOT.jar

# With JVM tuning flags
java -Xms256m -Xmx512m -jar target/bookstore-0.0.1-SNAPSHOT.jar
```

---

### Method 2: `mvn spring-boot:run` (Development)

Runs the application from source without building a JAR first — faster iteration during development:

```bash
# Standard run
mvn spring-boot:run

# With active profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# With additional JVM args
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# With extra application arguments
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090 --logging.level.root=DEBUG"
```

---

### Method 3: IntelliJ IDEA (Development)

1. Open `BookstoreApplication.java`
2. Click the green **▶ Run** button in the gutter next to `main()`
3. Or use **Run → Run 'BookstoreApplication'** from the menu bar
4. To configure active profiles: **Run → Edit Configurations → Active Profiles** field

IntelliJ automatically compiles changed classes and (with DevTools) triggers a fast restart.

---

### Startup Output

When the application starts successfully, you'll see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.2.0)

2024-01-15 10:22:01  INFO  BookstoreApplication     : Starting BookstoreApplication
2024-01-15 10:22:01  INFO  BookstoreApplication     : The following 1 profile is active: "dev"
2024-01-15 10:22:03  INFO  RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories
2024-01-15 10:22:04  INFO  TomcatWebServer          : Tomcat initialized with port(s): 8080 (http)
2024-01-15 10:22:05  INFO  BookstoreApplication     : Started BookstoreApplication in 3.421 seconds (JVM running for 4.012)
```

Key lines to check:
- **Active profile** — confirms which environment config loaded
- **Tomcat port** — confirms the server is up
- **Started in X seconds** — watch this as your app grows

---

### Graceful Shutdown

Graceful shutdown allows in-flight HTTP requests to complete before the JVM exits:

```properties
# application.properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

**How it works:**
1. Shutdown signal received (SIGTERM from OS or Kubernetes)
2. Server stops accepting **new** requests
3. In-flight requests are allowed to complete (up to 30s)
4. Spring context closes (beans destroyed in reverse order)
5. JVM exits

```
Signal received
│
├─ Stop accepting new connections
│
├─ Wait for in-flight requests to finish (max 30s)
│   └─ If requests finish: proceed immediately
│   └─ If 30s elapses: force shutdown
│
└─ Close ApplicationContext → JVM exit
```

> **In Kubernetes:** Set `terminationGracePeriodSeconds: 60` in your Pod spec (longer than your Spring `timeout-per-shutdown-phase`) to give Spring time to drain before K8s force-kills the Pod.

---

### Running Configuration Summary

| Method               | Command                                   | When to Use              |
|----------------------|-------------------------------------------|--------------------------|
| `java -jar`          | `java -jar bookstore.jar`                 | Production, Docker, CI   |
| `mvn spring-boot:run`| `mvn spring-boot:run`                     | Development (no JAR)     |
| IDE (IntelliJ)       | Green Run button                          | Development, debugging   |
| Docker               | `docker run bookstore:latest`             | Containerized deployment |
| Kubernetes           | Pod spec with `java -jar` in container    | Cloud-native deployment  |

---

## 5. Embedded Server vs External Server: Key Differences

| Aspect                    | Embedded Server                          | External Server (Traditional)              |
|---------------------------|------------------------------------------|--------------------------------------------|
| **Startup**               | `java -jar app.jar`                      | Deploy WAR, start `catalina.sh`            |
| **Configuration**         | `application.properties`                 | `server.xml`, `context.xml`, etc.          |
| **Version management**    | Spring Boot BOM controls server version  | Server version separate from app version   |
| **Multiple apps**         | One JVM per app                          | Multiple apps share one server             |
| **Resource overhead**     | Higher (server embedded per app)         | Lower (shared server)                      |
| **Container-friendliness**| ✅ Perfect                               | ⚠️ Extra complexity                       |
| **Cloud-native**          | ✅ Standard approach                     | ❌ Legacy pattern                          |

---

## Summary

| Concept          | Key Takeaway                                                                  |
|------------------|-------------------------------------------------------------------------------|
| Embedded Tomcat  | Included by default via `spring-boot-starter-web`; zero config required       |
| Switch to Jetty  | Exclude `spring-boot-starter-tomcat`, add `spring-boot-starter-jetty`         |
| DevTools         | Fast restart + LiveReload + H2 console; auto-excluded from production JAR      |
| Fat JAR          | Self-contained JAR with `BOOT-INF/classes/`, `BOOT-INF/lib/`, JarLauncher     |
| `mvn package`    | Produces fat JAR in `target/`; `spring-boot-maven-plugin` does the repackaging|
| Run production   | `java -jar bookstore.jar --spring.profiles.active=prod`                       |
| Graceful shutdown| `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase=30s`|
