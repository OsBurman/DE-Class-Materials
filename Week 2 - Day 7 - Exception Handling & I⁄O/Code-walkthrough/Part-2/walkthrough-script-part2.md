# Day 7 — Part 2: File I/O
## Walkthrough Script

**Files covered:**
1. `01-file-io-basics.java`
2. `02-buffered-streams.java`
3. `03-try-with-resources-and-text-files.java`

**Estimated time:** ~90 minutes  
**Setup:** All demos write to a temp directory (`/tmp/day7-io-demo/`). Run each file's `main()` in order.

---

## OPENING (3 min)

"Welcome to Part 2. We've spent the morning understanding exceptions. Now we're going to put them to work in the most common place you'll encounter checked exceptions: reading and writing files.

Every Java application touches files at some point — reading config, writing logs, processing CSVs, generating reports. Today you'll learn how Java's I/O system works from the ground up, and you'll see why try-with-resources is the modern standard.

Three files today. File one: the raw building blocks — `File`, `FileReader`, `FileWriter`. File two: buffered streams and why they matter for performance. File three: try-with-resources — the elegant solution to the resource management problem."

---

## FILE 1 — `01-file-io-basics.java`

---

### OPENING COMMENT — Stream Types (3 min)

[ACTION] Open `01-file-io-basics.java` and read the top comment.

"Java I/O has two main families.

Character streams — `Reader` and `Writer` subclasses — work with text. They handle the encoding for you: bytes on disk become Java chars automatically. Use these for `.txt`, `.csv`, `.json`, `.xml` — anything human-readable.

Byte streams — `InputStream` and `OutputStream` subclasses — work with raw bytes. Use these for images, PDFs, compiled `.class` files, anything binary.

Today we start with character streams because most of what you'll do as a backend developer involves text: reading JSON, writing logs, parsing CSVs.

And before we read any content, we need to talk about `java.io.File`."

---

### SECTION 1 — java.io.File (8 min)

[ACTION] Scroll to `demonstrateFileClass()`.

"Before any I/O happens, you need to know where the file lives. That's `java.io.File`.

⚠️ WATCH OUT: `File` does NOT open a file, read it, or write it. It's just a representation of a path. Creating a `File` object doesn't touch the disk at all.

I'm creating a `File` object pointing at a directory: `/tmp/day7-io-demo`. Then I call `demoDir.mkdirs()` — note the plural — it creates the directory AND all its parents if they don't exist. `mkdir()` without the 's' only creates the final directory and fails if the parent doesn't exist.

[ASK] What does `demoDir.exists()` return if the directory was just created by mkdirs()?

Now I create a `File` object for `sample.txt` inside that directory. Still no file on disk.

`createNewFile()` — this is the call that actually creates an empty file on disk. It returns `true` if the file was newly created, `false` if it already existed. This is an atomic check-and-create — safer than checking `exists()` first and then creating.

Then I show you the metadata you can inspect: absolute path, name, parent, isFile, isDirectory, canRead, canWrite, length.

Notice `length()` returns 0 right now because we just created an empty file.

The directory listing with `listFiles()` returns an array of `File` objects — one for each entry. I'm using a ternary to label directories with `[DIR]` and files with `[FILE]`.

[ASK] What does `listFiles()` return if the directory is empty? What if the path doesn't exist?

Answer: empty array if empty, `null` if the path doesn't exist or isn't a directory. Always null-check before iterating."

---

### SECTION 2 — FileWriter (8 min)

[ACTION] Scroll to `demonstrateFileWriter()`.

"Now we write. `FileWriter` takes a `File` object (or a String path) and opens it for writing. If the file doesn't exist, it's created. If it does exist — by default, it's OVERWRITTEN from the start. Previous content is gone.

Look at the structure. I declare `writer` as null outside the try. I initialize it inside the try. In the finally block I check if it's not null and close it.

Why close it? FileWriter buffers writes in an OS-level buffer. If you don't call `close()` — which calls `flush()` first — some of your data might never reach disk. It just evaporates.

`write(String)` writes raw characters. No newline is added automatically. I add `\n` manually.

Then I show append mode: `new FileWriter(outputFile, true)`. That `true` means 'append — don't overwrite.' Every call to this constructor in append mode will add to the end of the existing file.

[ASK] When would you want append mode vs overwrite mode?

Append: log files — you want to accumulate entries. Overwrite: report generation — you always want a fresh file."

