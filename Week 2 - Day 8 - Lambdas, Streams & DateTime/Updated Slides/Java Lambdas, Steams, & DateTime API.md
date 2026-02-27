SLIDE 1: Today's Agenda

Lambda Expressions — syntax, use cases
Functional Interfaces — Predicate, Function, Consumer, Supplier
Method References — shorthand for lambdas
Optional — eliminating NullPointerExceptions
Stream API — creation, intermediate & terminal operations
Common Stream Operations — filter, map, reduce, collect
DateTime API — LocalDate, LocalTime, LocalDateTime, Period, Duration, DateTimeFormatter

SPEAKER SCRIPT:
"Good [morning/afternoon], everyone. Today we're covering three of the most powerful and widely-used features introduced in modern Java — Lambdas, Streams, and the DateTime API. These topics are heavily used in real-world Java development and are commonly tested in interviews, so pay close attention."
"By the end of this lesson you will be able to write lambda expressions to replace verbose anonymous classes, use functional interfaces like Predicate and Function, chain Stream operations to process collections of data, handle null values safely using Optional, and work confidently with the Java DateTime API."
"We have a lot to cover, so let's get started."


SECTION 2: Lambda Expressions [~10 min]

SLIDE 2: What Is a Lambda Expression?

A lambda is a short block of code that takes parameters and returns a value
Think of it as an anonymous function — no name, no class needed
Introduced in Java 8 to enable functional-style programming
Replaces verbose anonymous inner classes
Syntax:  (parameters) -> expression   OR   (parameters) -> { statements; }

SPEAKER SCRIPT:
"Before Java 8, whenever you needed to pass behavior — for example, how to sort a list, or what to do when a button is clicked — you had to write an anonymous inner class. That's a full class declaration with brackets and boilerplate just to define a single method. It was wordy and hard to read."
"Java 8 introduced Lambda Expressions to fix this. A lambda lets you express that same behavior in one concise line. Think of a lambda as a mini function with no name. It has three parts: the parameter list in parentheses, the arrow operator ->, and then the body — which is either a single expression or a block of statements in curly braces."

SLIDE 3: Lambda Syntax — All Forms

No parameters:           () -> System.out.println("Hello")
One parameter (inferred): x -> x * x
One parameter (explicit): (int x) -> x * x
Multiple parameters:      (a, b) -> a + b
Block body:               (a, b) -> { int sum = a + b; return sum; }
Rule: if the body is a block, you MUST use an explicit return statement

SPEAKER SCRIPT:
"Let's walk through every syntax form. If there are no parameters, you write empty parentheses then the arrow. If there is exactly ONE parameter and you let Java infer the type, you can drop the parentheses entirely — just write x -> x * x. If you want to be explicit about the type, wrap it in parentheses. Multiple parameters always need parentheses."
"The body can be a single expression — no braces, no return keyword needed, Java returns it automatically. Or it can be a full block in curly braces, in which case you DO need an explicit return statement if the interface method returns something. If the method returns void, no return needed."
"One critical rule to remember before we move on: a lambda can only be used where the expected type is a functional interface — an interface with exactly one abstract method. We'll cover that next."

SLIDE 4: Lambda — Before vs. After

BEFORE (Anonymous inner class):

Runnable r = new Runnable() {

public void run() {


    System.out.println("Running!");


}

};


AFTER (Lambda):

Runnable r = () -> System.out.println("Running!");


Same behavior — one line instead of five
The compiler knows Runnable has one method (run), so your lambda IS that method

SPEAKER SCRIPT:
"Here's a side-by-side comparison. On top is the old way — create a new anonymous Runnable, override run(), write the body. That's five lines for one simple action. On the bottom, the lambda replaces all of that with one line."
"The compiler is doing work for you here. It sees that Runnable has exactly one abstract method called run, so it figures out that your lambda body IS the implementation of run. You don't have to say it explicitly."
"This isn't just about fewer lines — it makes code far more readable, especially when you're chaining many operations together, which you'll see when we get to Streams."
"Any questions on lambda syntax before we move on?"


SECTION 3: Functional Interfaces [~12 min]

SLIDE 5: What Is a Functional Interface?

An interface with exactly ONE abstract method
@FunctionalInterface annotation — optional, but recommended
If you add it and accidentally write two abstract methods, the compiler will error
Can still have default and static methods — that's fine
Your lambda expression IS the implementation of that one abstract method
Java provides built-in ones in the java.util.function package

SPEAKER SCRIPT:
"A Functional Interface is simply an interface that declares exactly one abstract method. That single method is what your lambda will implement. The @FunctionalInterface annotation is optional but highly recommended — if you add it and accidentally write two abstract methods, the compiler gives you an error immediately instead of failing at runtime in a confusing way."
"Java ships with many pre-built functional interfaces so you don't have to define your own in most situations. The four you MUST know are Predicate, Function, Consumer, and Supplier. Let's look at each one."

SLIDE 6: Writing Your Own Functional Interface

You can define your own — any interface with one abstract method qualifies
Example:

@FunctionalInterface
public interface Transformer {

String transform(String input);

}


Usage:

Transformer upper = s -> s.toUpperCase();
Transformer shout = s -> s + "!!!";
System.out.println(upper.transform("hello"));   // HELLO
System.out.println(shout.transform("hello"));   // hello!!!



