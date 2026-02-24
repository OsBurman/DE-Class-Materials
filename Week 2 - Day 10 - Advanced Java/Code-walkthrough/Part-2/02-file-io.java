import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * DAY 10 — PART 2 | File I/O Fundamentals
 * ─────────────────────────────────────────────────────────────────────────────
 * Java provides two I/O APIs:
 *
 * Classic I/O (java.io) — stream-based, byte or char level
 *   InputStream / OutputStream       — raw bytes (images, binary data)
 *   Reader / Writer                  — text (chars with encoding)
 *   BufferedReader / BufferedWriter  — wrap any Reader/Writer, add buffering
 *   FileReader / FileWriter          — convenience for files
 *   PrintWriter                      — formatted text output
 *
 * Modern NIO.2 (java.nio.file) — Java 7+, higher-level
 *   Files.readAllLines(), Files.writeString(), Files.copy()
 *   Path / Paths — represent file system paths (preferred over java.io.File)
 *
 * ALWAYS use try-with-resources to close streams automatically.
 * Unclosed streams → resource leaks → file descriptor exhaustion.
 */
public class FileIO {

    // Use system temp dir so demo works anywhere
    static final Path DEMO_DIR = Path.of(System.getProperty("java.io.tmpdir"), "java-file-io-demo");

