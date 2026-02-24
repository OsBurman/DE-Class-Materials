# Day 8 — Lambdas, Streams & DateTime
## Quick Reference Guide

---

## 1. Lambda Expressions

A lambda is an **anonymous function** — a concise way to implement a functional interface.

```java
// Syntax: (parameters) -> expression   OR   (parameters) -> { statements; }

// No params, expression body
Runnable r = () -> System.out.println("Hello");

// One param (parens optional), expression body
Consumer<String> print = s -> System.out.println(s);

// Multiple params, expression body
Comparator<Integer> cmp = (a, b) -> a - b;

// Multiple statements require braces + explicit return
Function<Integer, String> describe = n -> {
    if (n > 0) return "positive";
    if (n < 0) return "negative";
    return "zero";
};
```

---

## 2. Functional Interfaces

A **functional interface** has exactly **one abstract method** (SAM). Annotate with `@FunctionalInterface` to let the compiler enforce this.

| Interface | Signature | Use Case |
|-----------|-----------|----------|
| `Predicate<T>` | `boolean test(T t)` | Test / filter |
| `Function<T,R>` | `R apply(T t)` | Transform T → R |
| `Consumer<T>` | `void accept(T t)` | Consume / side effect |
| `Supplier<T>` | `T get()` | Produce / provide |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Two inputs → one output |
| `BiPredicate<T,U>` | `boolean test(T t, U u)` | Two-input test |
| `BiConsumer<T,U>` | `void accept(T t, U u)` | Two-input consumer |
| `UnaryOperator<T>` | `T apply(T t)` | Same type in/out |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | Two same-type → same type |

```java
Predicate<String> notEmpty = s -> !s.isEmpty();
notEmpty.test("hello");   // true

Predicate<String> longAndNotEmpty = notEmpty.and(s -> s.length() > 5);
Predicate<String> eitherWay       = notEmpty.or(s -> s.equals("N/A"));
Predicate<String> isEmpty          = notEmpty.negate();

Function<String, Integer> length = String::length;
Function<Integer, String> stars  = n -> "*".repeat(n);
Function<String, String>  chain  = length.andThen(stars);   // compose left→right
chain.apply("hello");   // "*****"
```

---

## 3. Method References

A method reference is shorthand for a lambda that **only calls an existing method**.

| Type | Syntax | Lambda equivalent |
|------|--------|-------------------|
| Static method | `ClassName::staticMethod` | `(args) -> ClassName.staticMethod(args)` |
| Instance method (specific object) | `instance::method` | `(args) -> instance.method(args)` |
| Instance method (arbitrary object) | `ClassName::instanceMethod` | `(obj, args) -> obj.instanceMethod(args)` |
| Constructor | `ClassName::new` | `(args) -> new ClassName(args)` |

```java
// Static
Function<String, Integer> parse = Integer::parseInt;

// Instance — specific object
String prefix = "Hello, ";
Function<String, String> greet = prefix::concat;

// Instance — arbitrary object of given type (first param is the receiver)
Function<String, String>  upper = String::toUpperCase;
Predicate<String>         empty = String::isEmpty;

// Constructor
Supplier<ArrayList<String>> newList = ArrayList::new;
Function<String, StringBuilder> sb  = StringBuilder::new;
```

---

## 4. Optional

`Optional<T>` wraps a potentially-null value — forces callers to handle the absent case.

```java
Optional<String> name = Optional.of("Alice");         // non-null value
Optional<String> none = Optional.empty();             // absent
Optional<String> safe = Optional.ofNullable(maybeNull); // nullable

// Check & retrieve
name.isPresent();                         // true
name.isEmpty();                           // false (Java 11+)
name.get();                               // "Alice" — throws if empty; avoid in production
name.orElse("Unknown");                   // "Alice"
none.orElse("Unknown");                   // "Unknown"
none.orElseGet(() -> computeDefault());   // lazy — evaluated only when absent
none.orElseThrow(() -> new RuntimeException("Missing"));

// Transform
name.map(String::toUpperCase);            // Optional["ALICE"]
name.filter(s -> s.length() > 3);        // Optional["Alice"]  (length 5 > 3)
name.flatMap(s -> findUser(s));           // unwraps nested Optional

// Side effects
name.ifPresent(System.out::println);
name.ifPresentOrElse(System.out::println, () -> System.out.println("none"));
```

