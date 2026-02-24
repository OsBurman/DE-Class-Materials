# Exercise 02: HashSet, TreeSet, and LinkedHashSet — Uniqueness and Ordering

## Objective
Practice using the three main `Set` implementations and understand how each handles uniqueness, ordering, and performance trade-offs.

## Background
A conference registration system needs to track unique attendees — no duplicate registrations allowed. Depending on whether you need the attendees sorted alphabetically, in registration order, or just quickly searchable, different `Set` implementations are appropriate. Sets enforce uniqueness automatically: adding a duplicate silently does nothing.

## Requirements

1. Create a `HashSet<String>` named `hashSet` and:
   - Add: `"Charlie"`, `"Alice"`, `"Dave"`, `"Bob"`, `"Alice"` (duplicate)
   - Print the size (should be 4 — duplicate was ignored)
   - Print the set (order will be unpredictable)
   - Check if `"Alice"` is present; print the result
   - Remove `"Dave"`; print the updated set

2. Create a `LinkedHashSet<String>` named `linkedSet` and:
   - Add the same 5 values in the same order: `"Charlie"`, `"Alice"`, `"Dave"`, `"Bob"`, `"Alice"`
   - Print the set — it should maintain insertion order, with duplicate ignored
   - Print the size (should be 4)

3. Create a `TreeSet<String>` named `treeSet` and:
   - Add the same 5 values
   - Print the set — should be sorted alphabetically
   - Print the first element using `first()`
   - Print the last element using `last()`
   - Print all elements less than `"Charlie"` using `headSet("Charlie")`
   - Print all elements from `"Charlie"` onward using `tailSet("Charlie")`

4. Print a comparison summary:
   - `"HashSet: no guaranteed order, O(1) add/contains"`
   - `"LinkedHashSet: insertion order preserved, slightly slower"`
   - `"TreeSet: always sorted, O(log n) add/contains"`

## Hints
- All three implement `Set<E>` — they share `add()`, `remove()`, `contains()`, `size()`
- When you add a duplicate to a Set, `add()` returns `false` but throws no exception
- `TreeSet` also implements `NavigableSet`, giving it `first()`, `last()`, `headSet()`, `tailSet()`
- The printed order for `HashSet` may differ from the expected output below — that's expected and correct behavior

## Expected Output

```
=== HashSet ===
Size: 4
HashSet contents (order not guaranteed): [Bob, Charlie, Alice, Dave]
Contains Alice: true
After removing Dave: [Bob, Charlie, Alice]

=== LinkedHashSet (insertion order) ===
LinkedHashSet: [Charlie, Alice, Dave, Bob]
Size: 4

=== TreeSet (sorted order) ===
TreeSet: [Alice, Bob, Charlie, Dave]
First: Alice
Last: Dave
Before Charlie (headSet): [Alice, Bob]
From Charlie onward (tailSet): [Charlie, Dave]

HashSet: no guaranteed order, O(1) add/contains
LinkedHashSet: insertion order preserved, slightly slower
TreeSet: always sorted, O(log n) add/contains
```
