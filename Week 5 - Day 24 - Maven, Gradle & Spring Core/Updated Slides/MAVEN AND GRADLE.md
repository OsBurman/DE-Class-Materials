SECTION 1 — What is Build Automation?
[ Slide 1: Title Slide ]
Content: "Maven, Gradle & Professional Java Development" — your name, date, course name. Clean, minimal.

[ Slide 2: "The Problem – Before Build Tools" ]
Content: Two-column layout. Left column: "Manual Workflow" — a numbered pain list: (1) Manually download every library JAR, (2) Add each JAR to the classpath by hand, (3) Run javac with a long list of arguments, (4) Run tests manually one by one, (5) Manually copy files to create a deployable archive. Right column: A cartoon image or icon of a developer looking overwhelmed at a terminal. Bottom quote: "What happens when you add a new developer to the team? They repeat all of this from scratch."
SCRIPT:
"Let's start at the very beginning — before Maven or Gradle existed.
Imagine you're building a Java application in 2002. You need your code to work with a database, so you need a JDBC driver. You need some logging, so you download a logging library. You want to write unit tests, so you download JUnit. You need to parse some XML, so you grab another library.
Now you have six or seven JAR files sitting in some folder on your machine. You add them all manually to your project's classpath. You compile by running a javac command with a string of arguments so long it wraps around your terminal. You run your tests by invoking the test runner by hand. When you're ready to ship, you manually create a JAR or ZIP of everything.
Now a second developer joins the project. They have to redo all of this from scratch. And they might download slightly different versions of some of those libraries. Or put things in different folders. Or miss one entirely. These are not hypothetical problems — they were real, everyday headaches that caused real, production bugs.
This is the problem that build automation tools solve. They give you a single, declarative way to describe your project — what it needs, how it should be built, what the output should be — and then a consistent, repeatable process for turning source code into a working application that every developer on the team can run with a single command."

[ Slide 3: "What Does a Build Tool Do?" ]
Content: Central graphic showing a build tool as a pipeline. Input on the left: "Source Code (.java files) + build config file." Output on the right: "Deployable artifact (JAR/WAR)." In between, a flowchart of what happens: (1) Resolve & Download Dependencies, (2) Compile Source Code, (3) Run Tests, (4) Package into Artifact, (5) Publish/Deploy. Each step has a small icon.
SCRIPT:
"So what exactly does a build tool do? At a high level, it automates five things.
First: dependency resolution. You declare the libraries you need in a configuration file, and the build tool downloads them, caches them, and makes them available to your code. You never manually download a JAR again.
Second: compilation. The build tool knows where your source files are, what your classpath should be, and how to invoke the Java compiler correctly. One command compiles your entire project.
Third: testing. The build tool compiles your test code, runs it, collects results, and can fail the build if tests don't pass. This means broken code can never accidentally be deployed.
Fourth: packaging. The build tool assembles your compiled code and any required resources into a distributable artifact — most commonly a JAR file for a library or standalone application, or a WAR file for a web application that gets deployed to a server.
Fifth: publishing. The build tool can push your finished artifact to a shared repository so other projects or other developers can use it as a dependency.
Both Maven and Gradle do all five of these things. They differ in how you configure them and how they perform, but they solve the same fundamental problem."

[ Slide 4: "Key Terminology" ]
Content: A glossary-style slide with these terms defined clearly: Artifact, Repository (Local vs Remote), Dependency, Transitive Dependency, Build Script, Plugin. Each term is bold with a one or two sentence plain-English definition. Leave lots of white space — this is a reference slide.
SCRIPT:
"Before we go any further, let's lock down some vocabulary. These terms will come up constantly and it's important you know exactly what they mean.
An artifact is anything a build tool produces or consumes — a JAR, a WAR, a POM file. When you publish your project, you publish an artifact.
A repository is a storage location for artifacts. Your local repository is a folder on your own machine — Maven uses ~/.m2/repository — where downloaded artifacts are cached. A remote repository is a server that hosts artifacts, like Maven Central.
A dependency is a library or project that your code requires to compile or run.
A transitive dependency is a dependency of one of your dependencies. If your project uses Library A, and Library A uses Library B, then Library B is a transitive dependency of your project. Build tools resolve the entire dependency graph automatically.
A build script is the configuration file that defines your project — pom.xml for Maven, build.gradle for Gradle.
A plugin extends what the build tool can do. Almost everything a build tool does — compiling, testing, creating JARs — is handled by a plugin."


SECTION 2 — Maven: Introduction & Installation
[ Slide 5: "What is Maven?" ]
Content: Apache Maven logo. Brief history: created by Jason van Zyl in 2002, became an Apache project in 2003. Core philosophy in large text: "Convention Over Configuration." Below that: three pillars — (1) Standard project structure, (2) Unified build lifecycle, (3) Dependency management. At the bottom: current version and download URL: maven.apache.org
SCRIPT:
"Maven was created in 2002 and has been the dominant Java build tool for most of the last two decades. Even if you end up using Gradle on your projects, you will encounter Maven constantly — existing projects, open source libraries, corporate codebases. You need to know it.
Maven's core philosophy is 'convention over configuration.' This means Maven has very strong opinions about how your project should look. Where source files go. Where test files go. What the output should be called. If you follow those conventions — and you almost always should — Maven requires very little configuration. It just works.
The flip side of this is that deviating from Maven's conventions is possible but painful. Maven will fight you if you try to do something in a non-standard way. This is actually a feature for teams and beginners — it eliminates a whole class of decisions and disputes.
Maven is installed on most Java development machines and is often bundled with IDEs like IntelliJ. You can verify it's installed by running mvn -v in your terminal, which will print the Maven version, the Java version it's using, and where it's installed."

[ Slide 6: "Verifying Maven Installation" ]
Content: Terminal screenshot (or styled code block) showing the output of mvn -v. Expected output shows: Apache Maven version, Maven home path, Java version, Java home path, OS info. Below, a note: "If this command fails, Maven is not on your PATH — check your environment variables." Second code block showing: mvn --help for viewing available options.
SCRIPT:
"The first thing to do any time you sit down at a new machine is verify your tools are available and what version you're running. For Maven, that's mvn -v.
The output tells you a lot: which version of Maven you're running, where it's installed, and critically, which JDK it's using. That last point matters — Maven needs to match your project's Java version requirements.
If mvn -v fails with a 'command not found' error, Maven is not on your system PATH. You'll need to either install it or add its bin directory to your PATH environment variable.
Also worth knowing: mvn --help gives you the full list of available options if you ever need a quick reference without leaving the terminal."


SECTION 3 — Maven: The POM File In Depth
[ Slide 7: "The pom.xml — Project Object Model" ]
Content: Definition at the top: "The pom.xml is the complete description of your project. Maven reads this file to know everything about what your project is and how to build it." Below: a two-column visual. Left: "What goes in a POM" — bulleted list: Project identity (GAV), Dependencies, Build plugins, Build configuration, Profiles, Parent POM reference. Right: "What Maven does with it" — matching bullets: Identifies your artifact uniquely, Downloads the right libraries, Extends Maven's default behavior, Overrides compile settings etc., Environment-specific behavior, Inherits shared configuration.
SCRIPT:
"Everything in Maven revolves around the pom.xml file. POM stands for Project Object Model — it's an XML file that lives at the root of your project and describes your project completely.
When you run any Maven command, the very first thing Maven does is read your pom.xml. It uses that file to understand who you are, what you need, and how to build your project.
Let's walk through every important section of a real pom.xml. I want you to be able to look at any pom.xml after today and understand every line."

[ Slide 8: "POM — Project Identity (GAV Coordinates)" ]
Content: Large annotated XML block:
xml<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>  <!-- Always 4.0.0 -->

    <groupId>com.acme.ecommerce</groupId>      <!-- Organisation -->
    <artifactId>order-service</artifactId>      <!-- Project name -->
    <version>1.0.0-SNAPSHOT</version>           <!-- Current version -->
    <packaging>jar</packaging>                  <!-- Output type -->

    <name>Order Service</name>
    <description>Handles order processing</description>

</project>
Callout boxes pointing to each element with explanations. At the bottom, a note explaining SNAPSHOT: "SNAPSHOT = in-development, not stable. Remove -SNAPSHOT for a release version."
SCRIPT:
"Let's start at the top. The <modelVersion> is always 4.0.0 — don't change this, it's the POM schema version and it's been 4.0.0 for over a decade.
Then you have your three coordinates, your GAV — GroupId, ArtifactId, Version. These three values uniquely identify your project in the entire Maven ecosystem.
groupId — Think of this as your organization's namespace. The universal convention is reversed domain name: if your company domain is acme.com, your groupId starts with com.acme. Then you can add a sub-identifier for the product line: com.acme.ecommerce. This prevents naming collisions across organizations. If two companies both create a library called 'utils', the groupId tells you which one is which.
artifactId — The name of this specific project. Use lowercase, use hyphens to separate words. order-service, user-auth, payment-gateway. This becomes part of your JAR filename.
version — The current version. You'll use semantic versioning: major.minor.patch — so 1.0.0, 2.3.1, etc. The -SNAPSHOT suffix has special meaning in Maven: it flags this as an unstable, in-development version. Maven treats SNAPSHOTs differently than release versions — it will re-download them to get the latest, whereas release versions are cached permanently. When you're ready to release, you remove -SNAPSHOT.
packaging tells Maven what to produce. jar is the default and you can omit it. Use war for web applications. Use pom for parent/aggregator projects which we'll see later.
The <name> and <description> are optional but good practice — they show up in generated documentation and make your project more human-readable."

[ Slide 9: "POM — The Properties Section" ]
Content: XML code block showing:
xml<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <!-- Custom properties for version management -->
    <spring.version>6.1.0</spring.version>
    <junit.version>5.10.0</junit.version>
</properties>
Annotation: "Properties act as variables in your POM — define once, reference anywhere with ${propertyName}". Second small snippet showing a dependency using ${spring.version}.
SCRIPT:
"The properties section is one of the most useful parts of the POM. Think of it as defining variables for your XML file.
The two most important built-in properties to always set are maven.compiler.source and maven.compiler.target. These tell Maven which version of Java to compile your code against. If you don't set these, Maven defaults to an ancient Java version — Java 6 or 7 depending on the Maven version — and you'll see confusing errors when you try to use modern Java features. Always set these.
project.build.sourceEncoding set to UTF-8 ensures your source files are compiled with UTF-8 encoding, which prevents weird character bugs.
Beyond the built-in properties, you can define your own. The most common use case is centralizing library versions. Define <spring.version>6.1.0</spring.version> here, and then throughout your dependencies section you reference it as ${spring.version}. When you want to upgrade Spring, you change one line instead of hunting through your entire POM. This is especially valuable in multi-module projects where the same version needs to be consistent everywhere."

[ Slide 10: "POM — The Dependencies Section" ]
Content: Annotated XML showing the dependencies block with four examples — one for each of the main scopes, and one default (no scope tag):
xml<dependencies>

    <!-- Spring Boot Web Starter - compile scope (default) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- H2 in-memory database - runtime only -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
        <scope>runtime</scope>
    </dependency>

    <!-- JUnit 5 - test scope only -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Servlet API - provided by app server -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>