SPEAKER SCRIPT:
"Before we get to the built-in ones, I want you to understand what's actually happening under the hood by writing one yourself. Here we define a Transformer interface with one method: transform, which takes a String and returns a String. That's it — one abstract method, so it's a functional interface."
"Now we can assign a lambda directly to a variable of that type. The lambda s -> s.toUpperCase() IS the implementation of the transform method. You can create two completely different behaviors from the same interface just by changing the lambda. This is the power of functional programming — behavior becomes a value you can pass around."
"In practice you'll mostly use the built-in ones, but understanding this foundation is important."

SLIDE 7: Predicate<T> — Test a Condition

Method:    boolean test(T t)
Purpose:   Answer a yes/no question about a value
Example:

Predicate<String> isLong = s -> s.length() > 5;
isLong.test("Hello");       // false
isLong.test("Hello World"); // true


Compose predicates:

isLong.and(anotherPred)   — BOTH must be true
isLong.or(anotherPred)    — EITHER must be true
isLong.negate()           — flips the result



SPEAKER SCRIPT:
"Predicate takes one input of type T and returns a boolean. It answers a yes/no question about the input. Is this string long enough? Is this number even? Is this user active?"
"You'll use Predicates constantly with the Stream filter() method, which we'll see later. You can also compose predicates together — .and() requires both to be true, .or() requires at least one, and .negate() flips true to false and vice versa. This lets you build complex conditions from simple building blocks without writing messy nested if statements."

SLIDE 8: Function<T, R> — Transform a Value

Method:    R apply(T t)
Purpose:   Convert or transform — takes T, produces R
T and R can be completely different types
Example:

Function<String, Integer> strLen = s -> s.length();
strLen.apply("Hello");  // returns 5


Chain transformations:

Function<String, Integer> lenThen = strLen.andThen(n -> n * 2);
lenThen.apply("Hello"); // 10


Variant: UnaryOperator<T> when input and output are the SAME type

SPEAKER SCRIPT:
"Function takes an input of type T and returns a result of type R — and T and R can be completely different types. It's a transformation or conversion. In the example, we transform a String into an Integer by getting its length."
"You can chain Functions using andThen() — the output of the first becomes the input of the second. If the input and output type happen to be the same, like String in and String out, use UnaryOperator instead — it's a cleaner, more descriptive type. You'll see Function used heavily in Stream map() operations."

SLIDE 9: Consumer<T> — Perform an Action, Return Nothing

Method:    void accept(T t)
Purpose:   Side effects — printing, saving, logging, sending
Example:

Consumer<String> printer = s -> System.out.println(s);
printer.accept("Hello");  // prints: Hello


Chain consumers:

Consumer<String> printAndLog = printer.andThen(s -> logger.log(s));
printAndLog.accept("Hello"); // prints AND logs


Used in Stream forEach()

SPEAKER SCRIPT:
"Consumer takes one input and returns nothing — the return type is void. It's for actions that have a side effect: printing to console, writing to a file, sending a notification, updating a database record. The result doesn't go anywhere; the action itself is the point."
"You can chain consumers using andThen() — both consumers run in sequence on the same input. You'll see Consumer used directly in Stream's forEach() method."

SLIDE 10: Supplier<T> — Produce a Value

Method:    T get()
Purpose:   Provide a value on demand — no input required
Example:

Supplier<LocalDate> today = () -> LocalDate.now();
today.get();  // returns today's date (only computed when called)


Lazy evaluation — value is not computed until get() is called
The opposite of Consumer: no input, only output
Used in Optional.orElseGet()

SPEAKER SCRIPT:
"Supplier is the opposite of Consumer — it takes no input and returns a value. It's used for lazy or deferred evaluation: you define HOW to produce a value, but the value isn't actually computed until get() is called."
"This is especially useful when producing the default value is expensive — like a database call or a network request. If you use orElse() on Optional, the default is always computed even if it's never needed. If you use orElseGet() and pass a Supplier, the default is only computed when actually needed. We'll see this in action shortly."

SLIDE 11: Functional Interface Quick Reference

Predicate<T>         →  T in,  boolean out    —  "Does this pass a test?"
Function<T, R>       →  T in,  R out          —  "Transform this value"
Consumer<T>          →  T in,  nothing out    —  "Do something with this"
Supplier<T>          →  nothing in, T out     —  "Give me a value"
BiPredicate<T, U>    →  T and U in, boolean out
BiFunction<T, U, R>  →  T and U in, R out
BiConsumer<T, U>     →  T and U in, nothing out
UnaryOperator<T>     →  T in,  T out  (special case of Function)

SPEAKER SCRIPT:
"Here's the full quick reference. The first four are the core ones you'll use every single day — memorize these. Predicate tests, Function transforms, Consumer acts, Supplier produces."
"The Bi- variants are for when you need TWO inputs instead of one. BiFunction<T, U, R> takes a T and a U and returns an R. BiPredicate takes two inputs and returns a boolean. BiConsumer takes two inputs and returns nothing. You'll encounter these when working with Maps or comparing two values."
"UnaryOperator is just a cleaner way to write a Function where the input and output type are the same. Any questions before we move on to method references?"


SECTION 4: Method References [~5 min]

SLIDE 12: Method References — Even Shorter Lambdas

Shorthand syntax when a lambda does nothing except call ONE existing method
Uses the :: (double colon) operator
Four types:



Static method:              Math::abs




Unbound instance method:    String::toUpperCase




Bound instance method:      myObject::myMethod




Constructor reference:      ArrayList::new




Rule: only use when the lambda ONLY calls one method — nothing else

