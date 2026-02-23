# Week 2 - Day 7 (Tuesday) Part 2: File I/O & Resource Management
## Reading and Writing Files with Java

---

## Slide 1: Welcome to File I/O

**Visual:** Folder and file icons; data flowing from disk to memory and vice versa

Welcome to Part 2. You've mastered exception handling theory. Now you'll apply it. File I/O is where exceptions become necessary. Every file operation can fail. The file doesn't exist. You don't have permissions. The disk is full. Without exception handling, these failures crash your program. With exception handling, you recover gracefully. This part covers file operations from basics to advanced resource management. By the end, you'll read and write files professionally.

---

## Slide 2: Why File I/O Matters

**Visual:** Data persistence: disk storage vs memory; programs that read/write files (databases, editors)

Disk storage is permanent. RAM is temporary. When your program exits, RAM is cleared. Data is lost. File I/O lets you persist data to disk. Read it back later. Every real application does this. Databases store data in files. Text editors save your work to files. Configuration applications read from files. Web servers serve files. Understanding file I/O is essential for practical programming.

---

## Slide 3: File Class: Representing Files

**Visual:** File object with path, filename, properties shown

The File class represents files and directories:

```java
import java.io.File;

File file = new File("data.txt");
File directory = new File("/path/to/folder");

System.out.println(file.getName());        // "data.txt"
System.out.println(file.getAbsolutePath()); // Full path
System.out.println(file.exists());         // true or false
System.out.println(file.isFile());         // Is it a file?
System.out.println(file.isDirectory());    // Is it a directory?
System.out.println(file.length());         // File size in bytes
```

File objects represent file paths. They don't read or write. They represent the file itself—name, path, properties. To actually read or write, you use Reader/Writer classes.

---

## Slide 4: File Paths: Absolute vs Relative

**Visual:** File system tree showing absolute path (/path/to/file) and relative path (./file)

Absolute paths start from the root: "/Users/emily/Documents/data.txt". Relative paths start from the current working directory: "data.txt", "./subfolder/data.txt". Absolute paths work anywhere. Relative paths depend on where the program is run from. For production code, use absolute paths or handle both carefully. For testing, relative paths are convenient.

---

## Slide 5: Checking File Properties

**Visual:** File object with methods showing exists(), isFile(), canRead(), canWrite()

Before operating on files, check properties:

```java
File file = new File("data.txt");

if (!file.exists()) {
    System.out.println("File does not exist");
    return;
}

if (!file.isFile()) {
    System.out.println("Path is not a file (it's a directory)");
    return;
}

if (!file.canRead()) {
    System.out.println("Cannot read file (permission denied)");
    return;
}
```

exists(): Does the file exist? isFile(): Is it a file (not directory)? canRead(): Can you read it? canWrite(): Can you write it? These checks prevent exceptions—prevention is better than exception handling.

---

## Slide 6: FileReader: Reading Text Files

**Visual:** FileReader class with constructor and read methods

FileReader reads text files character by character:

```java
try {
    FileReader reader = new FileReader("data.txt");
    int character = reader.read();  // Read one character
    System.out.println((char) character);
    reader.close();
} catch (IOException e) {
    System.out.println("Failed to read file: " + e.getMessage());
}
```

FileReader reads one character at a time. read() returns an int (the character code) or -1 if end-of-file. You must convert to char. FileReader implements AutoCloseable, so close() must be called. Use try-with-resources to ensure it's closed.

---

## Slide 7: FileReader with Try-With-Resources

**Visual:** Try-with-resources block with FileReader auto-closed

Modern Java always uses try-with-resources:

```java
try (FileReader reader = new FileReader("data.txt")) {
    int character;
    while ((character = reader.read()) != -1) {
        System.out.print((char) character);
    }
} catch (IOException e) {
    System.out.println("Failed to read file");
}
// reader is automatically closed
```

Try-with-resources manages the file handle. When the try block exits (normally or via exception), close() is automatically called. This is robust and idiomatic Java. Always use this pattern.

---

## Slide 8: FileWriter: Writing Text Files

**Visual:** FileWriter class with constructor and write methods

FileWriter writes text files character by character:

```java
try (FileWriter writer = new FileWriter("output.txt")) {
    writer.write('H');
    writer.write('e');
    writer.write('l');
    writer.write('l');
    writer.write('o');
} catch (IOException e) {
    System.out.println("Failed to write file");
}
```

FileWriter writes one character at a time. write() accepts a char or int. FileWriter buffers data internally and flushes it periodically. When you close the file, any remaining data is flushed. With try-with-resources, close() is called automatically.

---

## Slide 9: FileWriter Creates or Overwrites

