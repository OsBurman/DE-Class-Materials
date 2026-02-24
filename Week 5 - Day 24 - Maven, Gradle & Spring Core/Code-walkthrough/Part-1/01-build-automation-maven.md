# Build Automation & Maven
# Day 24 — Maven, Gradle & Spring Core | Part 1 (Section 1 of 3)

---

## Section 1 — Build Automation Concepts

### The Problem Build Tools Solve

Before build tools, building a Java project by hand looked like this:

```bash
# Compile every .java file individually
javac -cp lib/jackson-core-2.15.2.jar \
      -cp lib/spring-core-6.1.3.jar \
      -d target/classes \
      src/main/java/com/bookstore/*.java \
      src/main/java/com/bookstore/service/*.java \
      src/main/java/com/bookstore/repository/*.java

# Run tests
javac -cp target/classes:lib/junit-5.10.0.jar ...

# Package into a JAR
jar cvf target/bookstore-1.0.0.jar -C target/classes .

# Copy to shared server
scp target/bookstore-1.0.0.jar deploy@server:/opt/apps/
```

This is fragile, repetitive, and error-prone. Build automation tools solve this.

### What Build Tools Do

```
┌─────────────────────────────────────────────────────────────────────┐
│                      BUILD TOOL RESPONSIBILITIES                     │
├─────────────────────────────────────────────────────────────────────┤
│  1. DEPENDENCY MANAGEMENT                                            │
│     - Declare what libraries you need (name + version)               │
│     - Automatically download them from a central repository          │
│     - Resolve transitive dependencies (dependencies of dependencies) │
│                                                                      │
│  2. COMPILATION                                                      │
│     - Compile source code in the correct order                       │
│     - Separate main code from test code                              │
│                                                                      │
│  3. TESTING                                                          │
│     - Run unit and integration tests automatically                   │
│     - Fail the build if any test fails                               │
│                                                                      │
│  4. PACKAGING                                                        │
│     - Bundle compiled classes + resources into a JAR or WAR          │
│     - Include dependencies (fat JAR / uber JAR for Spring Boot)      │
│                                                                      │
│  5. DEPLOYMENT                                                       │
│     - Publish artifacts to artifact repositories (Nexus, Artifactory)│
│     - Integrate with CI/CD pipelines                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### The Two Dominant Build Tools in Java

```
MAVEN   → XML-based configuration (pom.xml). Convention over configuration.
          Released 2004. Still the most widely used in enterprise Java.

GRADLE  → DSL-based configuration (build.gradle). Groovy or Kotlin.
          Released 2007. Default for Android. Increasingly popular for Spring Boot.
```

---

## Section 2 — Maven: Project Object Model (POM)

### What is the POM?

The `pom.xml` (Project Object Model) is the heart of every Maven project.
It is an XML file in the project root that describes:
- Who the project is (groupId, artifactId, version)
- What it depends on (dependencies)
- How to build it (plugins, lifecycle)
- What modules it contains (multi-module projects)

### Minimal pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- ── PROJECT COORDINATES (GAV) ──────────────────────────────── -->
    <!-- Every artifact in the Maven ecosystem is identified by GAV:   -->
    <!-- GroupId : ArtifactId : Version                                -->

    <groupId>com.bookstore</groupId>
    <!--  ^ Reverse-domain namespace for your organization/project     -->

    <artifactId>bookstore-app</artifactId>
    <!--  ^ The name of THIS specific module/project                   -->

    <version>1.0.0-SNAPSHOT</version>
    <!--  ^ SNAPSHOT = under development / not yet released            -->
    <!--    Release versions: 1.0.0, 1.2.3, 2.0.0-RC1                 -->

    <packaging>jar</packaging>
    <!--  ^ jar (default), war (web app), pom (parent/aggregator)      -->

    <name>Bookstore Application</name>
    <description>A REST API for managing a bookstore catalog</description>

    <!-- ── JAVA VERSION ──────────────────────────────────────────── -->
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

</project>
```

