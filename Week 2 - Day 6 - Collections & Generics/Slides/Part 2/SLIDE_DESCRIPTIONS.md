# Week 2 - Day 6 (Monday) Part 2: Generics, Comparable, and Collections Utilities
## Type-Safe Collections & Advanced Operations

---

## Slide 1: Welcome to Part 2: Generics and Type Safety
**Visual:** Generic syntax examples: List<String>, Map<Integer, User>

Welcome back! In Part 1, you learned the Collections Framework. You understand ArrayList, HashSet, HashMap. Now we add type safety with Generics. Generics let you specify exactly what types a collection holds. Instead of a generic List (which could hold anything), you write List<String> (which holds only strings). This prevents bugs at compile time, not runtime. You'll also learn how to sort collections using Comparable and Comparator. And we'll explore Collections utility methods. By the end of Part 2, you'll write type-safe, sortable, efficient collections code.

---

## Slide 2: What Are Generics?
**Visual:** Without generics vs with generics; show type casting vs type-safe

Generics allow you to write code that works with different types while maintaining type safety. Before generics, Java Collections held Objects—anything. This required casting and was error-prone.

```java
// Without generics (old way):
List list = new ArrayList();
list.add("Apple");
list.add("Banana");
String fruit = (String) list.get(0);  // Manual cast required

// With generics (modern way):
List<String> fruits = new ArrayList<String>();
fruits.add("Apple");
fruits.add("Banana");
String fruit = fruits.get(0);  // No cast needed; type-safe
```

Generics eliminate casting and provide compile-time type checking.

---

## Slide 3: Generic Syntax Basics
**Visual:** Generic declaration syntax: <TypeParameter>; show angle brackets

Generic syntax uses angle brackets to specify types. When creating a collection, specify what it holds:

```java
List<String> names = new ArrayList<String>();
Set<Integer> numbers = new HashSet<Integer>();
Map<String, Integer> ages = new HashMap<String, Integer>();
Queue<Task> tasks = new LinkedList<Task>();
```

The type in angle brackets is the type parameter. It tells the compiler: "This list holds only strings." Any attempt to add a non-string is a compile error.

---

## Slide 4: Type Parameter Naming Conventions
**Visual:** Common type parameter names: T, E, K, V, S, U, R

Type parameters are usually single capital letters. Common conventions:

- **T**: Type (generic type)
- **E**: Element (for collections)
- **K**: Key (for maps)
- **V**: Value (for maps)
- **S, U, R**: Additional types if needed
- **N**: Number
- **X, Y**: Arbitrary types

These are just conventions. You could write List<MyType>, but the community uses single letters. Learn the conventions so code is readable.

---

## Slide 5: Generic Methods
**Visual:** Generic method definition and call

You can write generic methods—methods that work with any type:

```java
public <T> void printList(List<T> list) {
    for (T item : list) {
        System.out.println(item);
    }
}

// Call with different types:
printList(Arrays.asList("Apple", "Banana"));
printList(Arrays.asList(1, 2, 3));
printList(Arrays.asList(3.14, 2.71));
```

The method works with lists of any type. The compiler figures out the type parameter (T) from the argument.

---

## Slide 6: Generic Classes
**Visual:** Generic class definition: public class Box<T>

You can write generic classes:

```java
public class Box<T> {
    private T contents;
    
    public void set(T item) {
        this.contents = item;
    }
    
    public T get() {
        return contents;
    }
}

// Usage:
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get();

Box<Integer> intBox = new Box<>();
intBox.set(42);
int value = intBox.get();
```

One class, works with any type. This is the power of generics—code reuse with type safety.

---

## Slide 7: Wildcards and Flexibility
**Visual:** Wildcard syntax: List<?>, List<? extends Number>, List<? super Integer>

Wildcards (?) allow flexibility. `List<?>` means "a list of anything." `List<? extends Number>` means "a list of Number or its subclasses." `List<? super Integer>` means "a list of Integer or its superclasses."

```java
// Accepts lists of any type
public void printAny(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Accepts lists of Numbers (Integer, Double, etc.)
public double sum(List<? extends Number> numbers) {
    double total = 0;
    for (Number num : numbers) {
        total += num.doubleValue();
    }
    return total;
}
```

