# Java Exception Handling & I/O
### A Complete Guide for New Java Developers

---

## SLIDE 1: Title Slide
**Content:** "Java Exception Handling & I/O" | Subtitle: "A Complete Guide for New Java Developers" | Topics listed at bottom: Exception Hierarchy • try-catch-finally • Custom Exceptions • File I/O • Buffered Streams • try-with-resources

**SCRIPT:**
Good [morning/afternoon] everyone. Today we're covering one of the most practically important topics in Java: Exception Handling and File I/O.
Every real program has to deal with things going wrong — files not found, network failures, bad user input. Exception handling is Java's built-in system for managing these situations gracefully instead of crashing. And File I/O is how your program reads and writes data that persists beyond a single run.
By the end of today you'll be able to handle errors like a professional and read and write text files confidently. Let's go.

---

## SLIDE 2: Agenda
**Content:** Numbered list with time estimates — (1) Exception Hierarchy – 10 min, (2) try-catch-finally – 15 min, (3) Custom Exceptions & Throwing – 10 min, (4) File I/O Basics – 10 min, (5) Buffered Streams & try-with-resources – 10 min, (6) Putting It All Together – 5 min

**SCRIPT:**
Here's the roadmap. Six sections, each building on the last. We start with understanding the exception system itself, then learn to handle exceptions, create our own, and finish by putting it all together with actual file reading and writing. Any quick questions before we start? Great — let's go.

---

# SECTION 1: Exception Hierarchy (~10 minutes)

## SLIDE 3: Section Divider
**Content:** Large text — "Section 1: Exception Hierarchy" | Subtitle: "Understanding Checked vs. Unchecked Exceptions"

---

## SLIDE 4: What Is an Exception?
**Content:** Bullet points —
- An exception is an event that disrupts the normal flow of execution
- When an error occurs, Java creates an Exception object and "throws" it
- If not handled, the program terminates with a stack trace
- Why handle them? — Prevent crashes, give meaningful error messages, keep error-handling code separate from normal logic

**SCRIPT:**
Raise your hand if you've ever seen a Java program spit out a huge red error and stop running. Right, almost everyone. That's an unhandled exception.
An exception is simply an object that represents "something went wrong." When Java hits a problem — say you try to open a file that doesn't exist — it creates an exception object and throws it, hoping something in your code will catch it. If nothing does, it bubbles all the way to the JVM, which prints that stack trace and stops the program.
Why do we care? Three reasons. We don't want programs crashing on users. We want meaningful error messages, not internal Java gibberish. And we want our error-handling code separated from our normal logic so it stays clean and readable.
Quick question before we go further — what's a situation from real life where a program might fail that isn't actually a bug in your code? Think about it. [Take 2-3 answers — disk full, no internet, wrong password are good ones.] Exactly. Those are the things exception handling is built for.

---

## SLIDE 5: The Java Exception Hierarchy
**Content:** Visual tree diagram —
- Top: Throwable
- Branches to: Exception (left) and Error (right)
- Under Exception: Checked Exceptions (left branch) and RuntimeException (right branch)
- Under Error: examples like OutOfMemoryError, StackOverflowError
- Under Checked: IOException, SQLException, FileNotFoundException
- Under RuntimeException: NullPointerException, ArrayIndexOutOfBoundsException, IllegalArgumentException
- Color coding: Checked = green, RuntimeException = orange, Error = red

**SCRIPT:**
Look at this tree. At the very top is Throwable — the root of everything throwable in Java. It has two branches.
The left branch is Exception. This is what we deal with as programmers — things that go wrong that a well-written program should handle.
The right branch is Error. Errors represent serious problems inside the JVM itself — running out of memory, stack overflow. You generally don't catch Errors. If the JVM is out of memory, there's not much you can do programmatically.
Now inside Exception, there's another critical split: RuntimeException and everything under it, versus everything else directly under Exception. We'll cover exactly what that split means on the next slide.
[Trace the tree on screen as you speak. Point to specific classes.]

---

## SLIDE 6: Checked vs. Unchecked — Side by Side
**Content:** Two columns —

**Checked Exceptions:** Must be handled at compile time | Compiler forces you to catch or declare | Represent recoverable conditions | Examples: IOException, SQLException, FileNotFoundException

