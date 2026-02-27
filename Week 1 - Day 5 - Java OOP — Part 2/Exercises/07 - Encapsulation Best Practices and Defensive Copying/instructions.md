# Exercise 07: Encapsulation Best Practices and Defensive Copying

## Objective
Practice advanced encapsulation techniques beyond basic getters/setters: validation in setters, returning copies of mutable fields, and building classes that protect their own state from external modification.

## Background
Day 4 introduced basic encapsulation (private fields + getters/setters). This exercise goes deeper into the real-world concerns that arise when fields are mutable objects. If a class exposes a reference to an internal `ArrayList` or `Date`, external code can modify the object's state without going through any validation — defeating encapsulation entirely. You'll build a `Course` class that guards against this correctly.

## Requirements

1. Create class `Course` representing a university course:
   - Private field `String courseCode` (e.g., `"CS101"`)
   - Private field `String title`
   - Private field `int maxEnrollment` (must be between 1 and 500)
   - Private field `ArrayList<String> enrolledStudents` (the mutable field we need to protect)

2. Constructor `Course(String courseCode, String title, int maxEnrollment)`:
   - Validate `courseCode`: must not be null or blank; throw `IllegalArgumentException("Course code cannot be blank")` if invalid
   - Validate `title`: must not be null or blank; throw `IllegalArgumentException("Title cannot be blank")`
   - Validate `maxEnrollment`: must be 1–500; throw `IllegalArgumentException("Max enrollment must be between 1 and 500")`
   - Initialize `enrolledStudents` as a new empty `ArrayList<String>`

3. Getters for `courseCode` and `title` (standard — Strings are immutable so no copying needed)

4. Setter `setMaxEnrollment(int max)` with the same 1–500 validation

5. Method `void enroll(String studentName)`:
   - Validate `studentName` is not null/blank; throw `IllegalArgumentException("Student name cannot be blank")`
   - If the course is full (`enrolledStudents.size() >= maxEnrollment`), throw `IllegalStateException("Course is full")`
   - Otherwise, add to `enrolledStudents`

6. Method `void drop(String studentName)`:
   - Remove `studentName` from `enrolledStudents` (use `remove()`)
   - If not found (remove returns false), throw `IllegalArgumentException("Student not enrolled: " + studentName)`

7. Method `ArrayList<String> getEnrolledStudents()`:
   - **Return a copy** of the list, NOT the original: `return new ArrayList<>(enrolledStudents);`
   - This prevents callers from adding/removing students without going through `enroll()`/`drop()`

8. Method `int getEnrollmentCount()` — returns `enrolledStudents.size()`

9. Method `boolean isFull()` — returns `enrolledStudents.size() >= maxEnrollment`

10. In `main`:
    - Create a `Course("CS101", "Intro to Java", 3)`
    - Enroll 3 students, print the list after each enrollment
    - Demonstrate the copy protection: get the list, try to add a student to that copy, then call `getEnrolledStudents()` again and show the course's list is unchanged
    - Try to enroll a 4th student and catch the `IllegalStateException`
    - Drop a student, print the updated list
    - Try to create a course with invalid maxEnrollment and catch the `IllegalArgumentException`

## Hints
- `new ArrayList<>(existingList)` creates a **shallow copy** — changes to the returned list do NOT affect the original
- `String.isBlank()` returns true for empty strings and strings with only whitespace (Java 11+)
- Mutable fields like `ArrayList`, arrays, and `Date` should always be defensively copied in both getters (return a copy) and setters that accept them (store a copy of what was passed in)
- After calling `getEnrolledStudents()`, modify the returned list and call `getEnrolledStudents()` again to prove the original is unchanged

## Expected Output

```
=== Course Enrollment System ===

Course created: CS101 - Intro to Java (max: 3)

Enrolled Alice. Students: [Alice]
Enrolled Bob. Students: [Alice, Bob]
Enrolled Carol. Students: [Alice, Bob, Carol]

--- Defensive copy demonstration ---
External list (copy): [Alice, Bob, Carol]
Adding Eve to the external copy...
Course's internal list (unchanged): [Alice, Bob, Carol]
Course enrollment count: 3

--- Attempting to enroll a 4th student ---
Caught: Course is full

Dropped Bob. Updated list: [Alice, Carol]

--- Invalid course creation ---
Caught: Max enrollment must be between 1 and 500
```
