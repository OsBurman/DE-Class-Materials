# Week 2 - Day 8, Part 1: Lambdas, Functional Interfaces & Optional
## 60-Minute Lecture Script

---

[00:00-02:00] **Welcome & Context Setting**

Good morning, everyone! I'm excited to dive into one of the most transformative features in modern Java: lambda expressions, functional interfaces, and the Optional class. If you've been writing Java for years, these features might have seemed confusing at first, but by the end of today's first hour, you're going to see how they fundamentally change the way you think about and write Java code.

Before we jump in, let's quickly connect this to where we've been. Last week, we covered exception handling and file I/O. You learned how to manage errors and resources carefully. This week, we're shifting gears. We're moving into more elegant, more concise Java—the kind of Java that modern developers write every single day. By the end of Week 2, you'll have the full toolkit for writing production-grade Java applications.

Today is split into two parts. Part 1, what we're doing right now, is all about the foundation: lambdas, the functional interfaces that support them, method references, and the Optional class. Then after our break, Part 2 will show you how to leverage all of this with the Stream API—one of the most powerful tools in Java. And in the afternoon, we'll tackle the DateTime API. So stick with me through this first hour, and the Stream API in Part 2 will make so much sense.

Let's get started.

---

[02:00-04:00] **Problem Statement: Why Do We Need Lambdas?**

Here's the situation: You're writing Java code, and you need to pass behavior—a piece of logic—to a method. Maybe you're sorting a list with a custom comparator. Maybe you're setting up a button click handler. Maybe you're filtering a collection. In traditional Java, what did you do? You created an anonymous inner class.

Let me show you an example. Imagine you want to create a runnable task:

```java
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello from thread");
    }
};
```

Look at that. Six lines of code—six lines!—and all you're really trying to say is: "Please print 'Hello from thread'." Six lines of boilerplate, and one line of actual logic. The anonymous inner class syntax is so heavy that it drowns out your intent.

With lambdas, that becomes:

```java
Runnable task = () -> System.out.println("Hello from thread");
```

One line. Crystal clear. The intent leaps out at you instantly. That's the problem lambdas solve. They let you express behavior concisely, without all the ceremony.

Now, you might be thinking: "Is this just syntax sugar? Does it actually matter?" Yes. It matters more than you might think. When code is concise and clear, you can see the actual logic you're writing. You can compose operations together. You can reason about correctness more easily. And—spoiler alert—it opens the door to the Stream API, which is going to revolutionize the way you think about collections.

So, our problem: Java developers need to pass behavior as values, but the traditional syntax is too verbose. Our solution: lambdas.

---

[04:00-06:00] **Functional Programming Fundamentals**

Before we talk about lambdas specifically, let's step back and talk about functional programming. What is it, really?

Functional programming is a programming paradigm where functions are first-class objects. That means you can treat functions the same way you treat data: you can pass them as parameters, return them from methods, store them in variables. It's a fundamentally different way of thinking than imperative programming, where you give the computer a series of steps to follow.

In imperative programming, you say: "Do this, then do this, then check this condition, then do that." You're telling the computer how to do something, step by step.

In functional programming, you say: "I want this to happen. I want to transform data in this way. I want to filter these values." You're telling the computer what you want, not how to get it. The how becomes implicit.

Why does this matter? Several reasons. First, functional code is often more reusable. If you write a pure function—a function that always returns the same output for the same input and has no side effects—you can use it anywhere without worrying about hidden state changes. Second, functional code is easier to test. No hidden dependencies, no state mutations to track. Third, and this is becoming increasingly important, functional code is easier to parallelize. When you're not sharing mutable state, threads don't need to synchronize.

In traditional Java, methods were tightly bound to classes. You couldn't pass a method as a parameter. You couldn't store a method in a variable. Java was, fundamentally, an object-oriented language. But starting with Java 8, in 2014, Java evolved to support functional programming paradigms. Lambdas are the gateway to that world.

---

[06:00-12:00] **Checked vs Unchecked Exceptions in Lambdas & Lambda Syntax**

Alright, now let's talk about the actual syntax. How do you write a lambda?