SPEAKER SCRIPT:
"Method references are a further shorthand on top of lambdas. If your lambda does nothing except call a single existing method, you can replace the whole thing with a method reference using the double colon operator."
"There are four types. Static method references use ClassName::method. Unbound instance method references also use ClassName::method, but on a non-static method — Java passes the object as the first argument, so String::toUpperCase means 'call toUpperCase on whatever String is passed in.' Bound instance references are tied to a specific object you already have. Constructor references use ClassName::new to create new objects — useful when you need a Supplier that creates instances."

SLIDE 13: Method Reference Examples — Lambda vs. Reference

// Unbound instance method
list.forEach(s -> System.out.println(s));
list.forEach(System.out::println);        ← same thing

// Unbound instance method on type
list.stream().map(s -> s.toUpperCase());
list.stream().map(String::toUpperCase);   ← same thing

// Static method reference
list.stream().map(s -> Integer.parseInt(s));
list.stream().map(Integer::parseInt);     ← same thing

// Constructor reference
list.stream().map(s -> new StringBuilder(s));
list.stream().map(StringBuilder::new);   ← same thing

SPEAKER SCRIPT:
"Here are the most common patterns you'll see. System.out::println is the most classic — it replaces s -> System.out.println(s) completely. String::toUpperCase means 'for each element, call toUpperCase on it.' Integer::parseInt is a static method reference. StringBuilder::new is a constructor reference."
"The rule is simple: if your lambda is just x -> SomeMethod(x), replace it with SomeMethod reference. If the lambda is doing ANYTHING else — calculations, conditionals, multiple method calls — keep it as a lambda. Method references are about readability. Use them when they make the code cleaner, skip them when they'd make it more confusing."


SECTION 5: Optional — Avoiding NullPointerExceptions [~8 min]

SLIDE 14: The Problem — NullPointerException

NPE is one of the most common Java runtime errors
Happens when you call a method or access a field on a null reference
Old approach: wrap everything in if (obj != null) { ... } — clutters code
Easy to forget a null check, especially deep in nested calls
Optional<T> is the solution — a container that either holds a value OR is empty
Makes nullability explicit and part of the method's contract

SPEAKER SCRIPT:
"NullPointerException — or NPE — is probably the most famous Java bug. It happens when you try to use a variable that is null. The traditional fix was to add null checks everywhere, which cluttered code and was easy to forget. If you forgot one null check three method calls deep, your program crashed at runtime."
"Java 8 introduced Optional to fix this. Optional is a container — it either holds a non-null value, or it's empty. When a method returns Optional instead of a raw value, it's saying: 'I might not have a result for you — handle that explicitly.' It makes the possibility of no value visible in the code instead of hiding it in documentation."

SLIDE 15: Creating Optional Objects

Optional.of(value)        — value MUST be non-null (NPE if null — intentional)
Optional.ofNullable(v)    — value CAN be null (returns empty Optional safely)
Optional.empty()          — explicitly empty Optional

Examples:

Optional<String> a = Optional.of("Hello");
Optional<String> b = Optional.ofNullable(null);  // empty, no exception
Optional<String> c = Optional.empty();



Use ofNullable() when working with legacy code that may return null

SPEAKER SCRIPT:
"There are three factory methods. Optional.of() is for when you KNOW the value is not null — if you accidentally pass null it throws NPE immediately, which is intentional because it means your code has a bug. Optional.ofNullable() is the safe wrapper for values that might be null — it returns an empty Optional instead of throwing. Optional.empty() creates an explicitly empty Optional — use this when returning from a method that found nothing."

SLIDE 16: Optional — Core Retrieval Methods

isPresent()          — returns true if a value is present
isEmpty()            — returns true if empty (Java 11+)
get()                — returns the value, BUT throws NoSuchElementException if empty

                    NEVER call get() without checking isPresent() first

orElse(T default)    — return value OR the default if empty
orElseGet(Supplier)  — return value OR call supplier to compute default (lazy)
orElseThrow()        — return value OR throw NoSuchElementException

Example:

Optional<String> opt = Optional.ofNullable(getName());
String name = opt.orElse("Unknown");
String name = opt.orElseGet(() -> loadDefaultFromDB());



SPEAKER SCRIPT:
"Now the methods. The most important warning: never call get() blindly. If the Optional is empty, get() throws a NoSuchElementException. That's just trading one exception for another. Instead, always use the safe methods."
"orElse() is what you'll use most — give me the value, or if empty, use this fallback. orElseGet() is the lazy version — it takes a Supplier, so the fallback is only computed if the Optional is actually empty. Use orElseGet() when computing the default is expensive, like loading from a database. orElseThrow() is appropriate when an empty result truly is an error condition."

SLIDE 17: Optional — Transformation Methods

ifPresent(Consumer)    — run an action ONLY if value is present
map(Function)          — transform the value inside if present, returns Optional
filter(Predicate)      — keep value only if it passes the test, returns Optional

Examples:

opt.ifPresent(s -> System.out.println(s));
opt.ifPresent(System.out::println);   // same with method ref

opt.map(String::toUpperCase)
.ifPresent(System.out::println);   // transform then act

opt.filter(s -> s.length() > 3)
.ifPresent(System.out::println);   // only if length > 3



SPEAKER SCRIPT:
"These three methods let you work with the value inside an Optional without ever unwrapping it unsafely. ifPresent() runs a Consumer only if the value is there — nothing happens if empty. map() transforms the value and wraps the result in a new Optional — if the original was empty, you get back an empty Optional. filter() keeps the value only if it passes a test, otherwise returns empty."
"You can chain these: transform the value, filter it, then act on it — all without a single null check or risk of NPE."

SLIDE 18: Optional — Before and After

BEFORE (null checks):