</dependencies>
Note at bottom: "Each dependency needs its GAV coordinates. The <scope> element controls when it's available and whether it ends up in your final artifact. See next slide for the full scope reference."
SCRIPT:
"The dependencies section is where you declare everything your code needs. Let's walk through this carefully.
Each dependency needs its GAV coordinates — groupId, artifactId, version. Maven uses these to find the right artifact.
The <scope> element controls when the dependency is available and whether it ends up in your final artifact. The four examples here each show a different scope — we'll go through each one on the next slide."

[ Slide 11: "POM — Dependency Scopes Reference" ]
Content: A clean reference table on the left covering all five scopes. On the right, a short callout box highlighting the key insight about test scope.
Scope     | At Compile | In Final JAR/WAR | Use For
----------|------------|------------------|---------------------------
compile   | YES (def.) | YES              | Core application libraries
test      | Tests only | NO               | JUnit, Mockito, AssertJ
provided  | YES        | NO               | Servlet API (server provides)
runtime   | NO         | YES              | Database drivers (JDBC)
system    | YES (path) | Configurable     | Avoid — breaks portability
Callout box: "Test-scope dependencies cannot accidentally leak into production code. Trying to import a test-only class in your main code causes a compile error. That's a feature, not a bug."
SCRIPT:
"Here's the full scope breakdown — this is a reference you'll come back to often.
compile is the default scope when you don't specify one. The dependency is available when compiling your code, when running tests, and it gets included in your final JAR or WAR. This is correct for your core application libraries.
test means the dependency is only available when compiling and running tests. It is NOT included in your final artifact. JUnit, Mockito, AssertJ, Testcontainers — anything that only exists to support testing should have test scope. This keeps your production artifact lean and clean.
provided means the dependency is needed to compile but will be provided by the runtime environment — you should NOT bundle it. The classic example is the Servlet API. When you deploy a WAR to Tomcat, Tomcat already has the Servlet API. If you bundle it too, you get class conflicts. Using provided scope tells Maven to make it available to compile against but leave it out of the WAR.
runtime is the opposite of provided. Maven doesn't need it to compile your code, but it needs to be present at runtime. Database drivers are the textbook example — your code compiles against the JDBC interface, not the driver itself. But at runtime you obviously need it.
system is similar to provided but you point to a specific file on your filesystem. Avoid this in practice — it breaks portability.
One key insight: test-scoped dependencies cannot accidentally leak into your production code. If you try to import a test-only class in your main code, Maven will give you a compile error. That's a feature."

[ Slide 12: "POM — Transitive Dependencies" ]
Content: A visual tree diagram. At the top: "Your project". Branches down to: spring-boot-starter-web, junit-jupiter. spring-boot-starter-web branches to: spring-webmvc, spring-core, jackson-databind, tomcat-embed. jackson-databind branches further to: jackson-core, jackson-annotations. Title: "You declared 2 dependencies. Maven resolved 15+." At the bottom: "Run mvn dependency:tree to see the full graph."
SCRIPT:
"Here's something that surprises a lot of new developers: when you add a single dependency, you often get dozens. This is because of transitive dependencies.
When you add spring-boot-starter-web to your project, that library has its own dependencies — spring-webmvc, spring-core, jackson for JSON processing, embedded Tomcat, and more. And each of those has their own dependencies. Maven resolves the entire graph for you automatically.
This is incredibly convenient — you don't have to track down every transitive dependency manually. But it comes with a catch: version conflicts.
What happens if Library A needs Jackson version 2.13, and Library B needs Jackson version 2.14? You can't have two versions on the classpath. Maven has a resolution strategy — it uses the nearest definition in the dependency graph, which generally means the version that appears first or at the shallowest level.
This is why mvn dependency:tree is one of the most important commands you'll use. It shows you the full resolved dependency graph, which versions were selected, and which were omitted due to conflicts. When you get mysterious runtime errors or class not found exceptions, the dependency tree is the first place to look."

[ Slide 13: "POM — Dependency Management Section" ]
Content: Split slide. Left: Problem — "Without dependencyManagement, each module re-declares its own versions. Versions can drift." Right: Solution — XML showing <dependencyManagement> with <dependencies> block. Explanation: declaring versions here doesn't add the dependency — it just pins the version. Child modules reference it without a version tag. Annotated example showing parent pom with dependencyManagement and child pom consuming it version-free.
SCRIPT:
"Once you start building projects with multiple modules — which you will — you face a new problem: keeping dependency versions consistent across all of them.
The <dependencyManagement> section solves this. It's important to understand the distinction: adding a dependency inside <dependencyManagement> does NOT include that dependency in your project. It simply pins the version. It's like saying 'if anyone in this project tree uses this library, use this version.'
Then in your actual <dependencies> section, you reference those libraries without a <version> tag, and Maven knows to use the version specified in the management section.
This becomes extremely powerful in a parent POM — a single pom.xml at the top of a multi-module project that all the child modules inherit from. The parent declares all versions in dependencyManagement. Every child module can include the libraries they need without worrying about versions — they all get the same, centrally managed versions.
This pattern is exactly what Spring Boot uses in its own parent POM, which we'll look at on the very next slide."

[ Slide 14: "POM — The Build Section & Plugins" ]
Content: Annotated XML showing a build section:
xml<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <compilerArgs>
                    <arg>-Xlint:all</arg>
                </compilerArgs>
            </configuration>
        </plugin>

        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>3.2.0</version>
        </plugin>
    </plugins>
</build>
Note at bottom: "Plugins extend Maven's capabilities. Configuration blocks customize how a plugin behaves."
SCRIPT:
"The <build> section is where you configure and add plugins. We'll go deep on plugins in a moment, but I want you to see them in context first.
The maven-compiler-plugin is the most important plugin to configure explicitly. This is what compiles your Java code. Even though Maven uses this plugin by default under the hood, you almost always want to explicitly configure it to specify your Java version. Notice the <source> and <target> elements — these must match the Java version in your properties.
The spring-boot-maven-plugin is what makes a Spring Boot project special. Adding this plugin gives you the ability to run mvn spring-boot:run to launch your application directly from Maven. It also changes how the JAR is packaged — instead of a normal JAR, it creates a 'fat JAR' or 'über-JAR' that contains not just your compiled code but all of your dependencies bundled inside. This single file is everything you need to run the application — you just do java -jar myapp.jar and it runs anywhere Java is installed.
We're going to look at this plugin much more closely on the next slide."

[ Slide 15: "Maven + Spring Boot — The Parent POM" ]
Content: Two-part slide. Top half: Annotated XML showing the parent declaration:
xml<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<!-- With this parent declared, no explicit versions needed
     for any Spring-managed dependency: -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- No <version> tag needed! -->
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <!-- No <version> tag needed! -->
    </dependency>
</dependencies>
Bottom half: A callout box listing what spring-boot-starter-parent gives you: (1) Managed versions for 300+ common libraries via dependencyManagement, (2) Default compiler settings for Java, (3) UTF-8 source encoding configured automatically, (4) Preconfigured spring-boot-maven-plugin for fat JAR packaging.
SCRIPT:
"This slide connects everything we just covered — parent POMs, dependencyManagement, and the spring-boot-maven-plugin — into how real Spring Boot projects are actually structured.
The <parent> block tells Maven that your project inherits from another POM. When you set spring-boot-starter-parent as your parent, you are inheriting the Spring Boot team's carefully maintained POM, which contains a massive <dependencyManagement> block covering over 300 commonly used libraries — Spring itself, Jackson, JUnit, Hibernate, Tomcat, Flyway, and dozens more.
Because of that inherited dependencyManagement, you can declare Spring Boot dependencies in your own <dependencies> section with no <version> tag. The version is controlled centrally by the parent. Every Spring library in your project is guaranteed to be on versions that are tested to work together. This eliminates an entire class of 'works on my machine' problems caused by mismatched library versions.
The parent also configures the spring-boot-maven-plugin for you automatically, which is what enables mvn package to produce a fat JAR — an executable JAR with all your dependencies bundled inside — without you having to configure the plugin yourself.
When you create a Spring Boot project from Spring Initializr, this parent declaration is the very first thing in the generated pom.xml. Now you know exactly what it's doing."

[ Slide 16: "POM — Profiles" ]
Content: XML snippet showing a profile block with an id of "production" that overrides properties and activates on a specific condition. Explanation text: "Profiles allow different configurations for different environments (dev, test, production) — all within one pom.xml." Show activation by command line: mvn package -P production. Show activation by environment variable.
SCRIPT:
"The last major POM section worth knowing is profiles. Profiles let you define variations in your build that activate under specific conditions.
The most common use case is environment-specific configuration. Your development build might use an in-memory H2 database, but your production build should use real PostgreSQL. You could maintain two separate POMs, but that's a nightmare to keep in sync. Instead, you define profiles.
A profile has an ID and can contain any POM elements — dependencies, properties, build configuration. You activate a profile on the command line with the -P flag: mvn package -P production. You can also configure profiles to activate automatically based on environment variables, operating system, file existence, or JDK version.
You won't use profiles constantly, but when you need them they're invaluable, and you'll encounter them in real projects."


SECTION 4 — Maven: Standard Directory Structure & Project Setup
[ Slide 17: "Maven's Standard Directory Layout" ]
Content: Full directory tree with every folder labeled:
my-project/
├── pom.xml                          ← Project definition
├── src/
│   ├── main/
│   │   ├── java/                    ← Application source code
│   │   │   └── com/acme/myapp/
│   │   │       ├── MyAppApplication.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       └── repository/
│   │   └── resources/               ← Config files, properties
│   │       ├── application.properties
│   │       └── static/
│   └── test/
│       ├── java/                    ← Test source code (mirrors main)
│       │   └── com/acme/myapp/
│       │       └── service/
│       └── resources/               ← Test-specific config
└── target/                          ← Generated by Maven (NEVER commit)
    ├── classes/                     ← Compiled .class files
    ├── test-classes/
    ├── surefire-reports/            ← Test results
    └── my-project-1.0.0.jar         ← Your artifact
Large red warning box: "NEVER commit the target/ folder. Add it to .gitignore immediately."
SCRIPT:
"Maven's directory layout is non-negotiable. If you deviate from this structure, you will fight Maven the entire time. Follow it and Maven handles almost everything automatically.
Your entire project lives under two top-level items: the pom.xml at the project root, and the src/ directory.
Inside src/, you have main/ and test/. Main is your production code. Test is your test code. They are completely separate. This is important — it means your tests are never bundled into your production artifact.
Under main/, you have java/ for your Java source files and resources/ for everything else. Your .properties files, YAML config files, XML config files, static web assets — anything that isn't Java source code but needs to be part of your application goes in resources/.
Under test/, you have the same structure mirroring main/. Your test classes should be in the same package as the classes they're testing. So if you have com.acme.myapp.service.OrderService in main, your test should be com.acme.myapp.service.OrderServiceTest in test. This mirroring allows your test classes to access package-private methods.
The target/ directory is entirely generated by Maven. Every time you run a build, Maven creates or refreshes this folder. It contains your compiled .class files, your test results in surefire-reports, and your final JAR or WAR. Since it's always generated, it should never go into version control. The moment you create a Maven project, add target/ to your .gitignore — which is exactly what the next slide covers."

[ Slide 18: "Your .gitignore for Java Projects" ]
Content: A clean, annotated .gitignore file template. Group entries with comments explaining each section:
# ── Build output — always generated, never commit ──
target/
build/