The basic structure is: `(parameters) -> body`

Let me break that down:

First, the parameters. In parentheses, you list the parameters the lambda accepts. Examples: `()` for no parameters, `(x)` for one parameter, `(x, y)` for two. You can optionally include types: `(int x, String y)`. If you don't include types, Java will infer them from context. If there's only one parameter and no type, you can even drop the parentheses: `x -> ...`. But I recommend always using parentheses for clarity.

Then the arrow: `->`. That's just the syntax marker that says "and here's what happens next."

Then the body. The body is where your logic lives. If it's a single expression, you can write it directly: `x -> x * 2`. The return is implicit. If you need multiple statements, you wrap them in braces: `x -> { int y = x * 2; System.out.println(y); return y; }`. Now you need explicit return statements.

Here are some examples:

```java
// No parameters
() -> System.out.println("Hi");

// Single parameter, inferred type
x -> x * 2

// Single parameter, explicit type
(int x) -> x * 2

// Multiple parameters
(x, y) -> x + y

// With explicit types
(int x, int y) -> x + y

// Multiple statements
(x, y) -> {
    int sum = x + y;
    return sum * 2;
}
```

Now, here's something important. Lambdas can access variables from their surrounding scope. However, those variables must be effectively final. That means either explicitly declared as final, or never reassigned. Why? Because lambdas are objects, and they might be executed at a different time or in a different thread. If the variable could change, the lambda's behavior would be unpredictable.

Also, about exceptions: if your lambda body throws a checked exception, that checked exception needs to be part of the functional interface's throws clause. If the functional interface doesn't declare it, you can't throw it from the lambda. This becomes relevant when, say, you're passing a lambda that might throw an IOException to a method that expects a functional interface that doesn't declare IOException. You'd need to wrap it or use a different interface.

---

[12:00-18:00] **Functional Interfaces: The Bridge Between Objects and Functions**

Now, here's the crucial question: when can you use a lambda? Answer: only when you're implementing a functional interface.

What's a functional interface? It's an interface with exactly one abstract method. That's it. Just one. You can have multiple default methods or static methods, but only one abstract method.

For example, `Runnable` is a functional interface:

```java
@FunctionalInterface
public interface Runnable {
    void run();
}
```

One abstract method. You can also optionally add the `@FunctionalInterface` annotation. The annotation doesn't change anything functionally, but it tells the compiler to enforce the rule: if someone tries to add a second abstract method, the compiler will reject it. It's like a safety check.

Now, why does Java require functional interfaces to have exactly one abstract method? Because a lambda needs to know which method it's implementing. If an interface had two abstract methods, a lambda would be ambiguous—which method is it for?

Here's the beautiful part: the Java standard library comes pre-loaded with dozens of functional interfaces designed for common patterns. You don't have to create them yourself. They're in `java.util.function`. Let me walk you through the four core ones.

**Predicate<T>**: A predicate tests something. It takes a value and returns true or false. The abstract method is `boolean test(T t)`.

```java
Predicate<String> isEmpty = s -> s.isEmpty();
boolean result = isEmpty.test("");  // true
```

Predicates are super useful for filtering. And they compose: you can combine predicates with `and()`, `or()`, and `negate()`.

```java
Predicate<String> isNotEmpty = isEmpty.negate();
```

**Consumer<T>**: A consumer accepts a value and does something with it. Returns nothing—void. The abstract method is `void accept(T t)`.

```java
Consumer<String> printer = s -> System.out.println(s);
printer.accept("Hello");  // prints "Hello"
```

Consumers are for side effects: printing, logging, updating a database. They chain with `andThen()`:

```java
Consumer<String> first = s -> System.out.println(s);
Consumer<String> second = s -> System.out.println(s.length());
Consumer<String> combined = first.andThen(second);
```

**Supplier<T>**: A supplier provides a value. Takes no input. The abstract method is `T get()`.

```java
Supplier<LocalDate> today = () -> LocalDate.now();
LocalDate d = today.get();
```

Suppliers are great for lazy initialization. The value isn't computed until you call `get()`. Useful in factories, or for providing fresh instances.