String result = findUser(id);
if (result != null) {

String upper = result.toUpperCase();


System.out.println(upper);

}



AFTER (Optional):

findUser(id)

.map(String::toUpperCase)


.ifPresent(System.out::println);




RULE: Use Optional as a RETURN TYPE from methods — not as fields, not as parameters

SPEAKER SCRIPT:
"Here's a before-and-after. The old code has an explicit null check, a temp variable, and is easy to forget. The Optional version chains map and ifPresent — it reads almost like plain English: find the user, uppercase their name, print it if present."
"Important rule: Optional is designed to be a return type from methods that might not find a result. Don't use Optional as a field in a class — use null there. Don't use Optional as a method parameter — use overloading instead. This is the intended design pattern from the Java team."
"Any questions on Optional before we move to Streams?"


SECTION 6: Stream API Basics [~8 min]

SLIDE 19: What Is the Stream API?

A Stream is a sequence of elements supporting pipeline-style processing
NOT a data structure — does NOT store your data
Wraps a source (collection, array, I/O channel) and processes elements through it
Lazy — intermediate operations do NOTHING until a terminal operation is called
Single-use — once consumed, a stream CANNOT be reused
Supports method chaining for clean, expressive data pipelines

SPEAKER SCRIPT:
"The Stream API is one of the most powerful features of modern Java. A Stream is not a collection — it does not store your data. Think of it as a pipeline or an assembly line: you hook it up to a data source, describe a series of processing steps, and pull out the results at the end."
"Two critical characteristics you must understand. First, Streams are lazy — when you call filter() or map(), nothing actually happens yet. Those operations just describe what SHOULD happen. The pipeline only actually executes when you call a terminal operation at the end. This laziness is what allows the JVM to optimize the pipeline."
"Second, Streams are single-use. Once you call a terminal operation, the stream is closed and cannot be used again. If you need to process the same data twice, call .stream() again on the source collection to get a fresh stream."

SLIDE 20: Don't Reuse a Stream — Common Mistake

Streams can only be consumed ONCE
This will throw IllegalStateException:

Stream<String> s = list.stream().filter(x -> x.length() > 3);
s.forEach(System.out::println);  // OK — stream consumed here
s.count();                        // THROWS — stream already closed!



Correct approach — create a new stream each time:

list.stream().filter(x -> x.length() > 3).forEach(System.out::println);
list.stream().filter(x -> x.length() > 3).count();



SPEAKER SCRIPT:
"Here's a mistake new developers make constantly. They store a stream in a variable, use it once, then try to use it again. The second use throws an IllegalStateException: stream has already been operated upon or closed."
"The fix is simple: don't store streams in variables unless you're only going to use them once. Call .stream() fresh each time you need to process the collection. Streams are cheap to create — there is no performance reason to reuse them."

SLIDE 21: Creating Streams

From a Collection:    list.stream()
From an array:        Arrays.stream(arr)
Specific values:      Stream.of("a", "b", "c")
Empty stream:         Stream.empty()
Integer range:        IntStream.range(1, 10)        // 1 to 9

                   IntStream.rangeClosed(1, 10)  // 1 to 10

Infinite (must use limit!):

Stream.iterate(0, n -> n + 2)          // 0, 2, 4, 6 ...
Stream.generate(Math::random)          // random numbers forever
Stream.iterate(0, n -> n + 2).limit(5) // CORRECT — stops at 5



SPEAKER SCRIPT:
"The most common source is calling .stream() on any Collection — List, Set, anything that extends Collection. Arrays.stream() handles arrays. Stream.of() is useful for quick one-off streams in tests or examples."
"IntStream.range() is essentially a for loop as a stream — range gives you 1 up to but not including 10, rangeClosed includes the end value. Use these when you need sequential integers."
"Infinite streams with iterate and generate are interesting — they produce values forever. You MUST call limit() somewhere in the pipeline before a terminal operation or your program will run indefinitely. Never forget limit() on an infinite stream."

SLIDE 22: Stream Pipeline — Three Mandatory Parts



SOURCE          — where the data comes from


list.stream()





INTERMEDIATE    — zero or more transformation/filter steps


.filter(...)       returns a new Stream — LAZY, nothing runs yet
.map(...)          returns a new Stream — LAZY, nothing runs yet
.sorted(...)       returns a new Stream — LAZY, nothing runs yet





TERMINAL        — triggers execution, produces a result


.collect(...)      returns a Collection
.forEach(...)      returns void
.count()           returns long



Without a terminal operation, NOTHING in the pipeline ever executes

SPEAKER SCRIPT:
"Every Stream pipeline has exactly these three parts. The source creates the stream. Intermediate operations are the steps — they return a new Stream, which is why you can chain them. Crucially, they are lazy — nothing executes yet. The terminal operation is the trigger. When you call collect() or forEach() or count(), that's when the JVM walks through the entire pipeline and processes every element."
"Think of it like building an assembly line in a factory. You set up all the machines and conveyor belts — that's your intermediate operations. But the factory doesn't start running until someone presses the ON button — that's your terminal operation. If you never press ON, nothing ever gets made."
"Any questions before we go through each individual operation?"


SECTION 7: Common Stream Operations [~12 min]

SLIDE 23: filter() — Keep What Passes the Test

Takes a Predicate — keeps elements where test() returns true, drops the rest
Signature: Stream<T> filter(Predicate<T> predicate)
Does NOT modify the original collection

Examples:

// Keep only even numbers
List<Integer> evens = numbers.stream()

.filter(n -> n % 2 == 0)


