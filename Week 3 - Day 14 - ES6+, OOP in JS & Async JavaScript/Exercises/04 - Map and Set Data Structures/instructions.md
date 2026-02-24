# Exercise 04: Map and Set Data Structures

## Objective
Practice using JavaScript's built-in `Map` and `Set` collections: creating them, adding/removing entries, iterating over them, and understanding when to prefer them over plain objects and arrays.

## Background
`Map` is an ordered key-value collection where keys can be any type (not just strings). `Set` is an ordered collection of unique values that automatically deduplicates. Both have better performance characteristics for frequent add/delete operations than plain objects and arrays, and both are iterable with `for...of`.

## Requirements
1. In `script.js`, create a `Map` called `phoneBook`. Add three entries using `.set()`:
   - `"Alice"` → `"555-1234"`
   - `"Bob"` → `"555-5678"`
   - `"Carol"` → `"555-9012"`
2. Log the total number of entries using `.size`. Use `.get("Bob")` to log Bob's phone number.
3. Use `.has("Alice")` to log whether Alice is in the phone book. Use `.has("Dave")` to log whether Dave is.
4. Delete the entry for `"Carol"` using `.delete()`. Log the size again to confirm it dropped to 2.
5. Iterate over the Map using `for...of` with destructuring to log each entry as `"[name]: [number]"`.
6. Create a `Set` called `uniqueTags` and add the following values (some are duplicates): `"js"`, `"css"`, `"html"`, `"js"`, `"css"`, `"react"`. Log the set's `.size` (should be 4, not 6).
7. Use `.has("html")` to check for a tag. Use `.delete("css")` to remove it. Log the size again.
8. Iterate over `uniqueTags` with `for...of` and log each tag.
9. Convert `uniqueTags` to an array using `Array.from(uniqueTags)` and log the result.
10. Demonstrate a practical use case: given `const scores = [4, 8, 15, 16, 23, 42, 8, 4, 15]`, use a `Set` to remove duplicates and log the unique scores as an array.

## Hints
- `Map` preserves insertion order and allows any type as a key — even objects or functions. Use it when you need a key-value store that isn't limited to string keys.
- `Set` only stores unique values: adding a duplicate is silently ignored (no error, no replacement).
- `for...of` on a Map yields `[key, value]` pairs — destructure them directly: `for (const [key, value] of map)`.
- `new Set(array)` can initialize a Set from an existing array in one step — useful for the deduplication pattern.

## Expected Output

```
Phone book size: 3
Bob's number: 555-5678
Has Alice: true
Has Dave: false
Phone book size after delete: 2
Alice: 555-1234
Bob: 555-5678
Unique tag count: 4
Has html: true
Tag count after delete: 3
js
html
react
Unique tags array: [ 'js', 'html', 'react' ]
Unique scores: [ 4, 8, 15, 16, 23, 42 ]
```
