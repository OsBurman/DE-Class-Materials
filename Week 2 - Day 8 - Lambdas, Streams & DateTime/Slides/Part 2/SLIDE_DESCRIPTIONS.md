# Week 2 - Day 8, Part 2: Stream API & DateTime
## Slide Descriptions (60-minute lecture)

### Slides 1-2: Welcome & Recap
**Slide 1: Title Slide**
- Title: "Stream API & DateTime"
- Subtitle: "Processing Collections Functionally & Managing Time"
- Visual: Stream flowing through a pipeline, clock face
- Welcome back message after break

**Slide 2: Part 1 Recap & Bridge to Part 2**
- Quick review: Lambdas, functional interfaces (Predicate, Consumer, Function, Supplier), method references
- Key insight: Optional handles absence explicitly
- Today's Part 2: Apply these concepts to practical problems
- Stream API: Functional data processing pipeline
- DateTime API: Working with dates and times without pain
- Connection: Lambdas power streams, streams process temporal data

### Slides 3-8: Stream API Fundamentals
**Slide 3: What is a Stream?**
- Definition: A sequence of elements supporting functional-style operations
- NOT a collection: Stream doesn't store data, it processes it
- Stream is a view: Creates a processing pipeline
- Data source examples: Collections (List, Set), arrays, I/O channels, generators
- Key properties: Lazy evaluation (operations deferred until terminal), Functional transformations, Potentially parallel execution
- Contrast: Collections are about storage; streams are about processing
- Visual: Data flowing through a pipeline with transformations

**Slide 4: Stream Operations: Three Phases**
- Phase 1: Source (where stream originates)
- Phase 2: Intermediate operations (transformations, return Stream)
- Phase 3: Terminal operation (consumes stream, produces result)
- Visual pipeline: Source → filter → map → map → collect (terminal)
- Example: `list.stream().filter(...).map(...).collect(...)`
- Key insight: Stream is lazy; nothing happens until terminal operation
- Common mistake: Writing stream chain without terminal operation (pipeline builds but executes nothing)

**Slide 5: Creating Streams**
- `Collection.stream()`: Most common, from List, Set, etc.
- `Arrays.stream(array)`: From arrays
- `Stream.of(elements)`: Explicit stream with varargs or array
- `Stream.generate(Supplier)`: Infinite stream, generates values lazily
- `Stream.iterate(seed, function)`: Infinite stream, starts with seed, applies function repeatedly
- `Files.lines(path)`: Stream lines from file (useful for large files)
- `IntStream.range(start, end)`: Stream of integers (range)
- Example comparisons: Creating stream from different sources

**Slide 6: Intermediate Operations: Filtering & Mapping**
- `filter(Predicate<T>)`: Keeps elements matching predicate, removes others
- Example: `stream.filter(s -> s.length() > 5)`
- `map(Function<T,R>)`: Transforms each element
- Example: `stream.map(String::toUpperCase)` or `stream.map(s -> s.length())`
- `flatMap(Function<T, Stream<R>>)`: Transforms to stream, flattens result
- Use case: One-to-many transformations (each element becomes multiple elements)
- Example: `stream.flatMap(word -> Stream.of(word.split("")))`
- Returns Stream, allowing further operations

**Slide 7: Intermediate Operations: Sorting, Distinct, Limiting**
- `sorted()`: Natural sort order (must implement Comparable)
- `sorted(Comparator<T>)`: Custom sort order
- Example: `stream.sorted((a,b) -> b.length() - a.length())` (descending by length)
- `distinct()`: Removes duplicates (uses equals/hashCode)
- `limit(n)`: First n elements only (useful for large streams)
- `skip(n)`: Skips first n elements
- Use case for skip/limit: Pagination patterns
- Example: `stream.skip(10).limit(20)` (items 11-30)
- All return Stream, chainable

**Slide 8: Intermediate Operations: Peek & Intermediate Mutations**
- `peek(Consumer<T>)`: Executes action on each element, passes through unchanged
- Primarily for debugging: seeing values flowing through pipeline
- Example: `stream.filter(...).peek(System.out::println).map(...)`
- Important: Don't use peek for side effects in production (violates functional style)
- `map()` vs `peek()`: map transforms, peek doesn't
- Warning: Operations like collecting to list are terminal, not intermediate
- Real use: Troubleshooting stream operations during development

