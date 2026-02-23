# Week 2 - Day 8, Part 1: Lambdas, Functional Interfaces & Optional
## Slide Descriptions (60-minute lecture)

### Slides 1-2: Welcome & Problem Statement
**Slide 1: Title Slide**
- Title: "Lambdas, Functional Interfaces & Optional"
- Subtitle: "Writing Concise, Functional Java Code"
- Welcome message and day overview
- Visual: Modern Java logo, lambda symbol (λ)

**Slide 2: Problem Statement**
- Show a typical Java anonymous inner class for a simple operation
- Verbose boilerplate code obscuring the actual logic
- Question: "What if we could write this more elegantly?"
- Teaser: Lambdas reduce boilerplate significantly
- Example: Traditional button click handler vs lambda version (preview)

### Slides 3-8: Functional Programming Fundamentals
**Slide 3: What is Functional Programming?**
- Core concept: Functions as first-class objects (can be passed as parameters, returned from methods, stored in variables)
- Functional style emphasizes: What to do, not how to do it
- Pure functions: No side effects, same input always produces same output
- Contrast with imperative programming (step-by-step instructions)
- Real-world benefit: Enables code reusability, testability, parallelization

**Slide 4: Functions vs Methods in Java**
- Traditional Java: Methods are tightly bound to classes
- Functional Java: Treat logic/behavior as first-class values
- Before lambdas: Callback patterns required verbose anonymous inner classes
- After lambdas: Pass behavior directly as expressions
- Example: Sorting a list with custom comparator (old vs new)

**Slide 5: Anonymous Inner Classes (The Old Way)**
- Show complete anonymous inner class implementation for Runnable
- Boilerplate: 6-7 lines of code for 1 line of logic
- Multiple levels of nesting, hard to read
- Problem: Verbosity masks the intent
- Example code with extensive syntax noise

**Slide 6: Lambda Expressions (The Modern Way)**
- Same Runnable implementation as lambda: `() -> System.out.println("Running")`
- Single line, crystal clear intent
- Syntax: `(parameters) -> {body}`
- Optional braces and return for single expressions
- Key insight: Syntax reduces ceremony, not functionality

**Slide 7: Lambda Syntax Breakdown**
- Components: Parameters, arrow (->), Body
- Parameter list variations:
  - No parameters: `()`
  - Single parameter (optional parentheses): `x` or `(x)`
  - Multiple parameters: `(x, y)` with optional type hints `(int x, String y)`
- Body variations:
  - Single expression (implicit return): `x -> x * 2`
  - Multiple statements: `x -> { int y = x * 2; return y; }`
- Type inference: Java infers parameter types from context
- Visual examples with progressively complex lambdas

**Slide 8: Lambda Constraints & Scope**
- Lambdas can only be used with functional interfaces (single abstract method)
- Lambda body access to surrounding scope (effectively final variables)
- Cannot throw checked exceptions without wrapper
- `this` refers to enclosing class, not the lambda
- Local variable requirements: Must be effectively final or final
- Common pitfall: Trying to modify captured variables

### Slides 9-15: Functional Interfaces Deep Dive
**Slide 9: What is a Functional Interface?**
- Definition: Interface with exactly ONE abstract method
- @FunctionalInterface annotation (optional but recommended for clarity)
- Can have multiple default or static methods
- Key insight: Compiler enforces single abstract method constraint
- Example: Runnable (one method: run()), Comparable (one method: compareTo())
- Why it matters: Enables lambda syntax and better code clarity

**Slide 10: java.util.function Package Overview**
- Standard library provides 43+ functional interfaces for common patterns
- Four core families: Consumer (accept), Supplier (get), Function (apply), Predicate (test)
- Other categories: Operators (specializations), BiFunction (two parameters)
- Why provided: Avoid creating custom functional interfaces for common cases
- Discovery: Best practices for when to use vs when to create custom

**Slide 11: Predicate<T> — Testing Values**
- Purpose: Tests a value and returns boolean
- Method: `boolean test(T t)`
- Use case: Filtering collections, validation logic, conditional checks
- Example: `Predicate<String> isEmpty = s -> s.isEmpty()`
- Common operations: `and()`, `or()`, `negate()` for logical combinations
- Real-world: Filtering users by criteria, validating input
- Code example: Multiple predicates combined with logical operators

**Slide 12: Consumer<T> — Accepting Values**
- Purpose: Accepts a value and performs action (returns nothing)
- Method: `void accept(T t)`
- Use case: Processing items, side effects, printing, logging
- Example: `Consumer<String> printer = s -> System.out.println(s)`
- Common operation: `andThen()` for chaining multiple consumers
- Real-world: Event handlers, logging, output operations
- Code example: Processing list elements with forEach + Consumer

**Slide 13: Supplier<T> — Providing Values**
- Purpose: Generates/provides values (takes no input)
- Method: `T get()`
- Use case: Lazy initialization, factory patterns, generating data
- Example: `Supplier<LocalDate> today = () -> LocalDate.now()`
- Why useful: Defers computation until actually needed
- Real-world: Creating objects on demand, configuration providers
- Code example: Lazy database connection initialization

