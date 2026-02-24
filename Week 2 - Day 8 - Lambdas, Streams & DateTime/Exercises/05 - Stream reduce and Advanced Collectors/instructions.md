# Exercise 05: Stream Terminal Operations — reduce, Collectors, and Advanced Pipelines

## Objective
Apply terminal operations (`reduce`, `count`, `min`, `max`, `findFirst`, `anyMatch`) and advanced `Collectors` (`groupingBy`, `joining`, `toMap`) to extract meaningful results from stream pipelines.

## Background
Terminal operations consume the stream and produce a final result. `reduce` is the foundation: it folds all elements into a single value using a binary accumulator. The `Collectors` class provides ready-made collectors for grouping, joining strings, mapping to maps, and more. These tools are the difference between writing readable one-liners and verbose for-loop boilerplate.

## Requirements

1. **reduce**: Given `List<Integer> scores = Arrays.asList(85, 92, 78, 95, 88)`:
   - Use `reduce(0, Integer::sum)` to compute the sum; print `"Sum: 518"`
   - Use `reduce(Integer::max)` (no identity — returns `Optional<Integer>`) to find the max; print `"Max: 95"`
   - Compute the product of all scores using a reduce lambda

2. **count, min, max, findFirst, anyMatch/allMatch/noneMatch**:
   - `count()` of scores greater than 85
   - `min()` and `max()` using `Comparator.naturalOrder()`
   - `findFirst()` score above 90 (returns Optional)
   - `anyMatch(n -> n < 70)` — check if any score is below 70
   - `allMatch(n -> n >= 70)` — check if all scores are at least 70
   - `noneMatch(n -> n > 100)` — confirm no score exceeds 100

3. **Collectors.joining**: Given `List<String> words = Arrays.asList("Java", "Streams", "Are", "Powerful")`:
   - Join with `", "` → `"Java, Streams, Are, Powerful"`
   - Join with `" | "` and prefix `"[" ` and suffix `"]"` → `"[Java | Streams | Are | Powerful]"`

4. **Collectors.groupingBy**: Given a list of words, group by their length using `Collectors.groupingBy(String::length)` and print the resulting `Map<Integer, List<String>>`

5. **Collectors.toMap**: Given `List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry")`, collect into a `Map<String, Integer>` of fruit name → length using `Collectors.toMap(s -> s, String::length)` and print each entry

## Hints
- `reduce(identity, accumulator)` — the identity is the starting value (0 for sum, 1 for product)
- `reduce(accumulator)` without identity returns `Optional<T>` because the stream might be empty
- `Collectors.groupingBy(classifier)` returns `Map<K, List<V>>` where K is the result of the classifier
- `Collectors.joining(delimiter, prefix, suffix)` — all three arguments are strings
- `findFirst()` returns an `Optional` — use `.orElse()` to safely print it

## Expected Output

```
=== reduce ===
Sum: 518
Max: 95
Product: 4,568,791,680

=== count / min / max / findFirst / match ===
Count > 85: 3
Min score: 78
Max score: 95
First score > 90: 92
Any score < 70: false
All scores >= 70: true
No score > 100: true

=== Collectors.joining ===
Joined: Java, Streams, Are, Powerful
Wrapped: [Java | Streams | Are | Powerful]

=== Collectors.groupingBy ===
{2=[Are], 4=[Java], 7=[Streams], 8=[Powerful]}

=== Collectors.toMap ===
Apple -> 5
Banana -> 6
Cherry -> 6
```
