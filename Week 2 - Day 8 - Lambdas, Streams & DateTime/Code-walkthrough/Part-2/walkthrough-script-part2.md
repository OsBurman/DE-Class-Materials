# Day 8 — Part 2 Walkthrough Script
## Stream API & DateTime API
**Duration:** ~90 minutes | **Files:** 2 Java demos

---

## Pre-Class Setup (3 min)

[ACTION] Open both Part 2 files. Have the previous session's summary visible on a second monitor or whiteboard for reference.

[ACTION] Write on the board:
```
"A stream is a pipeline — source → transform → collect."
"Streams are lazy. Nothing runs until the terminal operation is called."
"java.time objects are immutable — every operation returns a NEW object."
```

[ASK] "Who has used a for-loop to filter a list before? Good. Today you'll learn to write that in one line — and do things in one expression that would take 10 lines of loops."

---

## FILE 1: `01-stream-api-basics-and-operations.java` (~55 min)

### Opening: The Concept (5 min)

[ACTION] Before opening the file, draw a pipeline on the board:

```
   [List]  →  .stream()  →  .filter()  →  .map()  →  .collect()
   Source     (creates)   (intermediate) (intermediate) (terminal)
```

Key points to say:
- "A stream is **not** a data structure. It doesn't store anything. It describes *how* to process data."
- "Intermediate operations are **lazy** — they do nothing until a terminal operation triggers them."
- "You can chain as many intermediate operations as you want, but you can only end with ONE terminal operation."
- "Once a stream is consumed (terminal op called), it's done. You cannot reuse it."

[ASK] "Why do you think laziness is an advantage?" (You don't waste work on elements that get filtered out before a later step. `limit(5)` after a `generate()` doesn't generate forever.)

---

### Section 1 — `demonstrateStreamCreation()` (8 min)

[ACTION] Open `01-stream-api-basics-and-operations.java`, scroll to `demonstrateStreamCreation()`.

Walk through each creation method:

1. **`Stream.of(...)`** — "Literal elements. Quick and clean for demos."
2. **`collection.stream()`** — "The most common form in production. Every Collection has this method."
3. **`Arrays.stream(array)`** — "Arrays don't have `.stream()` directly. Use `Arrays.stream()`."
4. **`Stream.generate(Supplier)`** — "Infinite. *Must* be paired with `limit()` or you'll wait forever."

   [ACTION] Show the random doubles example. Run it. Ask: "Why do we need `limit(5)` here?"

5. **`Stream.iterate(seed, UnaryOperator)`** — "Structured infinite stream. Like a while loop."

   [ACTION] Show the even numbers and powers of 2. Draw the expansion:
   ```
   iterate(0, n -> n + 2):  0 → 2 → 4 → 6 → 8 → ...
   iterate(1L, n -> n * 2): 1 → 2 → 4 → 8 → 16 → ...
   ```

6. **`IntStream.range(1, 6)`** — "Like a for-loop: start inclusive, end exclusive."
7. **`IntStream.rangeClosed(1, 5)`** — "Both ends inclusive."

⚠️ WATCH OUT — `range` vs `rangeClosed`: `range(1, 6)` = `[1, 2, 3, 4, 5]`. `rangeClosed(1, 5)` = same. But `range(1, 5)` = `[1, 2, 3, 4]`. Easy to be off by one.

→ TRANSITION: "OK, we have a stream. Now let's push data through it."

---

### Section 2 — `demonstrateIntermediateOperations()` (14 min)

[ACTION] Scroll to `demonstrateIntermediateOperations()`.

**`filter(Predicate)`** (2 min):
- "Keep only elements that pass the test. Everything else is discarded."
- [ASK] "What does filter return?" (A new Stream — not a list.)
- Show filter on the word list. Point out it returns a Stream, not a List.

**`map(Function)`** (3 min):
- "Transform each element. Input type can differ from output type."
- Show `String::toUpperCase` (String → String) and `String::length` (String → Integer).
- [ASK] "What's the difference between map and filter?" (map transforms, filter keeps/removes)

**`flatMap(Function)`** (4 min):
- "This is the tricky one."
- [ACTION] Draw on the board:
  ```
  map:     [[1,2,3], [4,5], [6,7,8,9]]  →  [Stream, Stream, Stream]  (nested!)
  flatMap: [[1,2,3], [4,5], [6,7,8,9]]  →  [1, 2, 3, 4, 5, 6, 7, 8, 9]  (flat!)
  ```
