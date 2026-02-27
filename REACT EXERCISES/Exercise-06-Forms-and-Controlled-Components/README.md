# Exercise 06 — Forms & Controlled Components

## Overview
Build a **Job Application Form** with comprehensive client-side validation. This exercise covers the full React forms pattern: controlled inputs, unified change handlers, validation logic, and the form-submission/confirmation workflow.

## Learning Objectives
- Bind all input types as **controlled components** (`value`/`checked` + `onChange`)
- Write a **single `handleChange` handler** that works for text, email, tel, select, textarea, and radio inputs
- Handle **checkbox arrays** (multi-select skills)
- Write a `validate()` function that returns an errors object
- Apply **error styling** and **error messages** conditionally
- Show a **confirmation summary** after successful submission

## What You'll Build
A professional job application form with:
- **Text inputs**: Full name, email, phone, portfolio URL
- **Select dropdown**: Position
- **Radio buttons**: Experience level
- **Checkboxes**: Skills (multi-select)
- **Textarea**: Cover letter with live character count
- **Terms checkbox**: Agree to terms
- **Validation** on all fields with inline error messages
- **Confirmation summary** displayed after successful submission

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```

## File Structure
```
src/
├── main.jsx
├── App.jsx                         ← Simple page shell
├── App.css
└── components/
    ├── ApplicationForm.jsx         ← All form state, validation, and submission logic
    └── ConfirmationSummary.jsx     ← Nicely renders the submitted form data
```

## TODO Checklist

All TODOs are in `components/ApplicationForm.jsx` unless noted.

- [ ] **TODO 1** — Declare `formData` state initialized with `INITIAL_FORM_DATA`
- [ ] **TODO 2** — Declare `errors` state as an empty object `{}`
- [ ] **TODO 3** — Implement `handleChange(e)` for text / email / tel / select / textarea / radio inputs using `e.target.name` and `e.target.value`
- [ ] **TODO 4** — Implement `handleCheckboxChange(skill)` to toggle a skill in/out of the `skills` array
- [ ] **TODO 5** — Implement `validate()` — returns an errors object; rules listed below
- [ ] **TODO 6** — Implement `handleSubmit(e)` — prevent default, run validation, set errors or set `submitted = true`
- [ ] **TODO 7** — Render `errors.fieldName` as an error message `<span>` beneath each invalid input
- [ ] **TODO 8** — Apply the `error` CSS class to inputs that have a corresponding error
- [ ] **TODO 9** — Add controlled binding (`value`/`checked` + `onChange`) to every input element
- [ ] **TODO 10** — Render `<ConfirmationSummary />` when `submitted` is `true`
- [ ] **TODO 11** — Pass an `onEdit` callback to `ConfirmationSummary` that sets `submitted` back to `false`

## Validation Rules

| Field | Rule |
|-------|------|
| Full Name | Required (non-empty after trim) |
| Email | Required + must match email format (must contain `@` and `.`) |
| Phone | **Optional** — if provided, stripped digits must be ≥ 10 |
| Position | Required (must select from dropdown) |
| Experience | Required (must pick a radio option) |
| Skills | At least 1 checkbox must be selected |
| Cover Letter | Required + minimum 50 characters |
| Agree to Terms | Must be checked |

## Key Concepts

### Controlled Components
A controlled component has its value driven entirely by React state:

```jsx
<input
  value={formData.name}     {/* ← binds to state (React controls the value) */}
  onChange={handleChange}   {/* ← updates state on every keystroke */}
/>
```

### Single Handler for Multiple Inputs
`name` attributes on inputs let one function route changes to the right field:

```js
function handleChange(e) {
  const { name, value } = e.target
  setFormData(prev => ({ ...prev, [name]: value }))
}
```

### Checkbox Arrays (Multi-Select)
Because a skills array is not a single value, it needs its own handler:

```js
function handleCheckboxChange(skill) {
  setFormData(prev => ({
    ...prev,
    skills: prev.skills.includes(skill)
      ? prev.skills.filter(s => s !== skill)   // remove
      : [...prev.skills, skill],               // add
  }))
}
```

### Validation Pattern
```js
function validate() {
  const errors = {}
  if (!formData.email.trim()) errors.email = 'Email is required.'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email))
    errors.email = 'Enter a valid email address.'
  // ... more rules
  return errors // empty object = valid
}

function handleSubmit(e) {
  e.preventDefault()
  const errs = validate()
  if (Object.keys(errs).length > 0) { setErrors(errs); return }
  setSubmitted(true)
}
```

## Expected Behavior
1. Form starts empty with no error messages
2. Submitting with empty fields shows all relevant validation errors
3. Fixing a field immediately clears its individual error
4. Phone field only validates when a value is actually entered
5. Cover letter shows a live character count (`n / 50 min`)
6. On valid submission, the confirmation summary appears with all entered data
7. "Edit Application" button returns to the form (pre-filled)
