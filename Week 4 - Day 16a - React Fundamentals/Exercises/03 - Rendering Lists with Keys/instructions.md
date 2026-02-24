# Exercise 03: Rendering Lists with Keys

## Objective
Use JavaScript's `Array.map()` to render dynamic lists of components, and understand why React requires a unique `key` prop on each list item.

## Background
When you have an array of data, you use `.map()` to transform each element into a JSX element. React needs a `key` prop on each element so it can efficiently update the DOM when the list changes. Keys must be **unique among siblings** and **stable** (they should not change between renders).

**Important:** Using the array index as a key is an anti-pattern when the list can be reordered or filtered, because the index changes — React will re-create items unnecessarily and can produce bugs with stateful components.

## Requirements

1. Create a `StudentCard` component that accepts:
   - `name` (string)
   - `grade` (string, e.g. `"A"`, `"B"`, `"C"`, `"F"`)
   - `subject` (string)
   
   Render a `<div className="student-card">` with `name`, `subject`, and `"Grade: " + grade`.

2. Create a `StudentList` component that:
   - Accepts a `students` prop (an array of `{ id, name, grade, subject }` objects)
   - Uses `.map()` to render a `<StudentCard />` for each student
   - Passes `key={student.id}` on each mapped element (the `key` goes on the outer element returned from `map`, not inside `StudentCard`)

3. Create a `PassingStudents` component that:
   - Accepts the same `students` array
   - **Filters** to only students with grade `"A"`, `"B"`, or `"C"` (not `"F"`)
   - Renders the filtered list using `.map()` with `key={student.id}`

4. In `App`, define the students array as a constant (at least 5 students, including at least one with grade `"F"`), then render:
   - `<h2>All Students</h2>` + `<StudentList students={students} />`
   - `<h2>Passing Students</h2>` + `<PassingStudents students={students} />`

## Key Rules to Demonstrate
- `key` must be placed on the JSX element returned from `.map()`, not passed as a regular prop
- `key` should come from a stable, unique ID — not the array index
- Do **not** pass `key` to the child component as a prop (React strips it — it is not accessible inside `StudentCard`)

## Expected Output
The browser shows all students, then a filtered list of only passing students.

```
All Students
┌──────────────────┐  ┌──────────────────┐  ...
│ Alice            │  │ Bob              │
│ Mathematics      │  │ Science          │
│ Grade: A         │  │ Grade: F         │
└──────────────────┘  └──────────────────┘

Passing Students
┌──────────────────┐  (Bob is excluded)
│ Alice            │
│ Mathematics      │
│ Grade: A         │
└──────────────────┘
```