- Show the nested list example. "Each `List<Integer>` becomes a `Stream<Integer>`. flatMap merges them."
- Show the sentences example. "Split each sentence into words — now each sentence 'fans out' into multiple words."
- [ASK] "When would you use flatMap vs map?" (When each element maps to zero, one, or multiple elements — like a 1-to-many relationship.)

**`distinct()`** (1 min):
- "Remove duplicates. Uses `.equals()` — make sure your objects implement it."

**`sorted()`** (1 min):
- "Sort naturally or with a Comparator. Combined with `distinct()` first — much cleaner output."

**`peek(Consumer)`** (2 min):
- "This is your debug tool. It lets you look at the stream without transforming it."
- [ACTION] Run the peek example and show the output. "See how it prints after filter but BEFORE map? That's the lazy pipeline in action."
- ⚠️ WATCH OUT — Don't use `peek` for production logic. Use it for debugging only. Its behavior with parallel streams can be unpredictable.

**`limit(n)` and `skip(n)`** (1 min):
- "limit: take at most n. skip: throw away the first n."
- Show the pagination pattern: `skip(page * pageSize).limit(pageSize)`. "This is how REST APIs implement paging."

---

### Section 3 — `demonstrateTerminalOperations()` (10 min)

[ACTION] Scroll to `demonstrateTerminalOperations()`.

Emphasize: "Everything we did so far was just *describing* what to do. The terminal operation is what actually *executes* the pipeline."

**`forEach`** (30 sec): "Just iterate. No return value."

**`count()`** (30 sec): "How many elements survive the pipeline. Returns a `long`."

**`min()` / `max()`** (1 min):
- "Both require a Comparator. Both return `Optional` — what if the stream is empty?"

**`findFirst()` / `findAny()`** (1 min):
- "findFirst: always the first element in encounter order. findAny: may be different in parallel streams."
- [ASK] "Why do these return Optional?" (The stream might be empty — nothing to find.)

**`anyMatch / allMatch / noneMatch`** (2 min):
- "Short-circuiting boolean tests."
- `anyMatch`: returns true as soon as ONE matches — doesn't check the rest.
- `allMatch`: returns false as soon as ONE fails — stops early.
- `noneMatch`: returns false as soon as ONE matches — stops early.
- "These are much faster than `count() > 0` for existence checks."

**`reduce(identity, BinaryOperator)`** (3 min):
- "Fold all elements into a single value. The identity is your starting point."
- [ACTION] Draw on the board:
  ```
  reduce(0, Integer::sum):
  0 + 3 = 3
  3 + 1 = 4
  4 + 4 = 8
  8 + 1 = 9
  ...
  ```
- Show sum, product, and string concatenation examples.
- "When you omit the identity (no starting value), you get `Optional<T>` back — because an empty stream has no result."

**`toList()` vs `collect(Collectors.toList())`** (1 min):
- "Java 16+ added `toList()` — cleaner syntax, returns an unmodifiable list."
- "Before Java 16, use `collect(Collectors.toList())`."

---

### Section 4 — `demonstrateCollectors()` (12 min)

[ACTION] Scroll to `demonstrateCollectors()`. Say: "If `reduce` is the foundation, `collect` is the workhorse. The `Collectors` utility class has around 40 factory methods."

**`toList / toSet / toUnmodifiableList`** (1 min): Quick show.

**`joining(separator, prefix, suffix)`** (1 min):
- "Turn a stream of strings into one string."
- Show the brackets example: `joining(", ", "[", "]")`.

**`counting()`** (30 sec): "Count downstream. Usually used inside `groupingBy`."

**`groupingBy`** (5 min — most important):
- "This is `GROUP BY` from SQL. Returns `Map<K, List<V>>`."
- [ACTION] Show basic grouping by category. Walk through the output.
- Show **downstream collector**: `groupingBy(category, counting())`.
   - "You can add a second argument — a downstream collector that processes each group."
- Show `groupingBy` with `summingDouble`.
- [ASK] "What SQL clause does this remind you of?" (`GROUP BY` + aggregate functions like `SUM`, `COUNT`.)

**`summarizingDouble`** (1 min):
- "One shot for min, max, avg, sum, count. Useful for reporting."

**`partitioningBy`** (1 min):
- "Special case of groupingBy with only TWO groups: true and false. Returns `Map<Boolean, List<T>>`."
- "Use this for binary splits: premium vs free, active vs inactive, pass vs fail."

**`toMap`** (1 min):
- "Build a Map directly from the stream. `toMap(keyExtractor, valueExtractor)`."
- ⚠️ WATCH OUT — If two elements produce the same key, `toMap` throws `IllegalStateException`. Add a merge function as a third argument to handle duplicates.

---

### Section 5 — `demonstratePrimitiveStreams()` (3 min)