.collect(Collectors.toList());


// Keep only non-empty strings
List<String> nonEmpty = words.stream()

.filter(s -> !s.isEmpty())


.collect(Collectors.toList());




SPEAKER SCRIPT:
"filter() is the most intuitive stream operation. You hand it a Predicate — a lambda returning true or false — and the stream keeps only the elements where the Predicate returns true. Everything else is dropped from the pipeline. The original list is never modified — you get a new filtered result."
"Notice that collect() at the end gathers the surviving elements into a new List. We'll cover collect() in detail shortly."

SLIDE 24: map() — Transform Every Element

Takes a Function — applies it to every element, replacing it with the result
Signature: Stream<R> map(Function<T, R> mapper)
Input type and output type CAN be different

Examples:

// Extract names from Employee objects (Employee -> String)
List<String> names = employees.stream()

.map(emp -> emp.getName())


.collect(Collectors.toList());


// Get lengths of strings (String -> Integer)
List<Integer> lengths = words.stream()

.map(String::length)


.collect(Collectors.toList());


Variants: mapToInt(), mapToLong(), mapToDouble() for primitive streams



SPEAKER SCRIPT:
"map() transforms every element in the stream. You provide a Function that takes one type and returns another — they don't have to be the same type. In the first example, we start with a stream of Employee objects and end up with a stream of Strings by extracting each employee's name. In the second, Strings become Integers by measuring their length."
"There are specialized variants — mapToInt(), mapToLong(), mapToDouble() — that produce primitive IntStream, LongStream, and DoubleStream. These are more memory-efficient when you're working with numbers and also give you access to built-in math operations like .sum() and .average() directly on the stream."

SLIDE 25: reduce() — Combine Everything Into One Value

Combines all elements into a single result using a BinaryOperator
Two forms:

With identity:    T reduce(T identity, BinaryOperator<T> accumulator)
Without identity: Optional<T> reduce(BinaryOperator<T> accumulator)



Examples:

// Sum with identity (always returns int, never Optional)
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
int sum = numbers.stream().reduce(0, Integer::sum); // same

// Max without identity (returns Optional — stream could be empty)
Optional<Integer> max = numbers.stream()

.reduce((a, b) -> a > b ? a : b);


Tip: for sum/min/max, prefer IntStream.sum() / .min() / .max()



SPEAKER SCRIPT:
"reduce() folds the entire stream down into one single value. You provide a starting value called the identity and an accumulator function that combines two values into one. For summing, the identity is 0. The accumulator starts with 0, combines it with the first element, then combines that result with the second element, and so on until all elements are processed."
"Without an identity value, reduce() returns an Optional — because if the stream is empty, there's no result to return. With an identity, you always get a concrete value even on an empty stream — you'd just get back the identity itself."
"One practical tip: for the most common reductions — sum, min, max — you don't need reduce() at all. If you call mapToInt() first, the resulting IntStream has built-in .sum(), .min(), and .max() methods that are simpler to read. Save reduce() for custom aggregations that don't have a shortcut."

SLIDE 26: collect() — Basic Collectors

Terminal operation — gathers stream elements into a new container
Powered by the Collectors utility class

Most common collectors:

Collectors.toList()              → new ArrayList with results
Collectors.toSet()               → new HashSet (removes duplicates)
Collectors.toUnmodifiableList()  → immutable list (Java 10+)

Collectors.joining()             → concat all strings: "abcdef"
Collectors.joining(", ")         → with delimiter: "a, b, c"
Collectors.joining(", ", "[", "]") → with prefix/suffix: "[a, b, c]"

Example:
String result = names.stream().collect(Collectors.joining(", "));
// "Alice, Bob, Charlie"



SPEAKER SCRIPT:
"collect() is the most versatile terminal operation because it can produce many different types of results depending on which Collector you pass it. The Collectors utility class has all the recipes you need."
"toList() is what you'll use the majority of the time. toSet() automatically removes duplicates because Set doesn't allow them. joining() is excellent for producing formatted strings — the three-argument version lets you add a delimiter between elements, a prefix at the start, and a suffix at the end. Very useful for building output strings."

SLIDE 27: collect() — Advanced Collectors

Collectors.counting()        → count how many elements
Collectors.groupingBy(fn)    → Map<K, List<V>> — groups by a key
Collectors.toMap(keyFn, valFn) → Map with custom key and value

groupingBy example:

// Group employees by department
Map<String, List<Employee>> byDept = employees.stream()

.collect(Collectors.groupingBy(Employee::getDepartment));




toMap example:

// Map employee ID to employee name
Map<Integer, String> idToName = employees.stream()

.collect(Collectors.toMap(


    Employee::getId,


    Employee::getName


));




SPEAKER SCRIPT:
"groupingBy() is one of the most powerful collectors — give it a function that extracts a key from each element, and it builds a Map where each key holds a List of all elements that produced that key. In one line you can group hundreds of employees by department, orders by status, products by category — no manual looping required."
"toMap() gives you full control over both the key and the value. In the example, we map each employee's ID to their name. One caution with toMap: if two elements produce the same key, it throws an exception by default. If you expect duplicates, there's a three-argument version where you provide a merge function to resolve conflicts."

SLIDE 28: Other Important Operations

Intermediate operations:

sorted()                              — natural order
sorted(Comparator.comparing(Person::getAge)) — sort by field
distinct()                            — remove duplicates (uses equals())
limit(n)                              — keep only first n elements
skip(n)                               — discard first n elements
peek(Consumer)                        — inspect elements (useful for debugging)