### Slides 9-15: Terminal Operations & Collectors
**Slide 9: Terminal Operations Overview**
- Terminal operations: Consume stream and produce result
- After terminal operation: Stream is consumed, cannot reuse
- Result types: Individual values (int, long, Optional), Collections, void (side effects)
- Common terminals: `collect()`, `forEach()`, `reduce()`, `count()`, `findFirst()`, `anyMatch()`, `allMatch()`, `min()`, `max()`
- Key insight: Pipeline executes only when terminal operation is called
- Example: `stream.filter(...).map(...)` does nothing; `stream.filter(...).map(...).collect(...)` executes

**Slide 10: forEach Terminal Operation**
- `forEach(Consumer<T>)`: Executes action on each element
- Returns void (side effects only)
- Example: `stream.forEach(System.out::println)`
- Use case: Printing, logging, updating external state
- Sequential vs parallel: forEach respects stream's sequential/parallel nature
- Comparison: Traditional for-each loop vs stream forEach
- Performance note: forEach might be slower than traditional loop (overhead)

**Slide 11: Reduction Operations: reduce()**
- `reduce()`: Combines elements into single value
- Three overloads:
  - `reduce(BinaryOperator<T>)`: Combines elements, returns Optional<T>
  - `reduce(T identity, BinaryOperator<T>)`: Identity value, returns T
  - `reduce(T identity, BiFunction, BinaryOperator)`: Complex reduction (parallel-safe)
- Example: `stream.reduce(0, (a,b) -> a + b)` (sum)
- Example: `stream.reduce((a,b) -> a > b ? a : b)` (max, Optional<T>)
- Use case: Aggregations (sum, product, concatenation)
- Identity value: Starting value, neutral for operation (0 for sum, 1 for product)

**Slide 12: Collection Operations: count, findFirst, anyMatch**
- `count()`: Number of elements
- Example: `stream.count()` returns long
- `findFirst()`: First element, returns Optional
- Example: `stream.findFirst().ifPresent(...)`
- Use case: Finding first matching element after filtering
- `anyMatch(Predicate)`: Boolean, true if any element matches
- `allMatch(Predicate)`: Boolean, true if all elements match
- `noneMatch(Predicate)`: Boolean, true if no elements match
- Example: `stream.anyMatch(s -> s.length() > 10)` (any long strings?)

**Slide 13: collect() Terminal Operation**
- Most powerful terminal operation
- `collect(Collector<T,A,R>)`: Accumulates elements into container
- Common collectors (from Collectors utility class):
  - `toList()`: Collect to List
  - `toSet()`: Collect to Set (duplicates removed)
  - `toMap(keyFunction, valueFunction)`: Collect to Map
  - `groupingBy(Function)`: Group by criteria, returns Map<K, List<V>>
  - `partitioningBy(Predicate)`: Partition into two groups (true/false)
  - `joining(delimiter)`: Join strings with delimiter
- Examples:
  - `stream.collect(Collectors.toList())`
  - `stream.collect(Collectors.groupingBy(Person::getAge))` (group by age)
  - `stream.collect(Collectors.joining(", "))` (comma-separated)

**Slide 14: Custom Collectors & Complex Reductions**
- `Collectors.mapping()`: Apply function while collecting
- `Collectors.summingInt()`, `summingLong()`, `summingDouble()`: Sum numeric values
- `Collectors.minBy()`, `maxBy()`: Min/max elements by comparator
- Example: `stream.collect(Collectors.toMap(Person::getId, Person::getName))`
- Example: `stream.collect(Collectors.groupingBy(Person::getDepartment, Collectors.counting()))`
- Creating custom collector: Rare, complex, usually use provided collectors
- Best practice: Use provided collectors first; only create custom if necessary

**Slide 15: Stream Consumption & Order**
- Stream can only be consumed once: After terminal operation, stream is closed
- Wrong: `Stream<String> s = list.stream(); s.forEach(...); s.forEach(...);` (second forEach fails)
- Correct: Create new stream for each terminal operation: `list.stream().forEach(...); list.stream().forEach(...)`
- Ordering: Streams preserve encounter order by default (sequential)
- Parallel streams: May not preserve order (performance trade-off)
- Immutability: Streams don't modify source collection (unless explicit mutation in lambda)
- Example: Stream from list, filter, collect to new list (original unchanged)