**Visual:** File operations showing FileWriter creating new file or overwriting existing

Important: FileWriter creates a new file if it doesn't exist. If the file exists, it overwrites it:

```java
try (FileWriter writer = new FileWriter("data.txt")) {
    writer.write("Hello");
} catch (IOException e) {
    System.out.println("Failed to write");
}
```

If data.txt exists, it's truncated to zero bytes and overwritten. This is often what you want. If you want to append instead of overwrite, use a different constructor:

```java
FileWriter writer = new FileWriter("data.txt", true);  // true = append
```

---

## Slide 10: Buffered Streams: Performance Optimization

**Visual:** Buffered reader showing buffer between file and program

FileReader and FileWriter read/write one character at a time. This is slow. Each operation might hit the disk. Buffered streams use a buffer:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line = reader.readLine();  // Read entire line
    System.out.println(line);
} catch (IOException e) {
    System.out.println("Failed");
}
```

BufferedReader wraps FileReader. It reads chunks into a buffer. readLine() returns the next line as a String. Much more efficient than character-by-character reading. Always use Buffered streams for file I/O.

---

## Slide 11: BufferedReader vs FileReader

**Visual:** Comparison showing FileReader (char-by-char) vs BufferedReader (line-by-line)

FileReader: Reads one character at a time. read() returns int. Inefficient.
BufferedReader: Reads lines. readLine() returns String. Efficient. More convenient.

```java
// FileReader (inefficient)
try (FileReader reader = new FileReader("data.txt")) {
    int ch;
    while ((ch = reader.read()) != -1) {
        System.out.print((char) ch);
    }
}

// BufferedReader (efficient)
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

BufferedReader provides readLine(), which reads until newline or end-of-file. Much cleaner for text files.

---

## Slide 12: BufferedWriter: Writing Lines

**Visual:** BufferedWriter with write and newLine methods

BufferedWriter wraps FileWriter and adds newLine():

```java
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt"))) {
    writer.write("Hello");
    writer.newLine();  // Write platform-specific newline
    writer.write("World");
    writer.newLine();
} catch (IOException e) {
    System.out.println("Failed");
}
```

BufferedWriter buffersoutput for efficiency. newLine() writes a newline character (platform-specific: \n on Unix, \r\n on Windows). write() doesn't add newlines automatically. You control when lines break.

---

## Slide 13: Reading an Entire File into a String

**Visual:** File contents loaded into String in memory

Common pattern: read entire file into a String:

```java
public String readFileAsString(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new FileReader(filename))) {
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }
}
```

StringBuilder accumulates lines. You could also use:

```java
Files.readString(Path.of(filename));  // Java 11+
```

This one-liner reads entire file into a String. Modern Java provides convenience methods. But understanding manual reading is important.

---

## Slide 14: Writing Multiple Lines to File

**Visual:** Lines written to file with newlines separating them

Pattern: write multiple lines:

```java
public void writeLinesToFile(String filename, List<String> lines) 
        throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
            new FileWriter(filename))) {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}

// Usage
List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");
writeLinesToFile("output.txt", lines);
```

Iterate through lines. Write each line. Call newLine() after each. BufferedWriter buffers the output and flushes when you close.

---

## Slide 15: Try-With-Resources: Multiple Resources

**Visual:** Try-with-resources with reader and writer both declared

Read from one file, write to another:

```java
try (BufferedReader reader = new BufferedReader(
            new FileReader("input.txt"));
     BufferedWriter writer = new BufferedWriter(
            new FileWriter("output.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        writer.write(line);
        writer.newLine();
    }
} catch (IOException e) {
    System.out.println("Failed");
}
```

Manage multiple resources in one try statement. Both reader and writer are automatically closed. This is concise and robust.

---

## Slide 16: FileNotFoundException: File Doesn't Exist

**Visual:** FileNotFoundException with file icon crossed out

FileReader throws FileNotFoundException if the file doesn't exist. This is a subclass of IOException:

```java
try (FileReader reader = new FileReader("nonexistent.txt")) {
    // ...
} catch (FileNotFoundException e) {
    System.out.println("File not found: " + e.getMessage());
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
}
```

Catch FileNotFoundException specifically to handle missing files. Catch IOException for other file operation failures. Order matters: catch specific exceptions first.

---

## Slide 17: IOException: General File Operation Failures

**Visual:** IOException with multiple causes: permission denied, disk full, etc.

IOException is thrown for various file operation failures: permission denied, disk full, etc.:

```java
try (FileReader reader = new FileReader("data.txt")) {
    // ...
} catch (IOException e) {
    System.out.println("IO operation failed: " + e.getMessage());
    e.printStackTrace();
}
```