**Unchecked Exceptions:** Not enforced by compiler | Subclasses of RuntimeException | Represent programming errors | Examples: NullPointerException, ArrayIndexOutOfBoundsException, ArithmeticException

**SCRIPT:**
Here's the most important distinction in Java exception handling — burn it into your memory.
Checked exceptions — the compiler forces you to deal with them. If you call a method that might throw a checked exception and you don't handle it, your code won't compile. The compiler is saying: "I know this can go wrong. Deal with it before I let you run." The classic example is IOException — try to open a file, and the compiler requires you to acknowledge it might fail.
Unchecked exceptions — the compiler doesn't care. These are subclasses of RuntimeException and usually represent bugs: you used a null reference, you went past the end of an array, you divided by zero. These are things you fix in your code, not things you wrap in try-catch.
The rule: RuntimeException subclasses are unchecked. Everything else directly under Exception is checked.

Let me do a quick check. I'll name an exception, you tell me: checked or unchecked?
- IOException — [pause] — checked. It extends Exception directly, not RuntimeException.
- NullPointerException — [pause] — unchecked. It extends RuntimeException.
- FileNotFoundException — [pause] — checked. It's a subclass of IOException, which is checked.

Good. That's the foundation. Let's now learn what to actually do with them.

---

# SECTION 2: try-catch-finally (~15 minutes)

## SLIDE 7: Section Divider
**Content:** "Section 2: try-catch-finally" | Subtitle: "Handling exceptions gracefully"

---

## SLIDE 8: The try-catch-finally Structure
**Content:** Code block —
```java
try {
    // Code that might throw an exception
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // Handle the specific exception
    System.out.println("Cannot divide by zero: " + e.getMessage());
} catch (Exception e) {
    // Catch-all — always last
    System.out.println("An error occurred: " + e.getMessage());
} finally {
    // ALWAYS runs — cleanup code goes here
    System.out.println("This always executes!");
}
```
> Note: "finally always runs — whether an exception occurred or not"

**SCRIPT:**
Here's the structure you'll write hundreds of times as a Java developer. Three parts.
The try block contains the code that might throw an exception. Think of it as: "I'm going to try this risky thing."
The catch block is your response if it goes wrong. You declare which exception type you're catching in the parentheses — like a parameter. You can have multiple catch blocks for different exception types.
The finally block — this is the one students forget — always runs. Whether an exception was thrown or not, whether it was caught or not. This is where cleanup code lives. Closing a file, releasing a connection, resetting some state. If you need something to happen no matter what, it goes in finally.
Let me point out something important in this code: we catch ArithmeticException first, then the broader Exception. The order matters — I'll explain why in a moment.
Also notice e.getMessage() — every exception has a message. You can retrieve it and display it or log it. Always do something useful with e in your catch block.

---

## SLIDE 9: Execution Flow Diagram
**Content:** Flowchart showing two paths —
- **Path A (no exception):** try block runs → skip catch → finally runs → continue
- **Path B (exception thrown):** try block starts → exception thrown → execution stops in try → matching catch runs → finally runs → continue
- Note: if no catch matches, finally still runs, then exception propagates up

**SCRIPT:**
Let's trace through the two scenarios.
Scenario A — no exception. The try block runs completely. All catch blocks are skipped. Finally runs. Program continues. Simple.
Scenario B — an exception IS thrown. Execution in the try block stops immediately at the line that threw. Java looks for a matching catch block from top to bottom. If it finds one, that catch runs. Then finally runs. Program continues after the whole structure.
There's a third scenario worth knowing: what if no catch block matches? The exception is still uncaught. But — and this is key — finally still runs. Then the exception propagates up to whoever called this method.
Three rules to memorize right now:
- One — catch blocks are checked in order, top to bottom.
- Two — more specific exceptions must come before more general ones.
- Three — a try must have at least one catch OR a finally. You can't have a bare try by itself.

---

## SLIDE 10: Multiple catch Blocks — Order Matters
**Content:** Two code blocks side by side —

**✅ CORRECT — specific first:**
```java
try {
    FileReader fr = new FileReader("data.txt");
    int data = fr.read();
} catch (FileNotFoundException e) {    // Most specific FIRST
    System.err.println("File not found: " + e.getMessage());
} catch (IOException e) {              // Less specific SECOND
    System.err.println("I/O error: " + e.getMessage());
} finally {
    System.out.println("Cleanup done.");
}
```