---

### SECTION 3 — FileReader (8 min)

[ACTION] Scroll to `demonstrateFileReader()`.

"Now we read. `FileReader` opens a file and lets us read it character by character.

The key method is `read()`. It returns an `int` — not a `char`. It returns the character as a Unicode code point integer. And critically: it returns `-1` when you've reached the end of file.

That's our loop condition: `while ((charCode = reader.read()) != -1)`. Assign the return value to `charCode`, and if it's not -1, we have a character. Cast it to `(char)` to get the actual character.

[ASK] Why does read() return an int instead of a char?

Because `char` can only hold Unicode values 0–65,535. The int gives room for the special EOF signal of -1, which can't be represented as a char. Without that, how would we signal end-of-file?

This is perfectly correct but inefficient for large files — one system call per character. Let's look at the buffer approach."

---

### SECTION 4 — char[] buffer (5 min)

[ACTION] Scroll to `demonstrateBufferRead()`.

"The overloaded `read(char[] buffer)` method reads up to `buffer.length` characters in one call and returns the number of characters actually read. On the last read of a file, you might get fewer than `buffer.length` characters.

⚠️ WATCH OUT: The critical line is `sb.append(buffer, 0, charsRead)`. NOT `sb.append(buffer)`. If the last read returns only 10 characters but your buffer is 64 chars, the remaining 54 slots still hold whatever was in memory before. Appending the whole buffer would add garbage."

---

### SECTION 5 — FileNotFoundException (3 min)

[ACTION] Scroll to `demonstrateFileNotFoundException()`.

"What happens when you try to open a file that doesn't exist? `FileNotFoundException` — a checked exception. The compiler won't let you create a `FileReader` without handling it.

Notice the catch block hierarchy — `FileNotFoundException` is caught first (more specific), then `IOException` handles any close errors. `FileNotFoundException` IS-A `IOException`, so if I had only the `IOException` catch, it would catch both. But catching them separately lets me give different messages."

---

### SECTIONS 6 & 7 — char array write, file operations (4 min)

[ACTION] Scroll through remaining sections quickly.

"Two more things. `FileWriter.write(char[], offset, length)` — you can write from a portion of a char array. Same offset/length pattern we saw with read().

`File.delete()` and `File.renameTo()` — basic file operations. ⚠️ `renameTo()` is platform-dependent and can fail silently if source and destination are on different file systems. The Java NIO `Files.move()` method is more reliable — we'll see that in file 3."

---

### TRANSITION to file 2 (1 min)

"File, FileReader, FileWriter — you now know the raw building blocks. But reading a file one character at a time with `FileReader.read()` is painful and slow.

BufferedReader and BufferedWriter are the wrappers that give you two huge improvements: line-by-line reading with `readLine()`, and dramatically better performance. Let's look."

---

## FILE 2 — `02-buffered-streams.java`

---

### OPENING COMMENT — Why Buffering (5 min)

[ACTION] Open `02-buffered-streams.java` and read the top comment block.

"Every time `FileReader.read()` executes, Java potentially makes a system call — a request to the OS kernel to get a character from the file. System calls are slow. They cross the user-space/kernel-space boundary.

BufferedReader maintains an 8 KB buffer in memory. On the first `readLine()` call, it reads up to 8 KB of data from the file in ONE system call and stores it in that buffer. Subsequent calls to `readLine()` are served from the buffer, in memory, at RAM speed — no system calls.

Same idea for BufferedWriter. Writes accumulate in the buffer. When the buffer is full (or you explicitly flush or close), everything goes to disk in one system call.

The wrapping pattern — called the 'Decorator pattern' — looks like this: `new BufferedReader(new FileReader(file))`. You're stacking capabilities."

---

### SECTION 1 — BufferedWriter (8 min)

[ACTION] Scroll to `demonstrateBufferedWriter()`.

"I'm writing a CSV of employees. `BufferedWriter` wraps `FileWriter`.

`bw.newLine()` — this is the key method that `FileWriter` doesn't have. On Windows it writes `\r\n`. On Mac/Linux it writes `\n`. Your code is automatically portable.

[ASK] What's the difference between flush() and close()?

`flush()` — forces everything in the buffer to disk. The stream stays open. Use this if you want to write incrementally and ensure data is saved at checkpoints — like writing progress in a long-running process.

`close()` — calls flush() first, then releases the file handle. You must always close.

