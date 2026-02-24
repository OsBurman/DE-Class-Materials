# Day 8 — Part 1 Walkthrough Script
## Lambdas, Functional Interfaces, Method References & Optional
**Duration:** ~90 minutes | **Files:** 4 Java demos

---

## Pre-Class Setup (5 min)

[ACTION] Open all four Part 1 files side by side. Have a blank scratch pad ready.

[ACTION] Write on the board:
```
"A lambda is an anonymous function you can pass around."
"A method reference is a shorthand when a lambda just calls one existing method."
"Optional is a container that makes 'might not exist' part of the type."
```

[ASK] "Before we start — who has used a forEach loop in Java? Great. Today, you'll learn to write that with one line instead of five."

---

## FILE 1: `01-lambda-expressions.java` (~25 min)

### Opening Hook (2 min)

[ACTION] Before opening the file, write this on the board:

```java
// Old way:
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello!");
    }
};

// New way:
Runnable r = () -> System.out.println("Hello!");
```

[ASK] "How many lines did we eliminate? What stayed? What went away?" Let them answer: the ceremony, the boilerplate, the class declaration.

→ TRANSITION: "That's what lambdas are: the *body* of a method, without all the packaging around it. Let's look at every form the syntax can take."

---

### Section 1 — `demonstrateSyntax()` (7 min)

[ACTION] Open `01-lambda-expressions.java`, scroll to `demonstrateSyntax()`.

[ACTION] Walk through the progression deliberately:

1. **Anonymous inner class vs lambda** — read both versions aloud. Point to each part that disappeared.
2. **Zero params:** `() -> expression`. "No input, one output. The parens are required even when empty."
3. **One param:** `name -> expression`. "One param — parens optional." Then show `(name) -> expression`. "Both work."
4. **Two params:** `(a, b) -> expression`. "Two or more — parens required, no way around it."
5. **Block body:** `{ ... return ...; }`. "Need multiple lines? Use curly braces and a return keyword."
6. **Expression body:** `n -> n * n`. "Single expression — no braces, no return, value is implicit."

[ASK] "When would you need a block body instead of an expression body?" (Answer: when you have multiple statements, conditionals, loops, etc.)

⚠️ WATCH OUT — Types are inferred from context. You don't declare `int n`, just `n`. The compiler figures it out from `Function<Integer, Integer>`.

---

### Section 2 — `demonstrateComparatorLambdas()` (5 min)

[ACTION] Scroll to this section.

[ACTION] Show the `List<String>` sorting examples first:
- "Alphabetical — `String::compareTo` equivalent. But look, we can write it inline."
- "By length — `a.length() - b.length()`. This is a valid Comparator because it returns -1/0/+1 conceptually."
- "By length then alpha — notice the block body with two-step logic."
- "Reverse — `Comparator.reverseOrder()` is cleaner than `-b.compareTo(a)` but both work."

[ACTION] Move to the `List<Product>` example. Point out:
- "Real data. Sort by price first. Then by category + within that by price — this is what production code looks like."

[ASK] "What does Comparator.comparingDouble do for us?" (It builds a Comparator from a key extractor function.)

---

### Section 3 — `demonstrateCollectionMethods()` (5 min)

[ACTION] Scroll to this section. Tell students: "The collections API got a full overhaul in Java 8. These four methods all accept lambdas."

Walk through:
- **`forEach(Consumer)`** — "Iterate and do something with each item. No index, no traditional loop."
- **`removeIf(Predicate)`** — "Delete elements that match a condition. Try doing this with a traditional for-loop — you need an Iterator to avoid ConcurrentModificationException. With removeIf, it's one line."
- **`replaceAll(UnaryOperator)`** — "Transform every element in place. UnaryOperator is just Function<T,T>."
- **`Map.forEach(BiConsumer)`** — "Two parameters — key and value."

⚠️ WATCH OUT — `removeIf` modifies the list. If your list is unmodifiable (e.g., from `List.of()`), this will throw.

