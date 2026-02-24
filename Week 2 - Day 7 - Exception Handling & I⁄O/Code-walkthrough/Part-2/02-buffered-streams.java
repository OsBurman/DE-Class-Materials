/**
 * Day 7 - Part 2: Buffered Streams
 *
 * WHY BUFFERING MATTERS
 * ─────────────────────
 * FileReader and FileWriter work directly with the OS file system.
 * Every read() or write() call may result in a system call — crossing the user/kernel boundary.
 * System calls are expensive (microseconds). Reading a 10,000-line file one char at a time
 * means 10,000+ system calls.
 *
 * Buffered wrappers maintain an in-memory buffer (default 8 KB).
 * Reads: fill the buffer in one system call, then satisfy subsequent read() calls from RAM.
 * Writes: accumulate writes in the buffer, then flush to disk in one system call.
 * Result: dramatically fewer system calls → much faster I/O.
 *
 * KEY BUFFERED CLASSES:
 *
 *   Character Streams (Text):
 *     BufferedReader  — wraps any Reader  (usually FileReader)
 *     BufferedWriter  — wraps any Writer  (usually FileWriter)
 *
 *   Byte Streams (Binary):
 *     BufferedInputStream  — wraps any InputStream  (usually FileInputStream)
 *     BufferedOutputStream — wraps any OutputStream (usually FileOutputStream)
 *
 * WRAPPING PATTERN:
 *   BufferedReader br = new BufferedReader(new FileReader("file.txt"));
 *   BufferedWriter bw = new BufferedWriter(new FileWriter("file.txt"));
 *
 * The "decorator pattern" — you stack capabilities by wrapping one stream in another.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BufferedStreams {

    static final String DEMO_DIR = System.getProperty("java.io.tmpdir")
            + File.separator + "day7-io-demo";

    // =========================================================================
    // SECTION 1 — BufferedWriter: Writing line by line
    // =========================================================================

    /**
     * BufferedWriter adds two things FileWriter doesn't have:
     *   1. An in-memory buffer — writes are batched before hitting the OS
     *   2. newLine() — writes the platform-appropriate newline (not just '\n')
     *
     * ⚠️ KEY POINT: You must close() or flush() the BufferedWriter after writing.
     *    Data sitting in the buffer that was never flushed = data silently lost.
     */
    static void demonstrateBufferedWriter() throws IOException {
        System.out.println("=== BufferedWriter — Efficient Line-by-Line Writing ===");

        File outputFile = new File(DEMO_DIR, "employees.csv");

        // ─── Manual try-finally (showing the pattern before try-with-resources) ───
        BufferedWriter bw = null;
        try {
            // Stack: BufferedWriter wraps FileWriter
            bw = new BufferedWriter(new FileWriter(outputFile));

            // Write the CSV header
            bw.write("id,name,department,salary");
            bw.newLine(); // platform-safe newline (on Windows: \r\n; on Unix: \n)

            // Write data rows
            String[][] employees = {
                {"1", "Alice Johnson", "Engineering", "95000"},
                {"2", "Bob Martinez", "Marketing",   "72000"},
                {"3", "Carol Lee",    "Engineering", "88000"},
                {"4", "David Kim",    "HR",          "65000"},
                {"5", "Eva Brown",    "Finance",     "78000"},
            };

            for (String[] emp : employees) {
                bw.write(String.join(",", emp));
                bw.newLine();
            }

            // flush() forces the buffer to disk without closing the stream
            // Useful if you want to keep the stream open but ensure data is written
            bw.flush();
            System.out.println("  Written " + (employees.length + 1) + " lines to employees.csv");
            System.out.println("  File size: " + outputFile.length() + " bytes");

        } finally {
            if (bw != null) bw.close(); // close() calls flush() first, then releases resources
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — BufferedReader: Reading line by line with readLine()
    // =========================================================================

    /**
     * BufferedReader.readLine() is the key method — it reads one complete line
     * and returns it as a String, stripping the newline character.
     * Returns null when the end of file is reached (not -1 like read()).
     */
    static void demonstrateBufferedReader() throws IOException {
        System.out.println("=== BufferedReader — Line-by-Line Reading with readLine() ===");

        File inputFile = new File(DEMO_DIR, "employees.csv");

        if (!inputFile.exists()) {
            System.out.println("  employees.csv not found — run demonstrateBufferedWriter() first.");
            return;
        }

        BufferedReader br = null;
        try {
            // Stack: BufferedReader wraps FileReader
            br = new BufferedReader(new FileReader(inputFile));

            System.out.println("  Reading employees.csv:");
            System.out.println("  ─────────────────────────────────────────");

            String line;
            int lineNumber = 0;
            List<String[]> records = new ArrayList<>();

            // readLine() returns null at EOF — classic while loop pattern
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    System.out.println("  HEADER: " + line);
                    System.out.println("  ─────────────────────────────────────────");
                } else {
                    String[] fields = line.split(",");
                    records.add(fields);
                    System.out.printf("  Row %d: id=%-4s name=%-15s dept=%-12s salary=%s%n",
                            lineNumber - 1, fields[0], fields[1], fields[2], fields[3]);
                }
            }

            System.out.println("  ─────────────────────────────────────────");
            System.out.println("  Total data rows: " + records.size());

            // Compute average salary
            double totalSalary = records.stream()
                    .mapToDouble(r -> Double.parseDouble(r[3]))
                    .sum();
            System.out.printf("  Average salary: $%.2f%n", totalSalary / records.size());

        } finally {
            if (br != null) br.close();
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — BufferedReader.lines() — Stream-based reading (Java 8+)
    // =========================================================================

    /**
     * BufferedReader.lines() returns a Stream<String> — each element is one line.
     * This integrates naturally with the Streams API (covered in Day 8).
     * The stream is lazy — lines are read on demand, not all at once.
     *
     * ⚠️ The BufferedReader must still be closed — use try-with-resources.
     */
    static void demonstrateBufferedReaderLinesStream() throws IOException {
        System.out.println("=== BufferedReader.lines() — Stream API Integration ===");

        File inputFile = new File(DEMO_DIR, "employees.csv");

        // Using try-with-resources (full coverage in file 3, preview here)
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            long engineeringCount = br.lines()
                    .skip(1)             // skip header line
                    .filter(line -> line.contains("Engineering"))
                    .count();

            System.out.println("  Engineering employees: " + engineeringCount);
        }

        // Re-open for a second query (stream was consumed above)
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            System.out.println("  High earners (salary ≥ 80000):");
            br.lines()
                    .skip(1)
                    .filter(line -> {
                        String[] parts = line.split(",");
                        return Double.parseDouble(parts[3]) >= 80000;
                    })
                    .forEach(line -> System.out.println("    " + line));
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — BufferedInputStream / BufferedOutputStream (Byte Streams)
    // =========================================================================

    /**
     * For binary files (images, PDFs, serialized objects), use byte streams.
     * FileInputStream / FileOutputStream are the raw versions.
     * BufferedInputStream / BufferedOutputStream are the buffered wrappers.
     *
     * Here we demonstrate by copying a file at the byte level.
     */
    static void demonstrateByteStreams() throws IOException {
        System.out.println("=== BufferedInputStream/OutputStream — Binary File Copy ===");

        // Source: our employees.csv (treat it as bytes for this demo)
        File source = new File(DEMO_DIR, "employees.csv");
        File dest   = new File(DEMO_DIR, "employees-backup.csv");

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        long startTime = System.nanoTime();

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest));

            byte[] buffer = new byte[4096]; // 4 KB buffer
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead); // write only the bytes actually read
            }

            bos.flush(); // ensure everything is written out

        } finally {
            // Close in reverse order of opening — most nested first
            if (bos != null) bos.close();
            if (bis != null) bis.close();
        }

        long elapsed = System.nanoTime() - startTime;

        System.out.println("  Copied " + source.length() + " bytes to " + dest.getName());
        System.out.println("  Source size: " + source.length() + " bytes");
        System.out.println("  Dest size:   " + dest.length() + " bytes");
        System.out.printf("  Time: %.3f ms%n", elapsed / 1_000_000.0);
        System.out.println("  ✓ Files match: " + (source.length() == dest.length()));

        System.out.println();
    }

    // =========================================================================
    // SECTION 5 — PrintWriter: Convenient formatted writing
    // =========================================================================

    /**
     * PrintWriter wraps a Writer and adds:
     *   - println() — like System.out.println but writes to a file
     *   - printf()  — formatted output
     *   - print()   — no newline
     *
     * It's essentially System.out but pointing at a file.
     * ⚠️ PrintWriter swallows IOExceptions — use checkError() if you care about them.
     *    For production code, prefer BufferedWriter and handle IOException explicitly.
     */
    static void demonstratePrintWriter() throws IOException {
        System.out.println("=== PrintWriter — printf/println to a File ===");

        File reportFile = new File(DEMO_DIR, "sales-report.txt");

        // PrintWriter can wrap FileWriter directly or a BufferedWriter
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));

            pw.println("=== SALES REPORT ===");
            pw.printf("%-20s %10s %15s%n", "Product", "Units", "Revenue");
            pw.println("─".repeat(47));

            String[][] salesData = {
                {"Widget Pro",    "1200", "48000.00"},
                {"Gadget Plus",   "850",  "63750.00"},
                {"ThingaMajig",   "3400", "17000.00"},
                {"Doohickey",     "220",  "11000.00"},
            };

            for (String[] row : salesData) {
                pw.printf("%-20s %10s %15s%n", row[0], row[1], row[2]);
            }

            pw.println("─".repeat(47));
            pw.println("Generated: " + java.time.LocalDate.now());

            // ⚠️ PrintWriter silently swallows IOExceptions — always check error state
            if (pw.checkError()) {
                System.out.println("  ⚠️  PrintWriter encountered an error during write!");
            } else {
                System.out.println("  Report written successfully to: " + reportFile.getName());
            }

        } finally {
            if (pw != null) pw.close();
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Performance Comparison: Unbuffered vs Buffered
    // =========================================================================

    /**
     * This section demonstrates WHY buffering matters.
     * We write 10,000 lines twice — once with raw FileWriter, once with BufferedWriter.
     * The difference can be an order of magnitude.
     */
    static void demonstrateBufferingPerformance() throws IOException {
        System.out.println("=== Performance: Unbuffered vs Buffered Writing ===");

        File unbufferedFile = new File(DEMO_DIR, "perf-unbuffered.txt");
        File bufferedFile   = new File(DEMO_DIR, "perf-buffered.txt");
        int lineCount = 10_000;

        // --- Unbuffered: raw FileWriter ---
        long start = System.nanoTime();
        FileWriter fw = null;
        try {
            fw = new FileWriter(unbufferedFile);
            for (int i = 0; i < lineCount; i++) {
                fw.write("Line " + i + ": The quick brown fox jumps over the lazy dog.\n");
            }
        } finally {
            if (fw != null) fw.close();
        }
        long unbufferedMs = (System.nanoTime() - start) / 1_000_000;

        // --- Buffered: BufferedWriter wrapping FileWriter ---
        start = System.nanoTime();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(bufferedFile));
            for (int i = 0; i < lineCount; i++) {
                bw.write("Line " + i + ": The quick brown fox jumps over the lazy dog.");
                bw.newLine();
            }
        } finally {
            if (bw != null) bw.close();
        }
        long bufferedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  Writing %,d lines:%n", lineCount);
        System.out.printf("    Unbuffered (FileWriter):          %4d ms%n", unbufferedMs);
        System.out.printf("    Buffered   (BufferedWriter):      %4d ms%n", bufferedMs);
        if (bufferedMs > 0) {
            System.out.printf("    Speedup: approximately %.1fx faster%n",
                    (double) unbufferedMs / bufferedMs);
        }
        System.out.println("  → Always use BufferedWriter/BufferedReader for text I/O.");
        System.out.println();
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║    Day 7 - Part 2: Buffered Streams          ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        // Ensure demo directory exists
        new File(DEMO_DIR).mkdirs();

        demonstrateBufferedWriter();
        demonstrateBufferedReader();
        demonstrateBufferedReaderLinesStream();
        demonstrateByteStreams();
        demonstratePrintWriter();
        demonstrateBufferingPerformance();

        System.out.println("=== All buffered stream demos complete. ===");
    }
}
