# Week 2 - Day 7 (Tuesday) Part 2: Lecture Script
## File I/O & Resource Management — 60-Minute Verbatim Delivery

**Pacing Note:** Aim for natural conversational delivery. Timing markers every ~2 minutes. Total time approximately 60 minutes.

---

## [00:00-02:00] Introduction and Transition from Part 1

Welcome back! You've mastered exception theory. Now you'll see why it matters. File I/O is where exceptions become absolutely necessary. Every file operation can fail. You'll read files, write files, handle failures. By the end of this hour, you'll perform real-world file operations. You'll read configuration files, write logs, process data files. You'll handle errors gracefully. You'll manage resources properly. Let's dive in.

---

## [02:00-04:00] Why File I/O Matters

Computers have two kinds of memory. RAM is fast but temporary. When your program exits, RAM is cleared. Everything is lost. Disk storage is permanent. Data persists even after shutdown. File I/O connects your program to disk storage. You write data to files. Persist it. Read it back later. Every real application does this. Databases store data in files. Your browser caches pages in files. Configuration applications read settings from files. Text editors save your writing to files. File I/O is how you make your program's data survive beyond its execution.

---

## [04:00-06:00] The File Class: Representing Files

The File class represents files and directories. It doesn't read or write. It represents the file itself—its name, path, properties:

```java
File file = new File("data.txt");
System.out.println(file.getName());        // "data.txt"
System.out.println(file.getAbsolutePath()); // Full path
System.out.println(file.exists());         // Does it exist?
System.out.println(file.isFile());         // Is it a file?
System.out.println(file.length());         // Size in bytes
```

You check properties before reading or writing. Does the file exist? Do you have permissions? Is it actually a file or a directory? Prevention is better than exception handling.

---

## [06:00-08:00] File Paths: Absolute vs Relative

Paths can be absolute or relative. Absolute: "/Users/emily/Documents/data.txt". Starts from the root. Works from anywhere. Relative: "data.txt", "./subfolder/data.txt". Starts from current directory. Depends on where the program runs.

```java
File absFile = new File("/Users/emily/data.txt");  // Absolute
File relFile = new File("data.txt");  // Relative
```

For production, use absolute paths. For testing, relative paths are convenient. Be aware of the distinction.

---

## [08:00-10:00] Checking Before Reading

Before reading a file, check its properties:

```java
File file = new File("data.txt");

if (!file.exists()) {
    System.out.println("File does not exist");
    return;
}

if (!file.isFile()) {
    System.out.println("It's a directory, not a file");
    return;
}

if (!file.canRead()) {
    System.out.println("Cannot read: permission denied");
    return;
}
```

Verify the file exists, is actually a file, and is readable. This prevents exceptions. Much better than catching FileNotFoundException.

---

## [10:00-12:00] FileReader: Character-By-Character Reading

FileReader reads text files one character at a time:

```java
try (FileReader reader = new FileReader("data.txt")) {
    int character = reader.read();  // Read one character
    System.out.println((char) character);  // Print it
} catch (IOException e) {
    System.out.println("Failed to read: " + e.getMessage());
}
```

read() returns an int. It's the character code. Convert to char. If there are no more characters, read() returns -1. This signals end-of-file. Always use try-with-resources to ensure the file closes.

---

## [12:00-14:00] Reading Until End of File

To read all characters:

```java
try (FileReader reader = new FileReader("data.txt")) {
    int character;
    while ((character = reader.read()) != -1) {
        System.out.print((char) character);  // Print each character
    }
} catch (IOException e) {
    System.out.println("Failed to read");
}
```

Loop until read() returns -1 (end-of-file). Print each character. This reads the entire file. But it's tedious. Buffered streams are better.

---

## [14:00-16:00] FileWriter: Character-By-Character Writing

FileWriter writes text files:

```java
try (FileWriter writer = new FileWriter("output.txt")) {
    writer.write('H');
    writer.write('e');
    writer.write('l');
    writer.write('l');
    writer.write('o');
} catch (IOException e) {
    System.out.println("Failed to write");
}
```

write() accepts a char. Each write() call outputs one character. FileWriter buffers internally. When you close (via try-with-resources), the buffer is flushed to disk. Important: FileWriter creates a new file if it doesn't exist. If the file already exists, it overwrites it. This is usually what you want. If you want to append instead, you need a different constructor.

---

## [16:00-18:00] FileWriter: Append vs Overwrite

```java
// Overwrite existing file (default)
FileWriter writer = new FileWriter("data.txt");

// Append to existing file
FileWriter writer = new FileWriter("data.txt", true);  // true = append
```

Default behavior is overwrite. Pass true as second parameter to append. This is important for logging or accumulating data.

---

## [18:00-20:00] Buffered Streams: Much Better