**❌ WRONG — general first (compile error):**
```java
try {
    FileReader fr = new FileReader("data.txt");
    int data = fr.read();
} catch (IOException e) {              // TOO BROAD — catches everything below it
    System.err.println("I/O error: " + e.getMessage());
} catch (FileNotFoundException e) {    // COMPILE ERROR: Exception already caught
    System.err.println("File not found: " + e.getMessage());
}
```
> ⚠️ "FileNotFoundException IS an IOException — always catch the child class first. Java will give you a compile error if you get this wrong."

**SCRIPT:**
Here's the mistake that trips up almost every beginner. In the correct version, FileNotFoundException comes first, IOException comes second. Why does the order matter?
Because FileNotFoundException is a subclass of IOException. It IS an IOException. If you flip the order and put IOException first, it would catch FileNotFoundException too — because FileNotFoundException satisfies the IOException type. The second catch would be unreachable.
The broken version on the right is exactly that mistake. Java actually gives you a compile error: "Exception already caught." That error is the compiler telling you your order is wrong — FileNotFoundException can never be reached because IOException already covers it.
The rule: most specific first, most general last. If you include a bare "catch Exception" as a safety net, it always goes at the very bottom.
Any questions about the order before we move on? [Take questions.] Good. Let's talk about creating our own exceptions.

---

# SECTION 3: Custom Exceptions & Throwing (~10 minutes)

## SLIDE 11: Section Divider
**Content:** "Section 3: Custom Exceptions & Throwing" | Subtitle: "Creating exceptions that mean something to your application"

---

## SLIDE 12: Why Create Custom Exceptions?
**Content:** Bullet points —
- Built-in exceptions are generic — they don't describe your problem domain
- Custom exceptions make your code self-documenting
- Callers can catch your specific exception separately from others
- You can add custom fields (error codes, context data)
- Two options: extend Exception → Checked | extend RuntimeException → Unchecked

**SCRIPT:**
Imagine you're writing a banking app and a user tries to withdraw more than their balance. What exception do you throw? You could throw a generic RuntimeException with a message string... but then callers can't distinguish a bank error from any other runtime problem.
Instead you create InsufficientFundsException. Now callers can specifically catch that one case. They can access extra data you stored on it — like exactly how much money was short. Your code becomes self-documenting: the exception name tells the whole story.
Two choices when creating custom exceptions:
- Extend Exception — your exception is checked. Callers must handle it at compile time.
- Extend RuntimeException — your exception is unchecked. Optional to handle.

When do you use each? Use checked when callers can reasonably be expected to recover from the error — like "insufficient funds," where the UI can prompt the user to add money. Use unchecked for programming errors or conditions where recovery is unusual.

---

## SLIDE 13: Creating a Custom Exception
**Content:** Code block —
```java
// Checked custom exception
public class InsufficientFundsException extends Exception {
    private double amount;

    public InsufficientFundsException(double amount) {
        super("Insufficient funds: need $" + amount + " more");
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}

// Unchecked custom exception
public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException(String msg) { super(msg); }
}
```

**SCRIPT:**
Creating a custom exception is surprisingly simple. You extend the right parent class, write a constructor, and call super() with your message.
That super() call is critical. It's what makes getMessage() work — which is what callers will use to retrieve the error description. Don't skip it.
Notice we also added a private field — amount. This stores exactly how much money was short. That's the power of custom exceptions: you can attach domain-specific data that callers can retrieve with a getter. When someone catches InsufficientFundsException, they can call getAmount() and display a precise message to the user.
The unchecked version at the bottom is even simpler — one-liner constructor, calls super with the message. That's really all you need in most cases.
[Ask the class] If you were writing a student grading system, what custom exceptions might make sense? [Take a couple answers — InvalidGradeException, StudentNotFoundException.] Exactly. That's the mindset: what domain-specific things can go wrong?

---

## SLIDE 14: Throwing and Catching Custom Exceptions
**Content:** Code block —
```java
public void withdraw(double amount) throws InsufficientFundsException {
    if (amount > balance) {
        double shortage = amount - balance;
        throw new InsufficientFundsException(shortage);
    }
    balance -= amount;
}

// Calling code
try {
    account.withdraw(500);
} catch (InsufficientFundsException e) {
    System.out.println(e.getMessage());
    System.out.println("Short by: $" + e.getAmount());
}
```
> **Callout box:** "The throws chain — if a caller doesn't catch this, they must also declare `throws InsufficientFundsException` in their own method signature, passing the responsibility further up. This continues until something catches it or it reaches main()."

