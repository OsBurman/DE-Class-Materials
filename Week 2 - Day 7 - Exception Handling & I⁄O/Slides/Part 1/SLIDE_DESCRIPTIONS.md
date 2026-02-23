# Week 2 - Day 7 (Tuesday) Part 1: Exception Handling Overview
## Managing Errors Gracefully in Java

---

## Slide 1: Welcome to Exception Handling

**Visual:** Broken chain link or error symbol transitioning to repaired chain; Java Exception icon

Welcome to Week 2, Day 7. Last week, you mastered Collections and Generics. This week, we dive into advanced Java. Today, exception handling. Every program encounters errors. Network connections fail. Files don't exist. Users enter invalid data. Beginners often crash their programs. Professionals handle errors gracefully. Exception handling is how Java developers manage errors without crashing. It's not optional—it's fundamental. By the end of today, you'll write robust code that handles problems elegantly. You'll catch exceptions, create custom exceptions, and recover from errors. Let's begin.

---

## Slide 2: What Are Exceptions?

**Visual:** Program flow chart showing normal execution vs exception occurring; error symbol branching off

Exceptions are runtime errors. Your program is executing normally. Then something unexpected happens. A file you're reading doesn't exist. A database connection times out. A number is divided by zero. Without exception handling, the program crashes. With exception handling, you catch the error, decide what to do, and continue. An exception is an object representing the error. It contains information about what went wrong and where. Java's exception system lets you handle these systematically.

---

## Slide 3: Exception Hierarchy: Throwable is the Root

**Visual:** Tree diagram showing Throwable at root, branching to Error and Exception, Exception branching to Checked and Unchecked

Everything that can be thrown in Java is a Throwable. Throwable has two main branches: Error and Exception. Errors are severe problems—OutOfMemoryError, StackOverflowError. These typically can't be recovered. We rarely catch Errors. Exceptions are problems we can often recover from. Exceptions split into two categories: checked and unchecked. This distinction is crucial in Java.

---

## Slide 4: Checked Exceptions: Compile-Time Enforcement

**Visual:** Compiler icon with a "check" mark; method signature with "throws" clause

Checked exceptions are checked by the compiler. If your code might throw a checked exception, the compiler forces you to handle it. You must either catch it or declare that your method throws it. Examples: IOException (file operations), SQLException (database operations), ClassNotFoundException. The compiler won't compile your code until you handle checked exceptions. This forces developers to think about error cases upfront. It's controversial—some say it's good design, others find it verbose.

---

## Slide 5: Unchecked Exceptions: Runtime Surprises

**Visual:** Runtime icon; method signature without "throws" clause; exception occurring

Unchecked exceptions aren't checked by the compiler. Your code compiles fine even if it throws an unchecked exception. Examples: NullPointerException (null reference), ArrayIndexOutOfBoundsException (invalid index), IllegalArgumentException (invalid arguments), ArithmeticException (division by zero). Unchecked exceptions are subclasses of RuntimeException. They typically indicate programming errors—bugs you should fix, not errors to handle. You don't have to catch them, but you can.

---

## Slide 6: Exception Hierarchy Details

**Visual:** Detailed tree showing Throwable → Exception → RuntimeException and Exception → IOException, SQLException, etc.

Throwable is the parent. Exception is for recoverable conditions. RuntimeException (unchecked) extends Exception but signals a different handling model. Checked exceptions: IOException, SQLException, FileNotFoundException (subclass of IOException). Unchecked exceptions: NullPointerException, ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassCastException. Understanding this hierarchy is essential for proper exception handling.

---

## Slide 7: Try-Catch: Catching Exceptions

**Visual:** Code block showing try {} catch {} structure with exception flowing into catch

The try-catch block is your primary exception handling tool. Code goes in the try block. If an exception occurs, it's caught by the matching catch block. Syntax:

```java
try {
    // Code that might throw exceptions
    int result = 10 / 0;  // ArithmeticException
} catch (ArithmeticException e) {
    // Handle the exception
    System.out.println("Cannot divide by zero");
}
```

The catch block specifies which exception type it handles. If the thrown exception matches, that catch block executes. If no catch matches, the exception propagates up.

---

## Slide 8: Multiple Catch Blocks

**Visual:** Single try block with multiple catch blocks below; different exception types in each

You can have multiple catch blocks for different exception types:

```java
try {
    int[] numbers = {1, 2, 3};
    int result = 10 / numbers[5];  // ArrayIndexOutOfBoundsException
} catch (ArithmeticException e) {
    System.out.println("Math error");
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Array index out of bounds");
}
```

