/**
 * Day 7 - Part 2: File I/O Basics
 *
 * JAVA FILE I/O — THE CORE CLASSES
 *
 *   java.io.File          — represents a file or directory PATH (not content)
 *                           use it to check existence, get metadata, list directory contents
 *
 *   java.io.FileReader    — reads characters from a file (character stream)
 *                           internally converts bytes to chars using platform default encoding
 *
 *   java.io.FileWriter    — writes characters to a file (character stream)
 *                           can create new files or OVERWRITE/APPEND existing ones
 *
 * STREAM TYPES AT A GLANCE:
 *   Character Streams (Reader/Writer) — for text files (.txt, .csv, .json, .xml)
 *   Byte Streams (InputStream/OutputStream) — for binary files (images, PDFs, .class files)
 *
 * NOTE: All of these work with try-with-resources (shown in file 3). This file intentionally
 * uses try-finally to show you the "manual" way first, so try-with-resources makes more sense.
 */

import java.io.*;
import java.nio.file.*;

public class FileIOBasics {

    // Helper — create a temp file path under /tmp for our demos
    static final String DEMO_DIR = System.getProperty("java.io.tmpdir") + File.separator + "day7-io-demo";

    // =========================================================================
    // SECTION 1 — java.io.File: The File & Directory API
    // =========================================================================