Wildcards are advanced. Start without them. Use when you need flexible type handling.

---

## Slide 8: Bounded Type Parameters
**Visual:** Bounded type syntax: <T extends Comparable<T>>

Type parameters can be bounded. Specify that T must extend (or implement) a certain type.

```java
// T must be a Number
public <T extends Number> void process(T value) {
    System.out.println(value.doubleValue());
}

// T must be Comparable
public <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) > 0 ? a : b;
}

// Usage:
System.out.println(max(5, 3));           // Works; Integer is Comparable
System.out.println(max("zebra", "apple"));  // Works; String is Comparable
```

Bounds ensure type parameters have required methods or properties.

---

## Slide 9: Type Erasure
**Visual:** Generics at compile-time vs runtime; type erasure explanation

Java generics use type erasure. At compile-time, the compiler checks types. At runtime, generic information is removed. `List<String>` becomes `List` in the bytecode.

```java
List<String> strings = new ArrayList<String>();
List<Integer> integers = new ArrayList<Integer>();

// At runtime, both are the same: ArrayList
System.out.println(strings.getClass() == integers.getClass());  // true
```

This is why you can't do:

```java
// WRONG: Not possible at runtime
if (list instanceof List<String>) { }  // Compile error

// CORRECT: You can check raw type
if (list instanceof List) { }  // Compile-time check only
```

Type erasure is a legacy of adding generics to Java after the language existed. It's a trade-off but works well in practice.

---

## Slide 10: Comparable Interface
**Visual:** Natural ordering; items can be compared to each other

The Comparable interface defines natural ordering. A class implementing Comparable provides a compareTo() method:

```java
public interface Comparable<T> {
    int compareTo(T other);
}
```

compareTo returns:
- Negative if this < other
- Zero if this == other
- Positive if this > other

```java
// String implements Comparable
String a = "apple";
String b = "banana";
System.out.println(a.compareTo(b));  // Negative (apple < banana)

// Integer implements Comparable
Integer x = 5;
Integer y = 10;
System.out.println(x.compareTo(y));  // Negative (5 < 10)
```

Natural ordering is the default. Collections sort by natural order if not specified otherwise.

---

## Slide 11: Creating Comparable Custom Classes
**Visual:** Custom class implementing Comparable<T>

You can make your own classes Comparable:

```java
public class Student implements Comparable<Student> {
    private String name;
    private int gpa;
    
    public Student(String name, int gpa) {
        this.name = name;
        this.gpa = gpa;
    }
    
    @Override
    public int compareTo(Student other) {
        // Sort by GPA descending (highest first)
        return Integer.compare(other.gpa, this.gpa);
    }
}

// Now students can be sorted:
List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 95));
students.add(new Student("Bob", 88));
students.add(new Student("Charlie", 92));

Collections.sort(students);  // Sorted by GPA, highest first
```

Implementing Comparable defines natural ordering for your class.

---

## Slide 12: Comparator Interface
**Visual:** Comparator as external comparison logic; multiple sorting options

Comparator allows external comparison logic. Instead of the object defining its own ordering, you provide a comparator:

```java
public interface Comparator<T> {
    int compare(T a, T b);
}

// Custom comparator: sort students by name
Comparator<Student> byName = new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
        return a.getName().compareTo(b.getName());
    }
};

List<Student> students = new ArrayList<>();
// ... add students ...
Collections.sort(students, byName);  // Sorted by name
```

Comparator is useful when you want multiple sorting options or when a class doesn't implement Comparable.

---

## Slide 13: Lambda Syntax for Comparators
**Visual:** Lambda expression vs anonymous class; cleaner syntax

Modern Java uses lambdas for comparators:

```java
// Old anonymous class way:
Collections.sort(students, new Comparator<Student>() {
    @Override
    public int compare(Student a, Student b) {
        return a.getGpa().compareTo(b.getGpa());
    }
});

// Modern lambda way:
Collections.sort(students, (a, b) -> a.getGpa().compareTo(b.getGpa()));
```

Lambdas are cleaner. You'll learn lambdas in detail on Day 8. For now, understand they provide concise comparator syntax.

---

## Slide 14: Comparable vs Comparator
**Visual:** Comparison table: purpose, method, usage