In the finally block I call `bw.close()`. This is still the manual pattern. File 3 will replace this with try-with-resources."

---

### SECTION 2 — BufferedReader.readLine() (8 min)

[ACTION] Scroll to `demonstrateBufferedReader()`.

"This is the pattern you'll use 90% of the time when reading text files.

`BufferedReader br = new BufferedReader(new FileReader(file))`

Then: `while ((line = br.readLine()) != null)`. `readLine()` returns one full line as a String, with the newline character stripped. Returns null at EOF.

[ASK] In `FileReader.read()` we checked for -1. Why does readLine() return null instead?

Because it returns a `String`, which can be null. Characters are primitives — null isn't a valid value. So the convention is different.

I'm parsing the CSV line by line, splitting on comma, and computing the average salary. This is realistic — you'd do exactly this to process a data file.

I track `lineNumber` to skip the header. In real code you'd probably use `skip(1)` on a stream — which we'll see in section 3."

---

### SECTION 3 — BufferedReader.lines() (5 min)

[ACTION] Scroll to `demonstrateBufferedReaderLinesStream()`.

"Java 8 added `BufferedReader.lines()` which returns a `Stream<String>`. Each element is one line.

This integrates directly with the Streams API — we'll cover that in depth on Day 8. For now, see how clean this is: `br.lines().skip(1).filter(...).count()`. Much more readable than a while loop for simple transformations.

⚠️ WATCH OUT: The stream is lazy — it reads from the file as you consume it. The `BufferedReader` must remain open while the stream is being consumed. I'm using try-with-resources here (a preview of file 3) to ensure it closes after the stream is done."

---

### SECTION 4 — BufferedInputStream/OutputStream (7 min)

[ACTION] Scroll to `demonstrateByteStreams()`.

"For binary files — images, PDFs, any non-text content — you use byte streams.

`BufferedInputStream` wraps `FileInputStream`. `BufferedOutputStream` wraps `FileOutputStream`. Same decorator pattern, different base types.

I'm doing a file copy: read into a 4 KB `byte[]` buffer, write it out. The loop: `while ((bytesRead = bis.read(buffer)) != -1)`. Same pattern as `FileReader.read()` — returns the number of bytes read, or -1 at EOF.

⚠️ WATCH OUT: The close order in finally matters. Close in REVERSE order of opening. `bos` was opened second, so close it first. Why? Because `bos.close()` calls `flush()` — you want all bytes written before closing the input stream.

Also note `bos.flush()` before the finally block. I'm explicit about it here for clarity, though `close()` would call it anyway."

---

### SECTION 5 — PrintWriter (5 min)

[ACTION] Scroll to `demonstratePrintWriter()`.

"`PrintWriter` is `System.out` but pointing at a file. It gives you `println()`, `printf()`, and `print()`.

I'm wrapping it around a `BufferedWriter`: `new PrintWriter(new BufferedWriter(new FileWriter(file)))`. Three layers deep — welcome to the decorator pattern.

⚠️ WATCH OUT: `PrintWriter` silently swallows `IOException`. It doesn't rethrow. Always call `checkError()` after writing to see if anything went wrong. This is a legacy design choice — it mirrors `System.out`, which also swallows errors. For production code, prefer `BufferedWriter` and handle IOException explicitly."

---

### SECTION 6 — Performance demo (5 min)

[ACTION] Scroll to `demonstrateBufferingPerformance()`.

"Let's make the performance difference concrete.

I write 10,000 lines twice — once with raw `FileWriter`, once with `BufferedWriter`. Run this and look at the timings.

[ASK] Before I run — how much faster do you expect BufferedWriter to be?

The difference depends on disk speed, but BufferedWriter is typically 5x to 50x faster because it collapses thousands of OS write calls into a handful.

Rule: always use BufferedWriter/BufferedReader for text I/O. There's no reason not to."

---

### TRANSITION to file 3 (1 min)

"You've been watching me write `try { ... } finally { if (br != null) br.close(); }` over and over. That's verbose, error-prone, and has a subtle bug with suppressed exceptions.

Java 7 gave us try-with-resources to solve this. Let's see it."

---

## FILE 3 — `03-try-with-resources-and-text-files.java`

---

### OPENING COMMENT (4 min)

[ACTION] Open `03-try-with-resources-and-text-files.java` and read the top comment.