FileReader and FileWriter are slow. Each read() or write() might hit the disk. Buffered streams use an internal buffer. They read/write in chunks. Much faster. BufferedReader wraps FileReader:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line = reader.readLine();  // Read entire line
    System.out.println(line);
} catch (IOException e) {
    System.out.println("Failed");
}
```

readLine() returns the next line as a String. Reads until newline or end-of-file. Returns null at end-of-file. Much cleaner than character-by-character reading. Always use Buffered streams.

---

## [20:00-22:00] Reading Multiple Lines

To read entire file line-by-line:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);  // Print each line
    }
} catch (IOException e) {
    System.out.println("Failed");
}
```

Loop while readLine() doesn't return null. Process each line. Clean and simple. This is idiomatic Java.

---

## [22:00-24:00] BufferedWriter: Writing Lines

BufferedWriter wraps FileWriter. It adds newLine():

```java
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt"))) {
    writer.write("Hello");
    writer.newLine();  // Write newline (platform-specific)
    writer.write("World");
    writer.newLine();
} catch (IOException e) {
    System.out.println("Failed");
}
```

write() writes the string. newLine() writes a newline character—platform-specific (\n on Unix, \r\n on Windows). Output file:

```
Hello
World
```

---

## [24:00-26:00] Multiple Resources with Try-With-Resources

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

Declare multiple resources separated by semicolons. Both are automatically closed when the try block exits. This is powerful and concise.

---

## [26:00-28:00] FileNotFoundException: Handling Missing Files

FileReader throws FileNotFoundException if the file doesn't exist:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("nonexistent.txt"))) {
    // ...
} catch (FileNotFoundException e) {
    System.out.println("File not found: " + e.getMessage());
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
}
```

Catch FileNotFoundException first. It's more specific than IOException. Catch IOException for other failures—permission denied, disk errors, etc.

---

## [28:00-30:00] Modern Java: Files Class

Modern Java provides convenient methods in java.nio.file.Files:

```java
import java.nio.file.Files;
import java.nio.file.Path;

// Read entire file into String
String content = Files.readString(Path.of("data.txt"));

// Write entire file
Files.writeString(Path.of("output.txt"), "Hello, World!");

// Copy file
Files.copy(Path.of("source.txt"), Path.of("dest.txt"));
```

Files class provides static convenience methods. readString() reads entire file. writeString() writes string to file. Much simpler than BufferedReader/Writer for simple operations. These methods were added in Java 11. They're idiomatic modern Java.

---

## [30:00-32:00] Traditional vs Modern I/O: Which to Use

Traditional I/O (BufferedReader/Writer): More control, flexible, handles line-by-line or character-by-character. Use for complex operations, streaming data, large files. Modern I/O (Files class): Simpler API, convenience methods. Use for simple read/write operations, small files. Both are valid. Use modern I/O when it fits. Use traditional I/O when you need more control.

---

## [32:00-34:00] Real-World Example: Configuration File

Many applications have configuration files:

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

Read configuration file with key=value pairs. Skip comments starting with #. Skip blank lines. Parse and store in Map. Return for application to use. Real-world pattern.

---

## [34:00-36:00] Real-World Example: Log File Appender

Applications log events for debugging:

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
```

Open log file in append mode. Write message with timestamp. Close. This code runs repeatedly, building a log file. Timestamps help debugging—you see the sequence of events.

---

## [36:00-38:00] Real-World Example: File Copy

Processing large files efficiently:

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
        System.out.println("File copy complete");
    }
}
```

Copy line-by-line. Report progress every 100 lines. This handles large files efficiently—doesn't load entire file into memory.

---

## [38:00-40:00] Beginner Mistake: Forgetting Try-With-Resources

Mistake one. Forgetting to close files:

```java
// WRONG
FileReader reader = new FileReader("data.txt");
int ch = reader.read();
System.out.println((char) ch);
// Forgot to close! File handle leaks
```

Without try-with-resources, you must manually close. If you forget or an exception occurs before close(), the file handle leaks. Eventually, you run out of file handles. System crashes. Modern solution:

```java
// CORRECT
try (FileReader reader = new FileReader("data.txt")) {
    int ch = reader.read();
    System.out.println((char) ch);
}  // Automatically closed
```

Try-with-resources is idiomatic. Always use it.

---

## [40:00-42:00] Beginner Mistake: Overwriting When You Meant to Append

Mistake two. Accidentally overwriting files:

```java
// WRONG: Accidentally overwrites log each time
try (FileWriter writer = new FileWriter("log.txt")) {
    writer.write("New log entry");
}