---

### Section 4 — `demonstrateVariableCapture()` (4 min)

[ACTION] Scroll to variable capture section.

[ACTION] Explain effectively final: "A variable captured by a lambda must not change after the lambda is defined. Java enforces this. Even if you don't write the word `final`, it has to *behave* as final."

[ACTION] Show the `int[] counter = {0}` workaround. "We're not changing the array reference — that's still final. We're changing the contents. This is a trick, not a best practice."

[ASK] "Why does Java require effectively final variables?" (Thread safety, predictability — the lambda might run at a different time, in a different context.)

⚠️ WATCH OUT — Don't use the `int[]` workaround in concurrent code. It's not thread-safe.

---

### Section 5 — `demonstrateLambdasAsArguments()` (2 min)

[ACTION] Show `processNumbers()`. "This is the real power: a method that takes behavior as an argument. The caller decides what to filter and what to transform. The method stays generic."

→ TRANSITION: "OK so we've seen lambdas everywhere. But there are rules for *where* you can use a lambda. Those rules come from functional interfaces."

---

## FILE 2: `02-functional-interfaces.java` (~25 min)

### Opening: The Four Core Interfaces (3 min)

[ACTION] Open `02-functional-interfaces.java`. Point to the table at the top.

[ACTION] Draw on the board:
```
Predicate<T>       → T → boolean    "Does this thing pass a test?"
Function<T, R>     → T → R          "Transform this into that"
Consumer<T>        → T → void       "Do something with this (side effect)"
Supplier<T>        → () → T         "Give me one of these"
```

[ASK] "Before I show code — given these four, which would you use to check if a password is strong enough? To convert a Celsius temperature to Fahrenheit? To print a log message? To generate a random ID?"

---

### Section 1 — `demonstratePredicate()` (5 min)

[ACTION] Walk through basic predicates (`isEven`, `isPositive`). "Notice they all return boolean."

[ACTION] Show composition: `.and()`, `.or()`, `.negate()`. "This is functional algebra. You build complex conditions by combining simple ones."

[ACTION] Show the employee filtering example. "One line. `isEngineer.and(isHighEarner).and(isActive)`. That's clear, readable, testable. Each piece can be tested independently."