# ── IDE files — personal to each developer ──
.idea/
*.iml
*.iws
*.ipr
.eclipse/
.settings/
.classpath
.project
*.class

# ── OS files ──
.DS_Store
Thumbs.db

# ── Log and temp files ──
*.log
*.tmp

# ── Sensitive config — never commit credentials ──
application-local.properties
*.env
Callout box: "Maven projects need target/ ignored. Gradle projects need build/ ignored. Add both if your repo will ever have either type of project."
SCRIPT:
"Every Java project you create needs a .gitignore file set up before your first commit. This is not optional — committing build output to version control is one of the most common mistakes new developers make, and cleaning it up is painful.
The most important entries are the build output folders: target/ for Maven and build/ for Gradle. These directories can contain hundreds of files that are completely regenerated on every build. They make your repository huge, create constant merge conflicts, and give you no useful information since they're always reproducible from source.
IDE files — the .idea/ folder, .iml files, Eclipse project files — are personal to each developer. Your IntelliJ settings shouldn't override your teammate's. Keep these out of the repo.
The single most important rule you will ever follow about version control: never commit credentials. No passwords, no API keys, no database connection strings with real credentials. If you commit a secret, assume it's compromised — even if you delete it in a later commit, it lives forever in git history. Your environment-specific config files like application-local.properties belong in .gitignore, not in the repo. Real credentials belong in environment variables or a secrets manager."

[ Slide 19: "The Local Maven Repository" ]
Content: Diagram showing: My Project → (mvn install) → ~/.m2/repository/com/acme/order-service/1.0.0/order-service-1.0.0.jar. Then an arrow from another project pointing to the same file with label "another local project uses this as a dependency." Directory tree showing the ~/.m2/repository structure and how artifacts are organized by groupId/artifactId/version.
SCRIPT:
"Every artifact that Maven downloads or that you install with mvn install ends up in your local repository, which by default is a folder called .m2 in your home directory — so ~/.m2/repository on Mac/Linux or C:\Users\YourName.m2\repository on Windows.
The structure inside that folder mirrors the GAV coordinates. If you have a library with groupId com.acme, artifactId order-service, version 1.0.0 — it ends up at ~/.m2/repository/com/acme/order-service/1.0.0/. Maven always looks here first before going to the internet.
This is why mvn install is important in multi-module projects. If Project A depends on Project B, and both are on your machine, you need to run mvn install on Project B first. That publishes it to your local repository. Then when Project A builds, Maven finds Project B there.
The local repository acts as a cache. Once an artifact is downloaded, it stays there. Builds don't re-download things they already have, which is why subsequent builds are faster than the first one. The only exception is SNAPSHOT versions — Maven will check if there's a newer snapshot available."


SECTION 5 — Maven: Lifecycle Phases
[ Slide 20: "Maven's Three Built-In Lifecycles" ]
Content: Three boxes side by side. Box 1: "default" — description: "Handles project build and deployment. The lifecycle you use most." Box 2: "clean" — description: "Handles project cleaning — deletes the target/ directory." Box 3: "site" — description: "Handles the creation of project documentation." Below: Note that today we focus on the default and clean lifecycles.
SCRIPT:
"Maven actually has three separate lifecycles, not one. Most tutorials only talk about the default lifecycle, but knowing all three helps you understand the command structure.
The default lifecycle is the main one. It goes from validating your project all the way through compiling, testing, packaging, and deploying. This is what you use for building your project.
The clean lifecycle is simple — it has one primary phase, clean, which deletes the target/ directory. This is how you get a fresh start.
The site lifecycle generates project documentation — Javadocs, dependency reports, test coverage reports. You'll encounter it in enterprise settings.
These lifecycles are independent. When you run mvn clean install, you're actually running two separate lifecycle executions — the clean lifecycle and the default lifecycle up through the install phase. The space between them is what separates them."

[ Slide 21: "The Default Lifecycle — All Phases" ]
Content: A vertical pipeline diagram showing every phase of the default lifecycle in order with a brief description of each. Group them visually with subtle background shading into three bands — Preparation (validate through process-resources), Compile & Test (compile through test), and Package & Publish (prepare-package through deploy):
── PREPARATION ──────────────────────────────────
validate      → Check project is correct, all info available
initialize    → Initialize build state, set properties
generate-sources → Generate source code (e.g., from annotations)
process-sources  → Process sources (filtering, etc.)
generate-resources → Generate resources
process-resources → Copy resources to target/classes

── COMPILE & TEST ───────────────────────────────
compile       → Compile source code               ★
process-classes  → Post-process compiled files
generate-test-sources → Generate test source code
process-test-sources  → Process test sources
generate-test-resources → Create resources for tests
process-test-resources → Copy test resources
test-compile  → Compile test source code
process-test-classes → Post-process test compiled files
test          → Run tests using test framework     ★

── PACKAGE & PUBLISH ────────────────────────────
prepare-package → Prepare before packaging
package       → Package into JAR/WAR              ★
pre-integration-test → Set up integration test environment
integration-test → Deploy and run integration tests
post-integration-test → Clean up integration test environment
verify        → Run checks to verify package validity ★
install       → Install to local repo (~/.m2)     ★
deploy        → Copy to remote repo               ★
★ marks the six phases you will use daily. Note at bottom: "Running any phase automatically runs all phases above it in the same band — and all phases in every band above that."
SCRIPT:
"Here's the full default lifecycle. I know there are a lot of phases — don't panic. Let me give you the mental map that makes this manageable.
Think of the lifecycle in three groups. The Preparation band at the top is housekeeping — validation, initialization, generating any code that needs to exist before compilation. In practice, these run silently in the background and you rarely interact with them directly.
The Compile & Test band is where your code comes to life. Your source gets compiled, your test code gets compiled, and your tests run. This is the feedback loop you'll be in constantly during development.
The Package & Publish band is the finish line. Your compiled code gets assembled into a JAR or WAR, integration checks run, and the artifact gets published — either to your local repo or a remote one.
The critical rule — and I'll say this multiple times because it trips people up — is that the lifecycle is sequential. When you run a phase, Maven runs that phase AND everything above it in the full list. So mvn package runs all of Preparation, all of Compile & Test, and then package. You don't chain them manually.
This also means if your tests fail, mvn package fails. You should never ship untested code. If you absolutely need to skip tests temporarily, you can do mvn package -DskipTests, but treat this as a workaround, not a habit."

[ Slide 22: "The Phases You'll Use Daily" ]
Content: Command reference card format. For each command, show: the command, what phases it runs, and when to use it.
mvn validate
→ Just validates POM is correct
→ Use: Quick sanity check

mvn compile
→ validate → ... → compile
→ Use: Verify code compiles without running tests

mvn test
→ validate → ... → test
→ Use: Run tests during development

mvn package
→ validate → ... → package
→ Use: Build the JAR/WAR

mvn verify
→ validate → ... → verify
→ Use: Run integration tests and quality checks

mvn install
→ validate → ... → install
→ Use: Build + publish to local repo for use by other local projects

mvn deploy
→ validate → ... → deploy
→ Use: Publish to remote repo (usually done by CI/CD)

mvn clean
→ (clean lifecycle) Deletes target/
→ Use: Get a fresh build

mvn clean install
→ Clean first, then full build + local install
→ Use: The most common full-build command
SCRIPT:
"Let's make this practical. Here are the commands you will actually type day to day.
mvn compile — Use this when you just want to check that your code compiles. It doesn't run tests, which makes it faster. Good for a quick sanity check after writing new code.
mvn test — Compiles everything and runs your test suite. Use this constantly during development. Get in the habit of running your tests frequently.
mvn package — The full build that gives you your JAR or WAR in the target folder. This is what you run when you want a distributable artifact.
mvn install — Everything package does, plus it installs the artifact to your local Maven repository. Use this when you have multiple projects on your machine that depend on each other.
mvn deploy — Pushes to a remote repository. In practice you rarely run this manually — it's typically automated by your CI/CD pipeline.
mvn clean install — This is probably the most commonly typed Maven command in the world. The clean ensures any previously compiled code is gone, then install does the full build. When in doubt, run this. It ensures you're building from a clean state and catches any issues that might be masked by stale compiled files."

[ Slide 23: "Skipping Tests & Other Common Flags" ]
Content: Reference table of common Maven CLI flags:
-DskipTests             Skip test execution (still compiles tests)
-Dmaven.test.skip=true  Skip test compilation AND execution
-P profile-name         Activate a specific profile
-pl module-name         Run for specific module in multi-module project
-am                     Also build dependencies of selected module
-o                      Offline mode — don't go to internet
-X                      Debug output (very verbose, for troubleshooting)
-q                      Quiet mode — minimal output
-e                      Show exception stack traces
Note: "These can be combined: mvn clean install -DskipTests -P production"
SCRIPT:
"A few command-line flags that will save you a lot of time.
-DskipTests skips running the tests but still compiles your test code. This is faster than a full build with tests and is useful when you're iterating quickly and have confirmed your tests pass. Note: the capital D means 'define a system property' — this is a standard Java mechanism Maven uses for command line parameters.
-Dmaven.test.skip=true goes further — it skips even compiling the test code. This is slightly faster but means any compilation errors in your tests won't be caught.
-X enables debug output. When a build is mysteriously failing and the normal output isn't telling you why, -X gives you extremely verbose output showing every decision Maven makes. It's a wall of text but it contains the information you need.
-o is offline mode. Maven won't try to connect to the internet and will only use what's already in your local repository. Useful if you're on a bad network connection and your local cache is warm."


SECTION 6 — Maven: Plugins & Goals
[ Slide 24: "How Maven Plugins Work" ]
Content: Diagram showing the relationship: Maven Core → calls → Plugin → contains → Goals. Each goal maps to a lifecycle phase. Central concept: "Maven itself has almost no built-in behavior. Almost everything is implemented in plugins." Table showing the default phase-to-plugin-goal bindings:
Phase              → Plugin                    → Goal
compile            → maven-compiler-plugin     → compile
test               → maven-surefire-plugin     → test
package (jar)      → maven-jar-plugin          → jar
install            → maven-install-plugin      → install
deploy             → maven-deploy-plugin       → deploy
SCRIPT:
"Here's a mental model that clarifies a lot of confusion about Maven: Maven's core does almost nothing by itself. It's basically a lifecycle engine. Everything that actually happens during a build is done by plugins.
A plugin is a JAR that contains one or more goals. A goal is a specific, well-defined task. The maven-compiler-plugin has a compile goal and a testCompile goal. The maven-surefire-plugin has a test goal. And so on.
Maven ships with a set of default plugins that are automatically bound to lifecycle phases. That's why mvn compile works without you explicitly adding the compiler plugin to your POM — Maven has a default binding that ties the compile phase to the compiler plugin's compile goal. You only need to explicitly declare a plugin in your POM when you want to use a non-default plugin, or when you want to customize the configuration of a default plugin."

[ Slide 25: "Running Plugin Goals Directly" ]
Content: Explanation of the syntax: mvn groupId:artifactId:goal or shorthand mvn prefix:goal. Examples:
bash# Full syntax
mvn org.apache.maven.plugins:maven-dependency-plugin:tree

