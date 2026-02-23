# Week 2 - Day 8, Part 2: Stream API & DateTime
## 60-Minute Lecture Script

---

[00:00-02:00] **Welcome Back & Bridge**

Welcome back, everyone. I hope you got some good air or water during the break. We've covered a lot of ground in Part 1: lambdas, functional interfaces, method references, and Optional. Hopefully those concepts are settling in.

Now, in Part 2, we're going to see those concepts in action, and it's going to be powerful. We're covering two major topics: the Stream API and the DateTime API.

The Stream API is built entirely on lambdas and functional interfaces. It's how modern Java processes collections—not with for loops, but with declarative operations that describe transformations. You'll see it everywhere in production code.

The DateTime API? It's the right way to work with dates and times. Not java.util.Date (which is mutable and broken), not Calendar (which is confusing), but the java.time package that's been standard since Java 8. Clean, immutable, thread-safe.

Let's start with streams.

---

[02:00-04:00] **Understanding Streams: Not Collections**

Okay, first thing: a Stream is not a collection. I know that sounds weird, but it's important.

A collection stores data. A List holds elements. A Set holds elements. A collection is about storage.

A Stream? A Stream is about processing. It's a pipeline. Data flows through it, gets transformed, and you get out results. It's more like a factory assembly line than a warehouse.

More formally: A Stream is a sequence of elements that supports functional-style aggregate operations. It's lazy—nothing happens until you actually ask for a result. And it can process data in parallel.

Here's the kicker: once you consume a stream with a terminal operation, it's done. You can't reuse it. You create new streams as needed.

Why design it this way? Because it opens the door to optimization and parallelization that collections don't support. A Stream knows its operation pipeline, so it can decide whether to process sequentially or in parallel. It can lazily evaluate operations. It's designed for efficiency.

---

[04:00-08:00] **Stream Operations: Source, Intermediate, Terminal**

Every stream has a basic structure. Let me walk you through it.

First: the Source. Where does the stream come from? Usually a collection:

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Stream<String> stream = names.stream();
```

But you can also create streams from arrays:

```java
String[] names = {"Alice", "Bob", "Charlie"};
Stream<String> stream = Arrays.stream(names);
```

Or from scratch:

```java
Stream<String> stream = Stream.of("Alice", "Bob", "Charlie");
```

Or infinite streams:

```java
Stream<Integer> infiniteNumbers = Stream.iterate(0, n -> n + 1);
```

Okay, so you have your source. Now what?

Second: Intermediate Operations. These transform the stream. They take a Stream in and return a Stream out. Examples: `filter`, `map`, `sorted`, `distinct`, `limit`. Each one modifies the stream, but nothing happens yet. It's all lazy.

```java
names.stream()
    .filter(n -> n.length() > 3)  // Intermediate
    .map(String::toUpperCase)      // Intermediate
    .sorted()                      // Intermediate
```

At this point, no filtering, no mapping, no sorting has happened. It's all set up, waiting.

Third: Terminal Operation. This is the final step. It consumes the stream and produces a result. Examples: `collect`, `forEach`, `reduce`, `count`, `findFirst`. Once you call a terminal operation, the whole pipeline executes.

```java
names.stream()
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .sorted()
    .forEach(System.out::println);  // Terminal - NOW it executes
```

That forEach is the terminal operation. And THAT'S when everything happens. The stream filters, maps, sorts, and then prints.

Common mistake: Writing a stream without a terminal operation. The code compiles, but nothing happens:

```java
// Wrong - does nothing
names.stream()
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase);

// Right - executes pipeline
names.stream()
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

That's lazy evaluation. The stream sets everything up, but execution waits until you ask for the result.

---

[08:00-14:00] **Key Intermediate Operations**

Let me walk through the most common intermediate operations you'll use.

**Filter**: Keeps elements that match a condition, discards the rest.

```java
Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
numbers.filter(n -> n % 2 == 0)  // Keeps only even numbers
```

The Predicate tells filter what to keep. Easy.

**Map**: Transforms each element.

```java
Stream<String> words = Stream.of("hello", "world");
words.map(String::toUpperCase)  // Each word becomes uppercase
```

Map takes a Function that says: "Given this element, produce that result." Each input becomes one output.