### Slides 16-22: Real-World Stream Examples
**Slide 16: Example 1: Filtering and Collecting**
- Scenario: Get names of all active users over 18
- Traditional loop approach (verbose)
- Stream approach:
  ```java
  List<String> names = users.stream()
      .filter(u -> u.isActive() && u.getAge() > 18)
      .map(User::getName)
      .collect(Collectors.toList());
  ```
- Readability: Intent is clear in one readable chain
- Comparison: Traditional vs stream (3-5 lines vs 1 readable line)
- Benefit: Concise, composable, chainable

**Slide 17: Example 2: Grouping and Aggregation**
- Scenario: Group users by department, count per department
- Traditional: Iterate, check department, create map, increment counter
- Stream approach:
  ```java
  Map<String, Long> deptCounts = users.stream()
      .collect(Collectors.groupingBy(User::getDepartment, Collectors.counting()));
  ```
- Advanced: Nested grouping
  ```java
  Map<String, Map<String, List<User>>> grouped = users.stream()
      .collect(Collectors.groupingBy(
          User::getDepartment,
          Collectors.groupingBy(User::getTeam)
      ));
  ```
- Real-world: Reporting, analytics, data aggregation

**Slide 18: Example 3: Sorting and Limiting (Pagination)**
- Scenario: Get top 5 highest-paid employees
- Stream approach:
  ```java
  List<Employee> topEarners = employees.stream()
      .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
      .limit(5)
      .collect(Collectors.toList());
  ```
- Pagination pattern: Skip and limit
  ```java
  int pageSize = 20;
  int pageNum = 2;
  List<User> page = users.stream()
      .skip(pageNum * pageSize)
      .limit(pageSize)
      .collect(Collectors.toList());
  ```
- Real-world: Leaderboards, reporting, API responses

**Slide 19: Example 4: Transformations with flatMap**
- Scenario: Extract all products from all orders
- Data: `List<Order>` where each order has `List<Product>`
- Traditional: Nested loops to flatten
- Stream approach:
  ```java
  List<Product> allProducts = orders.stream()
      .flatMap(order -> order.getProducts().stream())
      .collect(Collectors.toList());
  ```
- Advanced: Remove duplicates
  ```java
  Set<Product> uniqueProducts = orders.stream()
      .flatMap(order -> order.getProducts().stream())
      .collect(Collectors.toSet());
  ```
- Real-world: Flattening nested structures, combining collections

**Slide 20: Example 5: Partition and Filtering**
- Scenario: Separate valid and invalid emails
- Stream approach with partitioningBy:
  ```java
  Map<Boolean, List<String>> byValidity = emails.stream()
      .collect(Collectors.partitioningBy(this::isValidEmail));
  
  List<String> valid = byValidity.get(true);
  List<String> invalid = byValidity.get(false);
  ```
- Use case: Error handling, data validation
- Alternative with filter:
  ```java
  List<String> valid = emails.stream()
      .filter(this::isValidEmail)
      .collect(Collectors.toList());
  ```

**Slide 21: Example 6: Parallel Streams for Performance**
- Scenario: Processing large dataset
- Sequential: `list.stream().map(...).collect(...)`
- Parallel: `list.parallelStream().map(...).collect(...)`
- Or: `stream.parallel()`
- Benefits: Automatic parallelization across multiple threads
- When useful: Large datasets where computation is significant
- Caveat: Overhead for small collections; ensure lambdas are stateless
- Performance caution: Not always faster; test before relying on parallel
- Real-world: Processing millions of records, heavy computations

**Slide 22: Common Stream Mistakes**
- Mistake: Modifying source collection inside lambda (side effects)
- Mistake: Using stream operation without terminal (pipeline does nothing)
- Mistake: Reusing stream after terminal operation
- Mistake: Assuming parallel streams always faster (profile first)
- Mistake: Using mutable lambdas/stateful operations
- Mistake: Not understanding lazy evaluation (expecting execution before terminal)
- Best practice: Functional style—no side effects, chained operations, terminal required