---

## 5. Stream Pipeline

```
Source → [zero or more Intermediate operations] → Terminal operation
```

- **Intermediate** — return a new Stream; **lazy** (evaluated on terminal call)
- **Terminal** — triggers execution; produces a result or side effect; **stream is consumed after this**

```java
List<String> names = List.of("Alice", "Bob", "Charlie", "Anna", "Dave");

List<String> result = names.stream()
    .filter(s -> s.startsWith("A"))     // intermediate: Alice, Anna
    .map(String::toUpperCase)           // intermediate: ALICE, ANNA
    .sorted()                           // intermediate: ALICE, ANNA (already sorted)
    .collect(Collectors.toList());      // terminal: [ALICE, ANNA]
```

---

## 6. Intermediate Operations

| Operation | Signature | Effect |
|-----------|-----------|--------|
| `filter` | `filter(Predicate<T>)` | Keep elements matching predicate |
| `map` | `map(Function<T,R>)` | Transform T → R |
| `flatMap` | `flatMap(Function<T, Stream<R>>)` | Flatten nested streams |
| `distinct` | `distinct()` | Remove duplicates (uses `equals`) |
| `sorted` | `sorted()` / `sorted(Comparator)` | Sort elements |
| `limit` | `limit(n)` | Take first n elements |
| `skip` | `skip(n)` | Skip first n elements |
| `peek` | `peek(Consumer<T>)` | Side effect without consuming stream (debugging) |
| `mapToInt` | `mapToInt(ToIntFunction<T>)` | Returns `IntStream` (primitive specialization) |

---

## 7. Terminal Operations

| Operation | Returns | Effect |
|-----------|---------|--------|
| `collect(Collector)` | `R` | Accumulate into collection / result |
| `forEach(Consumer)` | `void` | Side effect on each element |
| `count()` | `long` | Number of elements |
| `findFirst()` | `Optional<T>` | First element (or empty) |
| `findAny()` | `Optional<T>` | Any element (faster in parallel) |
| `anyMatch(Predicate)` | `boolean` | Any element matches |
| `allMatch(Predicate)` | `boolean` | All elements match |
| `noneMatch(Predicate)` | `boolean` | No element matches |
| `reduce(identity, BinaryOp)` | `T` | Fold into single value |
| `min(Comparator)` | `Optional<T>` | Minimum element |
| `max(Comparator)` | `Optional<T>` | Maximum element |
| `toArray()` | `Object[]` | Collect into array |

---

## 8. Collectors

```java
import java.util.stream.Collectors;

// To collection
.collect(Collectors.toList())
.collect(Collectors.toUnmodifiableList())   // Java 10+
.collect(Collectors.toSet())
.collect(Collectors.toMap(Student::getId, Student::getName))

// Joining strings
.collect(Collectors.joining(", "))           // "Alice, Bob, Charlie"
.collect(Collectors.joining(", ", "[", "]")) // "[Alice, Bob, Charlie]"

// Grouping
Map<String, List<Student>> byDept =
    students.stream().collect(Collectors.groupingBy(Student::getDepartment));

// Counting per group
Map<String, Long> countByDept =
    students.stream().collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));

// Partitioning (split into two groups: true/false)
Map<Boolean, List<Student>> passOrFail =
    students.stream().collect(Collectors.partitioningBy(s -> s.getGrade() >= 60));

// Statistics
IntSummaryStatistics stats =
    students.stream().collect(Collectors.summarizingInt(Student::getGrade));
stats.getAverage();   stats.getMin();   stats.getMax();   stats.getCount();
```