**FlatMap**: Like map, but when your transformation produces multiple outputs per input.

```java
Stream<String> words = Stream.of("hello", "world");
words.flatMap(word -> Stream.of(word.split("")))  // Each word becomes individual letters
```

Each word becomes a stream of letters. FlatMap flattens all those letter streams into one. Without flatMap, you'd get `Stream<Stream<String>>`, which is awkward.

**Sorted**: Orders elements. If no comparator, natural order (must implement Comparable). With a comparator, custom order:

```java
Stream<String> words = Stream.of("apple", "a", "abc");
words.sorted()  // Natural order: ["a", "abc", "apple"]
words.sorted((a, b) -> b.length() - a.length())  // By length, descending
```

**Distinct**: Removes duplicates. Uses equals and hashCode:

```java
Stream<Integer> numbers = Stream.of(1, 2, 2, 3, 3, 3);
numbers.distinct()  // [1, 2, 3]
```

**Limit and Skip**: Limit takes the first n elements. Skip skips the first n elements.

```java
Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
numbers.skip(2).limit(2)  // [3, 4]
```

Skip and limit are useful for pagination. Skip 20 for page 2 (with page size 10), limit 10.

**Peek**: Executes an action on each element, passes it through unchanged. Mostly for debugging:

```java
Stream<String> words = Stream.of("hello", "world");
words
    .filter(w -> w.length() > 3)
    .peek(w -> System.out.println("Filtered: " + w))
    .map(String::toUpperCase)
    .peek(w -> System.out.println("Uppercase: " + w))
    .forEach(System.out::println);
```

Peek lets you see what's flowing through the pipeline at each stage. Great for troubleshooting.

---

[14:00-20:00] **Terminal Operations: Consuming the Stream**

Alright, so you've built your transformation pipeline. Now what? You need a terminal operation to actually execute it and get results.

**ForEach**: Executes an action on each element.

```java
names.stream()
    .filter(...)
    .forEach(System.out::println);
```

That Consumer—the action—runs on each element. This is for side effects: printing, logging, updating state.

**Collect**: Accumulates elements into a container. This is the powerhouse.

```java
List<String> result = names.stream()
    .filter(n -> n.length() > 3)
    .collect(Collectors.toList());
```

You pass a Collector that says what container to use. Collectors has lots of options: `toList()`, `toSet()`, `toMap()`, `joining()`, etc.

**Reduce**: Combines elements into a single value.

```java
// Sum of numbers
int sum = Stream.of(1, 2, 3, 4, 5)
    .reduce(0, (acc, n) -> acc + n);  // 15
```

The identity (0) is the starting value. The BinaryOperator combines accumulator with each element. For sum, 0 is neutral (0 + any = any). For product, you'd use 1.

Or without identity, reduce returns Optional:

```java
// Max of numbers
Stream.of(1, 2, 3, 4, 5)
    .reduce((a, b) -> a > b ? a : b)  // Optional[5]
```

**Count**: Number of elements.

```java
long count = names.stream()
    .filter(...)
    .count();
```

Returns a long.

**FindFirst**: Returns the first element, if any.

```java
Optional<String> first = names.stream()
    .filter(n -> n.contains("a"))
    .findFirst();
```

Returns Optional because there might not be any matching elements.

**AnyMatch, AllMatch, NoneMatch**: Boolean checks.

```java
boolean hasLongName = names.stream()
    .anyMatch(n -> n.length() > 5);  // Is there any name longer than 5?

boolean allLong = names.stream()
    .allMatch(n -> n.length() > 5);  // Are all names longer than 5?

boolean noneShort = names.stream()
    .noneMatch(n -> n.length() < 3);  // Are all names at least 3 characters?
```

These are short-circuit operations. They can stop early once they know the answer.

---

[20:00-26:00] **Collectors: The Power of collect()**

Collect is where streams really shine. Let me show you the power of Collectors.

The simplest: `toList()`.

```java
List<String> uppercase = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

That's obvious. But it gets more powerful.

**GroupingBy**: Group elements by a criteria.

```java
Map<Integer, List<String>> byLength = names.stream()
    .collect(Collectors.groupingBy(String::length));
