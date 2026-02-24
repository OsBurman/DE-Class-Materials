# Exercise 03: HashMap, TreeMap, and LinkedHashMap — Key-Value Storage

## Objective
Practice using the three main `Map` implementations for storing, retrieving, and iterating over key-value pairs, and understand their ordering behaviors.

## Background
A bookstore inventory system maps book ISBNs to their titles, prices, and stock counts. Maps are the go-to data structure when you need fast lookup by a key. Like Sets, the three Map implementations (`HashMap`, `LinkedHashMap`, `TreeMap`) differ only in how they order their entries.

## Requirements

1. Create a `HashMap<String, Integer>` named `stockMap` (key = book title, value = stock count) and:
   - Put: `"Clean Code" → 5`, `"Effective Java" → 3`, `"Design Patterns" → 8`, `"Refactoring" → 2`
   - Print the map
   - Print the stock for `"Effective Java"` using `get()`
   - Use `getOrDefault("Unknown Book", 0)` and print the result
   - Update `"Clean Code"` stock to `7` using `put()`
   - Remove `"Refactoring"` using `remove()`
   - Print `containsKey("Design Patterns")` and `containsValue(3)`
   - Print the size

2. Create a `LinkedHashMap<String, Double>` named `priceMap` (key = title, value = price) and:
   - Put the same 4 books in the same insertion order with prices: `"Clean Code" → 39.99`, `"Effective Java" → 49.99`, `"Design Patterns" → 44.99`, `"Refactoring" → 34.99`
   - Print the map — should maintain insertion order

3. Create a `TreeMap<String, Integer>` named `sortedStock` (same titles → stock) and:
   - Put all 4 books
   - Print the map — keys should be alphabetically sorted
   - Print `firstKey()` and `lastKey()`

4. **Iterate `stockMap`** (after modifications) using all three approaches:
   - Iterate `keySet()` and print each key
   - Iterate `values()` and print each value
   - Iterate `entrySet()` and print `"[key] = [value]"` for each entry

## Hints
- `map.get(key)` returns `null` if the key doesn't exist; use `getOrDefault(key, fallback)` to avoid null
- `put(key, value)` both inserts a new entry AND updates an existing one
- `map.entrySet()` gives you `Map.Entry<K,V>` objects — call `entry.getKey()` and `entry.getValue()`
- `TreeMap` requires keys to implement `Comparable` (or a `Comparator`) — `String` already does

## Expected Output

```
=== HashMap (stock) ===
Initial map: {Clean Code=5, Design Patterns=8, Refactoring=2, Effective Java=3}
Stock of Effective Java: 3
Unknown Book stock (default): 0
After updating Clean Code to 7 and removing Refactoring:
{Clean Code=7, Design Patterns=8, Effective Java=3}
Contains key Design Patterns: true
Contains value 3: true
Size: 3

=== LinkedHashMap (prices, insertion order) ===
{Clean Code=39.99, Effective Java=49.99, Design Patterns=44.99, Refactoring=34.99}

=== TreeMap (sorted by key) ===
{Clean Code=7, Design Patterns=8, Effective Java=3, Refactoring=2}
First key: Clean Code
Last key: Refactoring

=== Iterating stockMap ===
Keys: Clean Code Design Patterns Effective Java
Values: 7 8 3
Entries:
  Clean Code = 7
  Design Patterns = 8
  Effective Java = 3
```
