# Exercise 05 — Collections & Generics

## Overview

Build a **Student Gradebook** that stores and analyzes student data using the Java Collections Framework. You will work with `ArrayList`, `HashMap`, `HashSet`, sorting with `Comparable` and `Comparator`, and write your own generic `Pair<K,V>` class.

---

## Concepts Covered

- `List<E>` — `ArrayList`, `LinkedList`
- `Map<K,V>` — `HashMap`, iteration with `entrySet()`
- `Set<E>` — `HashSet` for uniqueness
- `Collections.sort()` and `List.sort(Comparator)`
- Implementing `Comparable<T>` for natural ordering
- Lambda `Comparator` expressions
- Generic classes: `class Pair<K, V>`
- Enhanced for-each and `forEach(lambda)` on collections
- `Map.of()` / `List.of()` factory methods

---

## TODOs

- [ ] **TODO 1** — Implement `compareTo` in `Student` (sort alphabetically by name)
- [ ] **TODO 2** — Complete the generic `Pair<K, V>` class (fields, constructor, getters, toString)
- [ ] **TODO 3** — Implement `getAverageGrade(Map<String,Integer>)` — average of map values
- [ ] **TODO 4** — Implement `getStudentsByMajor(List<Student>, String)` — filter list
- [ ] **TODO 5** — Implement `topStudentPerCourse(...)` — Map of course → best student name
- [ ] **TODO 6** — Implement `getUniqueMajors(List<Student>)` — return a Set
- [ ] **TODO 7** — Implement `rankStudents(...)` — sorted List\<Pair\<Student,Double\>\>
- [ ] **TODO 8** — Sort students by major using a lambda Comparator (one line)

---

## Running the Program

```bash
cd starter-code/src
javac Main.java
java Main
```
