# Exercise 03: Class Members — Fields and Methods

## Objective
Practice declaring instance fields, writing instance methods (including getters, setters, and a `toString()` override), and calling those methods on objects.

---

## Background
A class's **members** are its fields (data) and methods (behaviour). **Getters** (also called accessors) return a field's value; **setters** (mutators) validate and assign a new value. Overriding `toString()` lets you control how an object prints naturally with `System.out.println(obj)`.

---

## Requirements

1. Create a class `Student` with three private-style fields (just keep them package-private for now — no `private` keyword yet, that is covered in Exercise 04):
   - `String name`
   - `int    studentId`
   - `double gpa`

2. Write a parameterized constructor that sets all three fields.

3. Write **getter methods** for each field:
   - `String  getName()`
   - `int     getStudentId()`
   - `double  getGpa()`

4. Write **setter methods** for `name` and `gpa` only (id is immutable):
   - `void setName(String name)`
   - `void setGpa(double gpa)` — if the new GPA is outside `[0.0, 4.0]`, print `"Invalid GPA: [value]"` and do NOT update the field.

5. Write a method `String getLetterGrade()` that returns:
   - `"A"` for GPA ≥ 3.7
   - `"B"` for GPA ≥ 3.0
   - `"C"` for GPA ≥ 2.0
   - `"D"` for GPA ≥ 1.0
   - `"F"` otherwise

6. Override `toString()`: return `"Student{id=[id], name='[name]', gpa=[gpa], grade=[letter]}"`.

7. In `main`:
   - Create two students with the constructor.
   - Print each student using `System.out.println(student)` (calls `toString()` automatically).
   - Use `setName()` and `setGpa()` to update one student's name and GPA.
   - Try setting an invalid GPA (`5.5`) — observe the error message.
   - Print the updated student.

---

## Hints
- `toString()` is inherited from `Object`; override it with `@Override` annotation above the method signature.
- The setter's guard clause: `if (gpa < 0.0 || gpa > 4.0) { ... return; }` before assigning.
- `getLetterGrade()` is a simple `if-else-if` chain using the stored `this.gpa` field.
- `System.out.println(obj)` automatically calls `obj.toString()` — no explicit call needed.

---

## Expected Output
```
Student{id=1001, name='Alice', gpa=3.8, grade=A}
Student{id=1002, name='Bob', gpa=2.5, grade=C}
Invalid GPA: 5.5
Student{id=1001, name='Alicia', gpa=3.5, grade=B}
Bob's GPA: 2.5
```