# Shorthand (for well-known plugins)
mvn dependency:tree
mvn dependency:analyze
mvn compiler:compile
mvn surefire:test
mvn spring-boot:run
mvn spring-boot:build-image
Note: "Plugin goals can be run directly, bypassing the lifecycle entirely."
SCRIPT:
"You can also run plugin goals directly from the command line without going through the lifecycle. The full syntax is mvn groupId:artifactId:version:goal but Maven has a shorthand system — well-known plugins have a prefix, so you can just type mvn dependency:tree.
The most useful ones to know:
mvn dependency:tree — prints your full dependency tree. Run this whenever you have dependency conflicts or unexpected libraries appearing.
mvn dependency:analyze — analyzes your dependencies and reports any you declared but don't actually use, and any you use but didn't explicitly declare (relying on transitive dependencies). This is a code quality tool.
mvn spring-boot:run — when using the Spring Boot Maven plugin, this compiles and runs your application without first creating a JAR. Fast for development.
mvn spring-boot:build-image — creates a production-ready Docker container image from your application. No Dockerfile required."

[ Slide 26: "Important Plugins Reference" ]
Content: A reference table of commonly encountered Maven plugins:
Plugin                       | What it does
-----------------------------|------------------------------------------
maven-compiler-plugin        | Compiles Java source code
maven-surefire-plugin        | Runs unit tests (JUnit, TestNG)
maven-failsafe-plugin        | Runs integration tests
maven-jar-plugin             | Creates JAR files
maven-war-plugin             | Creates WAR files
maven-dependency-plugin      | Dependency analysis and management
maven-resources-plugin       | Copies resources to output
maven-clean-plugin           | Cleans build output
maven-install-plugin         | Installs to local repo
maven-deploy-plugin          | Deploys to remote repo
maven-checkstyle-plugin      | Enforces code style rules
jacoco-maven-plugin          | Measures test code coverage
spring-boot-maven-plugin     | Spring Boot executable JARs
SCRIPT:
"Here's a plugin reference you'll want to bookmark. You don't need to memorize all of these today, but I want you to be familiar with them so when you encounter them in a pom.xml you understand what they're doing.
The compiler, surefire, jar, install, deploy, and clean plugins are what Maven uses internally by default — you'll usually only see them explicitly in a POM when their behavior is being customized.
The failsafe plugin is important to distinguish from surefire. Surefire runs unit tests — fast tests with no external dependencies that run during the test phase. Failsafe runs integration tests — slower tests that might need a database or network connection — and runs them in the integration-test phase. This separation keeps your build fast, since unit tests run on every build but integration tests can be skipped when you just want a quick compile-and-test cycle.
JaCoCo is the code coverage plugin — it instruments your tests to measure which lines of your code are executed, and produces a report. Many teams set a minimum coverage threshold: if your tests cover less than 80% of your code, the build fails.
We've already talked about the Spring Boot plugin — it's essential for any Spring Boot project."


SECTION 7 — Maven: Dependency Management Deep Dive
[ Slide 27: "Maven Central Repository" ]
Content: Screenshot/mockup of search.maven.org interface. Explanation: "Maven Central is the world's largest repository of open source Java artifacts. It hosts millions of libraries." Diagram showing the complete resolution order: (1) Local repo ~/.m2, (2) configured remote repos, (3) Maven Central. Show how to add an additional repository to the POM (e.g., Spring milestones).
SCRIPT:
"Maven Central at search.maven.org is the primary public repository for the entire Java ecosystem. When you add a dependency to your pom.xml, by default Maven looks here.
The resolution order is important to understand. Maven first checks your local repository — your ~/.m2 folder. If the artifact is there, it uses it immediately without going to the network. If not, it checks any remote repositories configured in your POM or in Maven's settings, and finally Maven Central itself.
You can add additional repositories to your POM when you need artifacts that aren't in Maven Central. Spring publishes milestone and snapshot releases to their own repository at repo.spring.io. You add it to a <repositories> block in your POM. In enterprise environments, your company typically runs a private repository manager like Nexus or Artifactory, which proxies Maven Central and also hosts internal artifacts. All developer builds point to that internal server rather than directly to the internet — this gives the company control over what libraries are approved, ensures dependencies are always available even if the internet is down, and speeds up builds because everything is on the local network.
At search.maven.org you can search for any library, see available versions, and get the exact XML snippet to paste into your POM. Get comfortable using this site — it'll save you a lot of typing."

[ Slide 28: "Dependency Conflict Resolution" ]
Content: Diagram showing a conflict scenario:
Your project
├── library-A (v1.0)  →  requires commons-lang3 v3.11
└── library-B (v1.0)  →  requires commons-lang3 v3.12

Maven must choose ONE version of commons-lang3.
Maven's rule: "nearest definition wins"
→ The version declared closest to your project in the
  dependency graph wins.
Show how to force a specific version using <dependencyManagement>. Show how <exclusions> work to remove an unwanted transitive dep.
SCRIPT:
"Dependency conflicts are one of the most common sources of mysterious build failures, and you will encounter them. Let me explain exactly how Maven handles them.
The scenario: Library A needs commons-lang3 version 3.11. Library B needs commons-lang3 version 3.12. You can only have one on the classpath. Maven has to pick one.
Maven's rule is 'nearest definition wins' — the version that appears at the shallowest level in the dependency tree, or that appears first in the declaration order, wins. This can sometimes result in an older version winning, which causes runtime errors if the code expects a newer API.
You have two tools to deal with this.
First, <dependencyManagement>: explicitly declare the version of commons-lang3 you want at the top level of your project. This overrides whatever versions the transitive resolution would have chosen.
Second, <exclusions>: if a library is pulling in a transitive dependency you specifically don't want, you can exclude it at the declaration point. For example, if Library A pulls in an old version of commons-lang3 and you want to use the newer one from Library B, you can add an exclusion to Library A's dependency declaration to prevent it from bringing in its version.
Remember: whenever you hit a weird runtime error, especially a ClassNotFoundException or a NoSuchMethodError, your first move should be mvn dependency:tree. Version conflicts are the most likely culprit."


SECTION 8 — Maven Demo: Create & Build a Project
[ Slide 29: "Demo — Creating a Maven Project" ]

⚠️ INSTRUCTOR NOTE — LIVE DEMO: You will need a terminal and IDE open. The archetype command downloads from Maven Central on first run — if your classroom network is slow, run this command once beforehand to warm your local cache. No additional browser windows are needed; everything is scripted below.

Content: Step-by-step reference for the demo.
bash# Generate a minimal Maven project from a template
mvn archetype:generate \
  -DgroupId=com.acme.demo \
  -DartifactId=my-first-project \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.4 \
  -DinteractiveMode=false

# Navigate into the project
cd my-first-project

# Run through the lifecycle step by step
mvn compile          # Watch: downloads deps on first run, then compiles
mvn test             # Watch: Surefire prints test count + results
mvn package          # Watch: JAR appears in target/
mvn clean install    # Watch: target/ deleted, full rebuild, installed to ~/.m2
Annotated expected output for each step — what "BUILD SUCCESS" looks like, what the Surefire test summary looks like, where the JAR appears in target/.
SCRIPT:
"Let's build something real. I'm going to create a Maven project from scratch and walk through the entire lifecycle.
Maven has a feature called archetypes — these are project templates. The maven-archetype-quickstart is the simplest one, creating a minimal Maven project with a Hello World class and a test.
[Run the archetype:generate command in the terminal]
Watch what Maven does — it downloads the archetype from Maven Central if it's not already cached, generates the project structure, creates the pom.xml with our specified groupId and artifactId, and creates a sample App.java and AppTest.java.
[Open the project in the IDE]
Look at the directory structure — exactly what we discussed. src/main/java with the App class, src/test/java with the AppTest class. Look at the generated pom.xml — it's minimal but valid. Notice the JUnit dependency with test scope. Let's add the maven-compiler-plugin to set Java 17 as our version.
[Add compiler plugin configuration to the POM in the IDE]
Now mvn compile in the terminal. Watch the output — Maven prints each step, and you can see it downloading any dependencies it needs the first time.
[Run mvn compile, then mvn test, then mvn package]
When we run mvn test, the Surefire output shows how many tests ran, how many passed, how many failed. After mvn package, look in the target folder — our JAR is there, and surefire-reports has detailed XML and HTML test results.
Now mvn clean install — the target directory is deleted, a complete fresh build happens, and the artifact is installed to our local repository. The JAR is now at ~/.m2/repository/com/acme/demo/my-first-project."


SECTION 9 — Gradle: Introduction
[ Slide 30: "What is Gradle?" ]
Content: Gradle elephant logo. "Gradle is a build automation tool designed for multi-project builds. It combines the power and flexibility of a programming language with the conventions of Maven." Key facts: Released 2007, written in Java/Groovy, powers all Android builds, used widely in Spring ecosystem. Performance headline: "Up to 10x faster than Maven for large projects due to incremental builds and build cache."
SCRIPT:
"Gradle launched in 2007, initially in the Groovy community, and has grown to become the dominant build tool in the Android world and a major player in the broader Java ecosystem. Google chose Gradle as the official build tool for Android, which alone means millions of developers use it daily.
Gradle's core design principle is different from Maven's. Instead of convention over configuration with XML, Gradle gives you a programming language to write your build logic. This means infinite flexibility, but also more responsibility to use that flexibility wisely.
Gradle is built for performance in a way Maven wasn't. Its incremental build engine tracks what changed and only rebuilds what's affected. Its build cache stores task outputs and reuses them across machines. For large projects with hundreds of modules, these features can reduce build times by an order of magnitude.
If you're going into Android development, Gradle is mandatory — you have no choice. If you're in Spring or general Java development, you'll see both Maven and Gradle, and increasingly Gradle."

[ Slide 31: "Gradle Wrapper — Always Use It" ]
Content: Explanation of the Gradle Wrapper. File list showing the wrapper files that should be committed to version control:
gradlew              ← Shell script (Mac/Linux)
gradlew.bat          ← Batch script (Windows)
gradle/
└── wrapper/
    ├── gradle-wrapper.jar
    └── gradle-wrapper.properties  ← Specifies Gradle version
Content of gradle-wrapper.properties showing the distributionUrl with the pinned version. Large callout: "ALWAYS run ./gradlew (not gradle) — this guarantees everyone uses the same Gradle version."
SCRIPT:
"Before we look at any Gradle configuration, I need to tell you about the Gradle Wrapper, because it's the first thing you should understand about any Gradle project.
The Gradle Wrapper is a small set of scripts and a JAR that you commit alongside your project code. These files detect what version of Gradle is required for this project — specified in the gradle-wrapper.properties file — and automatically download and use that exact version if it's not already installed locally.
This solves a huge problem: 'it works on my machine.' Without the wrapper, if I'm running Gradle 8.5 and you're running Gradle 7.6, we might get different behavior. With the wrapper, we both run the same version, every time, automatically.
The rule is simple: always use ./gradlew on Mac/Linux or gradlew.bat on Windows, never the bare gradle command. The wrapper files — gradlew, gradlew.bat, and the entire gradle/wrapper/ directory — should always be committed to version control. This is the opposite of the target/ or build/ output folders, which should never be committed."