**SCRIPT:**
Here's how you use that exception. In the withdraw method, we check the condition. When it fails, we use the throw keyword — no 's' — followed by a new exception instance. The moment Java hits that throw, execution stops in that method and the exception propagates.
Notice the method signature says throws InsufficientFundsException — with an 's'. That's the declaration, telling callers "hey, this method might throw this."
Here's something important about what happens next: when a caller sees a checked exception declared on a method, they have exactly two choices. Either they catch it themselves — like the calling code shown here — or they declare throws in their own method signature and let it keep propagating up the call stack. This chain continues until something catches it or it reaches the top. If nothing ever catches a checked exception, your program won't even compile.
In the calling code, we catch it specifically and call both getMessage() and our custom getAmount(). That's the advantage of custom exceptions — you get exactly the information you need.

---

## SLIDE 15: throw vs. throws — Side by Side
**Content:** Two columns —

**throw (no 's'):** Used inside method body | Followed by an exception instance | Transfers control immediately | One exception at a time | Example: `throw new IOException("File error");`

**throws (with 's'):** Used in method signature | Declares what might be thrown | Callers must handle or re-declare | Can list multiple types | Example: `public void read() throws IOException, SQLException`

**SCRIPT:**
This is the classic confusion point. Let me make it crystal clear.
throw — no 's' — is inside method bodies. It's an action. It causes an exception to happen right now.
throws — with an 's' — is in method signatures. It's a declaration. It says "this method might produce this type of exception someday." It doesn't cause anything to happen — it's just documentation that the compiler enforces.
Mnemonic: throw is doing. throws is declaring.

Quick check — I'll describe a situation, you say throw or throws.
- "I'm in a method body and I want to cause an exception to happen right now." — [throw]
- "I want to tell callers that my method might produce an IOException." — [throws]
- "I'm writing a method signature that could fail." — [throws]
- "I've detected invalid input and I want to stop execution." — [throw]

Good.

---

## SLIDE 15b: Multi-catch Syntax (Java 7+)
**Content:** Code block —
```java
// WITHOUT multi-catch — repetitive
try {
    // risky code
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
} catch (SQLException e) {
    System.err.println("Error: " + e.getMessage());  // exact same body
}

// WITH multi-catch — clean and concise
try {
    // risky code
} catch (IOException | SQLException e) {
    System.err.println("Error: " + e.getMessage());
}
```
> ⚠️ Notes: "The variable `e` is implicitly final — you cannot reassign it inside the block. Do not use multi-catch with types that are related by inheritance — if one extends the other, the compiler will reject it."

**SCRIPT:**
Multi-catch was introduced in Java 7 and you will see it constantly in real codebases. When two or more exception types need identical handling, instead of writing the same catch body twice, you separate the types with a pipe `|` and use a single catch block. Cleaner, less repetitive, easier to maintain.
Two gotchas to know. First: the variable `e` becomes implicitly final inside a multi-catch block. You cannot reassign it. This usually doesn't matter, but it will give you a compile error if you try.
Second: don't use multi-catch with exception types that are related by inheritance. If one type is a subclass of the other, the compiler will reject it — because the parent type already covers the child, making the multi-catch redundant. Use multi-catch for unrelated exception types that happen to need the same handling.

---

# SECTION 4: File I/O Basics (~10 minutes)

## SLIDE 16: Section Divider
**Content:** "Section 4: File I/O Basics" | Subtitle: "File, FileReader, FileWriter — Reading and Writing Text"

---

## SLIDE 17: The File Class
**Content:** Left side — bullet points:
- java.io.File represents a file or directory path — it doesn't read/write content
- Key methods: exists(), createNewFile(), delete(), isFile(), isDirectory(), getName(), getPath(), length()
- Think of it as a remote control — it represents the file but isn't the file itself

Right side — small code example:
```java
File f = new File("data.txt");
if (f.exists()) {
    System.out.println(f.getName());
    System.out.println(f.length());
} else {
    f.createNewFile();
}
```