IOException is general. FileNotFoundException is specific. IOException for permission denied, permission denied on write, disk full, etc. Catch FileNotFoundException first if you want specific handling. Catch IOException for other failures.

---

## Slide 18: Java NIO: Modern File I/O

**Visual:** java.nio.file.Files class with static convenience methods

Java NIO provides modern file I/O through java.nio.file.Files:

```java
import java.nio.file.Files;
import java.nio.file.Path;

// Read entire file
String content = Files.readString(Path.of("data.txt"));

// Write entire file
Files.writeString(Path.of("output.txt"), "Hello, World!");

// Copy file
Files.copy(Path.of("source.txt"), Path.of("dest.txt"));

// List files in directory
Files.list(Path.of(".")).forEach(System.out::println);
```

Files class provides static convenience methods. readString() reads entire file into String. writeString() writes String to file. These are preferable to manual BufferedReader/Writer for simple operations.

---

## Slide 19: Choosing Between Traditional I/O and NIO

**Visual:** Comparison table: Traditional I/O (flexibility) vs NIO (convenience)

Traditional I/O (Streams/Readers/Writers): More control, character-by-character or line-by-line. Use for complex operations, large files, streaming data. NIO (java.nio.file.Files): Simpler API, convenience methods. Use for simple read/write operations, small files. Both are valid. NIO is often simpler. Traditional I/O is more flexible. Know both.

---

## Slide 20: Real-World Example: Configuration File Reader

**Visual:** Configuration file with key-value pairs; parsed into Map

```java
public Map<String, String> readConfig(String filename) throws IOException {
    Map<String, String> config = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(
            new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;  // Skip blank lines and comments
            }
            String[] parts = line.split("=");
            if (parts.length == 2) {
                config.put(parts[0].trim(), parts[1].trim());
            }
        }
    }
    return config;
}
```

Read configuration file. Parse key=value pairs. Skip comments. Return Map. Real-world application.

---

## Slide 21: Real-World Example: Log File Appender

**Visual:** Log messages appended to file with timestamps

```java
public void appendLog(String filename, String message) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
            new FileWriter(filename, true))) {  // true = append
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new Date());
        writer.write("[" + timestamp + "] " + message);
        writer.newLine();
    }
}

// Usage
appendLog("app.log", "Application started");
appendLog("app.log", "User logged in");
appendLog("app.log", "Error occurred");
```

Append messages to log file. Each call opens the file, appends the message, closes. FileWriter(filename, true) means append, not overwrite. With timestamps, logs are useful for debugging.

---

## Slide 22: Real-World Example: File Copy with Progress

**Visual:** Progress bar showing copying process

```java
public void copyFile(String source, String dest) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new FileReader(source));
         BufferedWriter writer = new BufferedWriter(
            new FileWriter(dest))) {
        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
            lineCount++;
            if (lineCount % 100 == 0) {
                System.out.println("Copied " + lineCount + " lines");
            }
        }
        System.out.println("Copy complete: " + lineCount + " lines");
    }
}
```

Copy file line-by-line. Report progress every 100 lines. Real-world pattern for processing large files incrementally.

---

## Slide 23: Permissions and Security

**Visual:** Lock icon; permission denied scenario

File permissions matter. You might not have permission to read or write:

```java
File file = new File("data.txt");
if (!file.canRead()) {
    System.out.println("Cannot read: Permission denied");
    return;
}
if (!file.canWrite()) {
    System.out.println("Cannot write: Permission denied");
    return;
}
```

Check permissions before operating. This prevents exceptions. Also be cautious with file paths. Don't allow users to specify arbitrary file paths—they might access sensitive files. Validate paths carefully.

---

## Slide 24: Beginner Mistake: Forgetting to Close Files

**Visual:** ❌ Manual close forgotten vs ✓ try-with-resources automatic close

**WRONG:**
```java
try {
    FileReader reader = new FileReader("data.txt");
    int ch = reader.read();
    System.out.println((char) ch);
    // Forgot to close! File handle leaks
} catch (IOException e) {
    System.out.println("Error");
}
```

**CORRECT:**
```java
try (FileReader reader = new FileReader("data.txt")) {
    int ch = reader.read();
    System.out.println((char) ch);
} catch (IOException e) {
    System.out.println("Error");
}
// Automatically closed
```

Always use try-with-resources. Never manually close. The IDE will warn you if you forget. File handles are limited resources. Leaking them eventually exhausts the system. Always close.

---

## Slide 25: Beginner Mistake: Not Handling FileNotFoundException

**Visual:** ❌ Unhandled FileNotFoundException vs ✓ caught and handled