[ Slide 32: "Maven Wrapper — The Same Idea for Maven" ]
Content: Side-by-side comparison of the Maven Wrapper and Gradle Wrapper. Show the Maven Wrapper files:
mvnw              ← Shell script (Mac/Linux)
mvnw.cmd          ← Batch script (Windows)
.mvn/
└── wrapper/
    ├── maven-wrapper.jar
    └── maven-wrapper.properties  ← Specifies Maven version
Callout box: "Every Spring Boot project generated by Spring Initializr includes the Maven Wrapper. Use ./mvnw instead of mvn for the same portability guarantee as ./gradlew."
SCRIPT:
"Maven has the same concept — the Maven Wrapper. It works identically to the Gradle Wrapper: a shell script and a small JAR that you commit alongside your source code. They pin the Maven version and automatically download it if a developer doesn't have it locally.
This is important to know because every project you generate from Spring Initializr at start.spring.io comes with the Maven Wrapper already included. When you open a Spring Boot project for the first time, you'll see mvnw and mvnw.cmd at the root alongside your pom.xml.
The rule is the same as Gradle: use ./mvnw instead of mvn when working on a project that includes the wrapper. Everything works identically — ./mvnw clean install, ./mvnw spring-boot:run, all your normal Maven commands — you just swap the mvn prefix for ./mvnw.
For all the Maven commands in this course, you can use either mvn or ./mvnw interchangeably if your project has the wrapper. On a new machine or a new developer's laptop, ./mvnw is the safer choice."


SECTION 10 — Gradle: Build Scripts In Depth
[ Slide 33: "Groovy DSL vs Kotlin DSL" ]
Content: Side-by-side comparison:
Groovy DSL (build.gradle)    |  Kotlin DSL (build.gradle.kts)
-----------------------------|--------------------------------
Dynamic typing               |  Static typing
Less verbose syntax          |  More verbose but more explicit
Older, more examples online  |  Modern, growing rapidly
Less IDE support             |  Excellent IDE support (IntelliJ)
Equivalent snippet side by side — declaring a dependency in Groovy vs Kotlin DSL. Recommendation: "For new projects, Kotlin DSL is recommended. This course uses Groovy DSL for broader compatibility, but the concepts are identical."
SCRIPT:
"Gradle supports two languages for build scripts. The original is Groovy, giving you a build.gradle file. The newer option is Kotlin, giving you a build.gradle.kts file. The .kts extension stands for Kotlin Script.
Both do the same thing. The concepts we're going to cover apply to both. The syntax is slightly different.
Kotlin DSL has been growing rapidly because it offers something Groovy doesn't: static typing. This means your IDE — especially IntelliJ — can give you autocompletion, type checking, and refactoring support inside your build scripts. It also means typos in your build script show up as compile errors immediately rather than as runtime build failures.
Groovy DSL has more examples online because it's been around longer, which makes it easier to search for solutions. It's also slightly more concise.
For new projects today, Kotlin DSL is the recommendation from the Gradle team and is what Android projects use. But you will encounter both in the real world. We'll use Groovy in this session because it has more compatibility with existing resources you'll find while learning, and once you understand Groovy DSL, reading Kotlin DSL is straightforward."

[ Slide 34: "The settings.gradle File" ]
Content: Annotated settings.gradle:
groovy// settings.gradle

// The name of the root project
rootProject.name = 'my-application'

// For multi-module projects, list all modules:
include 'core'
include 'web'
include 'data'

// Or using Kotlin DSL: rootProject.name = "my-application"
Explanation: "Every Gradle project must have a settings.gradle file. This is where the project name is defined and where multi-module structure is declared."
SCRIPT:
"Every Gradle project has a settings.gradle file at the project root, alongside your build.gradle. This file serves two purposes.
First, it defines the project name via rootProject.name. This is the equivalent of the artifactId in Maven.
Second, for multi-module projects, it lists all the sub-modules using include statements. Gradle reads this file first when it starts up to understand the overall project structure.
For a simple single-module project, your settings.gradle might only have two lines — the rootProject.name declaration and nothing else. For a large enterprise application with dozens of modules, this file lists all of them, and Gradle knows to look for a build.gradle in each corresponding subdirectory."

[ Slide 35: "Anatomy of build.gradle — Plugins Block" ]
Content: Focused annotated snippet:
groovyplugins {
    id 'java'                                            // Standard Java support
    id 'java-library'                                    // If building a library
    id 'org.springframework.boot' version '3.2.0'        // Spring Boot
    id 'io.spring.dependency-management' version '1.1.4' // Spring dependency BOM
    id 'checkstyle'                                      // Code style checking
}
Explanation below: "The plugins block is always the first thing in build.gradle. Plugins add tasks and extend Gradle's build model." Note on the 'java' plugin: "Adds compileJava, test, jar, and other standard tasks."
SCRIPT:
"Let's walk through a complete build.gradle file section by section, the same way we walked through the pom.xml.
The plugins block comes first, and this is critical — Gradle requires it to be the first block in the file with no code before it.
The id 'java' plugin is the foundation of any Java project. Without it, Gradle knows nothing about Java. Adding it gives you tasks like compileJava, test, and jar. It also sets up the standard directory structure — and yes, Gradle uses the same src/main/java and src/test/java structure as Maven by default. Convention over configuration shows up in Gradle too.
The id 'java-library' plugin extends the java plugin specifically for libraries — projects that will be consumed as dependencies by other projects. It adds an important distinction between API dependencies (which are exposed to consumers of your library) and implementation dependencies (which are internal). We'll cover this in the dependency section.
The Spring Boot and Spring Dependency Management plugins work together. The Spring Boot plugin creates executable JARs and adds the bootRun task. The dependency management plugin applies Spring Boot's Bill of Materials (BOM), which pins versions for hundreds of libraries — the same way spring-boot-starter-parent works in Maven.
Plugins with external IDs like 'org.springframework.boot' need a version number in the plugins block, because Gradle needs to download them. Core Gradle plugins like 'java' are bundled and don't need versions."

[ Slide 36: "Anatomy of build.gradle — Project Configuration" ]
Content: Annotated snippet:
groovygroup = 'com.acme.ecommerce'      // Equivalent to Maven groupId
version = '1.0.0-SNAPSHOT'        // Project version

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    // Or in newer Gradle:
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Equivalent to Maven's properties section
ext {
    springVersion = '6.1.0'
    junitVersion = '5.10.0'
}
Note: "In Gradle, artifactId defaults to the rootProject.name set in settings.gradle."
SCRIPT:
"After the plugins block, you set your project-level configuration.
group and version serve the same purpose as Maven's groupId and version. The artifactId equivalent — the project name — comes from settings.gradle rather than build.gradle.
Setting the Java version is important, just like with Maven. You can use the java block with sourceCompatibility and targetCompatibility, or the more modern toolchain approach. The toolchain approach is preferred in Gradle 7+ because it can automatically provision the correct JDK version if you don't have it installed.
The ext block defines extra properties — Gradle's equivalent of Maven's <properties> section. You can define version strings here and reference them with $springVersion or ${ext.springVersion} in your dependencies block."

[ Slide 37: "Anatomy of build.gradle — Repositories" ]
Content: Annotated snippet:
groovyrepositories {
    mavenCentral()           // Maven Central — always include this
    mavenLocal()             // Local ~/.m2 repo — use when needed
    google()                 // Google's repository — for Android
    maven {                  // Custom repository
        url 'https://repo.spring.io/milestone'
    }
}
Important note: "Repositories block tells Gradle WHERE to look for dependencies. This is per-project configuration — you must declare it explicitly. For enterprise setups with Nexus/Artifactory, replace mavenCentral() with your internal URL."
SCRIPT:
"The repositories block tells Gradle where to look for dependencies. This is explicit in Gradle in a way Maven handles partially through convention — you have to tell Gradle which repositories to use.
mavenCentral() is almost always the first repository you add. It points to Maven Central, the same repository Maven uses.
mavenLocal() tells Gradle to also check your local ~/.m2 repository. This is useful during development when you're working on a library and have installed it locally with mvn install — Gradle can find it there. But be careful — don't depend on mavenLocal() in production builds because the local repo content varies between machines.
google() is needed for Android projects.
For custom repositories — like Spring milestones for pre-release versions, or an enterprise Nexus/Artifactory instance — you use the maven { url '...' } syntax.
One important consideration: repository order matters. Gradle checks them in the order they're listed. Put your fastest and most reliable repository first."

[ Slide 38: "Anatomy of build.gradle — Dependencies" ]
Content: Fully annotated dependencies block:
groovydependencies {

    // Standard application dependency
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Dependency with explicit version
    implementation 'com.google.guava:guava:32.1.2-jre'

    // Runtime-only (DB driver needed at runtime, not compile time)
    runtimeOnly 'com.h2database:h2'

    // Test dependencies
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // Compile-only (e.g., Lombok — processed at compile time, not needed at runtime)
    compileOnly 'org.projectlombok:lombok:1.18.30'
    annotationProcessor 'org.projectlombok:lombok:1.18.30'

    // For java-library plugin: exposed to consumers of your library
    api 'org.apache.commons:commons-lang3:3.13.0'

    // String interpolation with ext variable
    implementation "com.example:some-lib:${someVersion}"
}
Note at bottom: "See next slide for a direct mapping of these configurations to Maven scopes."
SCRIPT:
"The dependencies block is where Gradle differs most visibly from Maven. The keyword before each dependency string isn't magic — it's the name of a configuration, which is Gradle's version of a scope.
implementation is the workhorse — it's what you use for most application dependencies. The dependency is available to compile your code and is included in your artifact at runtime. However, critically, it is NOT exposed to consumers of your library. If you're building a library, classes from an implementation dependency cannot be referenced in your public API — only in internal code.
api is for library projects only — it requires the java-library plugin. Use api for dependencies that appear in your public interface. If your method signature returns a type from a dependency, that dependency must be api, not implementation. This matters for consumers: api dependencies are part of your library's compile classpath, implementation dependencies are not.
compileOnly is Gradle's equivalent of Maven's provided scope. The dependency is needed to compile but won't be in your artifact. Lombok is a perfect example — it's an annotation processor that generates code at compile time. At runtime, the generated code is already there and you don't need Lombok anymore.
annotationProcessor is a special configuration for annotation processors. Lombok needs to be declared here as well as compileOnly. Other annotation processors like MapStruct follow the same pattern.
runtimeOnly is for things you don't compile against but need at runtime. Database drivers are the classic example.
Notice in the Spring Boot example there are no version numbers for the Spring Boot starter. That's because the Spring Dependency Management plugin applied the Spring Boot BOM, which manages all those versions. This is the same benefit as using spring-boot-starter-parent in Maven."