**Function<T,R>**: A function transforms. Takes a T, returns an R. The abstract method is `R apply(T t)`.

```java
Function<String, Integer> length = s -> s.length();
int len = length.apply("hello");  // 5
```

Functions are for transformations. They chain with `compose()` and `andThen()`. `andThen()` lets you pipe the output of one function into another.

```java
Function<String, Integer> length = s -> s.length();
Function<Integer, String> asString = i -> "Length: " + i;
Function<String, String> combined = length.andThen(asString);
String result = combined.apply("hello");  // "Length: 5"
```

There are also specialized versions: `BiFunction<T,U,R>` takes two parameters. And for primitives, to avoid boxing overhead, there's `IntFunction<R>`, `LongFunction<R>`, and specialized operators like `IntToLongFunction`. But the core concept is the same.

---

[18:00-22:00] **Method References: Lambda Shorthand**

Now, sometimes you're writing a lambda that just delegates to an existing method. For example:

```java
Function<String, Integer> length = s -> s.length();
```

All this lambda does is call `length()` on the parameter. There's existing method syntax for this: method references. You can write:

```java
Function<String, Integer> length = String::length;
```

That's a method reference. It's shorthand for the lambda above. The syntax is `Class::methodName` or `instance::methodName` or `Class::new`.

There are four types:

**Static method references**: `Math::abs`. Calls a static method. Equivalent to `x -> Math.abs(x)`.

```java
Function<Integer, Integer> absolute = Math::abs;
```

**Bound instance method references**: `System.out::println`. The method is called on a specific instance. Equivalent to `s -> System.out.println(s)`.

```java
Consumer<String> printer = System.out::println;
```

**Unbound instance method references**: `String::length`. The method is called on an instance that becomes the lambda's first parameter. Equivalent to `s -> s.length()`.

```java
Function<String, Integer> length = String::length;
```

**Constructor references**: `ArrayList::new`. Creates a new instance. Equivalent to `() -> new ArrayList()`.

```java
Supplier<ArrayList> supplier = ArrayList::new;
ArrayList list = supplier.get();  // Creates new ArrayList
```

Method references are concise and, once you're used to them, very clear. They're especially useful in stream operations—which, again, we'll cover in Part 2.

---

[22:00-28:00] **The Null Pointer Exception Problem**

Let me paint a scenario. You're writing a user management system. You write a method that fetches a user by ID from the database. What if the user doesn't exist? You could return null:

```java
User user = userService.findById(123);
```

Now, the caller doesn't know if `user` is null or not. They have to remember to check:

```java
if (user != null) {
    String name = user.getName();
    if (name != null) {
        String upper = name.toUpperCase();
        System.out.println(upper);
    }
}
```

This is the "pyramid of doom." Each level of uncertainty adds another null check. And it's error-prone. If you forget a null check, boom—NullPointerException at runtime.

The broader problem: null has no clear meaning. Does it mean "not found"? "Unknown"? "Empty"? "Not applicable"? You can't tell from the code. And there's no type system guidance that says "hey, this value might not exist."

Here's what's worse: Java's type system doesn't help. If a method signature says it returns `String`, the type system says nothing about whether it might return null. That's a contract left to documentation, or to faith.

What we need is a way to explicitly represent the absence of a value—to make that possibility part of the type system. Enter `Optional`.

---

[28:00-34:00] **Optional: Making Absence Explicit**

`Optional<T>` is a container. It either holds a value of type T, or it's empty. That's it. It forces you to acknowledge that the value might not exist.

Instead of:

```java
User user = userService.findById(123);
```

You'd have:

```java
Optional<User> user = userService.findById(123);
```

Now, the type signature tells the caller: "This might be empty." The caller is forced to handle that possibility.

How do you create an Optional? Three main ways:

`Optional.of(value)`: Wraps a non-null value. If you pass null, it throws an exception. Use this when you're certain the value is non-null.

`Optional.ofNullable(value)`: Wraps a value that might be null. If it's null, the Optional is empty. If it's not null, the Optional contains it.

`Optional.empty()`: Explicitly creates an empty Optional.