⚠️ WATCH OUT — `negate()` wraps the whole predicate. `isEven.negate()` = `isOdd`. Not the same as `!isEven` (which isn't valid Java syntax on an object).

---

### Section 2 — `demonstrateFunction()` (6 min)

[ACTION] Show basic transformations. "Input of type T, output of type R. Different types."

[ACTION] Show `andThen` vs `compose`. Draw on the board:
```
andThen:   f.andThen(g)   →   g(f(x))   — apply f first, then g
compose:   f.compose(g)   →   f(g(x))   — apply g first, then f
```
"andThen is like reading left to right. compose is reversed. Most people use andThen."

[ACTION] Show the temperature pipeline: `celsiusToFahrenheit.andThen(formatTemp)`. "Two functions, one chain."

[ACTION] Quickly cover `BiFunction` (two inputs), `UnaryOperator` (same in/out type).

[ASK] "When would you use UnaryOperator instead of Function<T,T>?" (When the input and output type are the same — cleaner API, avoids redundancy.)

---

### Section 3 — `demonstrateConsumer()` (4 min)

[ACTION] Show basic consumers. "They take input, return nothing. All about side effects — printing, writing to DB, sending events."

[ACTION] Show `andThen()` for Consumer. "Chain two side effects. Print name AND department, one after the other."

[ACTION] Show `BiConsumer` with `Map.forEach`. "This is the exact same thing we saw in lambdas section — now we know what interface it uses."

---

### Section 4 — `demonstrateSupplier()` (4 min)

[ACTION] Show basic suppliers. "No input, one output. They're producers — factories, generators."

[ACTION] Focus on the `orElse` vs `orElseGet` demo. This is important:

[ACTION] Uncomment (or walk through) the eager vs lazy evaluation example:
```java
opt.orElse(computeExpensiveDefault())       // ALWAYS runs computeExpensiveDefault()
opt.orElseGet(() -> computeExpensiveDefault())  // only runs if value is absent
```

[ASK] "If opt is present 99% of the time and computeExpensiveDefault() hits a database — which would you use?" (`orElseGet` — always.)

⚠️ WATCH OUT — This is a real production bug. People use `orElse(someMethod())` and wonder why their DB is getting hammered.

---

### Section 5 — `demonstrateCustomFunctionalInterface()` (3 min)

[ACTION] Show `@FunctionalInterface` annotation. "Marks the interface and tells the compiler: enforce exactly one abstract method."

[ACTION] Show `Transformer<A, B>` — "This is just Function<T,R> by another name, but illustrates that you can create your own."

[ACTION] Show `TriFunction<A, B, C, R>` — "Three inputs. The standard library stops at two (BiFunction). After that, define your own."

---

## FILE 3: `03-method-references.java` (~15 min)

### Opening: The Four Kinds (2 min)

[ACTION] Open `03-method-references.java`. Point to the table at the top.

[ACTION] Tell students: "A method reference is valid when your lambda does nothing but call ONE existing method. Think of it as a shortcut — the compiler rewires the method reference to the right functional interface."

---

### Section 1 — Static Method References (3 min)

[ACTION] Show `Integer::parseInt`. Write on board:
```
s -> Integer.parseInt(s)
Integer::parseInt          ← same thing
```

"See the pattern? ClassName::methodName. No parens on the right side. You're *describing* the method, not calling it."

[ACTION] Show `Math::abs` in a stream pipeline. "Every element in the list passes through `Math.abs`. Clean."

[ACTION] Show `MethodReferences::isValidEmail`. "It can be your own static helper too."

---

### Section 2 — Specific Object (3 min)

[ACTION] Show `System.out::println`. "This is the one you'll see most. `System.out` is a specific `PrintStream` instance. `println` is a method on it."

[ACTION] Show the `StringBuilder sb = new StringBuilder()` example. "The lambda `s -> sb.append(s)` captures that specific `sb` object. The method reference `sb::append` does the same."

[ACTION] Show the `Printer` class. "Your own object, your own method reference. Bound to that one instance."

---

### Section 3 — Arbitrary Object (4 min)

[ACTION] Show `String::toUpperCase`. This is the one that confuses people.

[ACTION] Draw:
```
Lambda:           s -> s.toUpperCase()
Method reference: String::toUpperCase

The String s is BOTH the argument AND the target.
```

"When you stream a `List<String>`, each element becomes the object on which `toUpperCase()` is called. The stream element IS the instance."

[ACTION] Show `String::compareTo` as a `Comparator`. "Two args: `(a, b)`. First arg becomes the object (`a.compareTo(b)`). Second arg becomes the argument to the method."

⚠️ WATCH OUT — This is the hardest one to internalize. If you see `ClassName::someMethod` and the class isn't all static, it's probably this kind. The first argument becomes the receiver.

---

### Section 4 — Constructor References (3 min)

[ACTION] Show `ArrayList::new`. Write on board:
```
() -> new ArrayList<>()
ArrayList::new               ← same thing
```

[ACTION] Show `BiFunction<String, String, Employee> = Employee::new`. "The constructor signature matches the BiFunction signature. Two strings in, one Employee out."

[ACTION] Show the dependency injection pattern (Supplier). "Frameworks do this constantly. You pass `ArrayList::new` and they call it later to create instances on demand."

---

## FILE 4: `04-optional.java` (~20 min)

### Opening: The Problem (2 min)

[ACTION] Before opening the file, write:
```java
User user = findUser(id);
// What if user is null?
System.out.println(user.getName());  // 💥 NullPointerException
```

"How many of you have hit a NullPointerException in a Spring or Java project? Optional is Java's way of saying: this method *might* return nothing, and I'm making you handle that possibility."

→ TRANSITION: "Open `04-optional.java`. Notice it starts with a full reference table."

---

### Section 1 — Creation (3 min)

[ACTION] Walk through the three factory methods:
- `Optional.of("Alice")` — "You KNOW it's non-null. Great."
- `Optional.ofNullable(value)` — "You DON'T know. Use this in 90% of cases."
- `Optional.empty()` — "Deliberately saying: nothing here."

[ACTION] Show that `Optional.of(null)` throws NPE immediately. "Counterintuitive. You're using Optional to avoid NPE but passing null to Optional.of causes one. Use ofNullable when in doubt."

---

### Section 2 — Retrieval (5 min)

[ACTION] Walk through `get()`. "Unsafe. Only call when you KNOW it's present."

[ACTION] Focus on `orElse` vs `orElseGet`. Run the eager evaluation demo:

[ACTION] Point to the console output:
```
[computeExpensiveDefault() called!]   ← orElse called it even though opt was present
(nothing printed for orElseGet)       ← orElseGet was lazy
```

"This is the single most important thing in this section. If your default computation has side effects or is expensive — always use orElseGet."

[ACTION] Show `orElseThrow`. "For your service layer: if you expected the user to exist and they don't — throw a meaningful exception."

---

### Section 3 — Transformation (5 min)

[ACTION] Walk through `map()`. "If present, apply a function. Result is still Optional."

[ACTION] Show chained maps: `name.map(String::trim).map(String::toUpperCase)`. "Each step passes the Optional forward."

[ACTION] Show `flatMap()`. Draw on board:
```
map(f)       — f returns a plain value  → Optional<R>
flatMap(f)   — f returns Optional<R>   → Optional<R> (not Optional<Optional<R>>)
```
"If your mapping function itself returns Optional, use flatMap. Otherwise you'd get an Optional inside an Optional — nobody wants that."

[ACTION] Show `filter()`. "Keep the value only if it passes the test. Otherwise, empty."

---

### Section 4 — Side Effects + Real-World Patterns (5 min)

[ACTION] Show `ifPresent`. "Run code only if the value is there. No if-check needed."

[ACTION] Show `ifPresentOrElse`. "If present, do X. Otherwise, do Y. Like an if-else but without the null check boilerplate."

[ACTION] Walk through the real-world patterns:
1. Repository pattern — `findUserById` returns `Optional<User>`. "The caller knows to handle absence."
2. Chained transformations — "user → address → city — no null checks, one elegant chain."
3. orElseThrow in service layer — "If the user must exist and doesn't, crash fast with clarity."
4. filter for premium users — "Conditional behavior without if statements."
5. Stream pipeline with Optional — "`filter(Optional::isPresent).map(Optional::get)` — a common pattern for processing a list where some lookups fail."

---

### Anti-Patterns (2 min)

[ACTION] Walk through the anti-patterns section quickly:

"Three rules to remember:
1. Don't use `isPresent()` + `get()` — it's the same as a null check, defeats the purpose.
2. Never return null from a method that returns Optional.
3. Don't use Optional as a field or parameter type — it's designed for return values only."

---

## Wrap-Up: Part 1 Summary (5 min)

[ACTION] Draw this on the board:

```
Lambda              = anonymous function you can pass around
Functional Interface = any interface with exactly ONE abstract method
Method Reference    = lambda shorthand when you're just calling an existing method
Optional            = container for "might not have a value"
```

[ASK] "Quick fire — what kind of method reference is `String::toUpperCase`?" (Arbitrary instance)
[ASK] "What's the difference between `orElse` and `orElseGet`?" (Eager vs lazy evaluation)
[ASK] "When should you use `Optional.of` vs `Optional.ofNullable`?" (When you're certain vs uncertain about null)

→ TRANSITION: "After the break, we go deep on Streams — which uses almost everything we just learned. Every lambda you wrote, every functional interface you know — they all show up in Stream pipelines. See you in 10."

---

*End of Part 1 Script*