**Slide 14: Function<T,R> — Transforming Values**
- Purpose: Transforms input to output (mapping operation)
- Method: `R apply(T t)`
- Use case: Transformations, conversions, data mapping
- Example: `Function<String,Integer> length = s -> s.length()`
- Common operations: `compose()`, `andThen()` for chaining functions
- Real-world: Data conversion, parsing, extraction
- Code example: Converting strings to integers, mapping objects

**Slide 15: BiFunctions & Specialized Operators**
- BiFunction<T,U,R>: Two input parameters, one output
- Example: `(x, y) -> x + y`
- Specialized operators for primitives: IntFunction, LongConsumer, etc.
- UnaryOperator<T>: Single input, same output type (extends Function<T,T>)
- BinaryOperator<T>: Two inputs same type, same output type
- When to use each: Matching parameter counts and types
- Code examples: Binary operations, reduce patterns

### Slides 16-20: Method References
**Slide 16: Method References — Conciseness Beyond Lambdas**
- Alternative syntax for invoking existing methods through functional interfaces
- Four types: Static, Bound instance, Unbound instance, Constructor
- Syntax: `Class::methodName` or `instance::methodName` or `Class::new`
- When useful: Method already exists, simplify lambda expressions
- Readability: Sometimes clearer than lambdas, sometimes comparable
- Example: `System.out::println` instead of `s -> System.out.println(s)`

**Slide 17: Static Method References**
- Format: `ClassName::staticMethodName`
- Invokes static method without instance
- Example: `Math::abs` or `Integer::parseInt`
- Use case: Wrapper methods for static utility functions
- Code example: Converting strings to integers with `String::valueOf`
- Comparison with equivalent lambda: Shows conciseness gain

**Slide 18: Instance Method References (Bound)**
- Format: `instance::methodName`
- Bound to specific object instance
- Method called on that instance
- Example: `System.out::println` (bound to System.out PrintStream)
- Use case: Using existing object methods as functional implementations
- Code example: Reference to instance's getters or utility methods
- Real-world: UI callbacks to button click handlers

**Slide 19: Instance Method References (Unbound)**
- Format: `ClassName::instanceMethodName`
- References instance method but not bound to specific instance
- First lambda parameter becomes the instance object
- Example: `String::length` (equivalent to `s -> s.length()`)
- Use case: Mapping operations on collections of objects
- Code example: Collecting string lengths from list
- Advanced: Useful in stream operations for transformations

**Slide 20: Constructor References**
- Format: `ClassName::new`
- Creates new instances through functional interface
- Example: `ArrayList::new` creates supplier that instantiates ArrayList
- Use case: Factory patterns, stream collection operations
- Supplier example: `Supplier<ArrayList> supplier = ArrayList::new`
- Function example: `Function<Integer,int[]> arrayCreator = int[]::new`
- Real-world: Creating objects dynamically in collections framework

### Slides 21-27: Optional Class & Null Handling
**Slide 21: The Problem with null**
- Null pointer exceptions: #1 cause of runtime errors in Java
- null is sentinel value with no meaning: empty collection? Not applicable? Unknown?
- Checking null everywhere makes code verbose and error-prone
- Example: Nested null checks create "pyramid of doom"
- Issue: No syntactic guidance that value might not exist

**Slide 22: Introducing Optional<T>**
- Container for a value that may or may not exist
- Represents presence/absence explicitly in type system
- Forces developer to handle the empty case
- Optional methods: get(), isPresent(), ifPresent(), orElse(), orElseThrow(), etc.
- Better readability: Code clearly expresses possibility of absence
- Benefit: Eliminates entire classes of null pointer exceptions

**Slide 23: Creating Optionals**
- `Optional.of(value)`: Wraps non-null value (throws exception if null)
- `Optional.ofNullable(value)`: Wraps value that might be null
- `Optional.empty()`: Explicitly empty Optional
- Best practice: Use ofNullable() for unknown values, of() for guaranteed non-null
- Method returning Optional: Communicates to caller "result may be empty"
- Code examples: Creating from method calls, from user input

**Slide 24: Checking Presence & Extracting Values**
- `isPresent()`: boolean check, old-school null checking equivalent
- `ifPresent(Consumer)`: Execute action only if value exists
- `ifPresentOrElse(Consumer, Runnable)`: Actions for both cases
- `get()`: Extract value, throws exception if empty (use carefully!)
- `orElse(defaultValue)`: Provide fallback if empty
- `orElseGet(Supplier)`: Lazily compute default only if needed
- `orElseThrow()`: Throw exception if empty (explicit failure)
- Comparison: When to use each method for different scenarios

**Slide 25: Mapping & Transforming Optionals**
- `map(Function)`: Transform value if present, returns Optional<NewType>
- `flatMap(Function)`: Transform to Optional, flatten result (avoids Optional<Optional<T>>)
- Chaining operations: Transformations preserve Optional structure
- Benefit: No null checks needed between transformations
- Example: User lookup → extract name → convert to uppercase → get result
- Code example: Chaining map/flatMap without intermediate null checks