[ACTION] Scroll to `demonstratePrimitiveStreams()`.

"When you have a stream of numbers and you call `.map(n -> n * 2)`, Java has to box each `int` into an `Integer` — that's overhead. Primitive streams (`IntStream`, `LongStream`, `DoubleStream`) avoid this."

- Show `.mapToInt(String::length)` then `.sum()`. "No boxing, direct aggregation."
- Show `IntStream.rangeClosed(1, 100).sum()`. "The classic 1+2+...+100 = 5050 in one line."
- Show `.boxed()`. "Convert back to `Stream<Integer>` when you need to collect to a List."

---

### Section 6 — `demonstrateRealWorldPipelines()` (3 min)

[ACTION] Scroll to real-world section. Read through pipelines 1 and 2 aloud, narrating each step.

"Notice that each pipeline reads like a sentence:
- 'Filter active employees, filter engineering department, sort by salary descending, take top 3, format, print each.'"

[ASK] "How would you write Pipeline 2 (average salary by department) without streams?" (Nested for-loops, manual Map operations, multiple accumulator variables — significantly more code.)

[ACTION] Briefly show the word frequency example (Pipeline 4). "This is a common interview question. With streams, it's 4 lines."

→ TRANSITION: "That's the full Stream API. Take a 5 minute break, then we tackle dates."

---

## FILE 2: `02-datetime-api.java` (~30 min)

### Opening: Why the New API? (3 min)

[ACTION] Write on the board:

```java
// OLD WAY (java.util.Date / Calendar) — Don't do this
Date date = new Date(2024 - 1900, 3 - 1, 15);  // ← what?! year offset, 0-indexed month
Calendar cal = Calendar.getInstance();
cal.set(Calendar.MONTH, Calendar.MARCH);        // constants needed for months
date.setYear(124);                              // ← 1900 + 124 = 2024 ???
```

"This is the old API. It's mutable, confusing, and thread-unsafe. Java 8 gave us `java.time`."

---

### Section 1 — `demonstrateLocalDate()` (5 min)

[ACTION] Scroll to `demonstrateLocalDate()`.

"Three ways to create: `now()`, `of()`, `parse()`. You'll use all three."

[ACTION] Walk through getters:
- `getMonth()` returns an enum: `MARCH` — that's intentional, self-documenting.
- `getMonthValue()` returns the int `3` — use when you need a number.
- `getDayOfWeek()` also an enum: `MONDAY`.

[ACTION] Show arithmetic. Emphasize: "Every method returns a NEW `LocalDate`. The original `today` never changes."

[ACTION] Show `withDayOfMonth()`. "Useful: 'give me the last day of this month' — `.withDayOfMonth(today.lengthOfMonth())`."

[ASK] "What do `isBefore` and `isAfter` return?" (boolean)

⚠️ WATCH OUT — Don't compare dates with `==` or `equals()` unless you're careful. Use `isBefore`, `isAfter`, `isEqual`, or `compareTo`.

---

### Section 2 — `demonstrateLocalTime()` (3 min)

[ACTION] Quick walkthrough — same pattern as LocalDate but for time. Point out the nanosecond precision option.

"Most of the time you'll use `LocalTime.of(hour, minute)` or `LocalTime.now()`."

---

### Section 3 — `demonstrateLocalDateTime()` (3 min)

[ACTION] "This is LocalDate + LocalTime in one object. Still no timezone."

Show `LocalDateTime.of(date, time)` — "combine two objects you already have."

Show `toLocalDate()` and `toLocalTime()` — "split apart when needed."

