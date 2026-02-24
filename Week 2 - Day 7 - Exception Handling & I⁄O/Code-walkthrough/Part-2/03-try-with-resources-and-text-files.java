/**
 * Day 7 - Part 2: try-with-resources & Reading/Writing Text Files
 *
 * THE PROBLEM WITH MANUAL FINALLY
 * ────────────────────────────────
 * In the previous two files we used:
 *
 *   SomeStream stream = null;
 *   try {
 *       stream = new SomeStream(...);
 *       // use stream
 *   } finally {
 *       if (stream != null) stream.close();
 *   }
 *
 * Problems with this pattern:
 *   1. Verbose — 6+ lines of boilerplate just to close one resource
 *   2. If close() throws an exception, it can suppress the ORIGINAL exception
 *   3. With multiple resources, nested try-finally becomes deeply indented and error-prone
 *
 * THE SOLUTION: try-with-resources (Java 7+)
 * ────────────────────────────────────────────
 *   try (SomeStream stream = new SomeStream(...)) {
 *       // use stream
 *   }                                     ← stream.close() called automatically here
 *
 * The JVM guarantees close() is called in ALL cases:
 *   - Normal exit from the try block
 *   - Exception thrown inside try
 *   - Exception thrown by close() itself (handled gracefully via suppressed exceptions)
 *
 * REQUIREMENT: The resource must implement java.lang.AutoCloseable (just one method: close())
 *   FileReader, FileWriter, BufferedReader, BufferedWriter, InputStream, OutputStream,
 *   Connection, ResultSet, PreparedStatement — all implement AutoCloseable.
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class TryWithResourcesAndTextFiles {

    static final String DEMO_DIR = System.getProperty("java.io.tmpdir")
            + File.separator + "day7-io-demo";

    // =========================================================================
    // SECTION 1 — AutoCloseable Interface
    // =========================================================================

    /**
     * Any class that implements AutoCloseable can be used in try-with-resources.
     * This is the ONLY requirement.
     *
     * Here's a custom 'DatabaseConnection' class that simulates a connection
     * which must be closed after use — the classic resource-management scenario.
     */
    static class DatabaseConnection implements AutoCloseable {
        private final String url;
        private boolean open;

        public DatabaseConnection(String url) {
            this.url = url;
            this.open = true;
            System.out.println("  [DB] Connected to: " + url);
        }

        public String query(String sql) {
            if (!open) throw new IllegalStateException("Connection already closed!");
            return "[DB] Result of: " + sql;
        }

        @Override
        public void close() {
            open = false;
            System.out.println("  [DB] Connection closed automatically.");
        }

        public boolean isOpen() { return open; }
    }

    static void demonstrateAutoCloseable() {
        System.out.println("=== AutoCloseable — Custom Resource Class ===");

        // try-with-resources: conn.close() is called automatically
        try (DatabaseConnection conn = new DatabaseConnection("jdbc:mysql://localhost:3306/mydb")) {
            String result = conn.query("SELECT * FROM users");
            System.out.println("  " + result);
            System.out.println("  Connection open during try: " + conn.isOpen());
        } // <-- conn.close() is called HERE, automatically, even if an exception occurred

        System.out.println("  ✓ Resource was closed automatically by try-with-resources.");
        System.out.println();

        // ── Contrast with manual finally (for comparison) ──
        System.out.println("--- Manual finally equivalent (for comparison) ---");
        DatabaseConnection conn = null;
        try {
            conn = new DatabaseConnection("jdbc:mysql://localhost:3306/mydb");
            System.out.println("  " + conn.query("SELECT * FROM products"));
        } finally {
            if (conn != null) conn.close(); // must remember to do this manually
        }
        System.out.println("  ← Much more verbose. try-with-resources is the modern way.");
        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — try-with-resources: Single Resource (FileReader)
    // =========================================================================

    static void demonstrateSingleResource() throws IOException {
        System.out.println("=== try-with-resources — Single Resource ===");

        // First write a test file
        File testFile = new File(DEMO_DIR, "greeting.txt");
        try (FileWriter fw = new FileWriter(testFile)) {
            fw.write("Hello, World!\nWelcome to Java File I/O.\nHave a great day!\n");
        }

        System.out.println("  Reading greeting.txt with try-with-resources:");

        // Read it back — the FileReader is automatically closed
        try (FileReader fr = new FileReader(testFile)) {
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = fr.read()) != -1) {
                sb.append((char) ch);
            }
            System.out.print(sb);
        }
        // fr.close() was called here — we don't have to write it

        System.out.println("\n  ✓ FileReader was closed automatically.");
        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — try-with-resources: Multiple Resources
    // =========================================================================

    /**
     * Multiple resources in one try-with-resources statement.
     * Syntax: try (Resource1 r1 = ...; Resource2 r2 = ...)
     *
     * CLOSING ORDER: Resources are closed in REVERSE order of declaration.
     * r2 is closed first, then r1. This mirrors the stack-based LIFO principle.
     */
    static void demonstrateMultipleResources() throws IOException {
        System.out.println("=== try-with-resources — Multiple Resources ===");

        File sourceFile = new File(DEMO_DIR, "employees.csv");
        File upperFile  = new File(DEMO_DIR, "employees-upper.csv");

        if (!sourceFile.exists()) {
            System.out.println("  employees.csv not found — run BufferedStreams demo first.");
            return;
        }

        System.out.println("  Transforming employees.csv → all uppercase...");

        // Both reader AND writer declared in the same try — both closed automatically
        // Closing order: bw (declared second) closed first, then br (declared first)
        try (
            BufferedReader br = new BufferedReader(new FileReader(sourceFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(upperFile))
        ) {
            String line;
            int lines = 0;
            while ((line = br.readLine()) != null) {
                bw.write(line.toUpperCase());
                bw.newLine();
                lines++;
            }
            System.out.println("  Transformed " + lines + " lines.");
        }
        // br.close() and bw.close() — both called automatically, in reverse order

        // Verify by reading back
        System.out.println("  Verifying transformed file (first 3 lines):");
        try (BufferedReader br = new BufferedReader(new FileReader(upperFile))) {
            br.lines().limit(3).forEach(l -> System.out.println("    " + l));
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — Suppressed Exceptions
    // =========================================================================

    /**
     * ⚠️ ADVANCED TOPIC: What if both the try block AND close() throw exceptions?
     *
     * With manual try-finally: the finally exception OVERWRITES the try exception.
     *   The original exception is silently lost. Very bad.
     *
     * With try-with-resources: the close() exception is SUPPRESSED and attached to
     *   the original exception. You can access it via e.getSuppressed().
     *   The original exception is preserved. 
     */
    static class FlickyResource implements AutoCloseable {
        private final boolean throwOnClose;

        FlickyResource(boolean throwOnClose) {
            System.out.println("  [FlickyResource] Opened. throwOnClose=" + throwOnClose);
        }

        public void use() throws Exception {
            throw new Exception("Exception from use()");
        }

        @Override
        public void close() throws Exception {
            if (throwOnClose) {
                throw new Exception("Exception from close()");
            }
            System.out.println("  [FlickyResource] Closed cleanly.");
        }
    }

    static void demonstrateSuppressedExceptions() {
        System.out.println("=== Suppressed Exceptions — try-with-resources advantage ===");

        // Case: both use() and close() throw
        try (FlickyResource r = new FlickyResource(true)) {
            r.use(); // throws "Exception from use()"
        } catch (Exception e) {
            System.out.println("  Primary exception: " + e.getMessage());
            System.out.println("  Suppressed exceptions: " + e.getSuppressed().length);
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("    Suppressed: " + suppressed.getMessage());
            }
            System.out.println("  ✓ Original exception preserved! close() exception is attached.");
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 5 — Complete Text File Pattern: Write, then Read
    // =========================================================================

    /**
     * A practical end-to-end example:
     * Write a structured config file, then read it back and parse it.
     * This is the most common text file pattern in real applications.
     */
    static void demonstrateWriteAndReadTextFile() throws IOException {
        System.out.println("=== Complete Pattern: Write then Read a Text File ===");

        File configFile = new File(DEMO_DIR, "app.properties");

        // ── WRITE ──────────────────────────────────────────────────────────────
        System.out.println("  Writing app.properties...");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(configFile))) {
            bw.write("# Application Configuration");
            bw.newLine();
            bw.write("# Generated: " + java.time.LocalDateTime.now());
            bw.newLine();
            bw.newLine();
            bw.write("app.name=MyJavaApp");
            bw.newLine();
            bw.write("app.version=2.1.0");
            bw.newLine();
            bw.write("app.port=8080");
            bw.newLine();
            bw.write("db.host=localhost");
            bw.newLine();
            bw.write("db.port=5432");
            bw.newLine();
            bw.write("db.name=production_db");
            bw.newLine();
            bw.write("cache.enabled=true");
            bw.newLine();
            bw.write("cache.ttl.seconds=300");
            bw.newLine();
        }

        System.out.println("  Wrote " + configFile.length() + " bytes.");

        // ── READ & PARSE ────────────────────────────────────────────────────────
        System.out.println("\n  Reading and parsing app.properties:");

        Map<String, String> config = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip blank lines and comment lines
                if (line.isEmpty() || line.startsWith("#")) continue;
                // Parse key=value
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key   = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    config.put(key, value);
                }
            }
        }

        System.out.println("  Parsed " + config.size() + " properties:");
        config.forEach((k, v) -> System.out.printf("    %-25s = %s%n", k, v));

        // Use the config values
        System.out.println("\n  Application starting on port: " + config.get("app.port"));
        System.out.println("  Database: " + config.get("db.host") + ":" + config.get("db.port")
                + "/" + config.get("db.name"));
        System.out.println("  Cache: " + (Boolean.parseBoolean(config.get("cache.enabled"))
                ? "ENABLED (TTL=" + config.get("cache.ttl.seconds") + "s)"
                : "DISABLED"));

        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Appending to an existing file
    // =========================================================================

    static void demonstrateAppendToFile() throws IOException {
        System.out.println("=== Appending to an Existing File (Log File Pattern) ===");

        File logFile = new File(DEMO_DIR, "application.log");

        // Each call to this method appends a new log entry
        String[] logMessages = {
            "INFO  - Application started successfully",
            "INFO  - Loading configuration from app.properties",
            "WARN  - Cache size exceeds recommended limit",
            "INFO  - Processed 1,247 requests in the last minute",
            "ERROR - Connection timeout to external payment API (retrying...)",
        };

        for (String message : logMessages) {
            appendLogEntry(logFile, message);
        }

        System.out.println("  Log file contents:");
        System.out.println("  ─────────────────────────────────────────");
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            br.lines().forEach(l -> System.out.println("  " + l));
        }
        System.out.println("  ─────────────────────────────────────────");

        System.out.println();
    }

    /**
     * Appends a single timestamped log entry to the file.
     * Uses try-with-resources + append mode (true).
     */
    static void appendLogEntry(File logFile, String message) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            String timestamp = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        }
    }

    // =========================================================================
    // SECTION 7 — java.nio.file.Files: Modern Convenience Methods (Java 7+)
    // =========================================================================

    /**
     * java.nio.file.Files provides one-liners for common file operations.
     * These use try-with-resources internally — you don't need to manage streams.
     * Perfect for simple read/write operations where you don't need fine control.
     */
    static void demonstrateNioFilesHelpers() throws IOException {
        System.out.println("=== java.nio.file.Files — Modern One-Liners ===");

        Path demoPath = Path.of(DEMO_DIR);

        // Write all lines at once (UTF-8 by default)
        Path poemPath = demoPath.resolve("poem.txt");
        List<String> lines = List.of(
                "Roses are red,",
                "Violets are blue,",
                "Java is great,",
                "And so are you!"
        );
        Files.write(poemPath, lines, StandardCharsets.UTF_8);
        System.out.println("  Wrote poem.txt with Files.write()");

        // Read all lines at once
        List<String> readBack = Files.readAllLines(poemPath, StandardCharsets.UTF_8);
        System.out.println("  Read back with Files.readAllLines():");
        readBack.forEach(l -> System.out.println("    " + l));

        // Read entire file as a String (Java 11+)
        String allContent = Files.readString(poemPath, StandardCharsets.UTF_8);
        System.out.println("  Files.readString() total chars: " + allContent.length());

        // Write a String directly (Java 11+)
        Path notePath = demoPath.resolve("quick-note.txt");
        Files.writeString(notePath, "This was written with Files.writeString() in one call!",
                StandardCharsets.UTF_8);
        System.out.println("  Wrote quick-note.txt with Files.writeString()");

        // Copy, move, delete
        Path copyPath = demoPath.resolve("poem-copy.txt");
        Files.copy(poemPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  Copied poem.txt → poem-copy.txt");

        System.out.println("\n  ⚠️  Files.readAllLines() loads the ENTIRE file into memory.");
        System.out.println("      For large files (GBs), use BufferedReader.lines() instead — it's lazy.");
        System.out.println();
    }

    // =========================================================================
    // SECTION 8 — Putting it all together: Process a CSV file
    // =========================================================================

    /**
     * Real-world task: Read a CSV of employees, filter by department,
     * and write the results to a new file.
     * This demonstrates the full read-transform-write pipeline.
     */
    static void demonstrateProcessCsvPipeline() throws IOException {
        System.out.println("=== Full Pipeline: Read CSV → Filter → Write Results ===");

        File inputCsv  = new File(DEMO_DIR, "employees.csv");
        File outputCsv = new File(DEMO_DIR, "engineering-team.csv");

        if (!inputCsv.exists()) {
            System.out.println("  employees.csv not found — run BufferedStreams demo first.");
            return;
        }

        int readCount  = 0;
        int writeCount = 0;

        try (
            BufferedReader br = new BufferedReader(new FileReader(inputCsv));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsv))
        ) {
            String header = br.readLine(); // read and preserve header
            if (header != null) {
                bw.write(header);
                bw.newLine();
            }

            String line;
            while ((line = br.readLine()) != null) {
                readCount++;
                String[] fields = line.split(",");
                // Keep only Engineering employees
                if (fields.length >= 3 && "Engineering".equals(fields[2])) {
                    bw.write(line);
                    bw.newLine();
                    writeCount++;
                }
            }
        }

        System.out.println("  Read " + readCount + " employee records.");
        System.out.println("  Wrote " + writeCount + " Engineering employees to: engineering-team.csv");

        // Verify output
        System.out.println("  Contents of engineering-team.csv:");
        try (BufferedReader br = new BufferedReader(new FileReader(outputCsv))) {
            br.lines().forEach(l -> System.out.println("    " + l));
        }

        System.out.println();
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  Day 7 - Part 2: try-with-resources & Text File I/O      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        new File(DEMO_DIR).mkdirs();

        demonstrateAutoCloseable();
        demonstrateSingleResource();
        demonstrateMultipleResources();
        demonstrateSuppressedExceptions();
        demonstrateWriteAndReadTextFile();
        demonstrateAppendToFile();
        demonstrateNioFilesHelpers();
        demonstrateProcessCsvPipeline();

        System.out.println("=== All demos complete. Files in: " + DEMO_DIR + " ===");
    }
}
