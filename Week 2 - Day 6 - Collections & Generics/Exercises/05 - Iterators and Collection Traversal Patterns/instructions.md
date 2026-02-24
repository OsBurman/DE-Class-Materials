# Exercise 05: Iterators and Collection Traversal Patterns

## Objective
Practice using explicit `Iterator` and `ListIterator` to safely traverse and modify collections, and understand when and why each traversal approach is appropriate.

## Background
Java's enhanced for-loop is convenient, but it hides the underlying `Iterator`. When you need to **remove elements while iterating**, you must use the explicit `Iterator` — removing elements through any other mechanism during a for-each loop causes a `ConcurrentModificationException`. Understanding iterators is also foundational to implementing your own custom data structures.

## Requirements

1. Create an `ArrayList<Integer>` named `numbers` containing: `1, 2, 3, 4, 5, 6, 7, 8`
   - Use an explicit `Iterator<Integer>` to print all values on one line, space-separated
   - Obtain a **new** `Iterator<Integer>` and use `iterator.remove()` to safely remove all **even** numbers while iterating
   - Print the remaining list (should contain only odd numbers)

2. Demonstrate why direct removal during a for-each loop fails:
   - Include a comment block in the code explaining that calling `list.remove()` inside a for-each loop causes `ConcurrentModificationException`
   - The comment should show the problematic code pattern (as a comment, not executed)

3. Create an `ArrayList<String>` named `letters` containing: `"A"`, `"B"`, `"C"`, `"D"`, `"E"`
   - Obtain a `ListIterator<String>` and traverse **forward**, printing each element with its index (`[0] A`, `[1] B`, etc.)
   - Without resetting, traverse **backward** using `hasPrevious()` / `previous()`, printing each element

4. Use `removeIf(Predicate)` on a new `ArrayList<String>` containing: `"apple"`, `"banana"`, `"avocado"`, `"cherry"`, `"apricot"` to remove all strings that start with `"a"`, then print the result.

## Hints
- `iterator.remove()` removes the last element returned by `next()` — you must call `next()` before calling `remove()`
- After a `ListIterator` reaches the end, `hasPrevious()` becomes `true` and you can walk back
- `listIterator.nextIndex()` returns the index of the element that would be returned by the next call to `next()`
- `removeIf()` internally uses an iterator safely — it is the modern, readable alternative

## Expected Output

```
=== Iterator: Print all elements ===
1 2 3 4 5 6 7 8

=== Iterator: Safe removal of even numbers ===
After removing evens: [1, 3, 5, 7]

=== ConcurrentModificationException Warning ===
// See comment in code — removing via list.remove() inside for-each throws CME

=== ListIterator: Forward traversal ===
[0] A
[1] B
[2] C
[3] D
[4] E

=== ListIterator: Backward traversal ===
[4] E
[3] D
[2] C
[1] B
[0] A

=== removeIf: Remove fruits starting with 'a' ===
After removeIf: [banana, cherry]
```