**Slide 26: Combining Optionals**
- `filter(Predicate)`: Keep Optional only if condition met, otherwise empty
- Chaining filter: Multiple conditions can be applied
- Example: Optional user → filter by active status → filter by premium membership
- `or(Supplier)`: Provide alternative Optional if first is empty
- Pattern: Expressing alternatives and fallbacks elegantly
- Code example: Cascading optional checks with or()

**Slide 27: Best Practices with Optional**
- Use Optional for return types from methods (document possibility of absence)
- Never use Optional for instance fields (wastes memory)
- Never use Optional for parameters (just check null instead)
- Don't chain get() calls without checking isPresent() first
- Use map/flatMap/filter chains instead of intermediate null checks
- Avoid `Optional.get()` without safety checks (anti-pattern)
- Real-world: Method return values, DAO queries, configuration lookups
- Performance note: Optional has small overhead, but clarity gain justifies it

### Slides 28-31: Common Beginner Mistakes
**Slide 28: Mistake #1 — Using Optional for Parameters or Fields**
- Anti-pattern: Method with Optional parameter
- Problem: Caller must wrap null in Optional (extra work, confusing)
- Correct approach: Check null in method, no Optional wrapping
- Wrong: `void setUser(Optional<User> user)`
- Right: `void setUser(User user)` with null check inside, or @NonNull annotation
- Wrong for fields: `private Optional<String> name;` (wastes 24 bytes per field)
- Why: Optional designed for return values, not as regular wrapper

**Slide 29: Mistake #2 — Chaining Optional.get() Without Checks**
- Anti-pattern: `Optional<User> user = ...; User u = user.get();` (unsafely)
- Problem: No different from null pointer exception if empty
- Defeat purpose: Optional supposed to force you to handle absence
- Wrong: Long chain of `map().get().map().get()`
- Right: Use `map().flatMap().orElse()` chains instead
- Correct: `Optional<String> name = user.map(User::getName).map(String::toUpperCase); name.ifPresent(System.out::println);`
- Anti-pattern: Forgetting Optional after map and using get() blindly

**Slide 30: Mistake #3 — Not Using filter() for Validation**
- Anti-pattern: Extract Optional value, then null check condition
- Example: `Optional<User> user = ...; if(user.isPresent()) { User u = user.get(); if(u.isActive()) { ... } }`
- Verbose and misses point of Optional
- Better: `Optional<User> user = ...; user.filter(User::isActive).ifPresent(...)`
- Pattern: Combine filter with map/flatMap for cleaner expressions
- Real-world: Validation chains in data processing

**Slide 31: Mistake #4 — Overusing Optional with Primitives (Boxing Overhead)**
- Anti-pattern: `Optional<Integer>` when primitive int would do
- Problem: Automatic boxing creates wrapper objects, memory overhead
- Better: Use `OptionalInt`, `OptionalLong`, `OptionalDouble`
- Example: `OptionalInt count = OptionalInt.of(5);` instead of `Optional<Integer>`
- Performance: Significant for large collections or hot code paths
- Rule: If working with primitives, use OptionalInt/Long/Double

### Slides 32-35: Real-World Applications & Summary
**Slide 32: Real-World Example: User Lookup Service**
- Scenario: REST API endpoint retrieves user by ID, may not exist
- Old approach: Return null, caller checks null, nested exception handling
- Modern approach: Return `Optional<User>`, caller chains operations
- Code flow: userService.findById(id) → map to User info → transform data → orElse default
- Benefit: Clear contract (method return says "might not exist"), chainable operations
- Complete example with error handling and fallback

**Slide 33: Real-World Example: Configuration with Lambdas & Functionals**
- Scenario: Application configuration with fallbacks and transformations
- Supplier pattern: Lazy load environment variables or file properties
- Predicate pattern: Validate configuration values before use
- Function pattern: Convert configuration strings to typed values
- Example: Config value → filter by validation → map to type → with default
- Benefit: Declarative configuration logic, easy to test individual predicates/functions

**Slide 34: Real-World Example: Event Handling with Method References**
- Scenario: UI button click handlers, event listeners in GUI application
- Traditional: Anonymous inner class (verbose)
- Modern: Method references to event handler methods
- Example: `button.setOnClickListener(this::handleButtonClick)`
- Consumer pattern: Button press → accept event → execute handler
- Benefit: Clean code, method name clarifies handler intent

**Slide 35: Summary & Key Takeaways**
- Lambdas: Write concise, functional code with minimal boilerplate
- Functional interfaces: Standard library provides common patterns (Predicate, Consumer, Function, Supplier)
- Method references: Alternative syntax for invoking existing methods
- Optional: Eliminate null pointer exceptions through explicit absence representation
- Best practices: Use Optional for returns, not parameters; chain operations with map/filter/flatMap
- Preparation: Understanding these enables Stream API (Part 2)
- Integration: Lambdas + Functional interfaces + Optional = Foundation for modern Java
- Next: Stream API builds on these concepts for powerful data processing

