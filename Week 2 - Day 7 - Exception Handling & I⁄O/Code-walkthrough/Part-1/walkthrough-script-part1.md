# Day 7 — Part 1: Exception Handling
## Walkthrough Script

**Files covered:**
1. `01-exception-hierarchy-and-try-catch.java`
2. `02-custom-exceptions.java`

**Estimated time:** ~90 minutes  
**Format:** Open both files side-by-side. Walk through file 1 first, then file 2.

---

## OPENING (5 min)

"Welcome to Day 7. Today we're covering one of the most important — and most often poorly done — topics in Java: exception handling and file I/O.

By the end of today, you'll know:
- Why Java has two kinds of exceptions and when to use each
- How try-catch-finally works and the order of execution
- How to write your own custom exceptions — which every real project does
- How to read and write files, including the modern try-with-resources pattern

Let's start with file one."

---

## FILE 1 — `01-exception-hierarchy-and-try-catch.java`

---

### SECTION: The Hierarchy Comment Block (10 min)

[ACTION] Open `01-exception-hierarchy-and-try-catch.java`. Scroll to the top and read the hierarchy diagram aloud to the class.

"Before we write any code, I want you to understand the family tree.

Everything throwable in Java extends `Throwable`. That's the root. From there you get two branches.

`Error` — these are JVM-level disasters. `OutOfMemoryError`. `StackOverflowError`. You cannot meaningfully recover from these, and you should never catch them. Java expects you to fix the underlying problem.

`Exception` — these are the ones you as a programmer deal with. And `Exception` has a critical split.

[ASK] Can anyone tell me what the difference is between a checked and an unchecked exception, even if you've only heard it before?

Right. Checked exceptions — like `IOException` and `SQLException` — the compiler forces you to handle. If a method can throw a checked exception, you either catch it or declare it in your `throws` clause. The compiler won't let you forget.

Unchecked exceptions — everything that extends `RuntimeException` — the compiler does NOT force you to handle. `NullPointerException`, `ArrayIndexOutOfBoundsException`, `NumberFormatException`. These are usually programming bugs, not recoverable scenarios.

A good mental model: checked = 'this might fail and your app should handle it gracefully.' Unchecked = 'this is a bug — fix your code.'

Let's see them in action."

---

### SECTION 1 — Unchecked Exceptions (8 min)

[ACTION] Scroll to `demonstrateUncheckedExceptions()`.

"Here I'm deliberately triggering five classic runtime exceptions so you can see what they look like in a try-catch.

The first is `NullPointerException`. I set `name` to null and then call `name.length()`. Java tries to dereference a null reference. Bang — NPE.

[ASK] Before I run this, what do you think the output will be for the `NullPointerException` block?

Notice I'm catching it specifically with `catch (NullPointerException e)`. The variable `e` gives me access to the exception object — I can call `e.getMessage()`, `e.getClass().getSimpleName()`, `e.printStackTrace()` on it.

Next, `ArrayIndexOutOfBoundsException`. My array has 3 elements — indices 0, 1, 2. Accessing index 5 throws this.

`NumberFormatException` — `Integer.parseInt('twenty-five')` can't work. If you're taking user input and parsing it, you'll see this constantly.

`ArithmeticException` — integer division by zero. Note: floating-point division by zero does NOT throw — it returns `Infinity`. Only integer division throws.

`ClassCastException` — `obj` is actually a `String`, but I try to cast it to `Integer`. Nope.

⚠️ WATCH OUT: These are all `RuntimeException` subclasses. The compiler does not require you to wrap them in try-catch. That's why you can write `int x = 10 / 0` without any red squiggles — but it still blows up at runtime. Unchecked does not mean 'won't fail,' it means 'compiler won't remind you.'"

---

### SECTION: Checked Exceptions (5 min)

[ACTION] Scroll to `demonstrateCheckedExceptions()`.

"Now look at the method signature: `throws IOException`. That's there because the code inside could throw a checked exception.

`FileReader` takes a file path and throws `FileNotFoundException` if the file doesn't exist. `FileNotFoundException` extends `IOException` — it's a subclass.

[ASK] Why do you think the compiler forces you to handle FileNotFoundException but not NullPointerException?

Because a missing file is a *foreseeable* problem. You can write code that gracefully handles it — show a message, ask the user for a different path, fall back to defaults. But a NPE is a *bug*. The correct response is to fix the code so it never happens.