Terminal operations:

count()                               — returns number of elements as long
findFirst()                           — returns Optional<T> of first element
anyMatch(Predicate)                   — true if ANY element matches
allMatch(Predicate)                   — true if ALL elements match
noneMatch(Predicate)                  — true if NO elements match



SPEAKER SCRIPT:
"sorted() sorts the stream — pass it Comparator.comparing() with a method reference to sort by any field. distinct() removes duplicates using the equals() method, so make sure your objects have equals() properly implemented. limit() and skip() work together for pagination — skip the first 20 and take the next 10."
"peek() is an intermediate operation that lets you inspect elements as they flow through the pipeline without modifying them. Use it for debugging — wrap your pipeline with .peek(System.out::println) to see what's passing through at each step."
"The match methods — anyMatch, allMatch, noneMatch — are terminal operations returning boolean. They short-circuit: anyMatch stops as soon as it finds one match, allMatch stops as soon as it finds one failure. Much faster than filtering and then counting."

SLIDE 29: Chaining Operations — Putting It Together

Problem: Given a list of people, get names of adults, sorted, as comma-separated string

Solution:

String result = people.stream()

.filter(p -> p.getAge() >= 18)       // keep adults


.map(Person::getName)                 // extract names


.sorted()                             // sort alphabetically


.collect(Collectors.joining(", "));   // combine to string




Read it left to right — it describes exactly what it does
No temp variables, no loops, no mutation of original list

SPEAKER SCRIPT:
"Here's a full pipeline combining what we've learned. Read it left to right, top to bottom — it says exactly what it does: start with people, keep the adults, get their names, sort them, join them into a comma-separated string. One expression. No loop counter, no temp variables, no risk of accidentally modifying the original list."
"This is the goal of the Stream API — code that reads like a description of the problem. Any questions before we move to the DateTime API?"


SECTION 8: Java DateTime API [~13 min]

SLIDE 30: Why a New DateTime API?

Old java.util.Date and java.util.Calendar had serious flaws:

Mutable — you could accidentally change a date object after creating it
Calendar months were 0-indexed: January = 0, December = 11
Date represented both a date AND a time — confusing and poorly designed
Not thread-safe — caused bugs in concurrent applications


Java 8 introduced java.time package (modeled after the Joda-Time library)
New API is: immutable, thread-safe, clearly named, consistent

SPEAKER SCRIPT:
"Java's original date and time handling was notoriously bad. java.util.Date was mutable — you could call setYear() on a date object and change it after the fact, which caused subtle bugs especially when passing dates between methods. Calendar's months were 0-indexed, so January was 0 and December was 11 — that caused endless off-by-one bugs in production code."
"Java 8 introduced the java.time package to replace all of that. The new classes are immutable — once you create a date, it cannot be changed. All operations return NEW objects. They're thread-safe. And the naming is intuitive. Let's go through the main classes."

SLIDE 31: LocalDate — Date Without Time

Represents: year, month, day — NO time, NO timezone
Use for: birthdays, holidays, deadlines, due dates

Creating:

LocalDate today = LocalDate.now();
LocalDate xmas  = LocalDate.of(2024, 12, 25);   // months are 1-indexed!
LocalDate d     = LocalDate.parse("2024-12-25");



Reading values:

today.getYear() / getMonth() / getDayOfMonth()
today.getDayOfWeek()  // returns DayOfWeek.MONDAY etc.



Arithmetic (always returns a NEW LocalDate — save it!):

LocalDate next = today.plusDays(7);
LocalDate prev = today.minusMonths(1);
boolean before = today.isBefore(xmas);



SPEAKER SCRIPT:
"LocalDate represents just a calendar date — year, month, day. No time of day. No timezone. Use it whenever you only care about the date itself: a birthday, a holiday, a project deadline."
"Notice that months are now 1-indexed — January is 1, December is 12. This was one of the most important fixes from the old Calendar API."
"LocalDate is immutable — when you call plusDays(), it does NOT modify today. It returns a brand new LocalDate. You MUST save it to a variable: LocalDate nextWeek = today.plusDays(7). If you just call today.plusDays(7) without capturing the result, nothing happens and you lose the value. This is the most common mistake with the DateTime API."

SLIDE 32: LocalTime — Time Without Date

Represents: hour, minute, second, nanosecond — NO date, NO timezone
Use for: business hours, alarm times, store open/close times

Creating:

LocalTime now  = LocalTime.now();
LocalTime noon = LocalTime.of(12, 0);
LocalTime t    = LocalTime.parse("09:30:00");



Reading values:

now.getHour() / getMinute() / getSecond()



Arithmetic (returns new LocalTime):

now.plusHours(2)
now.minusMinutes(30)
now.isBefore(noon)



Constants: LocalTime.MIDNIGHT (00:00) / LocalTime.NOON (12:00)

SPEAKER SCRIPT:
"LocalTime is the time-only counterpart to LocalDate — hours, minutes, seconds, and nanoseconds, with no date and no timezone. Use it when you care about a time of day that isn't tied to a specific date — business opening hours, a recurring alarm, a daily scheduled task."
"The same immutability rule applies — all arithmetic methods return new objects. The constants MIDNIGHT and NOON are convenient shortcuts you'll use often."

SLIDE 33: LocalDateTime — Date AND Time Combined

Combines LocalDate and LocalTime into one object — still NO timezone
Use for: meeting times, event timestamps, log entries

Creating:

LocalDateTime now = LocalDateTime.now();
LocalDateTime dt  = LocalDateTime.of(2024, 12, 25, 10, 30, 0);
LocalDateTime dt  = LocalDateTime.of(localDate, localTime);  // combine existing



