# Exercise 07: Bounded Types and Wildcards

## Objective
Understand the difference between bounded **type parameters** (`<T extends X>`) and bounded **wildcards** (`<? extends X>` / `<? super X>`), and learn when to use each through the PECS principle.

## Background
Wildcards (`?`) are used in **method signatures** when you want to accept a range of parameterized types. The PECS rule tells you which wildcard to use:
- **P**roducer **E**xtends — if the collection is a *source* you only read from, use `<? extends T>`
- **C**onsumer **S**uper — if the collection is a *sink* you only write to, use `<? super T>`

This is why `Collections.copy(dest, src)` uses `? super T` for `dest` (it receives/consumes) and `? extends T` for `src` (it produces).

## Requirements

1. **Write a method** `static double sumList(List<? extends Number> list)` that iterates over the list and returns the sum of all elements as a `double`. Call it with:
   - A `List<Integer>` containing `1, 2, 3, 4, 5`
   - A `List<Double>` containing `1.5, 2.5, 3.0`
   - Print the results

2. **Write a method** `static void addNumbers(List<? super Integer> list)` that adds `10`, `20`, `30` to the provided list. Call it with:
   - A `List<Integer>`
   - A `List<Number>`
   - Print both lists after adding

3. **Explain the difference** between upper and lower bounded wildcards by printing the PECS rule as output (see expected output). Add a comment block in the code that explains why you **cannot** call `list.add(element)` on an `<? extends Number>` list.

4. **Write a method** `static <T extends Comparable<T>> T clamp(T value, T min, T max)` that returns `min` if `value < min`, `max` if `value > max`, and `value` otherwise. Demonstrate with `clamp(15, 1, 10)` (returns 10) and `clamp(5, 1, 10)` (returns 5).

## Hints
- You cannot add to a `List<? extends Number>` — the compiler does not know the exact type (it could be `List<Integer>`, `List<Double>`, etc.), so adding anything (other than `null`) is unsafe
- You CAN read from `List<? extends Number>` as type `Number`
- `List<? super Integer>` accepts `List<Integer>`, `List<Number>`, `List<Object>` — you can safely add `Integer`s
- Type parameters (`<T extends X>`) are used when you need to refer to the type by name elsewhere (e.g., return type, multiple parameters)

## Expected Output

```
=== Upper Bounded Wildcard: <? extends Number> ===
Sum of integers [1, 2, 3, 4, 5]: 15.0
Sum of doubles [1.5, 2.5, 3.0]: 7.0

=== Lower Bounded Wildcard: <? super Integer> ===
List<Integer> after addNumbers: [10, 20, 30]
List<Number> after addNumbers: [10, 20, 30]

=== PECS Principle ===
Producer Extends: use <? extends T> when reading/consuming from the collection
Consumer Super:  use <? super T>  when writing/producing into the collection

=== Bounded Type Parameter: clamp() ===
clamp(15, 1, 10): 10
clamp(5, 1, 10): 5
```