Notice I said 'handle OR declare.' In this method I'm catching it. But the method itself also declares `throws IOException` — that's just because the method is called from `main` which also declares `throws Exception`. In most cases you'd catch and handle the exception right here."

---

### SECTION 2 — Multiple catch blocks & ordering (8 min)

[ACTION] Scroll to `demonstrateTryCatchOrdering()`.

"This is a really important concept. When you have multiple catch blocks, Java tries them in order from top to bottom, and takes the FIRST one that matches.

I'm simulating a simple withdrawal flow with 4 test inputs: `'100'`, `'not-a-number'`, `null`, and `'-50'`.

Look at my try block. Three things can go wrong:
1. `input.trim()` — NPE if input is null
2. `Integer.parseInt(trimmed)` — NumberFormatException if it's not numeric
3. My `if (amount < 0)` check — I manually throw `IllegalArgumentException`

Each catch block handles one of those.

⚠️ WATCH OUT: Order matters critically. If I put `catch (Exception e)` at the top, it would catch everything — the more specific blocks below become unreachable. Java won't even compile that — it'll give you 'exception has already been caught.' Always go most-specific at the top, most-general at the bottom.

[ASK] If I flip the NullPointerException and NumberFormatException blocks, does anything break?

No — NPE and NumberFormatException aren't in the same inheritance chain. Order between unrelated exceptions doesn't matter. But Exception must always be last if present."

---

### SECTION 3 — Multi-catch (5 min)

[ACTION] Scroll to `demonstrateMultiCatch()`.

"Java 7 introduced multi-catch. The syntax is `catch (TypeA | TypeB e)` — you handle both with the same code.

When should you use this? When your response to both exceptions would be identical. Here, whether the input is null or non-numeric, I'm going to print the same kind of 'invalid input' message.

⚠️ WATCH OUT: Multi-catch only works for unrelated exception types. If one is a subclass of the other, the compiler rejects it — you'd just catch the parent.

Also: with multi-catch, `e` is implicitly `final`. You can't reassign the exception variable inside the block."

---

### SECTION 4 — finally (10 min)

[ACTION] Scroll to `demonstrateFinally()`.

"finally is the block that ALWAYS runs — whether your try succeeded, whether your catch ran, whether you returned early from the try block. It always runs.

[ASK] Why would you ever need code that always runs, regardless of exceptions?

Classic answer: resource cleanup. Database connections, file handles, network sockets — you need to close them no matter what. If you open a resource in try, put the close in finally.

Let's walk through my three cases.

Case 1: no exception. Try runs to completion. Then finally runs. The catch block is skipped entirely.

Case 2: an exception is thrown. Try runs until the throw. Catch runs. Then finally runs.

[ASK] Before I show you Case 3 — what do you think happens if I put a `return` statement in both the try block and the finally block?

[ACTION] Scroll to `getValueWithReturn()`.

⚠️ WATCH OUT: This is a classic gotcha. The try returns 'from try.' But finally runs BEFORE the method actually returns. And if finally also has a return statement, that return *overrides* the one from try. The method returns 'from finally.'

Worse: if an exception was thrown in try and you return from finally, that exception is silently swallowed. You'd never know it happened.

Rule of thumb: **never return from finally**. Don't throw exceptions from finally either. Only use finally for cleanup."

---

### SECTION 5 — throw vs throws (8 min)

[ACTION] Scroll to `demonstrateThrowingExceptions()` and `validateAge()`.

"Two keywords that look similar but do completely different things.

`throw` — a statement. It creates and fires an exception instance right now, in this line of code.

`throws` — a modifier on a method signature. It declares to the caller 'this method might throw this exception type.'

Look at `validateAge`. It takes an int and throws `IllegalArgumentException` — an unchecked exception — if the age is invalid. No `throws` declaration needed on the signature because it's unchecked.

[ASK] What's the output when I call validateAge(-5)?

Now look at `processFile`. It throws `IOException` — checked. So it must declare `throws IOException`. The caller either wraps it in try-catch (which I do in the demo loop) or also declares `throws IOException` and passes the buck up further.

You'll see this a lot in service methods: `public void saveOrder(Order order) throws OrderException` — the method tells you upfront what can go wrong."

---

### SECTION 6 — Exception Chaining (7 min)