Converting:

LocalDate date = dt.toLocalDate();
LocalTime time = dt.toLocalTime();



Modifying (always returns new object):

dt.plusHours(3).minusDays(1)
dt.withHour(9).withMinute(0).withSecond(0)  // set specific fields



SPEAKER SCRIPT:
"LocalDateTime combines a date and a time into one object — still without a timezone. Use this when you need both the date and the time: when does a meeting start, when was this log entry written, when does this event begin."
"You can create one from a LocalDate and a LocalTime using .of(date, time) — very useful when you have them stored separately and need to combine them. The with() methods let you set specific fields: dt.withHour(9) returns a new LocalDateTime identical to dt but with the hour set to 9."

SLIDE 34: DateTimeFormatter — Creating and Using Formatters

Used to FORMAT dates to strings AND PARSE strings to dates
Create a custom formatter:

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");



Format a date to String:

String s = LocalDate.now().format(fmt);     // "27/02/2026"



Parse a String to LocalDate:

LocalDate d = LocalDate.parse("27/02/2026", fmt);



Built-in formatters (no need to create these):

DateTimeFormatter.ISO_LOCAL_DATE      // "2026-02-27"
DateTimeFormatter.ISO_LOCAL_DATE_TIME // "2026-02-27T10:30:00"
DateTimeFormatter.ISO_LOCAL_TIME      // "10:30:00"



SPEAKER SCRIPT:
"DateTimeFormatter is how you convert between date objects and Strings. You'll need it any time you're displaying a date to a user or reading a date from user input."
"Create a formatter with ofPattern() and a pattern string, then call .format() on a date object to get a String, or LocalDate.parse() with the formatter to turn a String back into a date. The built-in formatters like ISO_LOCAL_DATE give you the standard international format without needing to write a pattern."

SLIDE 35: DateTimeFormatter — Pattern Letter Reference

Pattern letters (case matters!):

d   — day of month (1 or 2 digits)       dd  — day (always 2 digits)
M   — month number (1 or 2 digits)        MM  — month (always 2 digits)
MMM — month abbreviation (Jan, Feb...)    MMMM — full month name (January)
y   — year (variable)                     yyyy — year (always 4 digits)
H   — hour 0-23 (24-hour)                 h   — hour 1-12 (12-hour)
m   — minute                              s   — second
E   — day abbreviation (Mon, Tue...)      EEEE — full day name (Monday)
a   — AM/PM marker



Common examples:

"dd/MM/yyyy"         → 27/02/2026
"MMMM d, yyyy"       → February 27, 2026
"EEEE, dd MMMM yyyy" → Friday, 27 February 2026
"HH:mm:ss"           → 10:30:00



SPEAKER SCRIPT:
"The most common confusion with pattern letters is case. Capital M is month — lowercase m is minutes. Capital H is 24-hour clock — lowercase h is 12-hour clock. Get them confused and your dates will be completely wrong with no error message to warn you."
"The number of letters controls the format. One M gives you 1 or 2 digit month. Two M letters gives always two digits. Three M letters gives the abbreviation like Jan or Feb. Four M letters gives the full name like January. The same logic applies to days of week with E."
"Write these down — you'll reference this table constantly until it becomes second nature."

SLIDE 36: Period — Calendar-Based Time Difference

Measures time in years, months, and days — calendar units
Used with LocalDate (not LocalTime)

Calculating a period:

Period p = Period.between(startDate, endDate);
p.getYears() / getMonths() / getDays()



Creating a period:

Period sixMonths = Period.ofMonths(6);
LocalDate renewal = startDate.plus(sixMonths);



Classic use — calculate age:

LocalDate birthday = LocalDate.of(2000, 6, 15);
Period age = Period.between(birthday, LocalDate.now());
System.out.println(age.getYears() + " years old");



SPEAKER SCRIPT:
"Period measures time the way humans think about it — in years, months, and days. If someone says 'I was away for 2 years and 3 months,' that's a Period. Period.between() takes two LocalDate objects and tells you the gap between them in those human-readable units."
"The classic use case is calculating age — Period.between(birthday, LocalDate.now()).getYears() tells you how many complete years old someone is. Much simpler than doing date arithmetic manually."

SLIDE 37: Duration — Clock-Based Time Difference

Measures time in hours, minutes, seconds, nanoseconds — exact elapsed time
Used with LocalTime and LocalDateTime

Calculating a duration:

Duration d = Duration.between(startTime, endTime);
d.toHours() / toMinutes() / toSeconds()



Creating a duration:

Duration twoHours = Duration.ofHours(2);
LocalTime later = LocalTime.now().plus(twoHours);



Period vs. Duration — key distinction:

Period  = years / months / days  — human calendar
Duration = hours / minutes / seconds — exact clock time
Use Period with LocalDate
Use Duration with LocalTime / LocalDateTime



SPEAKER SCRIPT:
"Duration measures exact elapsed time — hours, minutes, seconds, nanoseconds. Where Period is for human calendar thinking, Duration is for machine-level time measurement. Use Duration for performance measurement, timeouts, or measuring how long something took to run."
"The key rule is simple: Period goes with LocalDate, Duration goes with LocalTime and LocalDateTime. If you try to call Duration.between() with two LocalDate objects, you'll get a compile error. If you mix them up in your own logic, your results will be wrong. Period for calendar, Duration for clock — write that down."


SECTION 9: Putting It All Together [~7 min]

SLIDE 38: Real-World Example 1 — Stream + Lambda

