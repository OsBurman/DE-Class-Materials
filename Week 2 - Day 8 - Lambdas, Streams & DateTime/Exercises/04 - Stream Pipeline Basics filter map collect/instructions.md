# Exercise 04: Stream Pipeline Basics — filter, map, and collect

## Objective
Build stream pipelines using `filter`, `map`, `sorted`, `distinct`, and `collect` to transform and assemble collections declaratively.

## Background
The Stream API lets you express data transformations as a pipeline: create a stream → apply zero or more **intermediate operations** (lazy, return a new Stream) → trigger a **terminal operation** (eager, produces a result or side effect). The core mental model is: `source → filter → transform → collect`. Once you internalize this pattern, more complex pipelines are just combinations of the same building blocks.

## Requirements

1. **Stream creation**: Create streams from:
   - `Arrays.asList(...)` using `.stream()`
   - An array using `Arrays.stream(arr)`
   - `Stream.of("a", "b", "c")` directly
   - Print the count of each with `.count()`

2. **filter + collect**: Given `List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10)`:
   - Collect all even numbers into a new `List<Integer>` and print it
   - Collect all numbers > 5 into a new `List<Integer>` and print it

3. **map + collect**: Given `List<String> names = Arrays.asList("alice", "bob", "carol", "dave", "eve")`:
   - Map each name to its uppercase version, collect to list, and print
   - Map each name to its length, collect to a `List<Integer>`, and print

4. **filter + map chained**: Given `List<String> emails = Arrays.asList("alice@example.com", "invalid-email", "bob@example.com", "not-an-email", "carol@example.com")`:
   - Filter only strings containing `"@"`, then map to extract just the username (part before `@`), collect to list, and print: `[alice, bob, carol]`

5. **sorted and distinct**: Given `List<Integer> dupes = Arrays.asList(3,1,4,1,5,9,2,6,5,3,5)`:
   - Use `distinct()` then `sorted()` to get a sorted list of unique values, collect and print
   - Use `sorted(Comparator.reverseOrder())` to get reverse-sorted unique values, collect and print

## Hints
- `.collect(Collectors.toList())` is the most common terminal operation for building a list
- Intermediate operations are **lazy** — nothing executes until a terminal operation is called
- `String.contains("@")` works as a filter predicate: `s -> s.contains("@")`
- `String.split("@")[0]` extracts the part before the `@` symbol
- Chain order matters: `filter` before `map` means fewer elements pass through `map`

## Expected Output

```
=== Stream Creation ===
List stream count: 4
Array stream count: 3
Stream.of count: 3

=== filter + collect ===
Even numbers: [2, 4, 6, 8, 10]
Numbers > 5: [6, 7, 8, 9, 10]

=== map + collect ===
Uppercase names: [ALICE, BOB, CAROL, DAVE, EVE]
Name lengths: [5, 3, 5, 4, 3]

=== filter + map chained ===
Email usernames: [alice, bob, carol]

=== sorted and distinct ===
Unique sorted: [1, 2, 3, 4, 5, 6, 9]
Unique reverse sorted: [9, 6, 5, 4, 3, 2, 1]
```
