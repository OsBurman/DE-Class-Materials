# Day 2 Application — Core Java Fundamentals Part 1: Student Profile App

## Overview

You'll build a **Student Profile App** — a Java console application that creates and displays student profiles. It exercises every concept from today: primitives, data types, String operations, type conversions, casting, autoboxing, StringBuilder, and operators.

---

## Learning Goals

- Declare and use all primitive data types appropriately
- Perform type conversion (implicit widening) and explicit casting
- Demonstrate autoboxing and unboxing with wrapper classes
- Use `String` methods to process and format text
- Use `StringBuilder` for efficient string construction
- Apply arithmetic, comparison, and logical operators
- Write well-documented code with comments

---

## Prerequisites

- JDK 17+ installed — check with `java -version`
- An IDE (IntelliJ IDEA or VS Code with Java extension)

---

## Project Structure

```
starter-code/
└── src/
    ├── Main.java               ← entry point, run this
    └── StudentProfile.java     ← TODO: complete this class
```

---

## Part 1 — Complete `StudentProfile.java`

Open `StudentProfile.java`. The class skeleton is provided with fields and method stubs.

**Task 1 — Declare fields using appropriate primitives**
The student profile needs:
- `name` — a `String`
- `age` — an `int`
- `gpa` — a `double`
- `studentId` — a `long`
- `isEnrolled` — a `boolean`
- `grade` — a `char` (letter grade: A, B, C, D, F)

**Task 2 — Build a parameterized constructor**
Accept all fields as parameters and assign them using `this`.

**Task 3 — Implement `displayProfile()`**
Use `StringBuilder` to build and return a formatted profile string. Include all fields.

> **Why StringBuilder?** It's more efficient than `+` concatenation in a loop or multi-step build because it avoids creating multiple intermediate String objects.

**Task 4 — Implement `getGpaLetterGrade()`**
Return a letter grade based on the `gpa` field using this scale:
- 3.7–4.0 → `"A"`
- 3.3–3.69 → `"B+"`
- 3.0–3.29 → `"B"`
- below 3.0 → `"C or below"`

**Task 5 — Implement `getAgeCategoryMessage()`**
Cast `age` to a `byte` (since age fits in a byte), then return:
- age < 18 → `"Minor"`
- 18–24 → `"Traditional college age"`
- 25+ → `"Non-traditional student"`

Demonstrate the cast in your code: `byte ageAsByte = (byte) age;`

**Task 6 — Implement `getWrappedId()`**
Return `studentId` autoboxed into a `Long`. Then unbox it back to a `long` and verify it matches.
In the method body, log both operations with a comment explaining what's happening.

---

## Part 2 — Complete `Main.java`

**Task 7 — Create at least 3 StudentProfile objects** with different values.

**Task 8 — Call and print the results of all methods** for each student.

**Task 9 — Demonstrate String methods**
Using one student's `name`, show:
- `.toUpperCase()` and `.toLowerCase()`
- `.length()`
- `.substring()` to get first name only (assume space-separated)
- `.contains()` to check if name contains a specific string
- `.replace()`

**Task 10 — Use operators**
Calculate and print:
- The average GPA across all 3 students (arithmetic)
- Whether any student has a GPA above 3.5 (comparison + logical)
- Whether all students are enrolled (logical AND)

---

## Stretch Goals

1. Add a `fullSummary()` method that uses a `switch` statement on `grade` to return a descriptive message.
2. Add a `creditHours` field as an `int` and calculate tuition as `creditHours * 450.00` (use `double`).
3. Accept student input from the console using `Scanner` instead of hardcoded values.

---

## Submission Checklist

- [ ] All 6 primitive/reference field types declared correctly
- [ ] Parameterized constructor uses `this` keyword
- [ ] `displayProfile()` built with `StringBuilder`
- [ ] Explicit cast demonstrated in `getAgeCategoryMessage()`
- [ ] Autoboxing and unboxing demonstrated in `getWrappedId()`
- [ ] String methods used in `Main.java`
- [ ] Arithmetic, comparison, and logical operators all used
- [ ] Code has comments explaining non-obvious sections