**SCRIPT:**
The first class to know is java.io.File. And here's the crucial thing: File does not read or write content. It represents a path.
Think of it like a TV remote. The remote represents the TV — you can get information about it, you can control it — but it's not the TV itself. File is your remote control for the file system.
You create a File with a path string: new File("data.txt"). Then you can ask questions: does it exist? Is it a file or a directory? How large is it? You can create new files with createNewFile() and delete them with delete().
But to actually read the bytes inside — to open the file and look at the content — you need something else. That's where FileReader and FileWriter come in.
Good analogy to remember: File is having the address to a house. FileReader is actually going inside and reading what's there.

---

## SLIDE 18: FileReader and FileWriter
**Content:** Code block —
```java
// WRITING to a file
FileWriter fw = new FileWriter("output.txt");
fw.write("Hello, World!\n");
fw.write("Second line\n");
fw.close(); // Must close — flushes buffer and releases the file

// Append mode — pass true as second argument
FileWriter fw2 = new FileWriter("output.txt", true);
fw2.write("Appended line\n");
fw2.close();

// READING from a file (one character at a time)
FileReader fr = new FileReader("output.txt");
int ch;
while ((ch = fr.read()) != -1) {
    System.out.print((char) ch);
}
fr.close();
```

**Expected output of the above code:**
```
Hello, World!
Second line
Appended line
```

> Note: "Always close() — or use try-with-resources (coming up next)"

**SCRIPT:**
FileWriter is how you write text to a file. You create one pointing at your filename, call write() with your content, and call close() when done.
That close() call is mandatory. Closing flushes the buffer — meaning it forces any data sitting in memory out to the actual disk — and releases the operating system's lock on the file. Forget close() and your data might not actually be saved. Or worse, another part of your program can't open the file because you still have it locked.
Append mode: by default, every time you create a FileWriter for the same file, it overwrites from the beginning. Pass true as the second argument to append instead.
FileReader reads one character at a time. read() returns an int — when it returns -1, you've hit end of file. You cast it to char to use it. Look at the expected output panel on the slide — that's exactly what you'd see in the console if you ran this code. The first FileWriter writes two lines, the second appends a third, and then the FileReader reads all three back out.
This character-by-character approach is functional but slow for large files and a bit awkward to write. There's a much better way, which I'll show you in the next section.

---

# SECTION 5: Buffered Streams & try-with-resources (~10 minutes)

## SLIDE 19: Section Divider
**Content:** "Section 5: Buffered Streams & try-with-resources" | Subtitle: "Faster I/O and automatic resource management"

---

## SLIDE 20: Why Use Buffered Streams?
**Content:** Two boxes side by side —

**Without Buffer (slow):** Each read()/write() → direct disk access | 1,000 chars = 1,000 disk operations | Very slow for large files

**With Buffer (fast):** Reads/writes in chunks (8KB default) | 1,000 chars ≈ 1 disk operation | Up to 100x faster for large files

Code example below:
```java
BufferedReader br = new BufferedReader(new FileReader("file.txt"));
BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));

String line;
while ((line = br.readLine()) != null) {
    bw.write(line);
    bw.newLine();
}
br.close();
bw.close();
```
> Note: "readLine() returns null at end of file"

**SCRIPT:**
Look at the two boxes. Without buffering, every single read() or write() call goes directly to the disk. Reading 1,000 characters means 1,000 disk operations. Disk I/O is orders of magnitude slower than working in memory — this is genuinely slow.
With buffering, Java reads a big chunk — typically 8 kilobytes — into memory at once. Then your calls to read() pull from that in-memory buffer. Far fewer trips to the disk. We're talking potentially 100 times faster on large files.
The syntax is a wrapper pattern. You wrap a FileReader inside a BufferedReader:
```
BufferedReader br = new BufferedReader(new FileReader("file.txt"));
```
The huge payoff of BufferedReader is the readLine() method. It reads one full line at a time and returns null when the file ends. No more character-by-character loops. This is how virtually all real Java file reading is done.
BufferedWriter adds newLine() — a method that writes the correct line ending for whatever operating system you're running on. Windows uses CRLF, Linux and Mac use LF. Using newLine() instead of "\n" makes your code portable.
From this point forward, always wrap FileReader and FileWriter in their buffered counterparts. There's no good reason not to.