"Here's the problem we've been living with: manual finally requires 6 lines of boilerplate per resource. With multiple resources it's even worse — nested try-finally blocks.

But there's a subtler problem: if the try block throws an exception, AND then `close()` in finally also throws an exception, the finally exception overwrites the original one. You lose the exception that tells you what actually went wrong. That's a real debugging nightmare.

try-with-resources solves both: less code, and suppressed exceptions are attached to the original one instead of lost."

---

### SECTION 1 — AutoCloseable (8 min)

[ACTION] Scroll to `DatabaseConnection` and `demonstrateAutoCloseable()`.

"Any class that implements `AutoCloseable` can be used in try-with-resources. `AutoCloseable` has exactly one method: `close()`.

I've built a `DatabaseConnection` class that prints when it opens and closes. Watch.

`try (DatabaseConnection conn = new DatabaseConnection(...))` — the variable is declared inside the parentheses. It's opened at the start of the try block. And when execution leaves the try block — normally or via exception — `conn.close()` is called automatically.

[ASK] What is the scope of `conn`? Can I use it after the try block?

No. The variable `conn` only exists inside the try block. After it, the connection is closed and the reference is gone. This is intentional — it prevents you from accidentally using a closed resource.

Then I show the equivalent manual finally pattern. Same result, four times the code."

---

### SECTION 2 — Single Resource (5 min)

[ACTION] Scroll to `demonstrateSingleResource()`.

"Clean, minimal. One resource. No finally. No null check.

I write a file with `try (FileWriter fw = ...)`, then read it with `try (FileReader fr = ...)`. Each try block is three lines: open, use, close brace. That's it.

[ASK] If `fr.read()` throws an IOException mid-way through reading, is the FileReader still closed?

Yes. Always. That's the guarantee. Even if an exception is thrown anywhere inside the try block, the resource is closed before the exception propagates."

---

### SECTION 3 — Multiple Resources (6 min)

[ACTION] Scroll to `demonstrateMultipleResources()`.

"Multiple resources in one try. Syntax: semicolons between declarations inside the parentheses.

`try (BufferedReader br = ...; BufferedWriter bw = ...)`

The JVM closes them in REVERSE order — `bw` (declared second) is closed first, then `br`. LIFO order.

This is the standard pattern for file transformation: read from source, transform, write to destination. Both streams are guaranteed to be closed cleanly.

[ASK] Why do we close the writer before the reader?

Because the writer needs to flush before we're done. Closing the writer first ensures all data is on disk before we release the reader."

---

### SECTION 4 — Suppressed Exceptions (8 min)

[ACTION] Scroll to `FlickyResource` and `demonstrateSuppressedExceptions()`.

"This is an advanced topic but worth understanding. Let's set the scene.

`FlickyResource` throws from both `use()` and `close()`. What happens?

With try-with-resources: `use()` throws. Java tries to close the resource. `close()` also throws. Java suppresses the close exception and attaches it to the original exception. The original exception propagates to the catch block.

In the catch block: `e.getMessage()` shows 'Exception from use()' — the original. `e.getSuppressed()` gives an array of suppressed exceptions — we see 'Exception from close()' there.

[ASK] What would happen with the manual finally pattern instead?

The finally exception would completely replace the original. You'd catch 'Exception from close()' and never know that `use()` also threw. Debugging this in production would be a nightmare.

try-with-resources wins here. Always prefer it for any `AutoCloseable` resource."

---

### SECTION 5 — Write then Read a Config File (8 min)

[ACTION] Scroll to `demonstrateWriteAndReadTextFile()`.

"This is a realistic end-to-end example: write a properties-format config file, then read it back and parse it into a Map.

Writing: `BufferedWriter` in try-with-resources. I use `bw.newLine()` for portable line endings.

Reading: `BufferedReader` in try-with-resources. I skip blank lines and lines starting with `#` (comments). For each key=value line I find the `=` sign, split on it, and put into a `LinkedHashMap` to preserve insertion order.

Then I use the config values. `Boolean.parseBoolean(config.get('cache.enabled'))` — parsing a String to a boolean.

This is the pattern you'll use for `.properties` files, `.env` files, any text-based configuration. Java's built-in `Properties` class does something very similar, but understanding the manual way first helps you when you encounter non-standard formats."

---

### SECTION 6 — Appending (Log file pattern) (5 min)

[ACTION] Scroll to `demonstrateAppendToFile()` and `appendLogEntry()`.