Problem: Given a list of users, get names of adults, sorted alphabetically

List<String> adultNames = users.stream()

.filter(u -> u.getAge() >= 18)   // Predicate lambda — keep adults


.map(User::getName)               // method reference — extract name


.sorted()                          // natural alphabetical order


.collect(Collectors.toList());     // terminal — gather into List


Line by line:

.filter   — uses a Predicate, drops users under 18
.map      — uses a Function (method ref), String comes out
.sorted   — intermediate, alphabetical
.collect  — terminal, triggers the pipeline, returns List<String>



SPEAKER SCRIPT:
"Let's walk through a complete real-world example line by line. We start with a list of User objects. filter() uses a Predicate lambda — keep only users whose age is 18 or more. map() uses a method reference — extract the name from each surviving User, turning our stream from Stream<User> into Stream<String>. sorted() alphabetizes the names. collect() fires the whole pipeline and gives us back a List<String>."
"Notice how each step changes the stream's type or content. The original user list is untouched. We get a brand new list of strings."

SLIDE 39: Real-World Example 2 — groupingBy

Problem: Group a list of employees by their department

Map<String, List<Employee>> byDept = employees.stream()

.collect(Collectors.groupingBy(Employee::getDepartment));


Result structure:

{
"Engineering" -> [Alice, Bob, Charlie],
"Marketing"   -> [Dave, Eve],
"HR"          -> [Frank]
}



No loops, no manual map building, no null checks
Access a department: byDept.get("Engineering")

SPEAKER SCRIPT:
"groupingBy() is one of the most powerful things in the Stream API. One line replaces what used to be a full loop with an if-statement, a map lookup, a null check, a list initialization, and a list add. It gives you a Map where each key is a department name and each value is the list of employees in that department. This is production-level Java."

SLIDE 40: Real-World Example 3 — Streams + DateTime

Problem: Format each user's birthday as "dd MMM yyyy" and collect to list

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

List<String> formatted = users.stream()

.map(u -> u.getBirthDate().format(fmt))


.collect(Collectors.toList());


Result: ["15 Jun 2000", "03 Jan 1998", "22 Dec 1995"]

Streams and DateTime working together:

map() with a lambda that calls .format() on a LocalDate
DateTimeFormatter defined once, reused across all elements



SPEAKER SCRIPT:
"Here we combine Streams with the DateTime API. We define a formatter once outside the stream, then use it inside a map() lambda. For each user, we get their birth date — a LocalDate — and call .format(fmt) on it to produce a formatted string. The formatter is defined outside the stream so it's created once and reused, not recreated for every element."
"This is the kind of code you'll write constantly as a Java developer — these three features working together."

SLIDE 41: Key Takeaways

Lambda expressions replace verbose anonymous inner classes
Functional interfaces define the contract — Predicate, Function, Consumer, Supplier
BiFunction / BiPredicate / BiConsumer for two-input scenarios
Method references (::) are shorthand when a lambda only calls one method
Optional makes nullability explicit — use orElse / ifPresent, avoid raw get()
Streams: source → intermediate (lazy) → terminal (triggers execution)
Streams are single-use — create a fresh stream each time
DateTime API is immutable — always capture the returned object
Period for calendar differences (dates), Duration for clock differences (time)

SPEAKER SCRIPT:
"These are the things you must walk away knowing. Lambdas make behavior concise and passable. The four core functional interfaces cover almost every use case. Method references are just shorthand — use them when they help readability. Optional is a return type, not a magic cure-all. Streams are lazy and single-use, and always need a terminal operation. DateTime operations always return new objects. Period is calendar, Duration is clock."


SECTION 10: Wrap-Up & Q&A [~5 min]

SLIDE 42: Practice Exercises



Write a Predicate<String> that returns true if the string contains at least 3 vowels




Write a Function<Double, Double> to convert Celsius to Fahrenheit (°F = °C × 9/5 + 32)




Given List<Integer>, use streams to find the sum of the squares of all even numbers




Use Optional to safely retrieve the first element of a list, or return "empty" if the list is empty




Calculate how many complete years and months between January 1, 2020 and today using Period




Format today's date as "EEEE, dd MMMM yyyy" using DateTimeFormatter



SPEAKER SCRIPT:
"Here are six exercises that cover everything from today. Try to complete all six without looking at your notes first — that's the real test. These are exactly the types of problems you'll encounter on assessments and in technical interviews. Exercise 3 requires chaining filter, map, and reduce or mapToInt. Exercise 6 uses the pattern letter table from Slide 35 — make sure you have that reference handy."

SLIDE 43: Q&A

What questions do you have about:

Lambda syntax and forms?
Predicate / Function / Consumer / Supplier?
BiFunction, BiPredicate, BiConsumer?
Method references?
Optional — creation, retrieval, transformation?
Stream pipeline, lazy evaluation, single-use rule?
filter / map / reduce / collect?
LocalDate / LocalTime / LocalDateTime?
DateTimeFormatter and pattern letters?
Period vs. Duration?


Next lesson: [your upcoming topic here]

SPEAKER SCRIPT:
"And that's our full lesson on Lambdas, Streams, and the DateTime API. These are genuinely powerful features — once they click, you'll find yourself reaching for them constantly and wondering how you ever wrote Java without them."
"If no questions come up: 'Let me ask you one — if I have a List<String> and I want to count how many strings have more than 4 characters, what's the one-liner using streams?' Answer: list.stream().filter(s -> s.length() > 4).count()"
"Take some time with the exercises before next class. See you then."