[ Slide 39: "Gradle Configurations vs Maven Scopes" ]
Content: A clean, focused comparison table — this is a reference slide.
Gradle Config        | Maven Scope | In Artifact? | Use For
---------------------|-------------|--------------|---------------------------
implementation       | compile     | YES (private)| Core app dependencies
api                  | compile     | YES (public) | Library public API deps
compileOnly          | provided    | NO           | Lombok, Servlet API
runtimeOnly          | runtime     | YES          | DB drivers (JDBC)
testImplementation   | test        | NO           | JUnit, Mockito, AssertJ
testCompileOnly      | test        | NO           | Test-only compile tools
testRuntimeOnly      | test        | NO           | Test-only runtime tools
annotationProcessor  | (no equiv.) | NO           | Lombok, MapStruct
Callout box: "Key difference from Maven: Gradle separates 'API surface' (api) from 'internal implementation' (implementation). Maven's compile scope does not make this distinction."
SCRIPT:
"Here's the direct mapping between Gradle configurations and Maven scopes. Keep this as a reference when you're switching between projects.
The most important thing to notice: implementation is compile scope but private, and api is compile scope but public. Maven's single compile scope doesn't give you this distinction — Gradle is more precise here, especially when you're building libraries that other teams will consume.
For most application code — as opposed to library code — you'll rarely need api. The majority of your dependencies will be implementation, with testImplementation for test dependencies and runtimeOnly for database drivers.
The annotationProcessor configuration has no direct Maven equivalent because Maven handles annotation processors differently. In Gradle, you always need to declare annotation processors explicitly in this configuration alongside their compileOnly declaration."


SECTION 11 — Gradle: Tasks & Build Lifecycle
[ Slide 40: "Gradle Tasks — The Core Concept" ]
Content: "In Gradle, a task is the fundamental unit of work. Everything Gradle does is a task." Diagram showing task dependency chain: compileJava → processResources → classes → jar. And: compileTestJava → processTestResources → testClasses → test → check → build. Show how to list all tasks: ./gradlew tasks. Show how to see the task graph: ./gradlew tasks --all.
SCRIPT:
"In Maven, you think in terms of lifecycle phases. In Gradle, you think in terms of tasks. This is a fundamental difference in the mental model.
A Gradle task is a unit of work with defined inputs and outputs. Tasks can depend on other tasks — when you run a task, Gradle automatically runs all tasks it depends on first.
The Java plugin gives you a rich task graph. To compile your code, Gradle runs compileJava. But before compileJava can run, processResources needs to run to copy your resource files into the output directory. When you ask Gradle to run the jar task, it first makes sure compileJava and processResources have run, creating the classes directory that jar needs.
This dependency graph is more explicit and more flexible than Maven's linear lifecycle. You can insert tasks anywhere in the graph, make any task depend on any other task, and have much finer control over what gets executed.
Run ./gradlew tasks to see all available tasks for your project. Run ./gradlew tasks --all to see even the hidden utility tasks. This is your best resource for discovering what your build can do."

[ Slide 41: "Built-In Task Reference" ]
Content: Reference table grouped into three visual sections with subtle shading — Build Lifecycle Tasks, Spring Boot Tasks, and Diagnostic Tasks:
── BUILD LIFECYCLE TASKS ───────────────────────────────────────
clean               | Deletes build/ output directory
compileJava         | Compiles main Java sources
compileTestJava     | Compiles test Java sources
processResources    | Copies main resources to build/resources
classes             | Assembles main class files
test                | Runs unit tests; HTML report → build/reports/tests  ★
check               | Runs all verification tasks (includes test)
jar                 | Creates the JAR file in build/libs/
assemble            | Assembles all archives (includes jar)
build               | Full build: assemble + check                        ★

── SPRING BOOT TASKS ───────────────────────────────────────────
bootJar             | Creates executable Spring Boot fat JAR
bootRun             | Runs the Spring Boot application                    ★
bootBuildImage      | Creates a Docker image

── DIAGNOSTIC TASKS ────────────────────────────────────────────
tasks               | Lists all available tasks
dependencies        | Prints full dependency tree                         ★
dependencyInsight   | Shows why a specific dep was chosen
javadoc             | Generates Javadoc HTML
★ marks the most commonly used tasks.
SCRIPT:
"Let me walk you through the tasks you'll use every single day.
In the Build Lifecycle group: clean deletes the build/ directory entirely. Note — Gradle's output is build/, not target/ like Maven. test compiles your test code and runs it; test reports are generated as HTML in build/reports/tests/. build is the main task — it compiles everything, runs tests, and creates your JAR. It's roughly equivalent to mvn verify.
In the Spring Boot group: bootRun compiles your code and launches your application in place. You don't need to create a JAR first — this is the fastest way to run your app during development.
In the Diagnostic group: dependencies prints the full dependency tree for every configuration. Run ./gradlew dependencies --configuration implementation to see just one configuration. And dependencyInsight --dependency <libraryName> explains WHY a specific library is in your graph — which transitive dependency brought it in and what version was selected. This is invaluable when you're debugging an unexpected library appearing in your project."

[ Slide 42: "Writing Custom Gradle Tasks" ]
Content: Annotated custom task examples:
groovy// Simple custom task
tasks.register('hello') {
    doLast {
        println 'Hello from custom task!'
        println "Building version: ${project.version}"
    }
}

// Task that depends on another task
tasks.register('buildReport') {
    dependsOn 'test'   // Only runs after test completes
    doLast {
        println "Tests passed. Build ${version} is ready."
    }
}

// Typed task — copies files
tasks.register('copyConfig', Copy) {
    from 'src/main/config'
    to "${buildDir}/config"
}

// Configure an existing task
tasks.named('test') {
    useJUnitPlatform()  // Use JUnit 5
    testLogging {
        events 'passed', 'failed', 'skipped'
    }
}
Note: "doFirst{} runs before the task's action. doLast{} runs after. You can add behavior to any existing task this way."
SCRIPT:
"One of Gradle's most powerful features is that you can write your own tasks and customize existing ones using the same scripting language as the rest of your build.
tasks.register() is how you create a new task. You give it a name and a configuration block. The doLast block contains the code that runs when the task executes.
The dependsOn method creates a dependency between tasks. In the example, the buildReport task won't run until the test task has completed successfully. This is how you compose the task graph.
For common operations, Gradle has typed tasks like Copy, Sync, Exec, JavaExec, and more. These come with sensible defaults and a rich DSL for configuration.
The tasks.named() method lets you configure an existing task. Configuring the test task to use useJUnitPlatform() is something you'll need in virtually every Gradle project — it tells the test runner to use JUnit 5. Without this, JUnit 5 tests won't run even if you have JUnit 5 on the classpath.
You can also use testLogging to control what appears in the console during test execution — by default Gradle only prints failures, but configuring events to include 'passed' and 'skipped' gives you a fuller picture."

[ Slide 43: "Gradle's Incremental Build System" ]
Content: Diagram showing: Task has defined INPUTS (source files, dependencies, config) and OUTPUTS (compiled classes, JAR file). If inputs AND outputs haven't changed → UP-TO-DATE → skip. If inputs changed → re-run. Text: "Gradle fingerprints inputs and outputs using checksums, not timestamps." Build cache concept: "With build cache enabled, Gradle can reuse outputs from a previous build on ANY machine — like CI server outputs." Graph showing build time with/without incremental builds.
SCRIPT:
"One of the biggest practical advantages of Gradle is its incremental build system, and understanding this will change how you think about builds.
Every Gradle task has defined inputs and outputs. The test task's inputs include your test source files, your main source files, and your test dependencies. Its outputs are the test results. Gradle computes a fingerprint — essentially a checksum — of all the inputs.
When you run a build, before executing each task, Gradle checks: have any inputs changed since the last time this task ran? Have the outputs been tampered with or deleted? If neither has changed, Gradle marks the task as UP-TO-DATE and skips it entirely. It doesn't recompile, it doesn't rerun tests, it doesn't repackage — it just says 'nothing changed, outputs are still valid.'
For large projects, this is transformative. A build that takes 10 minutes from scratch might take 30 seconds if only one file changed, because only the tasks affected by that file change need to run.
The build cache goes further. It stores task output fingerprints either locally or on a shared server. If you check out a branch that was built yesterday by your CI server, and nothing has changed, Gradle can pull the cached outputs from the CI server instead of rebuilding locally. You effectively get the benefit of someone else's already-completed build."


SECTION 12 — Gradle: Dependency Management
[ Slide 44: "Gradle Dependency Management — Configurations" ]
Content: Explanation of configurations as named dependency buckets. Diagram showing how configurations compose:
testImplementation extends implementation
testRuntimeOnly extends runtimeOnly

When you run tests, the test classpath includes:
  + implementation deps
  + runtimeOnly deps
  + testImplementation deps
  + testRuntimeOnly deps
  + Your compiled classes
Note: "Configurations are Gradle's way of grouping dependencies by purpose. Plugins add new configurations. You can create custom configurations."
SCRIPT:
"In Gradle, the dependency configurations we've talked about — implementation, testImplementation, and so on — are not magic keywords. They're named containers called configurations that plugins create.
When the Java plugin is applied, it creates configurations like implementation, runtimeOnly, testImplementation, and more. When you add a dependency to the implementation configuration, you're putting it in that bucket. When Gradle needs to compile your code, it uses the contents of the implementation configuration as the compile classpath.
Configurations can extend each other. The testImplementation configuration extends implementation — meaning everything you put in implementation is also automatically available in testImplementation. So your test code can use everything your main code can, plus any additional testing libraries you add to testImplementation.
This composable configuration system is more explicit than Maven's scope system and allows much more fine-grained control, especially in complex multi-module projects."

[ Slide 45: "Handling Version Conflicts in Gradle" ]
Content: Gradle's default conflict resolution strategy and the force/failOnVersionConflict/exclusions tools:
groovy// Gradle's default: newest wins (opposite of Maven's nearest wins)

// Force a specific version regardless of conflict
configurations.all {
    resolutionStrategy {
        force 'com.google.guava:guava:32.1.2-jre'
    }
}

// Fail the build on any version conflict (strict mode)
configurations.all {
    resolutionStrategy.failOnVersionConflict()
}

// Exclude a transitive dependency
dependencies {
    implementation('org.apache.struts:struts2-core:2.5.30') {
        exclude group: 'commons-logging', module: 'commons-logging'
    }
}
SCRIPT:
"Gradle's default conflict resolution is the opposite of Maven's: when there's a version conflict, Gradle picks the newest version rather than the nearest. In practice, this tends to work better — newer versions usually contain bug fixes and the library is generally backward compatible.
When you need to force a specific version regardless of what transitive resolution would choose, use resolutionStrategy.force() inside a configurations.all block. This is the hammer — use it when you know exactly what version you need and don't want Gradle second-guessing you.
failOnVersionConflict() is excellent for strict builds where you want total control over every dependency. Instead of silently picking a winner, Gradle fails the build and tells you about the conflict. You then resolve it explicitly. This is a great practice for libraries because you want to be very deliberate about what goes into your artifact.
Exclusions work similarly to Maven — you can exclude a specific transitive dependency from a particular library's dependency graph."