| Aspect | Comparable | Comparator |
|--------|---|---|
| Purpose | Natural ordering | Custom/multiple orderings |
| Interface | `implements Comparable<T>` | Separate object `Comparator<T>` |
| Method | `compareTo()` | `compare()` |
| Location | In the class | External to the class |
| Usage | `Collections.sort(list)` | `Collections.sort(list, comparator)` |

Comparable: The object knows its natural order. Comparator: External rules for ordering. Use Comparable for the most natural ordering. Use Comparator for alternatives.

---

## Slide 15: Collections Utility Methods
**Visual:** Static methods in Collections class; sort, reverse, shuffle, etc.

The Collections class provides static utility methods for collections. These are game-changers:

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9));

Collections.sort(numbers);              // Sort in ascending order
Collections.reverse(numbers);           // Reverse order
Collections.shuffle(numbers);           // Random shuffle
Collections.rotate(numbers, 2);         // Rotate by 2 positions
Collections.swap(numbers, 0, 1);        // Swap two elements

System.out.println(Collections.max(numbers));       // Maximum
System.out.println(Collections.min(numbers));       // Minimum
System.out.println(Collections.frequency(numbers, 1));  // Count occurrences
```

These methods are incredibly useful. Master them and you'll solve many problems.

---

## Slide 16: Collections.sort() and Collections.reverse()
**Visual:** Before/after sorting; before/after reversing

sort() orders elements. reverse() flips the order.

```java
List<String> names = new ArrayList<>(Arrays.asList("Charlie", "Alice", "Bob"));

Collections.sort(names);  // [Alice, Bob, Charlie]
Collections.reverse(names);  // [Charlie, Bob, Alice]

// With custom comparator:
Collections.sort(names, (a, b) -> b.compareTo(a));  // Descending: [Charlie, Bob, Alice]
```

sort() uses natural ordering by default. Provide a comparator for custom ordering.

---

## Slide 17: Collections.shuffle(), rotate(), swap()
**Visual:** Before/after shuffling, rotating, swapping

shuffle() randomizes order. rotate() shifts elements. swap() exchanges two.

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

Collections.shuffle(numbers);     // Random order: [4, 1, 5, 2, 3] or similar
Collections.rotate(numbers, 2);   // Shift right by 2: [2, 3, 4, 1, 5]
Collections.swap(numbers, 0, 4);  // Swap index 0 and 4: [5, 3, 4, 1, 2]
```

shuffle() is useful for games, lotteries. rotate() for circular buffers. swap() rarely needed but available.

---

## Slide 18: Collections.max() and Collections.min()
**Visual:** Finding maximum and minimum in a collection

```java
List<Integer> scores = Arrays.asList(85, 92, 78, 95, 88);

int highest = Collections.max(scores);  // 95
int lowest = Collections.min(scores);   // 78

// With custom comparator:
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
String longest = Collections.max(names, (a, b) -> a.length() - b.length());  // "Charlie"
```

max() and min() work with any comparable collection. Provide a comparator for custom ordering.

---

## Slide 19: Collections.frequency() and Searching
**Visual:** Counting occurrences and binary search

frequency() counts how many times an element appears. binarySearch() finds an element in a sorted list.

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 2, 4);

int count = Collections.frequency(numbers, 2);  // 3 occurrences

// For binary search, list must be sorted:
List<String> names = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie", "Diana"));
Collections.sort(names);
int index = Collections.binarySearch(names, "Charlie");  // 2
```

frequency() is O(n). binarySearch() is O(log n) but requires sorted list.

---

## Slide 20: Collections.copy() and Collections.fill()
**Visual:** Copying a collection; filling with a value

copy() copies one list to another. fill() sets all elements to a value.

```java
List<Integer> source = Arrays.asList(1, 2, 3);
List<Integer> dest = new ArrayList<>(Arrays.asList(0, 0, 0));

Collections.copy(dest, source);  // dest: [1, 2, 3]

List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
Collections.fill(numbers, 0);    // [0, 0, 0]
```

copy() requires destination to be large enough. fill() replaces all elements.

---

## Slide 21: Unmodifiable and Synchronized Collections
**Visual:** Immutable wrapper and synchronized wrapper

Collections provides wrapper methods for safety:

```java
List<String> original = new ArrayList<>(Arrays.asList("Apple", "Banana"));