Java evaluates catch blocks from top to bottom. The first matching catch executes. The others are skipped. Order matters when catching is related exceptions (parent-child). Catch the specific exception first, the general one later.

---

## Slide 9: Catch Block Specificity Order

**Visual:** Exception hierarchy with arrows showing evaluation order from specific to general

Important: Order catch blocks from most specific to most general. If you catch the parent exception first, the child exception will never be caught:

```java
// WRONG: General catch first
try { ... }
catch (Exception e) {
    // Catches ALL exceptions
} catch (ArithmeticException e) {
    // Never reached! ArithmeticException is caught above
}

// CORRECT: Specific catch first
try { ... }
catch (ArithmeticException e) {
    // Catches ArithmeticException specifically
} catch (Exception e) {
    // Catches all other exceptions
}
```

---

## Slide 10: Finally: Cleanup Code

**Visual:** Try-catch-finally block structure; finally block highlighted as guaranteed execution

The finally block executes whether an exception occurs or not:

```java
try {
    // Code that might throw exceptions
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Error occurred");
} finally {
    System.out.println("This always executes");
}
```

Output: "Error occurred" and "This always executes". Finally is useful for cleanup: closing files, releasing resources, resetting state. Even if the catch block returns, finally executes first.

---

## Slide 11: Finally Always Executes

**Visual:** Flow diagram showing try → exception → catch → finally guaranteed; or try → no exception → finally guaranteed

Finally executes in all scenarios:
1. Exception occurs and is caught: try → catch → finally
2. Exception occurs and isn't caught: try → finally → (exception propagates)
3. No exception: try → finally (catch is skipped)
4. Return in try or catch: try/catch returns are deferred until finally completes

Finally is guaranteed, making it perfect for cleanup code.

---

## Slide 12: Accessing Exception Information

**Visual:** Exception object with properties highlighted: message, cause, stack trace

When you catch an exception, you have an Exception object with useful information:

```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println(e.getMessage());  // "/ by zero"
    System.out.println(e.getClass().getName());  // "java.lang.ArithmeticException"
    e.printStackTrace();  // Prints full stack trace
    System.out.println(e.getCause());  // Original cause if this was wrapped
}
```

getMessage(): Brief error message
printStackTrace(): Full stack trace showing where error occurred
getCause(): If this exception was caused by another exception
getClass(): Exception type

---

## Slide 13: Stack Trace Understanding

**Visual:** Stack trace output showing method calls from top (current) to bottom (entry point)

Stack trace shows the call chain when the exception occurred:

```
java.lang.ArithmeticException: / by zero
    at Main.divide(Main.java:15)
    at Main.main(Main.java:8)
```

Read from bottom up. main() called divide(), which threw ArithmeticException at line 15. Stack trace helps debugging—it shows exactly where and how the error occurred.

---

## Slide 14: Throwing Exceptions

**Visual:** throw keyword; exception being thrown from one method to caller

You can throw exceptions explicitly using the throw keyword:

```java
public void validateAge(int age) throws IllegalArgumentException {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative");
    }
    if (age > 150) {
        throw new IllegalArgumentException("Age unrealistic");
    }
}
```

throw creates a new exception object and throws it. The throws clause on the method signature declares that this method might throw that exception. Checked exceptions require throws declaration. Unchecked exceptions don't require it (but it's good documentation).

---

## Slide 15: Throws Clause: Propagating Exceptions

**Visual:** Exception propagating up through method call stack; throws keyword in method signature

The throws clause declares that a method might throw an exception:

```java
public void readFile(String filename) throws IOException {
    // This method might throw IOException
    FileReader reader = new FileReader(filename);
    // ... read file ...
}

// Caller must handle it
try {
    readFile("data.txt");
} catch (IOException e) {
    System.out.println("Failed to read file");
}
```

throws declares the exception to the caller. The caller must either catch it or declare it themselves. Checked exceptions must be handled or declared; unchecked exceptions don't require declaration.

---

## Slide 16: Exception Propagation Chain

**Visual:** Chain of method calls showing exception propagating up the stack; handler at top

Exceptions propagate up the call stack until caught:

```java
// Level 3: Throws ArithmeticException
public int divide(int a, int b) {
    return a / b;  // If b=0, ArithmeticException
}

// Level 2: Calls divide, propagates exception
public int calculate(int x, int y) throws ArithmeticException {
    return divide(x, y);  // Exception propagates up
}

// Level 1: Calls calculate, handles exception
public void process() {
    try {
        int result = calculate(10, 0);
    } catch (ArithmeticException e) {
        System.out.println("Handled");
    }
}
```