**WRONG:**
```java
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);  // Throws if not found
    // ...
}
```

**CORRECT:**
```java
public void readFile(String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
        throw new IOException("File not found: " + filename);
    }
    try (FileReader reader = new FileReader(filename)) {
        // ...
    }
}
```

Check if file exists before reading. Or catch FileNotFoundException specifically:

```java
try (FileReader reader = new FileReader(filename)) {
    // ...
} catch (FileNotFoundException e) {
    System.out.println("File not found: " + filename);
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
}
```

---

## Slide 26: Beginner Mistake: Not Flushing Before Close

**Visual:** Data in buffer not written to disk; data lost

BufferedWriter buffers data. If you close without flushing, data might be lost:

```java
BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"));
writer.write("Hello");
writer.close();  // close() flushes, so data is written
```

Actually, close() calls flush() automatically, so it's safe. But it's good practice to understand. If you want to force a flush without closing:

```java
writer.flush();  // Force buffered data to disk
```

This is rarely needed. close() is usually sufficient. But know it exists for completeness.

---

## Slide 27: Beginner Mistake: Mixing FileWriter Append Modes

**Visual:** ❌ overwrite by mistake vs ✓ append correctly

**WRONG:**
```java
// First write
try (FileWriter writer = new FileWriter("log.txt")) {
    writer.write("First entry");
} catch (IOException e) {}

// Second write - overwrites first!
try (FileWriter writer = new FileWriter("log.txt")) {
    writer.write("Second entry");
} catch (IOException e) {}

// File contains only "Second entry"
```

**CORRECT:**
```java
// First write
try (FileWriter writer = new FileWriter("log.txt")) {
    writer.write("First entry");
    writer.newLine();
} catch (IOException e) {}

// Second write - append
try (FileWriter writer = new FileWriter("log.txt", true)) {  // true = append
    writer.write("Second entry");
    writer.newLine();
} catch (IOException e) {}

// File contains both entries
```

Default FileWriter overwrites. Add true parameter to append. Easy mistake to make.

---

## Slide 28: Character Encodings: UTF-8 vs ASCII

**Visual:** Text with special characters; encoding shown (UTF-8 vs ASCII)

Files have character encodings. By default, FileReader uses the system default (often UTF-8). If your file uses a different encoding, you get garbage:

```java
// Specify encoding explicitly
try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream("data.txt"),
            StandardCharsets.UTF_8))) {
    String line = reader.readLine();
    System.out.println(line);
} catch (IOException e) {}
```

Specify StandardCharsets.UTF_8 explicitly for reliability. Modern Java defaults to UTF-8, which is good. But for robustness, be explicit.

---

## Slide 29: Real-World: CSV File Parser

**Visual:** CSV file with comma-separated values; parsed into list of records

```java
public List<String[]> parseCSV(String filename) throws IOException {
    List<String[]> records = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
            new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            records.add(fields);
        }
    }
    return records;
}

// Usage
List<String[]> data = parseCSV("data.csv");
for (String[] row : data) {
    System.out.println("Name: " + row[0] + ", Age: " + row[1]);
}
```

Read CSV file. Split lines by comma. Return list of records. Simple but real-world application.

---

## Slide 30: Real-World: JSON File Reader

**Visual:** JSON file structure; parsed into object representation

```java
public String readJSONFile(String filename) throws IOException {
    return Files.readString(Path.of(filename));
}

// Usage
String jsonContent = readJSONFile("config.json");
// Parse with Jackson, Gson, or built-in tools
```

Read JSON file as String. Parse with JSON libraries (Jackson, Gson). Store configuration, API responses, etc., in JSON. Common pattern in modern applications.

---

## Slide 31: Summary: File I/O Best Practices

**Visual:** Checklist of best practices with checkmarks

1. **Always use try-with-resources**: Automatic resource cleanup
2. **Use BufferedReader/Writer**: More efficient than FileReader/Writer
3. **Check existence and permissions**: Prevent exceptions
4. **Handle IOException specifically**: Catch FileNotFoundException separately
5. **Specify encoding explicitly**: UTF-8 is standard
6. **Use Files class for simple ops**: readString(), writeString() are convenient
7. **Validate file paths**: Don't allow arbitrary user input
8. **Log errors**: Always record what went wrong

---

## Slide 32: Try-With-Resources Recap

**Visual:** Try-with-resources structure showing automatic close

Try-with-resources is idiomatic Java:

```java
try (Resource resource = new Resource()) {
    // Use resource
} catch (Exception e) {
    // Handle error
}
// resource.close() called automatically
```