// Unmodifiable view (read-only)
List<String> unmodifiable = Collections.unmodifiableList(original);
// unmodifiable.add("Cherry");  // Throws UnsupportedOperationException

// Synchronized wrapper (thread-safe)
List<String> synchronized = Collections.synchronizedList(original);
// Safe to use from multiple threads
```

Unmodifiable collections prevent accidental modifications. Synchronized collections ensure thread-safety (we'll cover threads in Day 9).

---

## Slide 22: Real-World Example: Student Sorting
**Visual:** Students sorted by different criteria; natural vs custom ordering

```java
public class Student implements Comparable<Student> {
    private String name;
    private double gpa;
    
    public Student(String name, double gpa) {
        this.name = name;
        this.gpa = gpa;
    }
    
    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa);  // Descending GPA
    }
    
    @Override
    public String toString() {
        return name + " (GPA: " + gpa + ")";
    }
}

List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 3.95));
students.add(new Student("Bob", 3.45));
students.add(new Student("Charlie", 3.78));

// Sort by natural ordering (GPA descending)
Collections.sort(students);

// Sort by name
students.sort((a, b) -> a.name.compareTo(b.name));

// Find top student
Student top = Collections.max(students);
```

This demonstrates Comparable, Comparator, and Collections utilities together.

---

## Slide 23: Type Safety and Preventing Type Errors
**Visual:** Without generics: casting and potential ClassCastException; with generics: compile-time safety

Generics prevent a whole class of errors. Without generics, you could accidentally add wrong types:

```java
// Without generics: Disaster waiting
List list = new ArrayList();
list.add("Apple");
list.add(123);
list.add(new Date());

String fruit = (String) list.get(0);  // Works
String number = (String) list.get(1);  // ClassCastException at runtime!

// With generics: Compile-time safety
List<String> fruits = new ArrayList<>();
fruits.add("Apple");
// fruits.add(123);  // Compile error! Can't add integer to List<String>

String fruit = fruits.get(0);  // No cast; guaranteed string
```

Generics catch errors at compile-time, not runtime. This is massively better.

---

## Slide 24: Common Beginner Mistake: Raw Types
**Visual:** Red X on raw types; green check on parameterized types

Using raw types (no type parameter) defeats generics' purpose:

```java
// WRONG: Raw type
List list = new ArrayList();
list.add("Apple");
list.add(123);  // Silently added; type mixing

// CORRECT: Parameterized type
List<String> list = new ArrayList<>();
list.add("Apple");
// list.add(123);  // Compile error!
```

Always use parameterized types. If you see warnings about "raw use of parameterized class," fix them by specifying type parameters.

---

## Slide 25: Common Beginner Mistake: Generics with Primitives
**Visual:** Generics require objects; use wrapper classes like Integer, Double

Generics work with objects, not primitives. For primitives, use wrapper classes:

```java
// WRONG: Primitives
// List<int> numbers = new ArrayList<>();  // Compile error!

// CORRECT: Wrapper classes
List<Integer> numbers = new ArrayList<>();
List<Double> values = new ArrayList<>();
List<Boolean> flags = new ArrayList<>();

numbers.add(5);      // Autoboxed to Integer
values.add(3.14);    // Autoboxed to Double
flags.add(true);     // Autoboxed to Boolean
```

Autoboxing (primitive → object) and unboxing (object → primitive) happen automatically.

---

## Slide 26: Common Beginner Mistake: Forgetting Sort() Modifies In-Place
**Visual:** Before: unsorted list; after: list is modified, not returned

Collections.sort() modifies the list in-place. It doesn't return a new sorted list:

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));

// WRONG: Trying to assign result
List<Integer> sorted = Collections.sort(numbers);  // ERROR! sort() returns void

// CORRECT: sort() modifies in-place
Collections.sort(numbers);
System.out.println(numbers);  // [1, 1, 3, 4, 5] (modified)
```

sort() returns void. It modifies the original list. If you need original unchanged, copy first:

```java
List<Integer> original = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));
List<Integer> sorted = new ArrayList<>(original);
Collections.sort(sorted);
```

---

## Slide 27: Generic Wildcards in Collections
**Visual:** Wildcard patterns and their meanings

Wildcards enable flexible typing:

```java
// Process any list
public void processAny(List<?> list) {
    // Can read but not write (except null)
    for (Object item : list) {
        System.out.println(item);
    }
}

// Sum any list of Numbers
public double sum(List<? extends Number> numbers) {
    double total = 0;
    for (Number n : numbers) {
        total += n.doubleValue();
    }
    return total;
}

// Fill any list with integers or supertype
public void fillIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```

Wildcards are powerful but add complexity. Use when you need flexibility.

---

## Slide 28: Recap: Generics Benefits
**Visual:** Benefits checklist: type safety, no casting, compile-time checking, clearer code

Generics provide:
- **Type Safety**: Compiler catches type mismatches
- **No Casting**: `list.get(0)` returns correct type automatically
- **Compile-Time Checking**: Errors caught early, not at runtime
- **Clearer Code**: `List<String>` clearly says it holds strings
- **Reusability**: Generic code works with any type

These benefits make modern Java far better than pre-generics Java.

---

## Slide 29: Performance with Generics
**Visual:** Generic code performance vs non-generic; type erasure impact

Due to type erasure, generic code has no runtime performance penalty. `List<String>` and `List` have identical runtime behavior.

```java
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// At runtime, no difference. Both are ArrayList
// Generics are compile-time only.
```

Generics are free from a performance perspective. Use them freely. No trade-off.

---

## Slide 30: Collections Utility Methods Summary
**Visual:** Table of methods and their purposes

| Method | Purpose |
|--------|---------|
| sort() | Order elements |
| reverse() | Flip order |
| shuffle() | Random order |
| rotate() | Shift elements |
| swap() | Exchange two elements |
| max(), min() | Find extremes |
| frequency() | Count occurrences |
| binarySearch() | Find element in sorted list |
| copy() | Copy one list to another |
| fill() | Set all elements to value |
| unmodifiableList() | Read-only view |
| synchronizedList() | Thread-safe wrapper |

Master these methods and you solve most collection manipulation needs.

---

## Slide 31: Sorting Complex Objects
**Visual:** Sorting students by multiple criteria; custom comparator chaining

```java
List<Student> students = new ArrayList<>();
// ... add students ...

// Sort by GPA (natural ordering)
Collections.sort(students);

// Sort by name (alternative ordering)
students.sort((a, b) -> a.getName().compareTo(b.getName()));

// Sort by GPA descending, then by name alphabetical
students.sort((a, b) -> {
    if (a.getGpa() != b.getGpa()) {
        return Double.compare(b.getGpa(), a.getGpa());  // Descending GPA
    }
    return a.getName().compareTo(b.getName());  // Ascending name
});
```

Complex sorting combines multiple criteria. Use nested comparisons.

---

## Slide 32: Collections Streams (Brief Overview)
**Visual:** Modern stream API for collections; teaser for Day 8

Modern Java uses Streams for collection manipulation. Streams provide functional-style processing:

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Traditional way:
List<Integer> doubled = new ArrayList<>();
for (int n : numbers) {
    doubled.add(n * 2);
}

// Stream way (modern):
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

Streams are modern and powerful. Day 8 covers Streams in depth. For now, understand Collections utilities are traditional but still relevant.

---

## Slide 33: Real-World Example: Inventory System
**Visual:** Product collection with sorting and search

```java
public class Product implements Comparable<Product> {
    private String name;
    private double price;
    private int quantity;
    
    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);  // Sort by price ascending
    }
}

List<Product> inventory = new ArrayList<>();
// ... add products ...

// Find most expensive item
Product expensive = Collections.max(inventory, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));

// Find item by name
Product item = inventory.stream().filter(p -> p.getName().equals("Laptop")).findFirst().orElse(null);

// Sort by price
Collections.sort(inventory);

// Count low-stock items
int lowStock = Collections.frequency(inventory, null);  // Conceptually; actual implementation varies
```

Real applications combine collections, generics, and utilities constantly.

---

## Slide 34: Common Patterns: Filter and Collect
**Visual:** Filtering a collection and transforming results