Exception flows: divide() → calculate() → process(), caught at process().

---

## Slide 17: Custom Exceptions: Creating Your Own

**Visual:** Custom exception class extending Exception; constructor receiving message

You can create custom exceptions for domain-specific errors:

```java
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
```

Custom exceptions extend Exception (for checked) or RuntimeException (for unchecked). Include a constructor that calls super() with the message. Use them for domain-specific errors that need special handling.

---

## Slide 18: Custom Exception Example

**Visual:** Banking system throwing custom exception; caught by caller

```java
public class BankAccount {
    private double balance;
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Need " + (amount - balance) + " more");
        }
        balance -= amount;
    }
}

// Usage
BankAccount account = new BankAccount(100);
try {
    account.withdraw(150);
} catch (InsufficientFundsException e) {
    System.out.println("Withdrawal failed: " + e.getMessage());
}
```

Custom exceptions provide clarity. A generic exception says "something failed". A custom exception says exactly what failed in domain terms.

---

## Slide 19: Exception Constructors: Standard Pattern

**Visual:** Exception class showing multiple constructors: no-arg, message, cause, message+cause

Standard practice: provide multiple constructors

```java
public class CustomException extends Exception {
    // No-arg constructor
    public CustomException() {
        super();
    }
    
    // Message-only constructor
    public CustomException(String message) {
        super(message);
    }
    
    // Cause constructor (exception chaining)
    public CustomException(Throwable cause) {
        super(cause);
    }
    
    // Message and cause constructor
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

These constructors cover all usage patterns.

---

## Slide 20: Exception Chaining: Wrapping Exceptions

**Visual:** Wrapped exception showing cause; original exception nested inside

Exception chaining preserves the original exception while adding context:

```java
try {
    // Code that throws IOException
    readFile("data.txt");
} catch (IOException e) {
    // Wrap it in a custom exception with context
    throw new DataProcessingException("Failed to process data", e);
}
```

The original IOException is preserved as the cause. The stack trace shows both exceptions, helping debuggers understand the full error chain. getCause() retrieves the original exception.

---

## Slide 21: Try-With-Resources: Automatic Resource Cleanup

**Visual:** try-with-resources syntax; resource automatically closed arrow

Try-with-resources automatically closes resources:

```java
try (FileReader reader = new FileReader("data.txt")) {
    // Read file
    int data = reader.read();
    System.out.println(data);
} catch (IOException e) {
    System.out.println("Failed to read");
}
// reader is automatically closed here
```

Resources declared in try() must implement AutoCloseable. When the try block exits (normally or via exception), the resource's close() method is automatically called. No need for finally blocks to close resources.

---

## Slide 22: Try-With-Resources with Multiple Resources

**Visual:** try-with-resources with semicolon-separated multiple resources

You can manage multiple resources:

```java
try (FileReader reader = new FileReader("input.txt");
     FileWriter writer = new FileWriter("output.txt")) {
    // Use both reader and writer
    int data = reader.read();
    writer.write(data);
} catch (IOException e) {
    System.out.println("File operation failed");
}
// Both reader and writer are automatically closed here
```

Resources are closed in reverse order of declaration. If closing one throws an exception, the others still close (close is called even if it throws).

---

## Slide 23: Beginner Mistake: Catching Generic Exception

**Visual:** ❌ catch (Exception e) vs ✓ catch (IOException e); red X on generic, green check on specific

**WRONG:**
```java
try {
    int result = Integer.parseInt("abc");
    FileReader reader = new FileReader("file.txt");
} catch (Exception e) {
    System.out.println("Something failed");
}
```

**CORRECT:**
```java
try {
    int result = Integer.parseInt("abc");
    FileReader reader = new FileReader("file.txt");
} catch (NumberFormatException e) {
    System.out.println("Invalid number format");
} catch (IOException e) {
    System.out.println("File not found");
}
```

Catching generic Exception hides the real problem. You lose the opportunity to handle different errors differently. Be specific about which exceptions you expect and handle each appropriately.

---

## Slide 24: Beginner Mistake: Silent Exception Swallowing

**Visual:** ❌ empty catch block vs ✓ catch block with logging; warning symbol

**WRONG:**
```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // Empty catch block—exception disappears silently!
}
```

**CORRECT:**
```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Math error: " + e.getMessage());
    logger.error("Division by zero occurred", e);
}
```

Empty catch blocks hide errors. The exception occurs, is caught, and you never know. Always log or handle exceptions. If you truly want to ignore it, add a comment explaining why.

---

## Slide 25: Beginner Mistake: Catching Without Recovery

**Visual:** ❌ exception caught but program state broken vs ✓ exception caught and state restored

**WRONG:**
```java
BankAccount account = new BankAccount(100);
try {
    account.withdraw(150);  // Fails
} catch (InsufficientFundsException e) {
    // Caught, but account is now in bad state
}
account.printBalance();  // Unknown state
```

**CORRECT:**
```java
BankAccount account = new BankAccount(100);
try {
    account.withdraw(150);  // Fails
    account.printBalance();
} catch (InsufficientFundsException e) {
    System.out.println("Insufficient funds. Balance: " + 100);
    // Recover to valid state
}
```

Only catch exceptions you can recover from. If catching the exception leaves your program in an uncertain state, consider letting it propagate or catch it higher up where recovery is possible.

---

## Slide 26: Exception Handling Best Practices

**Visual:** Checklist of best practices with checkmarks

1. **Catch specific exceptions**, not generic Exception
2. **Fail fast**: Detect errors early in validation
3. **Recover or document**: Either recover or let caller know
4. **Log exceptions**: Always record what went wrong
5. **Don't swallow silently**: Every catch block should do something
6. **Use custom exceptions** for domain errors
7. **Close resources**: Use try-with-resources
8. **Preserve stack traces**: Don't lose the cause of the error

---

## Slide 27: Exception Handling vs Error Prevention

**Visual:** Two paths: prevention (validation upfront) vs exception handling (catch when it happens)

Exception handling isn't prevention. Prevention is better:

```java
// Exception handling approach (reactive)
try {
    int[] numbers = {1, 2, 3};
    System.out.println(numbers[index]);
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Index out of bounds");
}