[ACTION] Scroll to `demonstrateExceptionChaining()` and `loadUserFromDatabase()`.

"Exception chaining is a powerful technique you'll use constantly in layered architectures.

Imagine a DAO layer that catches a low-level `RuntimeException` from the database driver. You don't want to leak implementation details to the service layer. So you catch the low-level exception and re-throw it wrapped in a higher-level `UserNotFoundException` or similar.

But here's the key: you pass the original exception as the *cause*. `throw new Exception('Failed to load user...', e)` — notice the second argument `e`.

This preserves the full stack trace. When you print the exception later, you see the high-level message AND the root cause. Without chaining, the original stack trace is lost forever and debugging becomes a nightmare.

`e.getCause()` lets you access the original exception. `e.printStackTrace()` will show both the wrapper and the cause in the output.

⚠️ WATCH OUT: Always chain exceptions when re-throwing. Swallowing the original cause — `throw new RuntimeException('something went wrong')` without passing `e` — is a bad habit that makes production debugging extremely painful."

---

### SECTION 7 — Catching broad exceptions (5 min)

[ACTION] Scroll to `demonstrateBroadCatching()`.

"When is it OK to catch `Exception` — the parent of everything?

Application entry points. Main methods. REST controller `@ExceptionHandler` methods. Log aggregators. These are legitimate places to catch broad exceptions so you can return a graceful error to the user and log the details.

But deep inside your business logic? No. Catching `Exception` deep in a service method means you're silently swallowing things you didn't anticipate. A NPE, a ClassCastException, a random RuntimeException — all absorbed, and your application silently limps along in a corrupted state.

⚠️ WATCH OUT: NEVER catch `Throwable` or `Error`. You'd be catching `OutOfMemoryError`, `StackOverflowError` — situations where the JVM itself is in an unknown state. There's nothing meaningful you can do. Let them propagate and let the JVM or your application server handle them."

---

### TRANSITION to file 2 (1 min)

"Great. You now understand the hierarchy, how try-catch-finally works, multiple catch blocks, multi-catch, throw vs throws, and exception chaining.

Now let's take it to the next level. In every real project, you'll write your own exception classes. Let's see how."

---

## FILE 2 — `02-custom-exceptions.java`

---

### OPENING (3 min)

[ACTION] Scroll to the top comment block of `02-custom-exceptions.java`.

"Why write custom exceptions? Built-in ones are generic. `RuntimeException: invalid input` tells you nothing. `InsufficientFundsException: requested $400.00 but only $300.00 available` tells you exactly what happened and lets you respond appropriately.

Custom exceptions also let your callers catch specific types. A banking app might have five different exception types, and the controller catches them all differently.

I've created three custom exception classes here. Let's look at each one."

---

### InsufficientFundsException — Checked (8 min)

[ACTION] Scroll to `InsufficientFundsException`.

"This is a **checked** exception — it extends `Exception`. Any method that throws it must declare `throws InsufficientFundsException`.

Three things to notice.

First, I have three constructors. This is the standard Java exception pattern. A message-only constructor, a domain-specific constructor with context fields, and a chaining constructor that takes a cause. At minimum, give your exceptions the first and third.

Second, I have custom fields: `requested` and `available`. These carry domain context beyond the message string. When the caller catches this exception, it can call `e.getShortfall()` and use that to prompt the user for the exact amount needed.

Third, the message is formatted in the constructor using `String.format`. The exception is self-documenting — you don't need a comment to understand what happened.

[ASK] When should you extend Exception vs RuntimeException?

Rule of thumb: if the caller can reasonably *recover* from this condition — show a different UI, retry with different input, ask the user — make it checked. The compiler forces them to think about it. If it represents a programming bug or unrecoverable state, make it unchecked."

---

### InvalidProductException — Unchecked (5 min)

[ACTION] Scroll to `InvalidProductException`.

"This one extends `RuntimeException` — unchecked. No `throws` declaration needed.

I've added `productId` and `reason` fields. This is useful for logging — you can extract `e.getProductId()` and write structured log entries.

The chaining constructor passes `cause` to `super(message, cause)`. Always do this, even for unchecked exceptions. You never know when this will be thrown as a re-wrap of something else."

---

### ApiException — Checked with error code (5 min)

[ACTION] Scroll to `ApiException`.

"This is a pattern you'll see in every REST API. The exception carries an HTTP status code. The controller layer catches `ApiException` and maps the status code to an HTTP response.