// {3: ["Bob"], 5: ["Alice"], 7: ["Charlie"]}
```

You group by something (in this case, the length of each name), and you get a Map where the key is the grouping criteria and the value is a list of elements in that group.

You can even do nested grouping:

```java
Map<Integer, Set<String>> byLengthAsSet = names.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.toSet()  // Instead of toList()
    ));
```

**PartitioningBy**: Split elements into two groups: those matching a predicate and those not.

```java
Map<Boolean, List<String>> longVsShort = names.stream()
    .collect(Collectors.partitioningBy(n -> n.length() > 4));
// {true: ["Alice", "Charlie"], false: ["Bob"]}
```

**Joining**: Concatenate strings with a delimiter.

```java
String joined = names.stream()
    .collect(Collectors.joining(", "));
// "Alice, Bob, Charlie"
```

Add prefix/suffix:

```java
String result = names.stream()
    .collect(Collectors.joining(", ", "[", "]"));
// "[Alice, Bob, Charlie]"
```

**Summation**: Sum numeric values.

```java
int totalLength = names.stream()
    .collect(Collectors.summingInt(String::length));  // 14
```

There's also `summingLong()` and `summingDouble()`.

**Advanced**: Custom aggregations.

```java
Map<Integer, Long> countByLength = names.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.counting()  // Count instead of listing
    ));
// {3: 1, 5: 1, 7: 1}
```

You're combining collectors: grouping by length, but for each group, counting instead of collecting elements.

That's the power of collectors. They let you express complex aggregations clearly and concisely.

---

[26:00-32:00] **Real-World Stream Examples**

Let me show you real patterns you'll see and write.

**Example 1: Filtering and Transforming**

Scenario: You have a list of users. Get names of all active premium users.

```java
List<String> premiumNames = users.stream()
    .filter(u -> u.isActive() && u.isPremium())
    .map(User::getName)
    .collect(Collectors.toList());
```

That reads like English: "From users, filter to active and premium, map to names, collect to list." Crystal clear.

**Example 2: Grouping and Reporting**

Scenario: Group employees by department and count.

```java
Map<String, Long> employeesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.counting()
    ));
```

You get a map: department name to employee count. Perfect for reporting.

**Example 3: Finding Elements**

Scenario: Find the highest-paid employee in each department.

```java
Map<String, Employee> topEarnerByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
    ));
```

For each department, find the employee with max salary. Group and custom collector, combining operations.

**Example 4: Flattening Nested Structures**

Scenario: You have orders, each with multiple line items. Get all line items.

```java
List<LineItem> allItems = orders.stream()
    .flatMap(order -> order.getLineItems().stream())
    .collect(Collectors.toList());
```

FlatMap "unpacks" each order's items into one stream. Then collect all. Elegant.

**Example 5: Pagination**

Scenario: Get page 2 of users, 20 per page.

```java
List<User> page2 = users.stream()
    .skip(20)  // Skip first 20
    .limit(20)  // Take next 20
    .collect(Collectors.toList());
