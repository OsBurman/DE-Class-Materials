# Exercise 02: Method References

## Objective
Replace verbose lambda expressions with compact method references in all four forms: static method, instance method on a parameter, instance method on a specific object, and constructor reference.

## Background
A method reference (`ClassName::methodName`) is shorthand for a lambda that does nothing but call an existing method. When a lambda's entire body is a single method call, a method reference makes the code more readable by eliminating the `->` arrow and parameter noise. There are four forms, each applicable in different situations.

## Requirements

1. **Static method reference** (`ClassName::staticMethod`):
   - Create a `Function<String, Integer>` using `Integer::parseInt` (replaces `s -> Integer.parseInt(s)`)
   - Parse `"42"` and `"100"` and print: `"Parsed: 42"`, `"Parsed: 100"`
   - Create a `Predicate<String>` using `Objects::isNull` to check for null — test with `null` and `"hello"`

2. **Instance method reference on a parameter** (`ClassName::instanceMethod`):
   - Create a `Function<String, String>` using `String::toUpperCase` (replaces `s -> s.toUpperCase()`)
   - Apply to `"hello world"` and print the result
   - Create a `Predicate<String>` using `String::isEmpty` — test with `""` and `"java"`
   - Sort a `List<String>` of `["Zebra", "Mango", "Apple"]` using `String::compareTo` (pass as Comparator)

3. **Instance method reference on a specific object** (`instance::instanceMethod`):
   - Create a `String prefix = "Hello, "` and a `Function<String, String>` using `prefix::concat`
   - Apply to `"Alice"` and `"Bob"` and print the results

4. **Constructor reference** (`ClassName::new`):
   - Define a simple `Person` class (in the same file) with a `String name` field and constructor
   - Create a `Function<String, Person>` using `Person::new`
   - Use it to create `Person` objects for `"Carol"` and `"Dave"`, printing their names

5. **Method references with streams** (preview of Day 8 Part 2):
   - Given `List<String> words = Arrays.asList("one", "two", "three", "four", "five")`, use `words.forEach(System.out::println)` to print each word

## Hints
- The four forms: `Class::staticMethod`, `Class::instanceMethod` (called on the parameter), `object::instanceMethod` (called on a captured variable), `Class::new`
- The compiler infers which overload to use from the functional interface's parameter and return types
- `String::compareTo` works as a `Comparator<String>` because `Comparator<T>` has one method `compare(T o1, T o2)` and `s1.compareTo(s2)` matches that signature
- `Objects` is in `java.util.Objects`

## Expected Output

```
=== Static Method Reference ===
Parsed: 42
Parsed: 100
null isNull: true
"hello" isNull: false

=== Instance Method on Parameter ===
HELLO WORLD
"" isEmpty: true
"java" isEmpty: false
Sorted: [Apple, Mango, Zebra]

=== Instance Method on Specific Object ===
Hello, Alice
Hello, Bob

=== Constructor Reference ===
Created person: Carol
Created person: Dave

=== Method Reference with forEach ===
one
two
three
four
five
```