```java
Optional<String> some = Optional.of("hello");  // contains "hello"
Optional<String> none = Optional.ofNullable(null);  // empty
Optional<String> empty = Optional.empty();  // also empty
```

Now, how do you use it? Several methods:

`isPresent()` returns true if the Optional has a value. The old-school way:

```java
if (user.isPresent()) {
    User u = user.get();
    // ...
}
```

But that's verbose. Better options:

`ifPresent(Consumer)` executes a Consumer if the value is present:

```java
user.ifPresent(u -> System.out.println(u.getName()));
```

`get()` extracts the value. Use carefully—it throws an exception if empty. Generally, avoid this.

`orElse(defaultValue)` returns the value if present, or a default:

```java
String name = userName.orElse("Unknown");
```

`orElseGet(Supplier)` like orElse, but lazily computes the default:

```java
String name = userName.orElseGet(() -> "User " + generateId());
```

`orElseThrow()` throws an exception if empty. Explicitly fails:

```java
String name = userName.orElseThrow();  // throws if empty
```

Now, here's where it gets elegant. You can chain operations on Optional without null checks:

```java
Optional<User> user = userService.findById(123);
Optional<String> name = user.map(User::getName);
Optional<String> upper = name.map(String::toUpperCase);
upper.ifPresent(System.out::println);
```

`map(Function)` transforms the value if present, returning a new Optional. If the Optional is empty, the map is skipped, and you get an empty Optional back. No null checks needed anywhere.

If you have a function that itself returns an Optional, you use `flatMap(Function)` instead of `map()`:

```java
Optional<User> user = userService.findById(123);
Optional<Contact> contact = user.flatMap(u -> u.getContact());
```

If you used `map()` here, you'd get `Optional<Optional<Contact>>`, which is messy. `flatMap()` flattens it to `Optional<Contact>`. Much cleaner.

You can also filter:

```java
Optional<User> user = userService.findById(123);
Optional<User> active = user.filter(u -> u.isActive());
```

If the condition is false, you get an empty Optional.

---

[34:00-40:00] **Common Mistakes with Lambdas and Optional**

Let me highlight some mistakes I see developers make.

**Mistake one: Using Optional for method parameters.** 

```java
// DON'T DO THIS
void setUser(Optional<User> user) { ... }
```

Why? Because if the caller wants to pass a null, they have to wrap it: `setUser(Optional.ofNullable(null))`. That's extra work for no benefit. Just check null in the method:

```java
// DO THIS
void setUser(User user) {
    if (user == null) throw new IllegalArgumentException("User cannot be null");
    // ...
}
```

Or, better, use an annotation like `@NonNull` to signal to your IDE and code analysis tools that null isn't allowed.

**Mistake two: Chaining `get()` blindly.**

```java
// DON'T
Optional<User> user = findUser();
Optional<String> name = user.map(User::getName).flatMap(n -> Optional.ofNullable(n.toUpperCase()));
String result = name.get();  // What if empty? Exception!
```

You've defeated the purpose of Optional. You're back to handling exceptions or crashes.

Instead:

```java
// DO
Optional<User> user = findUser();
user.map(User::getName)
    .filter(n -> !n.isEmpty())
    .map(String::toUpperCase)
    .ifPresent(System.out::println);
```

Chain your operations and handle the empty case explicitly.

**Mistake three: Not using `filter()` effectively.**

```java
// VERBOSE
Optional<User> user = findUser();
if (user.isPresent()) {
    User u = user.get();
    if (u.isActive()) {
        process(u);
    }
}

// CLEAN
findUser()
    .filter(User::isActive)
    .ifPresent(this::process);
```

That's the power of composition.

**Mistake four: Using `Optional<Integer>` instead of `OptionalInt`.**

When you're working with primitives, use the primitive-specific versions: `OptionalInt`, `OptionalLong`, `OptionalDouble`. They avoid boxing overhead:

```java
// Don't
Optional<Integer> count = Optional.of(5);

// Do
OptionalInt count = OptionalInt.of(5);
```

For small collections, it doesn't matter. For large ones, or hot code paths, it matters.

---

