# Exercise 02: HTML Tables and Forms

## Objective
Build an HTML data table and an accessible registration form using proper input types, labels, and built-in HTML5 validation.

## Background
Tables are the correct HTML element for displaying **tabular data** (rows and columns of related values). Forms allow users to submit data — HTML5 added powerful input types and validation attributes that give browsers built-in error checking without JavaScript.

## Requirements

### Part A — Data Table

1. Create a `<table>` with a `<caption>` that reads "Student Grades".
2. Add a `<thead>` row with column headers: Name, Subject, Grade, Passed.
3. Add a `<tbody>` with at least four student data rows using `<tr>` and `<td>`.
4. Use `<th scope="row">` for the student name cell in each row (accessibility best practice).
5. Add a `<tfoot>` row that spans all columns and shows "End of report" using `colspan="4"`.

### Part B — Registration Form

Build a `<form>` with `action="#"` and `method="post"` that contains all of the following fields. **Every input must have a matching `<label>` using `for`/`id` pairing.**

1. **Full Name** — `<input type="text">` — required, `minlength="2"`, `placeholder="Jane Doe"`
2. **Email** — `<input type="email">` — required, `placeholder="jane@example.com"`
3. **Password** — `<input type="password">` — required, `minlength="8"`
4. **Age** — `<input type="number">` — `min="18"`, `max="120"`, required
5. **Favourite Colour** — `<input type="color">`
6. **Website** — `<input type="url">` — optional, `placeholder="https://example.com"`
7. **Subscribe to newsletter** — `<input type="checkbox">` with label
8. **Role** — `<select>` dropdown with at least three `<option>` values: Student, Instructor, Observer
9. **Bio** — `<textarea>` with `rows="4"`, `maxlength="200"`, and a placeholder
10. **Submit button** — `<button type="submit">Register</button>`
11. **Reset button** — `<button type="reset">Clear</button>`

## Hints
- Use `<label for="fieldId">` paired with `<input id="fieldId">` — this makes the label clickable and aids screen readers.
- HTML5 validation attributes (`required`, `minlength`, `min`, `max`, `type="email"`) are enforced by the browser on form submit — no JavaScript needed.
- `<th scope="col">` marks column headers; `<th scope="row">` marks row headers — both help screen readers announce the table correctly.
- `<fieldset>` and `<legend>` can be used to visually group related fields (e.g., wrap the password fields in one fieldset).

## Expected Output

When opened in a browser:
- A table with a caption, header row, 4 data rows, and a footer row
- A form with all listed fields
- Submitting with empty required fields shows browser-native validation messages
- The email field rejects input without an `@` sign
- The number field rejects values outside 18–120
