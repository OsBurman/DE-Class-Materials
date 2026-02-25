package com.academy;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Day 7 Part 2 — File I/O: FileReader/Writer, BufferedReader, try-with-resources
 *
 * Theme: Student Diary File Manager
 * Run: mvn compile exec:java
 */
public class Main {

    static final String DATA_FILE = System.getProperty("java.io.tmpdir") + "/students.txt";
    static final String CSV_FILE  = System.getProperty("java.io.tmpdir") + "/grades.csv";

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Day 7 Part 2 — File I/O Demo                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        demoFileWriter();
        demoFileReader();
        demoBufferedIO();
        demoCsvReadWrite();
        demoNioFiles();
    }

    static void demoFileWriter() {
        System.out.println("=== 1. FileWriter (write text to file) ===");
        // try-with-resources — automatically closes writer even if exception occurs
        try (FileWriter fw = new FileWriter(DATA_FILE);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("Alice Johnson");
            bw.newLine();
            bw.write("Bob Smith");
            bw.newLine();
            bw.write("Carol White");
            bw.newLine();
            System.out.println("  Written to: " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("  Error writing: " + e.getMessage());
        }

        // Append mode
        try (FileWriter fw = new FileWriter(DATA_FILE, true);   // true = append
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Dave Brown");
            bw.newLine();
            System.out.println("  Appended Dave Brown");
        } catch (IOException e) {
            System.out.println("  Error appending: " + e.getMessage());
        }
        System.out.println();
    }

    static void demoFileReader() {
        System.out.println("=== 2. FileReader (read text from file) ===");
        try (FileReader fr = new FileReader(DATA_FILE);
             BufferedReader br = new BufferedReader(fr)) {

            System.out.println("  Reading file contents:");
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                System.out.println("    Line " + lineNum++ + ": " + line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("  File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("  Error reading: " + e.getMessage());
        }
        System.out.println();
    }

    static void demoBufferedIO() {
        System.out.println("=== 3. Buffered I/O Performance ===");
        System.out.println("  BufferedReader/Writer wraps FileReader/Writer:");
        System.out.println("  → Reads/writes chunks of data (buffer) instead of char-by-char");
        System.out.println("  → Dramatically improves performance for large files");
        System.out.println("  → readLine() reads entire line at once");
        System.out.println("  → Always use BufferedReader/Writer in production code!");
        System.out.println();
    }

    static void demoCsvReadWrite() {
        System.out.println("=== 4. CSV File Read/Write ===");

        // Write CSV
        List<String[]> data = Arrays.asList(
            new String[]{"Name",    "Subject", "Score"},
            new String[]{"Alice",   "Math",    "92"},
            new String[]{"Bob",     "Math",    "78"},
            new String[]{"Carol",   "Science", "88"},
            new String[]{"Dave",    "English", "74"}
        );

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE)))) {
            for (String[] row : data) pw.println(String.join(",", row));
            System.out.println("  CSV written to: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("  Error writing CSV: " + e.getMessage());
        }

        // Read CSV back
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean header = true;
            System.out.println("  CSV contents:");
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (header) {
                    System.out.printf("    %-10s %-10s %-5s%n", parts[0], parts[1], parts[2]);
                    System.out.println("    " + "─".repeat(28));
                    header = false;
                } else {
                    System.out.printf("    %-10s %-10s %-5s%n", parts[0], parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("  Error reading CSV: " + e.getMessage());
        }
        System.out.println();
    }

    static void demoNioFiles() {
        System.out.println("=== 5. NIO Files (Java 7+ — modern approach) ===");
        Path path = Paths.get(DATA_FILE);
        try {
            // Read all lines at once
            List<String> lines = Files.readAllLines(path);
            System.out.println("  Files.readAllLines(): " + lines);

            // Write all lines at once
            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "temp-output.txt");
            Files.write(tempPath, Arrays.asList("Line 1", "Line 2", "Line 3"));
            System.out.println("  Files.write(): wrote 3 lines to " + tempPath.getFileName());

            // Check file properties
            System.out.println("  File exists:   " + Files.exists(path));
            System.out.println("  File size:     " + Files.size(path) + " bytes");
            System.out.println("  Is readable:   " + Files.isReadable(path));

            // Cleanup temp files
            Files.deleteIfExists(path);
            Files.deleteIfExists(tempPath);
            Files.deleteIfExists(Paths.get(CSV_FILE));
        } catch (IOException e) {
            System.out.println("  NIO error: " + e.getMessage());
        }
        System.out.println("\n✓ File I/O demo complete.");
    }
}