```

Skip and limit work together for pagination. Scalable for large datasets if you're streaming from a database.

---

[32:00-38:00] **DateTime API: The Right Way**

Alright, switching gears. Let's talk about date and time. 

Before Java 8, Java had `java.util.Date`. It was... problematic. Mutable (could be changed after creation, causing bugs). Confusing API (months were 0-based: January is 0). Not thread-safe. There was also `Calendar`, which was even more complex and unintuitive.

Many Java developers used Joda-Time, a third-party library, because it was so much better.

Then Java 8 came along and said: "Let's fix this." They created `java.time`, modeled on Joda-Time. Immutable, thread-safe, intuitive. It's been part of standard Java since 2014.

Key principle: Immutability. When you do operations on dates, you get new instances. The original never changes. This prevents bugs and makes it thread-safe.

Let me introduce the main classes: `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`.

**LocalDate**: Just the date, no time. Year, month, day.

```java
LocalDate today = LocalDate.now();  // Today's date
LocalDate christmas = LocalDate.of(2024, 12, 25);
```

Methods: `getYear()`, `getMonth()`, `getDayOfMonth()`, `getDayOfWeek()`, etc. And operations:

```java
LocalDate tomorrow = today.plusDays(1);
LocalDate nextMonth = today.plusMonths(1);
LocalDate nextyear = today.plusYears(1);  // All return new instances
```

**LocalTime**: Just the time, no date. Hour, minute, second, nanosecond.

```java
LocalTime now = LocalTime.now();
LocalTime meeting = LocalTime.of(14, 30, 0);  // 2:30 PM
```

Methods: `getHour()`, `getMinute()`, `getSecond()`, etc.

```java
LocalTime later = meeting.plusHours(1);
```

**LocalDateTime**: Date and time together.

```java
LocalDateTime now = LocalDateTime.now();
LocalDateTime meeting = LocalDateTime.of(2024, 12, 25, 14, 30, 0);
```

Combines everything from LocalDate and LocalTime.

Now, here's an important limitation: None of these have timezone information. They're "naive"—they don't know what timezone they represent.

**ZonedDateTime**: LocalDateTime with timezone.

```java
ZonedDateTime now = ZonedDateTime.now();  // Current time in system timezone
ZoneId tokyo = ZoneId.of("Asia/Tokyo");
ZonedDateTime tokyoTime = ZonedDateTime.now(tokyo);
```

Best practice for global applications: Store everything in UTC internally. When you need to display to a user, convert to their timezone.

```java
ZonedDateTime utcTime = ZonedDateTime.now(ZoneId.of("UTC"));
ZoneId userZone = ZoneId.of("America/New_York");
ZonedDateTime userTime = utcTime.withZoneSameInstant(userZone);
```

---

[38:00-44:00] **DateTime Operations: Formatting, Parsing, Ranges**

**Formatting and Parsing**:

```java
LocalDate date = LocalDate.of(2024, 12, 25);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
String formatted = date.format(formatter);  // "2024-12-25"
```

You create a formatter with a pattern, then format dates with it. Or parse strings back to dates:

```java
String dateString = "2024-12-25";
LocalDate parsed = LocalDate.parse(dateString, formatter);
```

Common patterns: "yyyy-MM-dd" (ISO standard), "dd/MM/yyyy" (European), "MM/dd/yyyy" (US), "dd.MM.yyyy HH:mm:ss" (European with time).

You can also use predefined formatters:

```java
String iso = date.format(DateTimeFormatter.ISO_DATE);  // "2024-12-25"
```

And localized formats:

```java
DateTimeFormatter german = DateTimeFormatter.ofPattern("dd.MMMM.yyyy")
    .withLocale(Locale.GERMAN);
String germanDate = date.format(german);  // "25.Dezember.2024"
```

**Measuring Differences: Period and Duration**:

`Period` measures differences in dates (days, months, years):

```java
LocalDate start = LocalDate.of(2020, 1, 1);
LocalDate end = LocalDate.now();
Period elapsed = Period.between(start, end);
int years = elapsed.getYears();
int months = elapsed.getMonths();
int days = elapsed.getDays();
```

Use case: Age calculation.

```java
LocalDate birthDate = LocalDate.of(1990, 5, 15);
int age = Period.between(birthDate, LocalDate.now()).getYears();
```

`Duration` measures differences in time (hours, minutes, seconds, nanoseconds):

```java
LocalTime start = LocalTime.of(10, 0, 0);
LocalTime end = LocalTime.of(14, 30, 0);
Duration duration = Duration.between(start, end);
long hours = duration.toHours();  // 4
long minutes = duration.toMinutes();  // 270
long millis = duration.toMillis();  // 16200000
```

Use case: Measuring elapsed time.

```java
long startNano = System.nanoTime();
// ... do work ...
long endNano = System.nanoTime();
Duration elapsed = Duration.ofNanos(endNano - startNano);
System.out.println("Elapsed: " + elapsed.toMillis() + "ms");
```

**Temporal Adjusters**:

Common adjustments have helper methods. `TemporalAdjusters` class provides them:

```java
LocalDate today = LocalDate.now();
LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());
LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
LocalDate firstDayOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());
```

These are super useful for business logic.

---

[44:00-50:00] **Real-World DateTime Examples**

**Example 1: Birthday Reminder**

Calculate days until next birthday:

```java
LocalDate today = LocalDate.now();
LocalDate birthDate = LocalDate.of(1990, 5, 15);

// This year's birthday
LocalDate thisYearBirthday = birthDate.withYear(today.getYear());