    public static void main(String[] args) throws IOException {
        // Create a working directory for all demo files
        Files.createDirectories(DEMO_DIR);
        System.out.println("Working directory: " + DEMO_DIR + "\n");

        demonstrateFileWriter();
        demonstrateFileReader();
        demonstrateBufferedReaderWriter();
        demonstratePrintWriter();
        demonstrateByteStreams();
        demonstrateNIOFiles();
        demonstrateTryWithResources();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — FileWriter: write text to a file char by char
    // FileWriter uses the platform default charset (UTF-8 on modern JVMs)
    // Provide the charset explicitly for portable code
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateFileWriter() throws IOException {
        System.out.println("=== FileWriter ===");

        Path file = DEMO_DIR.resolve("writer-demo.txt");

        // try-with-resources: FileWriter is AutoCloseable — closed automatically
        try (FileWriter writer = new FileWriter(file.toFile(), StandardCharsets.UTF_8)) {
            writer.write("Order ID: ORD-001\n");
            writer.write("Customer: Alice Smith\n");
            writer.write("Amount: $249.99\n");
        }   // ← writer.close() called here, even if exception thrown

        System.out.println("Written to: " + file);
        System.out.println("Content: " + Files.readString(file));

        // append=true — add to existing file instead of overwriting
        try (FileWriter appender = new FileWriter(file.toFile(), StandardCharsets.UTF_8, true)) {
            appender.write("Status: SHIPPED\n");
        }
        System.out.println("After append: " + Files.readString(file));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — FileReader: read text from a file
    // FileReader.read() returns int: -1 means end-of-file
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateFileReader() throws IOException {
        System.out.println("=== FileReader ===");

        Path file = DEMO_DIR.resolve("writer-demo.txt");  // use the file we just wrote

        // Character-by-character read — very common in interviews, rarely used in production
        try (FileReader reader = new FileReader(file.toFile(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {   // -1 = EOF
                sb.append((char) ch);
            }
            System.out.println("Read char-by-char (" + sb.length() + " chars)");
        }

        // char[] buffer read — more efficient than char-by-char
        try (FileReader reader = new FileReader(file.toFile(), StandardCharsets.UTF_8)) {
            char[] buffer = new char[64];
            int charsRead;
            int totalChars = 0;
            while ((charsRead = reader.read(buffer)) != -1) {
                totalChars += charsRead;
            }
            System.out.println("Read with buffer: " + totalChars + " chars\n");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — BufferedReader / BufferedWriter
    // Buffer I/O reduces system calls: instead of reading one char at a time,
    // it reads a chunk (default 8KB) and serves from memory buffer.
    // BufferedReader adds readLine() — the most common file reading pattern.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateBufferedReaderWriter() throws IOException {
        System.out.println("=== BufferedReader / BufferedWriter ===");

        Path logFile = DEMO_DIR.resolve("access-log.txt");

        // BufferedWriter wraps a FileWriter — write full lines efficiently
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(logFile.toFile(), StandardCharsets.UTF_8))) {
            String[] logEntries = {
                    "2025-01-15 09:00:01 INFO  GET /api/products 200 45ms",
                    "2025-01-15 09:00:02 INFO  POST /api/orders   201 120ms",
                    "2025-01-15 09:00:03 ERROR GET /api/users/999 404 5ms",
                    "2025-01-15 09:00:04 INFO  PUT /api/orders/1  200 98ms",
                    "2025-01-15 09:00:05 WARN  GET /api/report    200 2050ms"
            };
            for (String entry : logEntries) {
                bw.write(entry);
                bw.newLine();   // platform-neutral newline (\n on Unix, \r\n on Windows)
            }
        }
        System.out.println("Wrote " + 5 + " log entries");

        // BufferedReader — readLine() returns null at EOF
        System.out.println("Reading ERROR lines from log:");
        try (BufferedReader br = new BufferedReader(
                new FileReader(logFile.toFile(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {   // null = EOF (not "")
                lineNumber++;
                if (line.contains("ERROR")) {
                    System.out.println("  Line " + lineNumber + ": " + line);
                }
            }
        }

        // Custom buffer size — useful for large files
        try (BufferedReader br = new BufferedReader(
                new FileReader(logFile.toFile(), StandardCharsets.UTF_8), 16 * 1024)) { // 16KB buffer
            long lineCount = br.lines().count();   // Java 8+ streams from BufferedReader
            System.out.println("Total lines: " + lineCount + "\n");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — PrintWriter: formatted text output (like System.out.println)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstratePrintWriter() throws IOException {
        System.out.println("=== PrintWriter ===");

        Path reportFile = DEMO_DIR.resolve("sales-report.csv");

        // PrintWriter over BufferedWriter over FileWriter — best for formatted text
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(reportFile.toFile(), StandardCharsets.UTF_8)))) {
            // printf-style formatting
            pw.println("OrderId,Product,Quantity,Price,Total");
            pw.printf("ORD-001,Laptop,1,999.99,%.2f%n", 999.99);
            pw.printf("ORD-002,Mouse,3,29.99,%.2f%n", 3 * 29.99);
            pw.printf("ORD-003,Keyboard,2,79.99,%.2f%n", 2 * 79.99);
        }

        System.out.println("CSV report written:");
        Files.readAllLines(reportFile).forEach(line -> System.out.println("  " + line));
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — Byte Streams (InputStream / OutputStream)
    // Use byte streams for binary data: images, PDFs, audio, compressed files.
    // Never use FileWriter/Reader for binary — it corrupts data via charset conversion.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateByteStreams() throws IOException {
        System.out.println("=== Byte Streams (binary data) ===");

        Path binaryFile = DEMO_DIR.resolve("data.bin");

        // Write bytes directly
        try (FileOutputStream fos = new FileOutputStream(binaryFile.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] data = {0x48, 0x65, 0x6C, 0x6C, 0x6F};  // "Hello" in ASCII bytes
            bos.write(data);
            bos.write(new byte[]{0x20, 0x57, 0x6F, 0x72, 0x6C, 0x64});  // " World"
        }

        // Read bytes back
        try (FileInputStream fis = new FileInputStream(binaryFile.toFile());
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            byte[] buffer = new byte[11];
            int bytesRead = bis.read(buffer);
            System.out.println("Read " + bytesRead + " bytes: " + new String(buffer, 0, bytesRead));
        }

        // File copy using streams (NIO Files.copy is preferred in practice)
        Path copyFile = DEMO_DIR.resolve("data-copy.bin");
        try (InputStream in  = new BufferedInputStream(new FileInputStream(binaryFile.toFile()));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(copyFile.toFile()))) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
        }
        System.out.println("File copied successfully: " + Files.size(copyFile) + " bytes\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — NIO.2 Files API (Java 7+)
    // Higher-level than java.io — preferred for most file operations.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateNIOFiles() throws IOException {
        System.out.println("=== NIO.2 Files API ===");

        Path notes = DEMO_DIR.resolve("notes.txt");

        // Write all at once
        Files.writeString(notes, "Line 1\nLine 2\nLine 3\n", StandardCharsets.UTF_8);

        // Read all at once
        String content = Files.readString(notes, StandardCharsets.UTF_8);
        System.out.println("readString: " + content.strip());

        // Read as List<String>
        List<String> lines = Files.readAllLines(notes, StandardCharsets.UTF_8);
        System.out.println("readAllLines count: " + lines.size());

        // Stream lines lazily (good for large files — no heap spike)
        long linesWithLine = Files.lines(notes)
                .filter(l -> l.contains("Line"))
                .count();
        System.out.println("Lines containing 'Line': " + linesWithLine);

        // File operations
        Path copy = DEMO_DIR.resolve("notes-copy.txt");
        Files.copy(notes, copy, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied: " + Files.exists(copy));

        Path moved = DEMO_DIR.resolve("notes-moved.txt");
        Files.move(copy, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Moved — original exists: " + Files.exists(copy)
                + ", new exists: " + Files.exists(moved));

        Files.delete(moved);
        System.out.println("Deleted: " + !Files.exists(moved));

        // Directory listing
        System.out.println("Files in demo dir:");
        Files.list(DEMO_DIR).forEach(p -> System.out.println("  " + p.getFileName()));
        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — try-with-resources: the correct pattern for all I/O
    // Any class implementing AutoCloseable can be used in try-with-resources.
    // Multiple resources: all are closed in REVERSE order, even on exception.
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateTryWithResources() throws IOException {
        System.out.println("=== try-with-resources ===");

        Path input  = DEMO_DIR.resolve("access-log.txt");
        Path output = DEMO_DIR.resolve("errors-only.txt");

        // Multiple resources in one try — both closed in reverse order on exit
        try (BufferedReader reader = new BufferedReader(
                     new FileReader(input.toFile(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                     new FileWriter(output.toFile(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ERROR")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Filtered ERROR lines to: " + output.getFileName());
        }
        // reader and writer are BOTH closed here — writer first, then reader

        System.out.println("Error log contents:");
        Files.readAllLines(output).forEach(l -> System.out.println("  " + l));

        // The BAD pattern (never do this):
        // BufferedReader br = new BufferedReader(new FileReader(...));
        // // ... code that might throw ...
        // br.close();   ← never reached if exception is thrown!

        System.out.println("\n⚠️  ALWAYS use try-with-resources for I/O. Never rely on manual close().");
    }
}