// Prevention approach (proactive)
if (index >= 0 && index < numbers.length) {
    System.out.println(numbers[index]);
} else {
    System.out.println("Index out of bounds");
}
```

Prevention is preferable for predictable errors. Exception handling is for unexpected conditions. Use both strategies appropriately.

---

## Slide 28: Checked vs Unchecked: Decision Matrix

**Visual:** Table comparing checked and unchecked exception characteristics

| Aspect | Checked | Unchecked |
|--------|---------|-----------|
| Compiler enforcement | Yes | No |
| Must be caught/declared | Yes | No |
| Typical use case | Recoverable conditions | Programming errors |
| Examples | IOException, SQLException | NullPointerException, IllegalArgumentException |
| When to use custom | Expected business errors | Logic errors |

Use checked exceptions for conditions the caller might recover from. Use unchecked for programming errors the developer should fix.

---

## Slide 29: Real-World Example: File Operations with Validation

**Visual:** File validation flow showing multiple exception points

```java
public String readFile(String filename) throws IOException {
    // Validate input first
    if (filename == null || filename.isEmpty()) {
        throw new IllegalArgumentException("Filename cannot be null");
    }
    
    try (FileReader reader = new FileReader(filename)) {
        StringBuilder content = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            content.append((char) ch);
        }
        return content.toString();
    } catch (FileNotFoundException e) {
        throw new IOException("File " + filename + " not found", e);
    }
}
```

Combines validation, exception chaining, try-with-resources. Validates input (prevention), handles file not found (recovery), chains exceptions (context).

---

## Slide 30: Summary: Exception Handling Principles

**Visual:** Exception handling pyramid: prevention base, specific handling middle, generic handling top

1. **Prevent when possible**: Validate input upfront
2. **Handle specifically**: Catch exact exception types
3. **Document failures**: Always log or inform caller
4. **Recover appropriately**: Leave program in valid state
5. **Use try-with-resources**: Clean up automatically
6. **Chain exceptions**: Preserve error context
7. **Create custom exceptions**: Express domain errors clearly

---

## Slide 31: Recap: Part 1 Key Concepts

**Visual:** Recap of checked vs unchecked, try-catch-finally, custom exceptions

Checked exceptions: Compiler enforces handling. Examples: IOException, SQLException.
Unchecked exceptions: Runtime errors. Examples: NullPointerException, ArithmeticException.
try-catch-finally: Catch exceptions, always run cleanup code.
Custom exceptions: Create domain-specific error types.
Try-with-resources: Automatically close resources.
Propagate or handle: Exception travels up stack until caught or crashes program.

---

## Slide 32: Transition to Part 2

**Visual:** Exception handling (Part 1) → File I/O operations (Part 2) arrow

Part 1 covered exception theory and mechanisms. Part 2 applies these to real-world file operations. You'll read and write files, handle file-specific exceptions, and manage resources properly. Exception handling becomes practical when working with I/O. Let's move to Part 2.

---