[ Slide 46: "Gradle BOM Import — platform()" ]
Content: Focused code block showing the platform() BOM import pattern:
groovydependencies {
    // Import the Spring Boot BOM
    implementation platform('org.springframework.boot:spring-boot-dependencies:3.2.0')

    // Now declare Spring Boot deps without versions —
    // all aligned to the BOM's tested versions
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
Callout box: "A BOM (Bill of Materials) is a special POM that contains only dependencyManagement entries — a curated list of compatible dependency versions. Importing it with platform() achieves the same result as inheriting spring-boot-starter-parent in Maven."
SCRIPT:
"The platform() notation is Gradle's BOM import. A BOM — Bill of Materials — is a special POM file that contains only <dependencyManagement> entries. It's a curated list of compatible dependency versions, nothing more.
Spring Boot publishes one of the most widely used BOMs. By importing it with platform(), you can declare all Spring Boot dependencies without version numbers, and they'll all align to the BOM's carefully tested, compatible versions. This is how Gradle achieves the same result as inheriting from spring-boot-starter-parent in Maven.
This is the key connection between what you learned about dependencyManagement in Maven and how Gradle handles the same problem. The mechanism is different, but the goal is identical: one place to control all your library versions, guaranteed to work together."


SECTION 13 — Gradle Demo: Create & Build a Project
[ Slide 47: "Demo — Creating a Gradle Project" ]

⚠️ INSTRUCTOR NOTE — LIVE DEMO: You will need a terminal and IDE open. All commands are scripted below — no additional browser windows are needed beyond your IDE and terminal.

Content: Step by step commands and expected outputs.
bash# Create project directory
mkdir my-gradle-project && cd my-gradle-project

# Initialize with Gradle (interactive — select when prompted:)
# Type: application > Java > Groovy DSL > JUnit Jupiter
gradle init

# Project structure created:
# ├── gradlew
# ├── gradlew.bat
# ├── settings.gradle
# ├── build.gradle
# └── src/main/java/... and src/test/java/...

# Key commands to run through:
./gradlew tasks              # See all available tasks
./gradlew build              # Full build — watch EXECUTED vs UP-TO-DATE
./gradlew build              # Run again — see incremental: nearly all UP-TO-DATE
./gradlew test               # Run tests; HTML report in build/reports/tests/
./gradlew clean build        # Clean then full rebuild
./gradlew dependencies       # View dependency tree
./gradlew build --scan       # Build scan — generates shareable report URL
Annotated expected output for each command — what UP-TO-DATE looks like, what test output looks like, where the JAR appears in build/libs/.
SCRIPT:
"Now let's create a Gradle project and run through the same workflow we did with Maven.
gradle init is the interactive project initializer. It'll ask you a series of questions — choose application, Java, Groovy DSL, JUnit Jupiter.
[Run gradle init in the terminal]
Look at the project structure in the IDE — it's very similar to Maven. The src directory has the same layout. The main differences are the build.gradle instead of pom.xml, the settings.gradle, and the gradlew wrapper scripts.
[Open build.gradle in the IDE]
Notice how much more concise this is than the equivalent pom.xml. The plugins block applies the 'java' and 'application' plugins — that 'application' plugin gives us the ability to run the app and creates runnable distributions.
[Walk through each section of build.gradle]
Now ./gradlew build. Watch the output — each task name is printed with a status. EXECUTED means it ran, UP-TO-DATE means nothing changed so it was skipped.
[Run the build]
Now run ./gradlew build a second time immediately — nearly everything is UP-TO-DATE. That's the incremental build system in action. No source changed, no rebuilding needed.
[Run again immediately to show UP-TO-DATE]
./gradlew build --scan generates a detailed build report and publishes it to the Gradle build scan service. You get a URL to open in the browser — a full breakdown of every task, every dependency, and performance details. This is an invaluable debugging tool for complex build problems."


SECTION 14 — Maven vs Gradle Comparison
[ Slide 48: "Maven vs Gradle — Full Comparison" ]
Content: Detailed comparison table:
Category          | Maven              | Gradle
------------------|--------------------|---------------------------------
Config Language   | XML                | Groovy or Kotlin DSL
Config File       | pom.xml            | build.gradle / build.gradle.kts
Project Name      | <artifactId>       | settings.gradle rootProject.name
Version Pinning   | <dependencyMgmt>   | platform() BOM import
Conflict Strategy | Nearest wins       | Newest wins
Output Directory  | target/            | build/
Performance       | Moderate           | Fast (incremental + cache)
Flexibility       | Lower (opinionated)| Higher (programmable)
Learning Curve    | Lower              | Moderate
Plugin Ecosystem  | Very mature        | Mature, growing
Android           | Not supported      | Mandatory
IDE Support       | Excellent          | Excellent
Multi-Module      | Good               | Excellent
Below the table, three scenarios with recommendations: "Choose Maven if...", "Choose Gradle if...", "You'll encounter both, so know both."
SCRIPT:
"Let's put both tools side by side now that you have hands-on experience with both.
The fundamental difference is the build script language. XML is declarative and explicit — there's no ambiguity about what a pom.xml element does, but it can't express complex logic. Groovy/Kotlin DSL gives you a programming language, which is more powerful but requires more discipline.
Performance is where Gradle has a clear, objective advantage. Incremental builds and the build cache make Gradle significantly faster for large projects. For a small project with one module and 20 classes, you won't notice the difference. For a 50-module enterprise project with thousands of classes and a slow test suite, the difference can be enormous — the difference between a 15-minute build and a 3-minute build.
Maven's opinionated nature is actually an advantage in many team settings. There are fewer arguments about how to structure the build because there's essentially one correct way. Gradle's flexibility can lead to highly customized build scripts that are hard to understand and maintain if the team isn't disciplined.
Plugin ecosystems are mature for both. There are some areas where Maven has more options, and some where Gradle does, but for most common use cases both have excellent plugins.
My recommendation: if you're starting a new project and performance matters, choose Gradle. If you're joining a team with an existing Maven project, use Maven. In your career, you will work with both. The good news is that the concepts — dependency management, lifecycles/tasks, plugins — are the same. The vocabulary just changes."

[ Slide 49: "Equivalent Commands — Quick Reference" ]
Content: Side-by-side command reference:
Goal                     | Maven                    | Gradle
-------------------------|--------------------------|---------------------------
Clean output             | mvn clean                | ./gradlew clean
Compile                  | mvn compile              | ./gradlew compileJava
Run tests                | mvn test                 | ./gradlew test
Package                  | mvn package              | ./gradlew jar
Full build               | mvn verify               | ./gradlew build
Install to local repo    | mvn install              | ./gradlew publishToMavenLocal
Skip tests               | mvn package -DskipTests  | ./gradlew build -x test
View dependencies        | mvn dependency:tree      | ./gradlew dependencies
Run Spring Boot app      | mvn spring-boot:run      | ./gradlew bootRun
Create executable JAR    | mvn package              | ./gradlew bootJar
Clean + full build       | mvn clean install        | ./gradlew clean build
SCRIPT:
"Print this slide or save it — it's your day-to-day cheat sheet.
The equivalent to mvn clean install in Gradle is ./gradlew clean build. The equivalent to mvn dependency:tree is ./gradlew dependencies. The equivalent to mvn spring-boot:run is ./gradlew bootRun.
One Gradle command that has no direct Maven equivalent is -x for excluding a task: ./gradlew build -x test runs the full build but skips the test task. In Maven you'd use -DskipTests. The -x approach is more powerful because you can exclude any task, not just tests.
And remember: any Maven command can also be run as ./mvnw instead of mvn when working on a project that includes the Maven Wrapper — which every Spring Boot project from Spring Initializr does."


SECTION 15 — Coding Standards & Best Practices
[ Slide 50: "Why Coding Standards Matter" ]
Content: Two code blocks — same logic, one written without standards (cryptic variable names, no whitespace, undocumented), one written following standards (clear names, proper spacing, brief comment). Header: "Which would you rather debug at 2am?" Three pillars graphic with icons: Readability (book icon), Maintainability (wrench icon), Consistency (puzzle pieces icon). "Code is read far more often than it is written."
SCRIPT:
"Before we wrap up, I want to cover coding standards — and I want you to take this section seriously, because these habits are what will distinguish you as a professional developer.
Here's a truth that surprises many new developers: the hardest part of software development isn't writing code. It's reading and understanding code that already exists. You'll spend far more of your career reading other people's code — or your own code from six months ago when you've forgotten what you were thinking — than writing new code from scratch.
Code that follows consistent, clear conventions is dramatically easier to understand, debug, and change. Code without standards becomes what we call 'legacy code' — a codebase that people are afraid to touch because they can't understand it.
These standards aren't arbitrary rules. Each one exists because it makes code easier to work with. Let me walk through the key ones you need to internalize."

[ Slide 51: "Java Naming Conventions — Classes & Interfaces" ]
Content: Rules + examples + anti-examples table:
Type            | Convention  | Good Examples           | BAD Examples
----------------|-------------|-------------------------|-----------------
Class           | PascalCase  | OrderService            | orderservice
                |             | HttpRequestParser        | HTTPrequestparser
                |             | CustomerRepository       | Cust_Repo
Interface       | PascalCase  | Serializable            | ISerializable
                |             | UserValidator           | uservalidator
Abstract class  | PascalCase  | AbstractBaseEntity      | abstractbaseentity
Enum            | PascalCase  | OrderStatus             | ORDER_STATUS
Annotation      | PascalCase  | NotNull                 | not_null
Exception       | PascalCase  | PaymentFailedException  | PaymentFailed
  (must end in "Exception")
Rules summary: "Classes = nouns. Interfaces = nouns or adjectives. No abbreviations unless universal (Http, Url, Id are acceptable)."
SCRIPT:
"Classes and interfaces use PascalCase — also called UpperCamelCase. Every word in the name starts with a capital letter, with no separators of any kind. No underscores, no hyphens, no spaces.
Class names should be nouns or noun phrases because a class represents a thing. OrderService. HttpRequestParser. CustomerRepository. These names tell you exactly what the class is responsible for.
Interface names are often adjectives — Serializable, Runnable, Iterable — or nouns like List or Map.
Exception class names must end in Exception. Always. PaymentFailedException, IllegalArgumentException, UserNotFoundException. When someone catches an exception, the name should immediately tell them what went wrong.
A word on abbreviations: avoid them unless they're universally understood in the domain. Http, Url, Id, and Dto are acceptable abbreviations in Java. But don't invent your own. CustRepo is not clearer than CustomerRepository — it's just harder to read and harder to search for."

[ Slide 52: "Java Naming Conventions — Methods & Variables" ]
Content: Rules + examples + anti-examples table:
Type           | Convention    | Good Examples              | BAD Examples
---------------|---------------|----------------------------|-----------------
Method         | camelCase     | getUserById(long id)       | GetUserById()
(verb phrase)  |               | calculateTotalPrice()      | total()
               |               | isOrderValid()             | validorder()
               |               | hasPermission()            | permission()
Variable       | camelCase     | customerFirstName          | cfn
               |               | orderItems                 | lst
               |               | isActive                   | x
               |               | maxRetryAttempts           | MAX
Boolean        | is/has/can    | isVisible, hasChildren     | visible, children
               |               | canEdit, shouldRefresh     |
Parameter      | camelCase     | userId, orderDate          | id2, temp
Loop variable  | i, j, k       | for (int i = 0; ...)       | for (int x = 0;..)
SCRIPT:
"Methods and local variables use camelCase — first word lowercase, subsequent words start with capital.
Method names must be verb phrases — they describe actions. getUserById is correct. user is not — that describes a thing, not an action. getUser, findUser, fetchUser are all fine. The verb tells the reader what the method does.
Boolean methods and variables should use is, has, can, or should prefixes: isValid, hasPermission, canDelete. This makes conditions read like natural language: if (user.isActive()) reads better than if (user.active()).
Variable names must be descriptive. The single biggest source of unreadable code I see from beginners is meaningless variable names: x, temp, data, val, str. These tell the reader nothing. customerEmailAddress tells the reader everything.
The only exception: single-letter variables for loop counters — i, j, k — are universally understood and accepted because their scope is tiny. The moment you're doing anything more complex than a simple index loop, use a descriptive name."

[ Slide 53: "Java Naming Conventions — Constants, Packages, and Files" ]
Content: Continuation table:
Type         | Convention         | Good Examples              | BAD Examples
-------------|--------------------|-----------------------------|----------------
Constant     | UPPER_SNAKE_CASE   | MAX_CONNECTION_POOL_SIZE   | maxConnectionPoolSize
(static      |                    | DEFAULT_TIMEOUT_MS         | DefaultTimeoutMs
 final)      |                    | BASE_API_URL               | baseApiUrl
Package      | all lowercase      | com.acme.orderservice      | com.Acme.OrderService
             | dot-separated      | com.acme.orderservice.dto  | com.acme.orderService
             | no underscores     |                            | com.acme.order_service
File (.java) | Matches class name | OrderService.java          | orderservice.java
             | one public class   |                            |
             | per file           |                            |
Important note: "In Java, each public class must be in its own file with the exact same name. OrderService.java must contain public class OrderService."
SCRIPT:
"Constants — any variable declared as static final — use UPPER_SNAKE_CASE. All caps, words separated by underscores. MAX_RETRY_COUNT, DEFAULT_TIMEOUT_MS, BASE_API_URL. This convention makes constants instantly recognizable in code — when you see all-caps in Java, you know you're looking at a constant.
Package names are all lowercase, words separated by dots. Never uppercase in package names, never underscores. Follow the reversed domain convention: com.acme.ecommerce.orderservice. Sub-packages describe the layer or feature: .controller, .service, .repository, .dto.
Java file names must exactly match the public class they contain. If your class is OrderService, the file is OrderService.java. This isn't just convention — the Java compiler requires it."

[ Slide 54: "Package Structure Best Practices" ]
Content: Two full directory trees side by side. Left: Package by Layer:
com.acme.shop/
├── controller/
│   ├── OrderController.java
│   ├── UserController.java
│   └── ProductController.java
├── service/
│   ├── OrderService.java
│   ├── UserService.java
│   └── ProductService.java
├── repository/
│   ├── OrderRepository.java
│   └── UserRepository.java
└── model/
    ├── Order.java
    └── User.java
Right: Package by Feature:
com.acme.shop/
├── order/
│   ├── OrderController.java
│   ├── OrderService.java
│   ├── OrderRepository.java
│   └── Order.java
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   └── User.java
└── product/
    ├── ProductController.java
    └── ProductService.java
Pros/cons listed under each. Bottom note: "Either is valid — consistency is what matters most."
SCRIPT:
"How you organize your packages matters more and more as your project grows. There are two dominant approaches.
Package by layer groups all controllers together, all services together, all repositories together. This is intuitive for new developers because it mirrors the architectural layers. The downside is that everything related to 'orders' is spread across four different packages. If you need to understand the Order feature completely, you're jumping between packages constantly.
Package by feature groups everything related to a specific domain feature together. All Order-related classes — the controller, service, repository, and model — live in the com.acme.shop.order package. This makes it much easier to understand, modify, and even delete a feature. The downside is that it takes more discipline to implement correctly, especially knowing which classes should be package-private versus public.
In practice, smaller projects often start with package by layer because it's simpler to get right. Larger projects often migrate toward package by feature because it scales better. Some teams use a hybrid: the top level is by feature, and within each feature package there might be sub-packages by layer.
What matters most: pick one approach and apply it consistently throughout the entire codebase. The worst thing is a mix of both, which creates a confusing maze."

[ Slide 55: "Code Comments & Javadoc" ]
Content: Three sections. Section 1 — The Golden Rule: "Comment the WHY, not the WHAT." Good vs bad comment examples:
java// BAD: Restates what the code says — useless
count++;  // increment count

// GOOD: Explains a non-obvious decision
// Retry limit is 3 because vendor SLA guarantees
// response within 3 attempts under normal load
int maxRetries = 3;
Section 2 — Javadoc format:
java/**
 * Calculates the discounted price for a customer based on
 * their membership tier and the base price of the item.
 *
 * @param basePrice  the original item price; must be positive
 * @param tier       the customer's membership tier; must not be null
 * @return           the final price after discount, never negative
 * @throws IllegalArgumentException if basePrice is negative
 */
public BigDecimal calculateDiscountedPrice(BigDecimal basePrice,
                                           MembershipTier tier) {
Section 3 — Rules: What needs Javadoc (public classes, public methods), what doesn't (private methods unless complex), the lying comment warning.
SCRIPT:
"Let's talk about comments, because there's a lot of misguided advice out there — both 'comment everything' and 'never comment, just write clean code.'
The truth is in the middle: comment the WHY, not the WHAT.
If your code is well-written with good names, the code itself tells you WHAT it's doing. You don't need a comment saying // increment counter above count++ — that's noise that clutters the code and adds maintenance burden.
What your code can't explain is why. Why is this limit set to 3? Why do we reverse the list before processing? Why is there a Thread.sleep here? Why are we using this algorithm instead of the obvious one? These are the questions that comments should answer.
For Javadoc — the /** */ block comments — every public class and every public method in a production codebase should have one. This is not optional in professional environments. Javadoc is what populates the IDE tooltips when your teammates use your code, and what generates the HTML API documentation.
A Javadoc comment should explain what the method does (not how), what the parameters mean with @param, what it returns with @return, and what exceptions it can throw with @throws.
The most dangerous thing you can write is a lying comment — a comment that says the code does something it no longer does. This happens when code gets changed but the comment isn't updated. A lying comment is actively harmful because it makes the reader trust the wrong thing. The lesson: if you change code, update the comment."

[ Slide 56: "Additional Best Practices — Code Organization" ]
Content: List of rules with brief examples:
✓ One public class per file (enforced by Java)
✓ Class length: aim for < 300 lines (break up large classes)
✓ Method length: aim for < 20 lines (extract helper methods)
✓ Max parameters: aim for ≤ 4 (use a config/request object for more)
✓ No magic numbers — use named constants
✓ No commented-out code — use version control instead
✓ Consistent brace style within a project
✓ No wildcard imports (import com.acme.* is forbidden)
Show a magic number example:
java// BAD — what does 86400 mean?
if (elapsed > 86400) { ... }

// GOOD — self-documenting
static final int SECONDS_IN_A_DAY = 86400;
if (elapsed > SECONDS_IN_A_DAY) { ... }
SCRIPT:
"A few more quick rules that will make your code dramatically more professional.
Keep your methods short. If a method is more than about 20 lines, it's probably doing too many things. Break it into smaller, well-named helper methods. A method should do one thing and do it well. This is the Single Responsibility Principle applied at the method level.
Same goes for classes. A 1000-line class is almost certainly violating the Single Responsibility Principle. It's probably doing multiple things that should be split into separate classes.
No magic numbers. The number 86400 in the middle of your code tells the reader nothing. The constant SECONDS_IN_A_DAY tells the reader everything. Any numeric literal other than 0 and 1 should be a named constant.
No commented-out code. I see this constantly and it's a code smell. If code is commented out, it means you're not sure you want it gone but you also don't want it running. That's what version control is for. Delete the code. If you need it back, check out the old version. Commented-out code clutters your codebase and confuses future readers who wonder if it was intentional.
No wildcard imports — import com.acme.* is forbidden by most style guides. Explicit imports make it clear exactly which classes a file uses and prevent name collisions."


SECTION 16 — Wrap-Up
[ Slide 57: "Key Takeaways" ]
Content: Three columns. Left: "Maven" — five bullet summary. Center: "Gradle" — five bullet summary. Right: "Code Quality" — three bullets. Bottom strip: "Shared Concepts" — two bullets applying to both build tools.
SCRIPT:
"Let's bring this all home with the key takeaways.
For Maven: Everything is in pom.xml. The directory structure is sacred — follow it. The lifecycle is sequential — running a phase runs everything before it. mvn clean install is your go-to command. When using Spring Boot, let spring-boot-starter-parent manage your versions. And use the Maven Wrapper — ./mvnw — for portability.
For Gradle: The build script is code. Always use the wrapper — ./gradlew, not gradle. Incremental builds mean the second build is much faster than the first. ./gradlew build is your go-to command. Configure the test task with useJUnitPlatform() for JUnit 5.
For code quality: Follow naming conventions — not because someone said so, but because it makes your code usable by others. Structure your packages deliberately and consistently. Comment the why, not the what.
For both build tools: they both use Maven Central, they both have the same core concepts — dependency management, lifecycle, plugins. Learning one makes learning the other straightforward. You will use both in your career."

[ Slide 58: "Command Quick Reference Card" ]
Content: A single-page printable reference combining the most important commands from both tools.
═══════════════════════════════════════════════════════
    MAVEN & GRADLE COMMAND REFERENCE
═══════════════════════════════════════════════════════

MAVEN COMMANDS
──────────────
mvn -v                       Verify Maven installation
mvn clean                    Delete target/ directory
mvn compile                  Compile main source code
mvn test                     Compile and run tests
mvn package                  Compile, test, and create JAR/WAR
mvn verify                   Compile, test, and run integration checks
mvn install                  Build and install to local ~/.m2 repo
mvn deploy                   Build and push to remote repository
mvn clean install            Recommended full build command
mvn clean install -DskipTests  Full build, skip test execution
mvn dependency:tree          View full dependency graph
mvn dependency:analyze       Find unused/undeclared dependencies
mvn spring-boot:run          Run Spring Boot application
mvn -X [command]             Debug mode — verbose output
mvn [command] -P profile     Activate a Maven profile
(replace mvn with ./mvnw on projects with the Maven Wrapper)

GRADLE COMMANDS
───────────────
./gradlew -v                 Verify Gradle wrapper version
./gradlew clean              Delete build/ directory
./gradlew compileJava        Compile main source code
./gradlew test               Compile and run tests
./gradlew jar                Create JAR file
./gradlew build              Full build (compile + test + jar)
./gradlew clean build        Recommended full build command
./gradlew build -x test      Full build, skip test task
./gradlew dependencies       View full dependency graph
./gradlew bootRun            Run Spring Boot application
./gradlew bootJar            Create executable Spring Boot JAR
./gradlew tasks              List all available tasks
./gradlew tasks --all        List all tasks including internal
./gradlew build --scan       Build with detailed online report
./gradlew [task] --info      More detailed output
./gradlew [task] --debug     Maximum verbose output

IMPORTANT DIRECTORIES & FILES
──────────────────────────────
Maven output:      target/         (add to .gitignore)
Gradle output:     build/          (add to .gitignore)
Local Maven repo:  ~/.m2/repository
Maven Central:     https://search.maven.org
.gitignore must include: target/, build/, .idea/, *.iml
SCRIPT:
"This last slide is your reference card. Bookmark it, save a screenshot, print it — whatever works for you. These are the commands you'll type every day. You don't need to memorize all of them right now, but within a few weeks of actively using these tools, most of these will be muscle memory.
In our next sessions, we're going to start using Maven and Gradle for real Spring Core projects. Every project we build will use one of these tools, so the practice starts immediately. Don't be afraid to experiment — break things, fix them, look at error messages, run dependency:tree and ./gradlew dependencies just to see what they show. The best way to get comfortable with build tools is to use them constantly.
Any questions before we close?"