# Day 6 Application — Collections & Generics: Course Enrollment System

## Overview

You'll build a **Course Enrollment System** — a Java console app that manages students, courses, and enrollments using the Java Collections Framework. You'll choose the right collection type for each use case and implement sorting with `Comparable` and `Comparator`.

---

## Learning Goals

- Use `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`, `HashMap`, `TreeMap`
- Iterate collections with iterators and enhanced for loops
- Write generic classes and methods
- Implement `Comparable` on a class
- Use `Comparator` for custom sort orders
- Use `Collections` utility methods

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java
    ├── Student.java            ← TODO: implement Comparable<Student>
    ├── Course.java             ← provided
    └── EnrollmentSystem.java   ← TODO: complete all methods
```

---

## Part 1 — `Student.java`

**Task 1** — Fields: `studentId` (`int`), `name` (`String`), `gpa` (`double`)  
Implement `Comparable<Student>` — natural ordering by `name` alphabetically.  
Override `equals()` and `hashCode()` based on `studentId`.  
Override `toString()`.

---

## Part 2 — `EnrollmentSystem.java`

**Task 2 — `ArrayList` roster**  
`addStudent(Student s)` — add to an `ArrayList<Student>`.  
`removeStudent(int id)` — find and remove by id using an Iterator (not `removeIf`).  
`getAllStudents()` — return the list.

**Task 3 — `HashSet` for unique course codes**  
`addCourseCode(String code)` — add to a `HashSet<String>`.  
`isCourseOffered(String code)` — return whether it exists in the set.

**Task 4 — `TreeSet` for sorted waitlist**  
`addToWaitlist(Student s)` — add to a `TreeSet<Student>` (uses natural ordering from `Comparable`).  
`getWaitlist()` — return the TreeSet (already sorted alphabetically).

**Task 5 — `HashMap` for enrollments**  
`enroll(String courseCode, Student s)` — add student to `HashMap<String, List<Student>>`.  
`getEnrolledStudents(String courseCode)` — return the list for that course.

**Task 6 — `TreeMap` for sorted course catalog**  
`addCourseToCatalog(String code, String title)` — add to `TreeMap<String, String>`.  
`printCatalog()` — iterate and print (TreeMap keeps keys in sorted order).

**Task 7 — Sorting with Comparator**  
`sortByGpaDescending(List<Student> students)` — use `Comparator.comparingDouble(Student::getGpa).reversed()` and `Collections.sort()`.  
`sortByNameThenGpa(List<Student> students)` — chain comparators.

**Task 8 — Generic utility method**  
Write a generic method `<T> void printCollection(Collection<T> items)` that prints each item.

---

## Part 3 — `Main.java`

Create 5+ students, add courses, enroll students, test all methods. Demonstrate sort before/after.

---

## Stretch Goals

1. Use `LinkedList` as a queue — add a `waitlistQueue` that uses `offer()`, `poll()`, `peek()`.
2. Implement a `getTopStudents(int n)` method using streams (preview for Day 8).
3. Add a `Map<Student, Set<String>>` to track which courses each student is enrolled in.

---

## Submission Checklist

- [ ] `ArrayList`, `HashSet`, `TreeSet`, `HashMap`, `TreeMap` all used
- [ ] Iterator used to remove an element (not `removeIf`)
- [ ] `Comparable` implemented on `Student`
- [ ] `Comparator` used for custom sort
- [ ] `Collections.sort()` called
- [ ] Generic method written and called
- [ ] `equals()` and `hashCode()` overridden