### Slides 23-30: DateTime API Fundamentals
**Slide 23: The Problem with java.util.Date**
- java.util.Date: Mutable, confusing API (month 0-based), not thread-safe
- SimpleDateFormat: Not thread-safe (causes bugs in concurrent code)
- Calendar class: Complex, unintuitive methods
- Timezone handling: Confusing, error-prone
- Third-party libraries: Before Java 8, many used Joda-Time
- Modern solution: java.time package (Java 8+)
- Key difference: New API is immutable, thread-safe, comprehensive

**Slide 24: LocalDate, LocalTime, LocalDateTime**
- `LocalDate`: Date without time (year-month-day)
  - Example: `LocalDate.of(2024, 12, 25)` or `LocalDate.now()`
  - Operations: `plusDays()`, `minusMonths()`, `getYear()`, `getDayOfWeek()`
- `LocalTime`: Time without date (hour-minute-second)
  - Example: `LocalTime.of(14, 30, 0)` or `LocalTime.now()`
  - Operations: `plusHours()`, `minusMinutes()`, `getHour()`
- `LocalDateTime`: Date AND time together
  - Example: `LocalDateTime.of(2024, 12, 25, 14, 30, 0)`
  - Operations: Combined LocalDate and LocalTime methods
- Immutable: All operations return new instances, originals unchanged
- Thread-safe: Can safely share across threads

**Slide 25: ZonedDateTime & Timezone Handling**
- `LocalDateTime` has no timezone info
- `ZonedDateTime`: LocalDateTime with timezone
- Example: `ZonedDateTime.now()` includes timezone
- Timezone handling:
  ```java
  ZoneId tokyo = ZoneId.of("Asia/Tokyo");
  ZonedDateTime tokyoTime = ZonedDateTime.now(tokyo);
  ```
- Converting: `LocalDateTime.atZone(ZoneId)` → `ZonedDateTime`
- Use case: Global applications, server time vs user local time
- Best practice: Store UTC, format for display in user's timezone

**Slide 26: Formatting & Parsing**
- `DateTimeFormatter`: Format dates/times to strings and parse back
- Predefined formatters:
  ```java
  LocalDate date = LocalDate.now();
  String formatted = date.format(DateTimeFormatter.ISO_DATE);  // "2024-12-25"
  ```
- Custom formatters:
  ```java
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  String formatted = date.format(formatter);  // "25/12/2024"
  String parsed = LocalDate.parse("25/12/2024", formatter);
  ```
- Common patterns: "yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss"
- Localization: Formatter can use Locale for language-specific output
- Real-world: API responses, user input parsing, display formatting

**Slide 27: Period & Duration**
- `Period`: Difference in dates (days, months, years)
  - Example: `Period.between(startDate, endDate)` returns `Period`
  - Operations: `getDays()`, `getMonths()`, `getYears()`
  - Use case: Age calculation, event recurrence
- `Duration`: Difference in time (seconds, nanos)
  - Example: `Duration.between(startTime, endTime)` returns `Duration`
  - Operations: `getSeconds()`, `getNano()`, `toMillis()`
  - Use case: Elapsed time, performance measurement
- Immutable: Both return new instances
- Real-world examples:
  - Age: `Period.between(birthDate, LocalDate.now()).getYears()`
  - Elapsed time: `Duration.between(start, end).toMillis()`

**Slide 28: Real-World DateTime Examples**
- Example 1: Birthday reminder (next birthday calculation)
  ```java
  LocalDate today = LocalDate.now();
  LocalDate birthday = LocalDate.of(1990, 5, 15);
  LocalDate thisBirthday = birthday.withYear(today.getYear());
  if (thisBirthday.isBefore(today)) {
      thisBirthday = thisBirthday.plusYears(1);
  }
  Period untilBirthday = Period.between(today, thisBirthday);
  System.out.println("Days until birthday: " + untilBirthday.getDays());
  ```
- Example 2: Meeting schedule (finding next available slot)
  ```java
  LocalDateTime now = LocalDateTime.now();
  LocalDateTime nextSlot = now.plusHours(1).withMinute(0);  // Next full hour
  if (nextSlot.isBefore(businessStart)) nextSlot = businessStart;
  ```
- Example 3: Timeout tracking
  ```java
  LocalDateTime start = LocalDateTime.now();
  Duration timeout = Duration.ofMinutes(5);
  if (Duration.between(start, LocalDateTime.now()).compareTo(timeout) > 0) {
      // Timeout exceeded
  }
  ```

