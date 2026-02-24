# Exercise 09: Collections Utility Methods and Capstone

## Objective
Use the `Collections` utility class to perform common operations on collections, then apply multiple collection types together in a small word-frequency capstone.

## Background
`java.util.Collections` is a utility class (similar to `java.util.Arrays`) that provides static methods for operating on `Collection` and `List` objects. It covers sorting, searching, shuffling, min/max, frequency counting, unmodifiable views, and more. These are tools you will reach for constantly in real-world Java code.

## Requirements

### Part 1: Core Utility Methods
Create an `ArrayList<Integer>` named `nums` containing: `3, 1, 4, 1, 5, 9, 2, 6, 5, 3`

- Call `Collections.sort(nums)` and print the result
- Call `Collections.reverse(nums)` and print the result
- Call `Collections.shuffle(nums, new Random(42))` and print (seeded for determinism)
- Print `Collections.min(nums)` and `Collections.max(nums)`
- Print `Collections.frequency(nums, 5)` — how many times `5` appears

### Part 2: Unmodifiable and Filled Lists
- Create an unmodifiable view of `nums` using `Collections.unmodifiableList(nums)`
- Attempt to `add(99)` to the unmodifiable list inside a try/catch and print the exception message
- Use `Collections.nCopies(5, "Java")` to create a list and print it
- Create an `ArrayList<String>` of 5 `"_"` placeholders, call `Collections.fill(list, "done")`, print result

### Part 3: Capstone — Word Frequency Counter
Given the sentence: `"the quick brown fox jumps over the lazy dog the fox"`
- Split into words and count frequency using a `HashMap<String, Integer>`
- Collect the entries into a list and sort by frequency descending, then alphabetically for ties
- Print the top 5 words with their counts in the format: `the: 3`

## Hints
- `Collections.unmodifiableList()` returns a **view** — it doesn't copy; mutations to the original list are still reflected, but the view itself can't be written to
- `nCopies()` returns an **immutable** list — you cannot add/remove from it
- For the capstone, use `entrySet()` to get the Map entries, convert to a `List`, then sort with a Comparator on `Map.Entry`
- `Map.Entry.comparingByValue(Comparator.reverseOrder())` or a custom lambda comparator both work

## Expected Output

```
=== Collections Utility Methods ===
After sort:    [1, 1, 2, 3, 3, 4, 5, 5, 6, 9]
After reverse: [9, 6, 5, 5, 4, 3, 3, 2, 1, 1]
After shuffle: [3, 6, 5, 1, 9, 4, 5, 1, 3, 2]
Min: 1  |  Max: 9
Frequency of 5: 2

=== Unmodifiable and Filled Lists ===
Unmodifiable list caught: java.lang.UnsupportedOperationException
nCopies(5, "Java"): [Java, Java, Java, Java, Java]
After fill with "done": [done, done, done, done, done]

=== Capstone: Word Frequency Counter ===
the: 3
fox: 2
brown: 1
dog: 1
jumps: 1
```