[40:00-46:00] **Real-World Examples**

Let me tie this together with some realistic examples.

**Configuration loading:** Suppose you're loading configuration from environment variables, with fallbacks:

```java
Optional<String> fromEnv = Optional.ofNullable(System.getenv("API_KEY"));
String apiKey = fromEnv
    .filter(s -> !s.isEmpty())
    .orElseGet(() -> configFile.get("api.key"))
    .orElse("default-key");
```

Read an environment variable. If present and not empty, use it. Otherwise, try the config file. Otherwise, use a default. All in one readable chain.

**Event handlers in UI code:**

```java
button.setOnClick(event -> handleClick(event));
```

Or with method reference:

```java
button.setOnClick(this::handleClick);
```

The lambda or method reference passes behavior directly. No anonymous inner class needed.

**Filtering a collection:**

```java
List<User> activeUsers = users.stream()
    .filter(u -> u.isActive())
    .filter(u -> u.isPremium())
    .collect(Collectors.toList());
```

We'll see stream syntax in Part 2, but notice: you're passing predicates as lambdas, chaining filters, expressing exactly what you want.

**Transformations:**

```java
List<String> names = users.stream()
    .map(User::getName)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

Transform each user to a name, then uppercase it. Method references keep it concise.

---

[46:00-52:00] **Best Practices Summary**

Let me crystallize the key takeaways:

One: Write concise lambdas. If your lambda spans multiple lines, consider extracting it to a named method. Lambdas should be brief, readable expressions of intent.

Two: Use functional interfaces from `java.util.function` when you can. Don't create custom functional interfaces for common patterns.

Three: Understand the four core interfaces: Predicate (test), Consumer (accept), Supplier (get), Function (apply). Master these, and everything else follows.

Four: Method references are your friend. When an existing method does what you need, use a method reference instead of a verbose lambda.

Five: Use Optional explicitly for methods that might return empty. Don't use Optional for fields or parameters. It's for return types and intermediate values.

Six: Chain Optional operations with `map()`, `flatMap()`, and `filter()`. Avoid extracting with `get()` and then null-checking. Let the Optional chain handle absence.

Seven: Remember that lambdas can access variables from their surrounding scope, but those variables must be effectively final. If you need mutable state, use a class field.

Eight: Understand that checked exceptions are tricky with lambdas. If your lambda throws a checked exception, the functional interface must declare it. Otherwise, wrap it or use a different interface.

---

[52:00-58:00] **Connection to Part 2 & Stream API**

Now, all of this—lambdas, functional interfaces, Optional—is the foundation for what's coming in Part 2.

The Stream API is a way to process sequences of data—collections, for example—in a functional, composable way. Instead of writing loops, you describe transformations: filter, map, reduce. And how do you pass those transformations? Lambdas. How do you represent the absence of a result? Optional.

In Part 2, you'll see patterns like:

```java
list.stream()
    .filter(x -> x > 10)
    .map(x -> x * 2)
    .forEach(System.out::println);
```

Each of those operations—`filter`, `map`, `forEach`—expects a functional interface. `filter` expects a `Predicate`. `map` expects a `Function`. `forEach` expects a `Consumer`. You'll pass lambdas or method references. And if you're unsure whether a stream has any elements, you'll use Optional.

So this is the foundation. Understand lambdas and functional interfaces well, and the Stream API clicks into place instantly.

---

[58:00-60:00] **Wrap-Up & Transition**

Alright, let's recap. Lambdas give us concise syntax for implementing functional interfaces. Functional interfaces are the bridge between traditional OOP and functional programming. Method references provide even more concise syntax. And Optional forces us to handle the absence of values explicitly, eliminating entire classes of null pointer exceptions.

These features transform how you write Java. Code becomes cleaner, more expressive, easier to test.

In a few moments, we'll take a short break. When we come back for Part 2, you'll see these concepts in action with the Stream API. We'll process collections in ways that feel almost like magic—powerful, composable transformations. And then we'll touch on the DateTime API.

For now, let the ideas sink in. Practice writing lambdas. Play with Optional. Trust that it'll all come together.

See you in a few minutes for Part 2!