Works with any class implementing AutoCloseable. FileReader, FileWriter, BufferedReader, BufferedWriter all implement it. Always use this pattern. It's simpler, safer, and more concise than try-finally.

---

## Slide 33: Streams and Filters: Advanced Reading

**Visual:** Stream processing pipeline: read → filter → process

Beyond basic file reading, you can use streams:

```java
try (Stream<String> lines = Files.lines(Path.of("data.txt"))) {
    lines.filter(line -> !line.isEmpty())
         .filter(line -> !line.startsWith("#"))
         .forEach(System.out::println);
} catch (IOException e) {
    System.out.println("Error");
}
```

Files.lines() returns a Stream<String>. Filter empty lines, skip comments. Process efficiently. Modern Java approach. Prepares for Stream API (Day 8).

---

## Slide 34: Directory Operations

**Visual:** Directory structure with subdirectories; listing operations

Beyond files, work with directories:

```java
Path dir = Path.of(".");
try (Stream<Path> paths = Files.list(dir)) {
    paths.filter(Files::isRegularFile)
         .forEach(System.out::println);
} catch (IOException e) {}
```

Files.list() lists directory contents. Filter for regular files. Useful for batch operations—process all files in directory.

---

## Slide 35: Real-World: File Watcher

**Visual:** Notification arrow; file changed icon

```java
public void watchDirectory(String dir) throws IOException {
    WatchService watcher = FileSystems.getDefault().newWatchService();
    Path path = Path.of(dir);
    path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
    
    WatchKey key;
    while ((key = watcher.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
            System.out.println("File created: " + event.context());
        }
        key.reset();
    }
}
```

Watch directory for changes. Detect file creation, modification, deletion. Advanced but important for real applications (IDE file indexers, build tools watching for changes, etc.).

---

## Slide 36: Exception Handling in File Operations

**Visual:** File operation error paths with exception handling

Exception handling is critical in file operations:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        processLine(line);
    }
} catch (FileNotFoundException e) {
    System.out.println("File not found. Creating new file.");
    Files.createFile(Path.of("data.txt"));
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
    logger.error("Failed to read file", e);
}
```

Different exceptions warrant different responses. File not found? Create it. Permission denied? Log and abort. Disk full? Notify user. Handle appropriately.

---

## Slide 37: Memory Considerations: Large Files

**Visual:** Large file warning; memory buffer shown

Reading entire large files into memory is risky:

```java
// WRONG for large files
String content = Files.readString(Path.of("1GB_file.bin"));  // OutOfMemoryError!

// CORRECT for large files
try (BufferedReader reader = new BufferedReader(
        new FileReader("large_file.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        processLine(line);  // Process one line at a time
    }
}
```

For large files, read line-by-line or in chunks. Process incrementally. Don't load entire file into memory. Stream processing is essential for scalability.

---

## Slide 38: Temporary Files

**Visual:** Temporary file icon with X mark; cleanup arrow

For temporary files:

```java
Path tempFile = Files.createTempFile("prefix", ".txt");
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(tempFile.toFile()))) {
    writer.write("Temporary data");
}

// Later, delete
Files.deleteIfExists(tempFile);
```

createTempFile() creates unique temporary file. Clean up with deleteIfExists(). Useful for intermediate processing.

---

## Slide 39: File Permissions and Attributes

**Visual:** File attributes: owner, permissions, creation date, etc.

Access file attributes:

```java
Path file = Path.of("data.txt");
FileTime created = (FileTime) Files.getAttribute(file, "creationTime");
FileTime modified = (FileTime) Files.getAttribute(file, "lastModifiedTime");
Set<PosixFilePermission> perms = Files.getPosixFilePermissions(file);

System.out.println("Created: " + created);
System.out.println("Modified: " + modified);
System.out.println("Permissions: " + perms);
```

Access creation time, modification time, permissions. Useful for auditing, backup scheduling, permission validation.

---

## Slide 40: Summary: Part 2 Key Concepts

**Visual:** Summary of File, Reader/Writer, Buffered, Try-with-Resources, NIO

File class represents files and directories. FileReader/Writer for character I/O. BufferedReader/Writer for line-by-line I/O. Try-with-resources for automatic cleanup. NIO for modern convenience methods. IOException for file operation failures. FileNotFoundException for missing files. Always close resources. Always handle exceptions. Process large files incrementally.

---

## Slide 41: Closing Remarks: Professional File Handling

**Visual:** Professional development practices; best practices checklist

Professional file handling means: using try-with-resources, checking permissions, handling exceptions properly, processing large files efficiently, validating file paths, specifying character encodings, cleaning up resources. Apply these principles in every application. Files are how data persists. Handle them with care.

---
