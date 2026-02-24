# Exercise 04: Arrays and Core Array Methods

## Objective
Create and manipulate JavaScript arrays using core mutating and non-mutating methods for adding, removing, searching, and transforming elements.

## Background
Arrays are the most-used data structure in JavaScript. Before reaching for higher-order methods like `map` and `filter`, you need fluency with the core mutation methods (`push`, `pop`, `shift`, `unshift`, `splice`) and read methods (`slice`, `indexOf`, `includes`, `concat`, `join`, `reverse`, `sort`). Understanding which methods mutate the original array and which return a new one is critical.

## Requirements

1. **Creating arrays:**
   - Create an array `fruits` with values: `"apple"`, `"banana"`, `"cherry"`.
   - Create a mixed array `mixed` with at least one number, one string, one boolean, and one nested array.
   - Log both arrays with their `length` property.

2. **Accessing and updating elements:**
   - Log `fruits[0]` and `fruits[fruits.length - 1]`.
   - Update `fruits[1]` to `"blueberry"` and log the updated array.

3. **Adding and removing elements:**
   - `push("date")` to the end of `fruits` and log the new length returned.
   - `pop()` and log the removed element.
   - `unshift("avocado")` to the front and log the new length.
   - `shift()` and log the removed element.
   - After each operation log the current state of `fruits`.

4. **`splice` — inserting, removing, and replacing:**
   - Remove 1 element at index 1 using `splice(1, 1)` — log the removed items and the array after.
   - Insert `"elderberry"` and `"fig"` at index 1 (remove 0 elements) — log the array after.
   - Replace the element at index 2 with `"grape"` using splice — log the array after.

5. **Non-mutating: `slice`, `concat`, `indexOf`, `includes`:**
   - `slice(1, 3)` — log the extracted sub-array (original must be unchanged).
   - `concat(["honeydew", "kiwi"])` — log the new array (original unchanged).
   - `indexOf("cherry")` — log the index (or -1 if not found after mutations).
   - `includes("fig")` — log `true` or `false`.

6. **`reverse` and `sort`:**
   - **`reverse()`** mutates — log `fruits` after reversing, then reverse it back.
   - **`sort()`** — create `const nums = [10, 1, 21, 2]`, sort with the default comparator and log (explain in a comment why the result may surprise you), then sort with a numeric comparator `(a, b) => a - b` and log the correct result.

7. **Spread and destructuring (foundational array sub-skills):**
   - Use the spread operator to copy `fruits` into a new array `fruitsCopy`. Mutate `fruitsCopy` and show `fruits` is unchanged.
   - Destructure the first two elements: `const [first, second, ...rest] = fruits`.
   - Log `first`, `second`, and `rest`.

## Hints
- `push`/`pop` work at the **end**; `unshift`/`shift` work at the **start**.
- `splice` modifies the original array in place and **returns the removed elements**.
- `slice` does **not** modify the original — it returns a new array.
- Default `sort()` converts elements to strings and sorts lexicographically — `[10, 1, 21, 2].sort()` gives `[1, 10, 2, 21]`. Always provide a comparator for numbers.

## Expected Output

```
fruits: [ 'apple', 'banana', 'cherry' ]  length: 3
mixed: [ 42, 'hello', true, [ 1, 2 ] ]  length: 4

fruits[0]: apple
fruits[last]: cherry
After update fruits[1]: [ 'apple', 'blueberry', 'cherry' ]

push "date" → new length: 4  fruits: [ 'apple', 'blueberry', 'cherry', 'date' ]
pop → removed: date           fruits: [ 'apple', 'blueberry', 'cherry' ]
unshift "avocado" → length: 4 fruits: [ 'avocado', 'apple', 'blueberry', 'cherry' ]
shift → removed: avocado      fruits: [ 'apple', 'blueberry', 'cherry' ]

splice remove at index 1 → removed: [ 'blueberry' ]  fruits: [ 'apple', 'cherry' ]
splice insert at index 1 → fruits: [ 'apple', 'elderberry', 'fig', 'cherry' ]
splice replace index 2   → fruits: [ 'apple', 'elderberry', 'grape', 'cherry' ]

slice(1,3): [ 'elderberry', 'grape' ]
fruits still: [ 'apple', 'elderberry', 'grape', 'cherry' ]
concat: [ 'apple', 'elderberry', 'grape', 'cherry', 'honeydew', 'kiwi' ]
indexOf "cherry": 3
includes "fig": false

reverse: [ 'cherry', 'grape', 'elderberry', 'apple' ]
reversed back: [ 'apple', 'elderberry', 'grape', 'cherry' ]

nums default sort: [ 1, 10, 2, 21 ]   (lexicographic — "10" < "2" as strings)
nums numeric sort: [ 1, 2, 10, 21 ]

fruitsCopy after mutation: [ 'apple', 'elderberry', 'grape', 'cherry', 'ADDED' ]
fruits unchanged: [ 'apple', 'elderberry', 'grape', 'cherry' ]
first: apple
second: elderberry
rest: [ 'grape', 'cherry' ]
```