### Full pom.xml — Bookstore Application

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <!-- ── PROJECT IDENTITY ──────────────────────────────────────── -->
    <groupId>com.bookstore</groupId>
    <artifactId>bookstore-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>Bookstore Application</name>

    <!-- ── PROPERTIES: CENTRALIZED VERSION MANAGEMENT ────────────── -->
    <!-- Define versions once here, reference with ${property.name}   -->
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Library versions — change here, updates everywhere -->
        <spring.version>6.1.3</spring.version>
        <jackson.version>2.16.1</jackson.version>
        <junit.version>5.10.1</junit.version>
        <mockito.version>5.8.0</mockito.version>
        <lombok.version>1.18.30</lombok.version>
    </properties>

    <!-- ── DEPENDENCIES ──────────────────────────────────────────── -->
    <!-- Each <dependency> downloads a JAR from Maven Central          -->
    <!-- (https://mvnrepository.com) and adds it to the classpath.     -->
    <dependencies>

        <!-- Spring Core — IoC container, ApplicationContext, Beans -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
            <!-- scope defaults to "compile" — available everywhere  -->
        </dependency>

        <!-- Jackson — JSON serialization/deserialization -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- Lombok — reduces boilerplate (getters, setters, builders) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
            <!-- ^ "provided" means: needed at compile time but NOT   -->
            <!--   packaged into the final JAR. Lombok's annotation   -->
            <!--   processor runs at compile time and is not needed   -->
            <!--   at runtime.                                        -->
        </dependency>

        <!-- SLF4J API — logging facade -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>

        <!-- Logback — SLF4J implementation (runtime only) -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
            <scope>runtime</scope>
            <!-- ^ "runtime" = not needed to compile, only to run    -->
        </dependency>

        <!-- ─── TEST DEPENDENCIES ────────────────────────────────── -->
        <!-- scope "test" = only available in test code, not packaged -->

        <!-- JUnit 5 — unit testing framework -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Mockito — mocking framework for unit tests -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>

        <!-- Spring Test — testing support for Spring applications -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <!-- ── DEPENDENCY SCOPES SUMMARY ─────────────────────────────── -->
    <!--
    compile  (default) → available everywhere (compile + test + runtime)
    provided           → available at compile time; provided by container at runtime
                         (e.g., Lombok, servlet-api on a Tomcat server)
    runtime            → not needed to compile, but needed to run
                         (e.g., JDBC drivers, Logback implementation)
    test               → only for test compilation and execution
    system             → like "provided" but you specify the JAR path manually (rare)
    import             → used only with <dependencyManagement> for BOMs
    -->

    <!-- ── BUILD CONFIGURATION ───────────────────────────────────── -->
    <build>

        <!-- ── PLUGINS ───────────────────────────────────────────── -->
        <!-- Plugins extend the Maven build lifecycle with extra goals -->
        <plugins>

            <!-- Maven Compiler Plugin — controls Java version for compilation -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.12.1</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <encoding>UTF-8</encoding>
                    <!-- Enable Lombok annotation processing -->
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin — runs JUnit 5 tests during the "test" phase -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.3</version>
                <configuration>
                    <!-- Include tests matching these patterns -->
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                        <include>**/*Spec.java</include>
                    </includes>
                </configuration>
            </plugin>

            <!-- Maven JAR Plugin — controls how the JAR is packaged -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <!-- Which class has the main() method -->
                            <mainClass>com.bookstore.BookstoreApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

        </plugins>
    </build>

    <!-- ── REPOSITORIES (usually not needed — Maven Central is default) ── -->
    <!--
    <repositories>
        <repository>
            <id>central</id>
            <url>https://repo1.maven.org/maven2</url>
        </repository>
        <repository>
            <id>company-nexus</id>
            <url>https://nexus.company.com/repository/maven-public/</url>
        </repository>
    </repositories>
    -->

</project>
```

---

## Section 3 — Maven Standard Directory Structure

Maven enforces a standard project layout. Every Maven project follows the same structure,
which means you can instantly understand any Maven project you open.

```
bookstore-app/
│
├── pom.xml                          ← Project configuration (the POM)
│
├── src/
│   ├── main/
│   │   ├── java/                    ← Production source code
│   │   │   └── com/
│   │   │       └── bookstore/
│   │   │           ├── BookstoreApplication.java   ← Entry point
│   │   │           ├── model/
│   │   │           │   └── Book.java
│   │   │           ├── service/
│   │   │           │   └── BookService.java
│   │   │           └── repository/
│   │   │               └── BookRepository.java
│   │   └── resources/               ← Config files, templates, static assets
│   │       ├── application.properties
│   │       └── logback.xml
│   │
│   └── test/
│       ├── java/                    ← Test source code (mirrors main/java structure)
│       │   └── com/
│       │       └── bookstore/
│       │           ├── service/
│       │           │   └── BookServiceTest.java
│       │           └── repository/
│       │               └── BookRepositoryTest.java
│       └── resources/               ← Test-specific config files
│           └── application-test.properties
│
└── target/                          ← BUILD OUTPUT (generated by Maven, git-ignored)
    ├── classes/                     ← Compiled .class files from src/main/java
    ├── test-classes/                ← Compiled test .class files
    ├── surefire-reports/            ← JUnit test results (XML + HTML)
    └── bookstore-app-1.0.0-SNAPSHOT.jar  ← The packaged artifact
```

### Key Conventions

```
src/main/java       → Production Java source code
src/main/resources  → Production config (application.properties, logback.xml, etc.)
src/test/java       → Test Java source code
src/test/resources  → Test config files
target/             → ALL build output — NEVER commit this to git
                      Add target/ to your .gitignore
```

---

## Section 4 — Maven Lifecycle Phases

Maven organizes build tasks into **lifecycles**. The most important is the **default lifecycle**.

### The Default Lifecycle (in order)

```
validate    → Validates the project is correct and all necessary information is available.
              Checks that pom.xml is well-formed.

initialize  → Initializes build state (sets properties, creates directories).

compile     → Compiles src/main/java → target/classes
              Runs: javac on all .java files in src/main/java

test        → Compiles src/test/java, then runs all unit tests with Surefire.
              BUILD FAILS if any test fails.

package     → Packages compiled code into JAR/WAR.
              Creates target/bookstore-app-1.0.0-SNAPSHOT.jar

verify      → Runs integration tests and checks that quality criteria are met.

install     → Installs the package to your LOCAL Maven repository (~/.m2/repository).
              Makes this artifact available to other projects on your machine.

deploy      → Copies the artifact to a REMOTE repository (Nexus, Artifactory).
              Used in CI/CD pipelines.
```

### Running Maven Lifecycle Phases

```bash
# Run a specific phase (also runs all preceding phases)
mvn compile             # compile only
mvn test                # compile + test
mvn package             # compile + test + package
mvn install             # compile + test + package + install to ~/.m2
mvn deploy              # compile + test + package + install + deploy

# Skip tests (useful during rapid development, NOT recommended in CI)
mvn package -DskipTests

# Clean the target/ directory first, then run phase (use this often!)
mvn clean package       # delete target/ → compile → test → package
mvn clean install
mvn clean test

# Run with verbose output
mvn package -X          # debug mode — shows every decision Maven makes

# Run just a specific goal (not a phase)
mvn dependency:tree                  # show the full dependency graph
mvn dependency:analyze               # find unused or undeclared dependencies
mvn help:describe -Dplugin=compiler  # describe what the compiler plugin does
mvn versions:display-dependency-updates  # show available version upgrades
```

### Other Lifecycles

```
clean lifecycle:
  pre-clean → clean → post-clean
  mvn clean  → deletes the target/ directory

site lifecycle:
  pre-site → site → post-site → site-deploy
  mvn site  → generates HTML documentation from Javadoc + project info
```

---

## Section 5 — Dependency Management and Maven Central

### How Dependency Resolution Works

```
1. You declare a dependency in pom.xml (groupId, artifactId, version)
2. Maven checks your local cache: ~/.m2/repository/
   └── If found → use cached version (no internet needed)
   └── If not found → download from Maven Central
3. Maven downloads the JAR and its pom.xml
4. Maven reads the downloaded pom.xml to find TRANSITIVE DEPENDENCIES
   (dependencies that your dependency needs)
5. This continues recursively until all transitive deps are resolved
6. Maven adds all JARs to the build classpath
```

### Maven Central Repository

```
URL: https://repo1.maven.org/maven2/
Search: https://mvnrepository.com/

Local cache: ~/.m2/repository/
  e.g., ~/.m2/repository/org/springframework/spring-context/6.1.3/
            spring-context-6.1.3.jar
            spring-context-6.1.3.pom     ← maven reads this for transitive deps
            spring-context-6.1.3.pom.sha1 ← checksum for integrity verification
```

### Transitive Dependency Example

```xml
<!-- You declare this one dependency in your pom.xml: -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.3</version>
</dependency>

<!-- Maven automatically pulls in spring-context's dependencies: -->
<!--   spring-core-6.1.3.jar    (spring-context needs this)      -->
<!--   spring-beans-6.1.3.jar   (spring-context needs this)      -->
<!--   spring-aop-6.1.3.jar     (spring-context needs this)      -->
<!--   spring-expression-6.1.3.jar (spring-context needs this)   -->
<!-- ...and those deps may have their own deps, etc.              -->
```

### Dependency Tree

```bash
mvn dependency:tree

# Output example:
[INFO] com.bookstore:bookstore-app:jar:1.0.0-SNAPSHOT
[INFO] +- org.springframework:spring-context:jar:6.1.3:compile
[INFO] |  +- org.springframework:spring-aop:jar:6.1.3:compile
[INFO] |  +- org.springframework:spring-beans:jar:6.1.3:compile
[INFO] |  +- org.springframework:spring-core:jar:6.1.3:compile
[INFO] |  |  \- org.springframework:spring-jcl:jar:6.1.3:compile
[INFO] |  \- org.springframework:spring-expression:jar:6.1.3:compile
[INFO] +- com.fasterxml.jackson.core:jackson-databind:jar:2.16.1:compile
[INFO] |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.16.1:compile
[INFO] |  \- com.fasterxml.jackson.core:jackson-core:jar:2.16.1:compile
[INFO] \- org.junit.jupiter:junit-jupiter:jar:5.10.1:test
[INFO]    +- org.junit.jupiter:junit-jupiter-api:jar:5.10.1:test
[INFO]    \- org.junit.jupiter:junit-jupiter-engine:jar:5.10.1:test
```

### Dependency Conflict Resolution (Nearest Wins)

```
Problem: Two dependencies both depend on jackson-core but different versions:
  spring-context → jackson-core:2.16.1
  my-lib         → jackson-core:2.14.0

Maven uses "nearest wins":
  The dependency CLOSEST to your project in the dependency tree wins.
  If you need a specific version, declare it directly in your pom.xml — 
  that version wins because it's at depth 1 (nearest possible).
```

### BOM (Bill of Materials) — Centralized Version Management

```xml
<!-- Spring Boot BOM manages versions for all Spring libraries -->
<!-- Import it in dependencyManagement to use its version recommendations -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- Now you can declare Spring deps WITHOUT specifying versions — -->
<!-- the BOM provides them:                                        -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- No version needed — BOM defines it -->
    </dependency>
</dependencies>
```

---

## Section 6 — Maven Plugins and Goals

### Plugin vs Goal vs Phase

```
PLUGIN  → A JAR containing build logic. e.g., maven-compiler-plugin
GOAL    → A specific task a plugin can perform. e.g., compiler:compile, compiler:testCompile
PHASE   → A step in a lifecycle. Goals are BOUND to phases.

When you run a phase, Maven runs all goals bound to that phase and all preceding phases.

Default bindings (what runs when you run `mvn package`):
  validate    → (nothing bound)
  compile     → maven-compiler-plugin:compile
  test        → maven-surefire-plugin:test
  package     → maven-jar-plugin:jar
```

### Common Plugins

```xml
<!-- ── Maven Compiler Plugin ─────────────────────────────────────── -->
<!-- Controls Java source/target version and annotation processors    -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.1</version>
    <configuration>
        <source>21</source>
        <target>21</target>
    </configuration>
</plugin>

<!-- ── Maven Surefire Plugin ─────────────────────────────────────── -->
<!-- Runs unit tests (bound to "test" phase)                          -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.3</version>
</plugin>

<!-- ── Maven Failsafe Plugin ─────────────────────────────────────── -->
<!-- Runs INTEGRATION tests (bound to "verify" phase)                 -->
<!-- Naming convention: *IT.java or *IntegrationTest.java             -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.2.3</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- ── Maven Shade Plugin ────────────────────────────────────────── -->
<!-- Creates an "uber JAR" (fat JAR) with all dependencies included   -->
<!-- Note: Spring Boot uses its own Spring Boot Maven Plugin instead  -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
        </execution>
    </executions>
</plugin>

<!-- ── JaCoCo Plugin ─────────────────────────────────────────────── -->
<!-- Measures code coverage and enforces coverage thresholds          -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>    <!-- instruments bytecode -->
                <goal>report</goal>           <!-- generates HTML report -->
            </goals>
        </execution>
    </executions>
</plugin>
```

### Running Plugin Goals Directly

```bash
# Run a goal directly (bypasses lifecycle)
mvn compiler:compile          # only compiles, no test
mvn surefire:test             # only runs tests, no compile
mvn dependency:tree           # show dependency graph
mvn dependency:analyze        # find unused dependencies
mvn help:effective-pom        # show the fully-resolved POM (includes parent POMs)
mvn help:active-profiles      # show which profiles are active
```

---

## Section 7 — Maven Quick Reference

```bash
# ── Project creation ──────────────────────────────────────────────
# Create a new Maven project using the quickstart archetype
mvn archetype:generate \
  -DgroupId=com.bookstore \
  -DartifactId=bookstore-app \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.4 \
  -DinteractiveMode=false

# ── Most used commands ────────────────────────────────────────────
mvn clean                   # delete target/
mvn compile                 # compile main sources
mvn test                    # compile + run tests
mvn package                 # compile + test + create JAR
mvn install                 # package + install to ~/.m2
mvn clean package           # clean then package (safest)
mvn clean package -DskipTests  # skip tests (fast, not for CI)

# ── Diagnostics ───────────────────────────────────────────────────
mvn dependency:tree         # show full dependency tree
mvn dependency:analyze      # find unused/undeclared deps
mvn help:effective-pom      # show final resolved POM
mvn -v                      # show Maven version
mvn --version               # same as above

# ── Where Maven stores local cache ────────────────────────────────
ls ~/.m2/repository/        # your local artifact cache
```