---

## 9. Primitive Streams

```java
IntStream ints    = IntStream.of(1, 2, 3);
IntStream range   = IntStream.range(0, 10);       // [0, 10)
IntStream rangeCl = IntStream.rangeClosed(1, 10); // [1, 10]

// Boxed/unboxed conversions
IntStream unboxed = list.stream().mapToInt(Integer::intValue);
Stream<Integer> boxed = IntStream.range(0, 5).boxed();

// Numeric reductions
IntStream.of(3, 1, 4, 1, 5).sum();     // 14
IntStream.of(3, 1, 4, 1, 5).average(); // OptionalDouble[2.8]
```

---

## 10. DateTime API (java.time)

| Class | Represents | Has time? | Has zone? |
|-------|-----------|-----------|-----------|
| `LocalDate` | Date only | ❌ | ❌ |
| `LocalTime` | Time only | ✅ | ❌ |
| `LocalDateTime` | Date + time | ✅ | ❌ |
| `ZonedDateTime` | Date + time + zone | ✅ | ✅ |
| `Instant` | Machine timestamp (epoch) | ✅ | UTC only |
| `Duration` | Amount of time (seconds/nanos) | ✅ | — |
| `Period` | Date-based amount (years/months/days) | ❌ | — |

```java
// Creating
LocalDate today   = LocalDate.now();
LocalDate date    = LocalDate.of(2024, 3, 15);       // or Month.MARCH
LocalTime time    = LocalTime.of(14, 30, 0);
LocalDateTime dt  = LocalDateTime.of(date, time);
ZonedDateTime zdt = ZonedDateTime.of(dt, ZoneId.of("America/New_York"));
Instant now       = Instant.now();

// Reading
today.getYear();         today.getMonthValue();   today.getDayOfMonth();
today.getDayOfWeek();    // DayOfWeek.WEDNESDAY
today.getDayOfYear();    // 75

// Manipulation — all DateTime objects are IMMUTABLE; return new instances
LocalDate tomorrow = today.plusDays(1);
LocalDate lastYear = today.minusYears(1);
LocalDate firstDay = today.withDayOfMonth(1);

// Comparison
date1.isBefore(date2);
date1.isAfter(date2);
date1.isEqual(date2);

// Duration between two times
Duration d = Duration.between(time1, time2);
d.toHours();   d.toMinutes();   d.toSeconds();

// Period between two dates
Period p = Period.between(date1, date2);
p.getYears();  p.getMonths();  p.getDays();
```

---

## 11. DateTimeFormatter

```java
DateTimeFormatter iso    = DateTimeFormatter.ISO_LOCAL_DATE;        // 2024-03-15
DateTimeFormatter custom = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
DateTimeFormatter locale = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                                           .withLocale(Locale.US);

// Format (DateTime → String)
String formatted = today.format(custom);                   // "15/03/2024 00:00"
String formatted = dt.format(custom);                      // "15/03/2024 14:30"

// Parse (String → DateTime)
LocalDate parsed = LocalDate.parse("15/03/2024", custom);
LocalDateTime parsedDt = LocalDateTime.parse("15/03/2024 14:30", custom);
```

---

## 12. Common Stream Patterns

```java
// Sum of a field
int total = students.stream().mapToInt(Student::getGrade).sum();

// Average
OptionalDouble avg = students.stream().mapToInt(Student::getGrade).average();

// Max by field
Optional<Student> top = students.stream().max(Comparator.comparingInt(Student::getGrade));

// Flat list from nested collections
List<String> allCourses = departments.stream()
    .flatMap(d -> d.getCourses().stream())
    .collect(Collectors.toList());

// Distinct sorted list
List<Integer> unique = numbers.stream().distinct().sorted().collect(Collectors.toList());

// Parallel stream (use for CPU-intensive, stateless, order-independent operations)
long count = largeList.parallelStream().filter(predicate).count();
```