[ASK] "When would you use LocalDateTime vs ZonedDateTime?" (LocalDateTime for business logic that doesn't need timezone — e.g., store open/close hours. ZonedDateTime when you need to coordinate across timezones.)

---

### Section 4 — `demonstrateZonedDateTimeAndInstant()` (4 min)

[ACTION] Scroll to this section.

Show the New York / London / Tokyo times. "This is the real world — your users are everywhere."

Show `withZoneSameInstant()`. "Convert the same point in time to another timezone. The Instant (the actual moment) doesn't change — just the representation."

[ACTION] Briefly show `Instant`. "Think of it as a GPS coordinate on the timeline. No timezone, just epoch seconds. Useful for logging, measuring performance, storing timestamps in databases."

⚠️ WATCH OUT — `withZoneSameInstant` ≠ `withZoneSameLocal`. 
- `withZoneSameInstant`: same moment in time, different clock display.
- `withZoneSameLocal`: same clock display, different moment in time (dangerous!).

---

### Section 5 — `demonstrateDateTimeFormatter()` (5 min)

[ACTION] Scroll to `demonstrateDateTimeFormatter()`.

[ACTION] Walk through built-in formatters: ISO_LOCAL_DATE, ISO_LOCAL_DATE_TIME. "These match the default `toString()` output."

[ACTION] Walk through `ofPattern()`. Write the key letters on the board:
```
y = year    M = month    d = day
H = 24h     h = 12h      m = minute    s = second
a = AM/PM   E = weekday  z = timezone
```

"More M's or d's = longer format: `MM` → `03`, `MMMM` → `March`. One `M` → `3`. Same for E: `EEE` → `Mon`, `EEEE` → `Monday`."

[ACTION] Show parsing: "Use the SAME formatter to parse that you'd use to format. If the format doesn't match the string exactly, you get a `DateTimeParseException`."

⚠️ WATCH OUT — `DateTimeFormatter` is thread-safe. You can (and should) define formatters as `static final` constants and reuse them.

---

### Section 6 — `demonstratePeriodAndDuration()` (4 min)

[ACTION] Draw on the board:
```
Period   → date-based: years, months, days    (between two LocalDates)
Duration → time-based: hours, minutes, seconds, nanos (between two times/datetimes/instants)
```

[ACTION] Show `Period.between()`. Walk through the output: `P4Y5M5D` — that's ISO 8601 period notation.

"If you just want total days, use `ChronoUnit.DAYS.between()` — simpler."

[ACTION] Show `Duration.between()`. Show `toHours()`, `toMinutes()`, `toMinutesPart()` (Java 9+).

[ACTION] Show adding a Period to a date: `today.plus(threeMonths)`. "Subscription renewal, expiry, scheduling — all of these."

---

### Section 7 — `demonstrateChronoUnit()` (2 min)

[ACTION] Quick walkthrough. "ChronoUnit is the simplest way to get a plain number of units between two dates."

Show `ChronoUnit.DAYS.between(today, christmas)`. "How many days until Christmas? One line."

Show `truncatedTo`. "Zero out the smaller time units. Useful when you care about 'which hour' but not minutes/seconds."

---

### Section 8 — `demonstratePracticalExamples()` (4 min)

[ACTION] Walk through the examples narrating each:

1. **Age Calculator** — "`Period.between(dob, today)` gives you years+months+days in one call. `ChronoUnit.DAYS.between()` for the raw day count."

2. **Deadline Checker** — "Check isBefore/isAfter, then show a message. Real-world use: task managers, project tools, invoicing systems."

3. **Subscription Expiry** — "`plusMonths(12).minusDays(1)` — this is the exact logic subscription services use. Annual subscription starting Jan 1 expires Dec 31, not Jan 1 of next year."

4. **Meeting Scheduler across timezones** — "Your distributed team at 10am Chicago time. What time is that in London? `withZoneSameInstant()` handles DST automatically."

5. **Business Hours** — "Simple `isBefore/isAfter` on `LocalTime`. No timezone needed for 'is it between 9am and 5pm local time?'"

6. **Next 5 Mondays** — "TemporalAdjusters.next(DayOfWeek.MONDAY) finds the upcoming Monday. Then add weeks. Used in calendar apps."

---

## Wrap-Up: Day 8 Full Summary (5 min)

[ACTION] Draw a quick mind-map on the board:

```
Day 8 Topics:
  Part 1:
    Lambda          → anonymous function, passes behavior
    Functional IF   → Predicate / Function / Consumer / Supplier
    Method Refs     → static / instance-specific / instance-arbitrary / constructor
    Optional        → container for "might not exist"

  Part 2:
    Stream API      → source → [intermediate ops] → terminal op
    DateTime        → LocalDate / LocalTime / LocalDateTime / ZonedDateTime
                      Period / Duration / ChronoUnit / DateTimeFormatter
```

[ASK] "What functional interface does `Stream.filter()` accept?" (Predicate)
[ASK] "What functional interface does `Stream.map()` accept?" (Function)
[ASK] "What functional interface does `Stream.forEach()` accept?" (Consumer)

"Notice — you've just connected everything from Part 1 into Part 2. Streams are the functional interfaces and lambdas you learned, just applied to data processing."

[ASK] "If I give you a `List<Employee>` and ask for the average salary of active employees grouped by department — can you sketch the pipeline?" (Let a student try: `.stream().filter(active).collect(groupingBy(dept, averagingDouble(salary)))`)

→ TRANSITION: "Day 9 is Multithreading. Streams actually have a parallel mode — `parallelStream()` — that we'll touch on there. Well done today."

---

*End of Part 2 Script*