---

## SLIDE 21a: The Problem with Manual Resource Cleanup
**Content:** Code block —
```java
// OLD WAY — verbose and error-prone
FileReader fr = null;
try {
    fr = new FileReader("file.txt");
    // ... work ...
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fr != null) {
        try { fr.close(); } catch (IOException e) { }
    }
}
```
> ⚠️ "Seven lines of boilerplate just to close one resource. Easy to write incorrectly. A lot of real-world bugs come from developers getting this wrong."

**SCRIPT:**
Before we see the modern approach, let's feel the pain of the old way so you understand *why* the improvement exists.
Here's what you had to write before Java 7 to safely close a file. You declare the resource outside the try so the finally block can see it. You initialize it to null so the null check in finally doesn't crash. You write a nested try-catch inside the finally just to handle the case where close() itself throws. That's seven lines of boilerplate for one resource. For two resources? Twelve lines. Easy to get wrong, ugly to read, and a common source of real bugs — developers would skip the finally, or skip the null check, and end up leaking file handles.
There is a better way.

---

## SLIDE 21b: try-with-resources — The Modern Approach
**Content:** Code block —
```java
// NEW WAY — try-with-resources (Java 7+)
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"));
     BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"))) {

    String line;
    while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.newLine();
    }
} catch (IOException e) {
    e.printStackTrace();
}
// br and bw are AUTOMATICALLY closed here — no finally needed
```
> ✅ "Resources declared in try(...) are automatically closed in reverse order — whether the block succeeds or an exception is thrown. You cannot forget to close them."

**SCRIPT:**
Try-with-resources. You declare your resources inside parentheses right after the try keyword. At the end of the block — whether it succeeded or an exception was thrown — Java automatically calls close() on every resource declared there. Automatically. In reverse order of declaration.
No finally block. No null checks. No nested try-catch for close(). The cleanup is guaranteed by the language itself.
You can declare multiple resources, separated by semicolons. They close in reverse order — so bw closes before br here — which matters when resources depend on each other.
From today forward, always use try-with-resources for any I/O or other resource that needs closing. This is the professional Java way to write I/O code.

---

## SLIDE 22: The AutoCloseable Interface
**Content:** Bullet points —
- try-with-resources works with any class implementing AutoCloseable
- AutoCloseable has one method: `void close() throws Exception`
- All Java I/O classes already implement this
- You can make your own classes work with try-with-resources by implementing AutoCloseable and defining close()
- Closing order: resources closed in REVERSE order of declaration

```java
// Example: custom resource that works with try-with-resources
public class DatabaseConnection implements AutoCloseable {
    public DatabaseConnection() { /* open connection */ }

    @Override
    public void close() {
        // release the connection
        System.out.println("Connection closed.");
    }
}

// Usage
try (DatabaseConnection conn = new DatabaseConnection()) {
    // use conn
} // close() called automatically here
```

**SCRIPT:**
Why does try-with-resources work with FileReader and BufferedReader? Because they implement the AutoCloseable interface.
AutoCloseable is a simple interface with exactly one method: close(). Any class that implements it and defines close() can be used in a try-with-resources statement. All Java I/O classes already do this.
Here's the cool part — and this matters for you as a full stack developer: you can make your own classes work with try-with-resources too. Writing a class that manages a database connection? A network socket? Anything that needs cleanup? Implement AutoCloseable, define close(), and your class can go in a try-with-resources block just like a FileReader. The example on the slide shows exactly this pattern.

---

# SECTION 6: Putting It All Together (~5 minutes)

## SLIDE 23: Section Divider
**Content:** "Section 6: Putting It All Together" | Subtitle: "Reading and writing text files with proper exception handling"

---

## SLIDE 24: Exception Chaining — Adding Context When Re-throwing
**Content:** Code block —
```java
// Without chaining — loses the original cause
catch (FileNotFoundException e) {
    throw new IOException("Input file not found: " + inputPath); // original stack trace LOST
}

// With chaining — preserves the original cause
catch (FileNotFoundException e) {
    throw new IOException("Input file not found: " + inputPath, e); // e is the "cause"
}

// Retrieving the cause later
catch (IOException e) {
    System.err.println("Top-level message: " + e.getMessage());
    System.err.println("Root cause: " + e.getCause().getMessage());
}
```
> **Why it matters:** Exception chaining preserves the full debugging trail. The original stack trace is stored inside the new exception as its "cause." Without it, you lose the exact line where things first went wrong.