Though Streams (Day 8) are modern, traditional approaches using Collections still work:

```java
List<Student> students = new ArrayList<>();
// ... add students ...

// Filter high-GPA students
List<Student> highGPA = new ArrayList<>();
for (Student s : students) {
    if (s.getGpa() >= 3.5) {
        highGPA.add(s);
    }
}

// Or, sort and filter together
Collections.sort(students, (a, b) -> Double.compare(b.getGpa(), a.getGpa()));

List<String> topNames = new ArrayList<>();
for (int i = 0; i < Math.min(3, students.size()); i++) {
    topNames.add(students.get(i).getName());
}
```

These patterns predate Streams. Still valid but verbose. Streams simplify them (Day 8).

---

## Slide 35: Best Practices Summary
**Visual:** Checklist of best practices

- ✓ Always use parameterized types (e.g., `List<String>` not `List`)
- ✓ Default to ArrayList and HashMap; change only if needed
- ✓ Implement Comparable for natural ordering, use Comparator for alternatives
- ✓ Use Collections utility methods (sort, max, min, etc.) for manipulation
- ✓ Avoid modifying collections during iteration; use iterator.remove()
- ✓ Use enhanced for loops for clean iteration
- ✓ Know Big O performance characteristics of each collection
- ✓ Consider thread-safety when needed (Day 9 covers concurrency)

Follow these, and you'll write effective collection code.

---

## Slide 36: Comparing Generics Then and Now
**Visual:** Timeline: pre-generics Java vs modern Java with generics

Pre-generics (Java 1.4 and earlier):
```java
List list = new ArrayList();
list.add("Apple");
String fruit = (String) list.get(0);  // Casting required
```

Modern (Java 5+):
```java
List<String> list = new ArrayList<>();
list.add("Apple");
String fruit = list.get(0);  // No casting
```

The language evolved. Modern syntax is superior. Always use generics.

---

## Slide 37: Recap: Part 2 Concepts
**Visual:** Summary of generics, comparable, and utilities

- Generics: Type-safe collections with compile-time checking
- Type parameters: <T>, <E>, <K>, <V> for flexibility
- Comparable: Natural ordering within a class
- Comparator: Custom ordering external to a class
- Collections utilities: sort, reverse, shuffle, max, min, frequency, etc.
- Type safety benefits: No casting, compiler catches errors
- Type erasure: Generics exist at compile-time; removed at runtime
- Wildcards: Advanced flexibility when needed

Master these, and collections work will be efficient and error-free.

---

## Slide 38: Preview: Week 2 Continues
**Visual:** Teaser for Days 7-10; exceptions, lambdas, multithreading, advanced Java

This week continues. Tomorrow, exceptions and I/O. Wednesday, lambdas and streams. Thursday, multithreading. Friday, advanced Java. You're building rapidly. Collections are the foundation. Everything else builds on this knowledge.

---

## Slide 39: Your Mastery Path
**Visual:** Progressive skill building: Part 1 basics → Part 2 generics → real projects

You've now learned:
- Collections Framework architecture
- When to use ArrayList, LinkedList, HashSet, TreeSet, HashMap, TreeMap
- Performance characteristics (Big O)
- Iterating safely
- Generics for type safety
- Comparable and Comparator for sorting
- Collections utilities for manipulation

This is solid foundational knowledge. With practice, it becomes muscle memory. Real projects will test and deepen this knowledge. You're prepared.

---

## Slide 40: Closing: Collections Matter
**Visual:** Collections powering real applications; databases, caches, data structures

Collections are everywhere. They power databases (which are maps), caches (hashmaps and LRU data structures), search algorithms (trees and heaps), and pretty much every non-trivial application. Master collections, and you master a core skill every Java programmer needs. Practice with these collections. Build small projects using them. Feel the performance differences. Over time, choosing the right collection becomes instinct. That instinct is professionalism. See you next class!

---

## Slide 41: Q&A and Review
**Visual:** Open discussion; students ask clarifying questions

Take this time to ask questions. Any concepts unclear? Any collections confusing? This is your opportunity to clarify before we move to exceptions and I/O tomorrow. Collections are fundamental. Get them solid now. Questions?

---