**Slide 29: Temporal Adjusters & Advanced Operations**
- `TemporalAdjuster`: Predefined logic for common date adjustments
- Examples: `firstDayOfMonth()`, `lastDayOfYear()`, `next(DayOfWeek.MONDAY)`
- Usage:
  ```java
  LocalDate today = LocalDate.now();
  LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
  LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
  ```
- Custom adjusters: Implement `TemporalAdjuster` for domain-specific logic
- Real-world: Business logic (end of quarter reporting, next business day)

**Slide 30: DateTime Best Practices & Common Mistakes**
- Best practice 1: Use `LocalDateTime` for application logic, `ZonedDateTime` for external communication
- Best practice 2: Always store UTC internally, convert to user timezone for display
- Best practice 3: Use `DateTimeFormatter` for parsing/formatting strings
- Mistake 1: Using `LocalDateTime` for international systems (missing timezone)
- Mistake 2: Mutable Date/Calendar (memory, concurrency issues)
- Mistake 3: Not specifying formatter patterns (incompatible formats)
- Mistake 4: Forgetting immutability (expecting mutation, getting new instance)
- Mistake 5: Timezone conversions after parsing (parse with timezone info, not after)

### Slides 31-35: Integration & Summary
**Slide 31: Combining Streams with DateTime**
- Scenario: Filter events by date range, count by day
- Stream approach:
  ```java
  Map<LocalDate, Long> eventsByDay = events.stream()
      .filter(e -> e.getDate().isAfter(startDate) && e.getDate().isBefore(endDate))
      .collect(Collectors.groupingBy(
          event -> event.getDateTime().toLocalDate(),
          Collectors.counting()
      ));
  ```
- Real-world: Analytics, reporting, time-series data
- Pattern: Stream + filter + group + count for temporal data

**Slide 32: Parallel Streams with Heavy Computations**
- When to use parallel: Expensive operations on large datasets
- Example: Time-consuming processing
  ```java
  List<Result> results = data.parallelStream()
      .map(d -> expensiveComputation(d))
      .filter(r -> r.isValid())
      .collect(Collectors.toList());
  ```
- Timing with Duration:
  ```java
  LocalTime start = LocalTime.now();
  // Processing...
  LocalTime end = LocalTime.now();
  Duration elapsed = Duration.between(start, end);
  System.out.println("Time: " + elapsed.toMillis() + "ms");
  ```

**Slide 33: Real-World Integration Example: Report Generation**
- Scenario: Generate monthly report from transaction data
- Requirements: Group by category, sum amounts, filter by date range
- Stream + DateTime approach:
  ```java
  LocalDate monthStart = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
  LocalDate monthEnd = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
  
  Map<Category, Double> monthlySummary = transactions.stream()
      .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
      .collect(Collectors.groupingBy(
          Transaction::getCategory,
          Collectors.summingDouble(Transaction::getAmount)
      ));
  ```
- Real-world: Financial reports, analytics, business intelligence

**Slide 34: Performance Considerations**
- Stream creation overhead: Minimal for one-time operations
- Parallel streams: Only faster for large collections (>1000 elements) with heavy operations
- Collectors: `toList()` is efficient; custom collectors might be slower
- DateTime operations: Immutability has minimal performance impact; thread-safety worth cost
- Benchmarking: Profile before optimizing; streams often offer better code clarity than loops
- GC considerations: Stream operations create intermediate objects; generational GC handles well

**Slide 35: Summary & Key Takeaways**
- Stream API: Functional data processing with filter, map, reduce, collect
- Three phases: Source (collection/generator) → Intermediate operations (transformations) → Terminal operation (result)
- Lazy evaluation: Nothing happens until terminal operation
- Collectors: Powerful for grouping, partitioning, custom aggregations
- DateTime API: Immutable, thread-safe alternatives to Date/Calendar
- LocalDate/LocalTime: Date/time components without timezone
- ZonedDateTime: Timezone-aware temporal representation
- Period/Duration: Measure time differences in different units
- Integration: Streams process temporal data; DateTime fills in where collections lack time support
- Best practices: Functional style, immutability, proper timezone handling
- Production ready: Use streams for readability; DateTime for all date/time logic
- Connection: Mastering these transforms Java programming from imperative to declarative