"A log file is the canonical use case for append mode. Every time the application logs something, it opens the file in append mode, writes one line, and closes it.

`appendLogEntry()` is a helper method that wraps this pattern: `new FileWriter(logFile, true)`. The `true` is append mode. Each call adds one timestamped line.

In production you'd never open and close a log file for every message — you'd keep a BufferedWriter open for the lifetime of the application. But this pattern is perfect for learning and for low-frequency writes."

---

### SECTION 7 — java.nio.file.Files (7 min)

[ACTION] Scroll to `demonstrateNioFilesHelpers()`.

"Java 7 added the NIO.2 API — `java.nio.file.Files` — which provides elegant one-liners for common operations.

`Files.write(path, lines, charset)` — writes a list of strings to a file in one call.

`Files.readAllLines(path, charset)` — reads the entire file into a `List<String>`.

`Files.readString(path, charset)` — Java 11, reads the entire file into one String.

`Files.writeString(path, content, charset)` — Java 11, writes a String to a file.

These all handle open-read/write-close internally with try-with-resources.

⚠️ WATCH OUT: `readAllLines()` and `readString()` load the ENTIRE file into memory. If you're reading a 2 GB log file, that's 2 GB of RAM consumed instantly — you'll get an OutOfMemoryError. For large files, always use `BufferedReader.lines()` which is lazy — it only holds one line in memory at a time.

But for config files, small CSVs, templates — anything under a few MB — these one-liners are perfect."

---

### SECTION 8 — Full CSV Pipeline (6 min)

[ACTION] Scroll to `demonstrateProcessCsvPipeline()`.

"Here's the complete read-transform-write pipeline. Read employees.csv, keep only Engineering employees, write to engineering-team.csv.

Both the reader and writer are declared in the same try-with-resources block.

I read the header first with a separate `br.readLine()` call to preserve it in the output.

Then I loop through the data rows, filter by department, and write matching rows.

When the try block exits, both streams are closed. Clean and safe.

[ASK] What happens if the output file already exists? Does FileWriter in default mode overwrite it?

Yes — `new FileWriter(outputCsv)` without `true` always overwrites. If you want to be safe, check `outputCsv.exists()` first and handle it, or use `StandardOpenOption.CREATE_NEW` with NIO if you want to fail on existing files."

---

### PART 2 SELF-CHECK (5 min)

[ASK] Self-check questions:

1. What does `java.io.File` represent, and what does `createNewFile()` actually do?
2. What's the difference between `FileWriter(file)` and `FileWriter(file, true)`?
3. Why does `FileReader.read()` return `int` instead of `char`?
4. What's the difference between `BufferedWriter.flush()` and `BufferedWriter.close()`?
5. In a try-with-resources with multiple resources, in what order are they closed?
6. What interface must a class implement to be used in try-with-resources?
7. `Files.readAllLines()` vs `BufferedReader.lines()` — when would you choose each?
8. What are suppressed exceptions, and why does try-with-resources handle them better than manual finally?
9. What does `bw.newLine()` do differently from `bw.write('\n')`?
10. Write the one-liner to read an entire text file into a String using the NIO.2 API.

---

### DAY 7 WRAP-UP (3 min)

"You've covered a lot today. Let me summarize the big ideas.

**Exceptions:**
- Java has two kinds — checked (compiler-enforced) and unchecked (runtime bugs).
- try-catch-finally is the foundation. Most-specific catch first.
- Custom exceptions give your code domain meaning and help callers respond intelligently.
- Always chain exceptions with the cause. Never swallow the original.

**File I/O:**
- `File` = path. `FileReader`/`FileWriter` = character streams. Always close them.
- Wrap in `BufferedReader`/`BufferedWriter` for line-by-line access and performance.
- Use try-with-resources — it's shorter, safer, and handles suppressed exceptions correctly.
- For quick operations, `java.nio.file.Files` gives you clean one-liners.

[ASK] Final question: You're writing a method that reads a JSON config file and parses it. The file might not exist, the JSON might be malformed, and you want to log any errors. Which exception types would you use? How would you structure the try-catch? Would you use a custom exception?

Take a minute to discuss with the person next to you.

Great work today. Tomorrow we dive into Lambdas and Streams — where you'll use `BufferedReader.lines()` constantly. See you then."

---

*End of Part 2 Walkthrough Script*