**SCRIPT:**
Before we look at the full example, I want to introduce one more important pattern: exception chaining.
Sometimes you catch a low-level exception but want to re-throw it with a more meaningful message for the caller. The naive approach is to just create a new exception with a new message — but that throws away the original stack trace. You've lost the exact line where things first went wrong, which makes debugging much harder.
Exception chaining solves this. When you construct the new exception, you pass the original exception `e` as a second argument — the "cause." The original exception is stored inside the new one. Whoever catches the outer exception can call getCause() to retrieve the inner one, and they get the full story: both your meaningful message AND the original stack trace.
You'll see this pattern constantly in real Java code. Catch a low-level exception, wrap it in something domain-appropriate, pass the original as the cause.

---

## SLIDE 25: Complete File Processing Example
**Content:** Code block —
```java
public static void processFile(String inputPath, String outputPath)
        throws IOException {

    try (BufferedReader reader =
             new BufferedReader(new FileReader(inputPath));
         BufferedWriter writer =
             new BufferedWriter(new FileWriter(outputPath))) {

        String line;
        int lineCount = 0;

        while ((line = reader.readLine()) != null) {
            writer.write(line.toUpperCase());
            writer.newLine();
            lineCount++;
        }

        System.out.println("Processed " + lineCount + " lines.");

    } catch (FileNotFoundException e) {
        throw new IOException("Input file not found: " + inputPath, e); // exception chaining
    }
}
```
> Callout annotations: try-with-resources handles close() | specific exception caught first | original exception preserved as cause via chaining

**SCRIPT:**
Here's everything from today in one real method. Walk through it with me.
try-with-resources with two resources — both reader and writer are automatically closed at the end no matter what happens.
BufferedReader wrapping FileReader for efficient reading. BufferedWriter wrapping FileWriter for efficient writing.
The readLine() loop with the null check — standard pattern for reading every line of a file.
newLine() for portable line endings.
And then — look at the catch block. We catch FileNotFoundException specifically and re-throw it as a new IOException with extra context. The second argument to the IOException constructor is `e` — the original exception — which is exception chaining, exactly as we just covered. The original stack trace is preserved inside the new exception, and the caller gets a more descriptive message.
This is a complete, professional, production-quality file processing method using every technique we covered today.

---

## SLIDE 26: Quick Reference Table
**Content:** Two-column table —

| When you need... | Use... |
|---|---|
| Represent a file path | File |
| Read text character by character | FileReader |
| Write text to a file | FileWriter |
| Read text line by line efficiently | BufferedReader + FileReader |
| Write text efficiently | BufferedWriter + FileWriter |
| Automatic resource cleanup | try-with-resources |
| Domain-specific errors | Custom exception class |
| Handle recoverable errors | try-catch |
| Mandatory cleanup code | finally block |
| Signal an error occurred | throw new XException() |
| Declare a method can fail | throws in method signature |
| Handle two exception types identically | Multi-catch with \| |

**SCRIPT:**
Your cheat sheet. Take 30 seconds and look at it. [Give actual silence — 30 seconds.]
Bookmark this. It answers "what do I use for this?" for everything we covered today.

---

## SLIDE 27: Common Mistakes vs. Best Practices
**Content:** Two columns —

**Anti-patterns:**
- Catching Exception (too broad)
- Empty catch blocks — silent failures
- Not closing resources
- Using FileReader without buffering on large files
- Catching and ignoring the exception

**Best Practices:**
- Catch specific exceptions first
- Always at minimum log: `e.printStackTrace()` — in production, use a logging framework (SLF4J, Log4j) that writes errors to a file instead of the console; you'll cover this in a later module, but for now printStackTrace() is acceptable
- Use try-with-resources
- Always wrap in Buffered streams
- Re-throw with context (exception chaining) if you can't handle it