// If it's already passed this year, use next year's
if (thisYearBirthday.isBefore(today)) {
    thisYearBirthday = thisYearBirthday.plusYears(1);
}

Period until = Period.between(today, thisYearBirthday);
System.out.println("Days until birthday: " + until.getDays());
```

**Example 2: Business Hours Check**

Is it currently business hours?

```java
LocalTime now = LocalTime.now();
LocalTime businessStart = LocalTime.of(9, 0, 0);
LocalTime businessEnd = LocalTime.of(17, 0, 0);

boolean isBusinessHours = !now.isBefore(businessStart) && now.isBefore(businessEnd);
```

**Example 3: Scheduling the Next Available Slot**

Find next available meeting slot:

```java
LocalDateTime now = LocalDateTime.now();
LocalDateTime nextSlot = now.plusHours(1).withMinute(0).withSecond(0);

LocalTime businessStart = LocalTime.of(9, 0);
LocalTime businessEnd = LocalTime.of(17, 0);

if (nextSlot.toLocalTime().isBefore(businessStart)) {
    nextSlot = nextSlot.with(LocalTime.of(9, 0, 0));
} else if (nextSlot.toLocalTime().isAfter(businessEnd)) {
    nextSlot = nextSlot.plusDays(1).with(LocalTime.of(9, 0, 0));
}
```

**Example 4: Reporting on Date Ranges**

Group events by date:

```java
LocalDate start = LocalDate.now().minusMonths(1);
LocalDate end = LocalDate.now();

Map<LocalDate, List<Event>> eventsByDate = events.stream()
    .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
    .collect(Collectors.groupingBy(Event::getDate));
```

You're filtering within a date range, then grouping by date. Perfect for calendars or time-series reports.

---

[50:00-56:00] **Common DateTime Mistakes & Best Practices**

**Mistake 1: Using LocalDateTime for global systems.**

LocalDateTime has no timezone. If your app is used worldwide, you don't know what timezone the user is in.

Better: Use `ZonedDateTime` for everything touching the outside world. Store UTC internally, convert for display.

**Mistake 2: Using java.util.Date anywhere.**

Just don't. It's mutable, it's confusing. Always use java.time.

**Mistake 3: Not specifying a formatter when parsing.**

```java
// Bad - default format only
LocalDate date = LocalDate.parse("25/12/2024");  // Fails

// Good - specify format
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date = LocalDate.parse("25/12/2024", formatter);
```

**Mistake 4: Forgetting immutability.**

```java
// Wrong
LocalDate date = LocalDate.now();
date.plusDays(1);  // This doesn't modify date
System.out.println(date);  // Still today

// Right
LocalDate date = LocalDate.now();
LocalDate tomorrow = date.plusDays(1);  // Assign to new variable
System.out.println(tomorrow);  // Tomorrow
```

**Best Practice 1: Standardize on UTC internally.**

```java
ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
// Store this, not system timezone
```

**Best Practice 2: Convert to user timezone only for display.**

```java
ZonedDateTime userTime = utcTime.withZoneSameInstant(userTimeZone);
String displayText = userTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
```

**Best Practice 3: Use LocalDate for date-only logic, LocalDateTime for date+time, ZonedDateTime for internationalization.**

---

[56:00-60:00] **Wrap-Up & Integration**

We've covered a lot. Streams and DateTime. Let me bring it together.

Streams transformed how you process collections. Instead of loops, you describe: filter, transform, collect. Lazy evaluation, composable operations, potential parallelization. And it's built on lambdas and functional interfaces from Part 1.

DateTime gives you the right tools to work with dates and times. Immutable, thread-safe, intuitive API. Covers all scenarios: dates, times, timezones, durations, periods.

Real applications combine both: Stream events, group by date using DateTime, aggregate results using collectors.

This is Week 2. You've learned exception handling and I/O. Lambdas, functional interfaces, and Optional. Streams and DateTime. You're building a comprehensive toolkit.

Next comes Week 3, where we step into multithreading. Concurrent collections, executors, asynchronous workflows. All of this—lambdas, streams—enables cleaner, safer concurrent code.

But that's tomorrow. For now, practice streams. Write some lambdas. Play with the DateTime API. Let it sink in.

Great work today. We'll see you tomorrow for multithreading.