I've overridden `toString()` to include the status code in the string representation. When this gets logged, you'll see `ApiException [HTTP 404]: User not found` — immediately actionable.

This is a checked exception because controllers *must* handle all service errors. We don't want them to accidentally fall through."

---

### Demo 1 — BankAccount (8 min)

[ACTION] Scroll to `demonstrateBankAccount()`.

"Let's watch the exceptions work in context.

I create an account for Alice with $500. I successfully withdraw $200 — no exception.

Then I try to withdraw $400 when only $300 remains. `InsufficientFundsException` is thrown. I catch it and access `e.getShortfall()`. In a real app, this is where you'd offer the user a 'top up your balance' button.

Then I try to withdraw -$50. That's not an InsufficientFundsException — that's a programming bug, so I throw `IllegalArgumentException` (unchecked) inside the BankAccount class.

[ASK] Why don't I throw InsufficientFundsException for the negative amount?

Because a negative withdrawal isn't a 'funds' problem. It's an invalid argument. Using the right exception type communicates intent to other developers."

---

### Demo 2 — ProductService (4 min)

[ACTION] Scroll to `demonstrateProductService()`.

"The unchecked exception in action. `addProduct` doesn't need a `throws` declaration.

Notice I can still catch it — I just don't have to. Here I catch it to show you what it looks like. In a real application you might have a global exception handler at the top level that catches `InvalidProductException` and returns a 400 Bad Request response."

---

### Demo 3 — ApiException (4 min)

[ACTION] Scroll to `demonstrateApiExceptions()`.

"I test three user IDs: valid, negative, and 999 (simulated 'not found').

For each ApiException, I check the status code and respond appropriately. This is exactly how a Spring `@ExceptionHandler` works — you catch the exception and decide what HTTP response to return.

Notice my custom `toString()` in the output: `ApiException [HTTP 404]: User with ID 999 not found`. This is what would appear in your server logs."

---

### Demo 4 — Re-throwing as custom (5 min)

[ACTION] Scroll to `demonstrateReThrowingAsCustom()` and `loadConfig()`.

"This is the chaining pattern in context. `loadConfig` tries to open a file. `FileReader` throws a `FileNotFoundException`. We catch it and re-throw it as our higher-level `ApiException` with status code 500.

The original `FileNotFoundException` is passed as the cause.

When the caller catches `ApiException`, they get `e.getMessage()` which explains what went wrong at the application level. And `e.getCause()` gives them the raw `FileNotFoundException` with the full file path. Both levels of context are preserved."

---

### Demo 5 — Hierarchy catching (4 min)

[ACTION] Scroll to `demonstrateHierarchyCatching()`.

"One final concept: because `InsufficientFundsException` extends `Exception`, a `catch (Exception e)` block will catch it. The IS-A relationship works here just like with class inheritance.

I'm using Java 16's pattern matching `instanceof` to downcast inside the catch block: `if (e instanceof InsufficientFundsException ife)` — this lets me call `ife.getShortfall()` even though the catch block declared `Exception e`.

This is the most flexible pattern for global exception handlers — catch `Exception`, then inspect the actual type to decide how to respond."

---

### PART 1 SELF-CHECK (5 min)

[ASK] Self-check questions:

1. What's the difference between `Error` and `Exception` in the Throwable hierarchy?
2. What does it mean for an exception to be "checked"? Name two examples.
3. In a try-catch chain, should you catch `Exception` first or last? Why?
4. When does the `finally` block NOT run? *(answer: if the JVM exits with System.exit())*
5. What's the difference between `throw` and `throws`?
6. Why should you always pass the original exception as the `cause` when re-throwing?
7. Should you extend `Exception` or `RuntimeException` for a `DatabaseConnectionException`? Why?
8. What's wrong with `catch (Exception e) { }` — an empty catch block?
9. What's multi-catch, and when would you use it?
10. If I call `e.getCause()` and it returns null, what does that tell me?

---

### TRANSITION TO PART 2

"Excellent work on Part 1. You now understand the full exception model.

In Part 2 we're going to put exceptions to work in a real-world context: reading and writing files. File I/O is one of the most common places you'll encounter checked exceptions in Java — and it's also where try-with-resources becomes your best friend. Let's go."

---

*End of Part 1 Walkthrough Script*