**SCRIPT:**
Two mistakes I see constantly from new Java developers.
First — the empty catch block:
```java
catch (IOException e) {
    // nothing
}
```
This is the silent killer. Your program swallows the exception, nothing happens, and you have no idea why your feature isn't working. You'll spend hours debugging something that would have been instantly obvious if you'd just logged the exception. Always do at minimum `e.printStackTrace()`. In real production code, you'd use a logging framework like SLF4J or Log4j — these write errors to a log file that persists and can be monitored. You'll learn logging in a later module, but the key point is: never let exceptions disappear silently.
Second — forgetting to close resources. This causes file handle leaks. The operating system has a limit on how many files it lets a process have open at once. Keep leaking handles and eventually your program can't open any files at all. Use try-with-resources and you never have to think about this.

---

## SLIDE 28: Summary / What We Covered
**Content:** Numbered recap —
1. **Exception Hierarchy** — Throwable → Exception / Error | Checked vs. Unchecked
2. **try-catch-finally** — Syntax, flow, specific before general, finally always runs
3. **Custom Exceptions** — Extend Exception (checked) or RuntimeException (unchecked)
4. **throw vs. throws** — Doing vs. declaring
5. **Multi-catch** — `catch (A | B e)` for identical handling of unrelated types
6. **File I/O** — File for paths, FileReader/FileWriter for character I/O
7. **Buffered Streams** — BufferedReader/BufferedWriter for performance
8. **try-with-resources** — AutoCloseable, automatic cleanup, the modern standard
9. **Exception Chaining** — Preserve original cause when re-throwing

**SCRIPT:**
Let's recap. Nine things you should be able to explain to someone right now.
One — the exception hierarchy. Throwable at top. Exception and Error branch. Checked exceptions must be handled at compile time. Unchecked are RuntimeException subclasses.
Two — try-catch-finally. Try is the risky code. Catch handles specific types, specific before general. Finally always runs, perfect for cleanup.
Three — custom exceptions. Extend Exception for checked, RuntimeException for unchecked. Always call super() with a message. Add fields for extra context.
Four — throw vs. throws. throw is an action in a method body. throws is a declaration in a method signature.
Five — multi-catch. Use the pipe operator `|` to handle multiple unrelated exception types with one catch block. e is implicitly final. Don't use with related types.
Six — File I/O basics. File represents a path. FileReader and FileWriter do the actual reading and writing.
Seven — buffered streams. Always wrap FileReader in BufferedReader and FileWriter in BufferedWriter. readLine() is your best friend. newLine() for portable line endings.
Eight — try-with-resources. Declare resources in the try parentheses. They're automatically closed. Any class implementing AutoCloseable works with it.
Nine — exception chaining. When re-throwing, pass the original exception as the cause to preserve the full debugging trail.

For homework: write a program that reads a text file, counts words on each line, and writes a summary to an output file. Use try-with-resources, BufferedReader, BufferedWriter, and create a custom exception that fires if the input file is empty.

Next class we'll look at more advanced I/O — FileInputStream, FileOutputStream for binary data, and we'll introduce the modern java.nio.file API.

Any questions? [Take the last few minutes.] Great work today.

---

# APPENDIX: Q&A Prep & Timing Notes

**Frequently asked questions:**

**Q: Can I have a finally without a catch?**
Yes — `try { } finally { }` is valid. Useful when you want cleanup but want exceptions to propagate naturally to the caller.

**Q: What if finally itself throws an exception?**
The original exception is lost and replaced by the finally exception. This is another reason to prefer try-with-resources — it handles this with suppressed exceptions rather than losing the original.

**Q: Can I re-throw in a catch block?**
Yes: `throw e;` or `throw new WrappedException("context", e);`. Re-throwing is common when you want to add context but still propagate the exception up.

**Q: Multi-catch syntax?**
```java
catch (IOException | SQLException e) { ... }
```
Shorthand when two types should be handled identically. Available since Java 7. The variable `e` is implicitly final. Do not use with types related by inheritance.

**Q: When checked vs. unchecked for my custom exceptions?**
If callers can reasonably be expected to recover — use checked. If it's a programming error or unusual condition — use unchecked. Many modern frameworks prefer unchecked because they keep method signatures cleaner.

**Q: What are suppressed exceptions?**
If a resource's close() method throws during try-with-resources, and the try block also threw, the try block's exception takes priority. The close() exception is "suppressed" — stored inside the main exception. You can retrieve suppressed exceptions with `e.getSuppressed()`. This is an edge case you rarely need to handle, but it's useful to know it doesn't silently disappear.