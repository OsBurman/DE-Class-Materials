# Exercise 08: Comparable and Comparator Sorting

## Objective
Implement natural ordering with `Comparable` and custom ordering with `Comparator` to sort objects in multiple ways without modifying the class.

## Background
Java's sorting infrastructure (`Collections.sort()`, `List.sort()`, `Arrays.sort()`) requires objects to be comparable. There are two mechanisms:
- **`Comparable<T>`** — the class defines its own *natural* ordering (e.g., `String` sorts alphabetically, `Integer` sorts numerically). Only one natural order is possible.
- **`Comparator<T>`** — an *external* strategy object that defines an ordering independent of the class. You can have as many Comparators as you need.

## Requirements

1. **Define a `Student` class** (static nested class or separate top-level in same file) with fields:
   - `String name`, `double gpa`, `int age`
   - Constructor and getters
   - `toString()` returning `"Student{name, gpa, age}"`
   - Implement `Comparable<Student>` with natural ordering by **GPA descending** (highest first)

2. Create an `ArrayList<Student>` with at least 4 students (mix of GPAs, names, ages):
   - `"Alice"`, gpa=3.8, age=22
   - `"Bob"`, gpa=3.5, age=20
   - `"Carol"`, gpa=3.9, age=21
   - `"Dave"`, gpa=3.5, age=23

3. **Sort and print using natural order** (`Collections.sort(list)` — uses `Comparable`)

4. **Sort and print using `Comparator.comparing(Student::getName)`** — alphabetical by name

5. **Sort and print using a chained Comparator**: GPA descending, then by name alphabetically for ties:
   ```java
   Comparator.comparingDouble(Student::getGpa).reversed().thenComparing(Student::getName)
   ```

6. **Sort by age ascending** using a lambda Comparator and print

## Hints
- `compareTo()` for descending: `Double.compare(other.gpa, this.gpa)` (note the reversed argument order)
- `Comparator.comparing()` accepts a key extractor (method reference or lambda)
- `reversed()` flips any Comparator — applies AFTER `comparingDouble`
- `thenComparing()` breaks ties: only applied when the first comparator returns 0
- Both `Collections.sort(list)` and `list.sort(comparator)` sort in-place

## Expected Output

```
=== Natural Order (Comparable — GPA descending) ===
Student{Carol, 3.9, 21}
Student{Alice, 3.8, 22}
Student{Bob, 3.5, 20}
Student{Dave, 3.5, 23}

=== Comparator by Name (alphabetical) ===
Student{Alice, 3.8, 22}
Student{Bob, 3.5, 20}
Student{Carol, 3.9, 21}
Student{Dave, 3.5, 23}

=== Chained Comparator (GPA desc, then Name asc) ===
Student{Carol, 3.9, 21}
Student{Alice, 3.8, 22}
Student{Bob, 3.5, 20}
Student{Dave, 3.5, 23}

=== Comparator by Age (ascending) ===
Student{Bob, 3.5, 20}
Student{Carol, 3.9, 21}
Student{Alice, 3.8, 22}
Student{Dave, 3.5, 23}
```