// Correct: Appends to log
try (FileWriter writer = new FileWriter("log.txt", true)) {
    writer.write("New log entry");
    writer.newLine();
}
```

FileWriter defaults to overwrite. Easy mistake—you lose previous data. Always specify true for append if that's your intent.

---

## [42:00-44:00] Beginner Mistake: Not Handling FileNotFoundException

Mistake three. Not handling missing files:

```java
// WRONG: Crashes if file doesn't exist
try (BufferedReader reader = new BufferedReader(
        new FileReader("required_file.txt"))) {
    String line = reader.readLine();
}
```

If the file doesn't exist, FileNotFoundException is thrown. Uncaught exception crashes. Better approach:

```java
// CORRECT: Check first, then read
File file = new File("required_file.txt");
if (!file.exists()) {
    System.out.println("File not found. Creating new file.");
    Files.createFile(file.toPath());
} else {
    try (BufferedReader reader = new BufferedReader(
            new FileReader(file))) {
        String line = reader.readLine();
    }
}
```

Or catch the exception:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("optional_file.txt"))) {
    String line = reader.readLine();
} catch (FileNotFoundException e) {
    System.out.println("File not found. Using default.");
}
```

---

## [44:00-46:00] Character Encodings: Important Detail

Files have character encodings—how characters are represented. By default, FileReader uses system default (usually UTF-8). If your file uses a different encoding, you get garbage:

```java
// WRONG: Might not handle encoding correctly
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line = reader.readLine();
}

// CORRECT: Specify encoding explicitly
try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream("data.txt"),
            StandardCharsets.UTF_8))) {
    String line = reader.readLine();
}
```

Specify StandardCharsets.UTF_8 explicitly. UTF-8 is standard. It handles all characters correctly. Other encodings like ASCII are limited.

---

## [46:00-48:00] Permissions Matter

You might not have permission to read or write:

```java
File file = new File("protected_file.txt");
if (!file.canRead()) {
    System.out.println("Cannot read: Permission denied");
    return;
}
if (!file.canWrite()) {
    System.out.println("Cannot write: Permission denied");
    return;
}
```

Check permissions before operating. Prevents exceptions. Also validate file paths—don't let users specify arbitrary paths. They might access sensitive files.

---

## [48:00-50:00] Memory Considerations: Large Files

For large files, don't read entire file into memory:

```java
// WRONG for large files: 1GB file causes OutOfMemoryError
String content = Files.readString(Path.of("1GB_file.txt"));

// CORRECT: Process line-by-line
try (BufferedReader reader = new BufferedReader(
        new FileReader("1GB_file.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        processLine(line);  // Process one line at a time
    }
}
```

Stream processing is essential for scalability. Read and process incrementally. Never load huge files into memory.

---

## [50:00-52:00] Practical Pattern: CSV File Parser

Common use case—parsing CSV (comma-separated values) files:

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
List<String[]> data = parseCSV("sales.csv");
for (String[] row : data) {
    System.out.println("Product: " + row[0] + ", Quantity: " + row[1]);
}
```

Read CSV line-by-line. Split by comma. Store records. Return list. Simple but practical.

---

## [52:00-54:00] Practical Pattern: JSON File Reader

JSON is standard for configuration and data interchange:

```java
public String readJSONFile(String filename) throws IOException {
    return Files.readString(Path.of(filename));
}

// Usage
String jsonContent = readJSONFile("config.json");
// Parse with Jackson, Gson, or other libraries
```

Read JSON file as String. Parse with JSON libraries (Jackson, Gson). Store configuration, API responses, etc. Common in modern applications.

---

## [54:00-56:00] Summary: File I/O Best Practices

Let me recap. One: always use try-with-resources. Automatic cleanup. Two: use BufferedReader/Writer, not FileReader/Writer directly. Much faster. Three: check existence and permissions before operating. Prevents exceptions. Four: handle IOException and FileNotFoundException appropriately. Five: specify character encoding explicitly (UTF-8). Six: use Files class for simple operations. Seven: process large files incrementally. Never load entire file into memory. Eight: log and handle errors gracefully. Always inform the user if something goes wrong.

---

## [56:00-58:00] Exception Handling Tie-In

Remember Part 1? Exception handling. File I/O is where it matters:

```java
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        processLine(line);
    }
} catch (FileNotFoundException e) {
    System.out.println("File not found. Using defaults.");
    useDefaults();
} catch (IOException e) {
    System.out.println("IO error: " + e.getMessage());
    logger.error("Failed to read file", e);
}
```

Try-with-resources closes automatically. Multiple exceptions are handled appropriately. Each exception warrants a different response. This is professional error handling.

---

## [58:00-60:00] Closing Remarks: Professional File Handling

File I/O is fundamental. Every real application reads and writes files. Master the patterns. Use try-with-resources. Handle exceptions properly. Process large files efficiently. Validate paths and permissions. Specify encodings. Professional developers handle files carefully. Files are how data persists. Get this right, and your applications are robust. That's today. You've learned exception handling and file I/O. You can now read configuration, write logs, process data files. Your programs are becoming real. Excellent work. See you tomorrow for lambdas and streams.

---