    /**
     * java.io.File does NOT open a file. It's a representation of a PATH.
     * You can inspect metadata, create directories, list contents — all without
     * reading or writing any actual data.
     */
    static void demonstrateFileClass() throws IOException {
        System.out.println("=== java.io.File — Paths and Metadata ===");

        // Create the demo directory
        File demoDir = new File(DEMO_DIR);
        if (!demoDir.exists()) {
            boolean created = demoDir.mkdirs(); // mkdirs() creates parent dirs too
            System.out.println("  Created demo directory: " + demoDir.getAbsolutePath()
                    + " → " + created);
        }

        // --- Create a File object --- (no file on disk yet)
        File myFile = new File(DEMO_DIR, "sample.txt");

        // --- Check existence before doing anything ---
        System.out.println("  File exists before creation: " + myFile.exists());

        // --- Create a new empty file ---
        boolean wasCreated = myFile.createNewFile();
        System.out.println("  createNewFile() returned: " + wasCreated
                + " (false if it already existed)");

        // --- Inspect metadata ---
        System.out.println("  Absolute path:    " + myFile.getAbsolutePath());
        System.out.println("  File name:        " + myFile.getName());
        System.out.println("  Parent directory: " + myFile.getParent());
        System.out.println("  Is a file?        " + myFile.isFile());
        System.out.println("  Is a directory?   " + myFile.isDirectory());
        System.out.println("  Can read?         " + myFile.canRead());
        System.out.println("  Can write?        " + myFile.canWrite());
        System.out.println("  File size (bytes): " + myFile.length()); // 0 — file is empty

        // --- Directory listing ---
        System.out.println("\n  Listing demo directory:");
        File[] contents = demoDir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                System.out.println("    " + (f.isDirectory() ? "[DIR]  " : "[FILE] ") + f.getName());
            }
        }

        // --- Create a second file for the listing demo ---
        new File(DEMO_DIR, "notes.txt").createNewFile();
        new File(DEMO_DIR + File.separator + "reports").mkdirs();

        System.out.println("\n  After adding notes.txt and reports/ directory:");
        File[] updated = demoDir.listFiles();
        if (updated != null) {
            for (File f : updated) {
                System.out.println("    " + (f.isDirectory() ? "[DIR]  " : "[FILE] ") + f.getName());
            }
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 2 — FileWriter: Writing Text to a File
    // =========================================================================

    /**
     * FileWriter writes character data to a file.
     * Default mode: OVERWRITE (previous content is replaced).
     * Append mode: FileWriter(file, true) — content is added to the end.
     *
     * ⚠️ WARNING: FileWriter must be closed after writing.
     *    If you don't close it, the OS buffer may never be flushed to disk.
     *    The manual try-finally pattern below shows the correct approach.
     *    (We'll replace this with try-with-resources in file 3.)
     */
    static void demonstrateFileWriter() throws IOException {
        System.out.println("=== FileWriter — Writing Text to a File ===");

        File outputFile = new File(DEMO_DIR, "diary.txt");
        FileWriter writer = null;

        try {
            // Create a FileWriter — this opens the file for writing (creates it if missing)
            writer = new FileWriter(outputFile);  // OVERWRITE mode

            // write(String) — writes raw characters, no newline added automatically
            writer.write("Dear Diary,\n");
            writer.write("Today I learned about Java file I/O.\n");
            writer.write("FileWriter writes one character at a time, or a String.\n");
            writer.write("I must remember to close() the writer when I'm done.\n");

            System.out.println("  Written to: " + outputFile.getAbsolutePath());
            System.out.println("  File size after writing: " + outputFile.length() + " bytes");

        } finally {
            // ALWAYS close in finally — this ensures flush + resource release
            if (writer != null) {
                writer.close();
                System.out.println("  FileWriter closed.");
            }
        }

        System.out.println();

        // --- APPEND mode demo ---
        System.out.println("--- FileWriter in APPEND mode ---");
        FileWriter appendWriter = null;
        try {
            // Pass 'true' as the second argument to enable append mode
            appendWriter = new FileWriter(outputFile, true); // APPEND mode
            appendWriter.write("P.S. Append mode adds to the end, not replaces.\n");
            System.out.println("  Appended a line.");
        } finally {
            if (appendWriter != null) appendWriter.close();
        }

        System.out.println("  File size after appending: " + outputFile.length() + " bytes");
        System.out.println();
    }

    // =========================================================================
    // SECTION 3 — FileReader: Reading Text from a File (character by character)
    // =========================================================================

    /**
     * FileReader.read() returns ONE character at a time as an int.
     * It returns -1 when the end of the file is reached (EOF).
     * This is the lowest-level text reading — in practice you'll almost always
     * wrap it in a BufferedReader (shown in the next file) for line-by-line reading.
     */
    static void demonstrateFileReader() throws IOException {
        System.out.println("=== FileReader — Reading Text Character by Character ===");

        File inputFile = new File(DEMO_DIR, "diary.txt");

        if (!inputFile.exists()) {
            System.out.println("  diary.txt not found — run demonstrateFileWriter() first.");
            return;
        }

        FileReader reader = null;
        try {
            reader = new FileReader(inputFile);

            System.out.println("  Reading '" + inputFile.getName() + "' character by character:");
            System.out.println("  ─────────────────────────────────────────────");

            StringBuilder content = new StringBuilder();
            int charCode;

            // read() returns the character as an int (Unicode code point), or -1 at EOF
            while ((charCode = reader.read()) != -1) {
                content.append((char) charCode); // cast int back to char
            }

            System.out.print(content);
            System.out.println("  ─────────────────────────────────────────────");
            System.out.println("  Total characters read: " + content.length());

        } finally {
            if (reader != null) reader.close();
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 4 — Reading into a char[] buffer (more efficient than one-by-one)
    // =========================================================================

    /**
     * FileReader.read(char[] buffer) reads multiple characters at once into an array.
     * Returns the number of characters actually read (may be less than buffer size at EOF).
     * This is more efficient than reading one char at a time, but still unformatted.
     */
    static void demonstrateBufferRead() throws IOException {
        System.out.println("=== FileReader with char[] buffer ===");

        File inputFile = new File(DEMO_DIR, "diary.txt");
        FileReader reader = null;

        try {
            reader = new FileReader(inputFile);

            char[] buffer = new char[64]; // read 64 characters at a time
            StringBuilder sb = new StringBuilder();
            int charsRead;

            while ((charsRead = reader.read(buffer)) != -1) {
                // charsRead tells us how many chars were actually placed in the buffer
                sb.append(buffer, 0, charsRead); // don't use the whole buffer — only charsRead chars
            }

            System.out.println("  File contents (read with 64-char buffer):");
            System.out.print(sb);
            System.out.println("  Total chars: " + sb.length());

        } finally {
            if (reader != null) reader.close();
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 5 — FileNotFoundException: what happens when the file doesn't exist
    // =========================================================================

    static void demonstrateFileNotFoundException() {
        System.out.println("=== FileNotFoundException — Checked Exception ===");

        File missingFile = new File("/tmp/this-file-does-not-exist.txt");

        try {
            FileReader reader = new FileReader(missingFile);
            // If we get here, the file existed — close it
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("  FileNotFoundException caught!");
            System.out.println("  Message: " + e.getMessage());
            System.out.println("  This is a CHECKED exception — the compiler forced us to handle it.");
            System.out.println("  We can: ask the user for a different file path,");
            System.out.println("          create a new file, or load a default resource.");
        } catch (IOException e) {
            System.out.println("  IOException during close: " + e.getMessage());
        }

        System.out.println();
    }

    // =========================================================================
    // SECTION 6 — Writing with FileWriter.write(char[], int, int)
    // =========================================================================

    /**
     * FileWriter can also write from a char array — useful when you've built
     * up a buffer and want to flush it all at once.
     */
    static void demonstrateFileWriterCharArray() throws IOException {
        System.out.println("=== FileWriter — Writing from char array ===");

        File outputFile = new File(DEMO_DIR, "chararray-demo.txt");
        String message = "Written from a char array buffer!";
        char[] chars = message.toCharArray();

        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            writer.write(chars, 0, chars.length); // write(char[], offset, length)
            writer.write('\n');                    // write a single char (newline)
            System.out.println("  Wrote " + chars.length + " chars to " + outputFile.getName());
        } finally {
            if (writer != null) writer.close();
        }

        System.out.println("  File size: " + outputFile.length() + " bytes");
        System.out.println();
    }

    // =========================================================================
    // SECTION 7 — Deleting and renaming files with File
    // =========================================================================

    static void demonstrateFileOperations() {
        System.out.println("=== File.delete() and File.renameTo() ===");

        File fileToDelete = new File(DEMO_DIR, "chararray-demo.txt");
        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            System.out.println("  Deleted chararray-demo.txt: " + deleted);
        }

        File original = new File(DEMO_DIR, "notes.txt");
        File renamed  = new File(DEMO_DIR, "notes-renamed.txt");
        if (original.exists()) {
            boolean success = original.renameTo(renamed);
            System.out.println("  Renamed notes.txt → notes-renamed.txt: " + success);
        }

        // ⚠️ renameTo() is platform-dependent and can fail if src/dest are on different
        //    file systems. For reliable file moves, prefer Files.move() from java.nio.file.
        System.out.println("  ⚠️  Prefer Files.move() from java.nio.file for reliable renames.");

        System.out.println();
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║    Day 7 - Part 2: File I/O Basics           ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        demonstrateFileClass();
        demonstrateFileWriter();
        demonstrateFileReader();
        demonstrateBufferRead();
        demonstrateFileNotFoundException();
        demonstrateFileWriterCharArray();
        demonstrateFileOperations();

        System.out.println("=== All demos complete. Files written to: " + DEMO_DIR + " ===");
    }
}
