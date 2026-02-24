# Exercise 07 – Template-Driven Form with Validation

## Learning Objectives
- Import `FormsModule` to enable template-driven forms
- Use `[(ngModel)]` for two-way data binding on form inputs
- Create a template reference variable (`#f="ngForm"`) to access form state
- Apply built-in Angular validators: `required`, `minlength`, `maxlength`, `email`, `pattern`
- Display conditional error messages using `*ngIf` and control state (`touched`, `invalid`)

## Background
Template-driven forms are configured entirely in the HTML template using Angular directives.
Angular automatically tracks each input's validity state. The `NgForm` directive (exposed via
`#f="ngForm"`) gives you access to the overall form validity, and each `NgModel` instance
(e.g. `#username="ngModel"`) gives per-field validity.

## Exercise

Build a **User Registration** form with the following fields:

| Field | Validators |
|---|---|
| Username | required, minlength(3), maxlength(20) |
| Email | required, email |
| Password | required, minlength(8) |

### Starter code TODOs

**`app.module.ts`**
- TODO 1 – Import `FormsModule` from `@angular/forms` and add it to `imports`

**`registration.component.html`**
- TODO 2 – Add `#f="ngForm"` to the `<form>` tag and bind `(ngSubmit)="onSubmit(f)"`
- TODO 3 – Add `name="username"` and `[(ngModel)]="model.username"` with validators to the username input; create `#username="ngModel"`
- TODO 4 – Add validation error `<div>` blocks that show when the field is `touched` and `invalid`
- TODO 5 – Repeat for email and password fields
- TODO 6 – Disable the submit button when `f.invalid`

**`registration.component.ts`**
- TODO 7 – Import `NgForm` from `@angular/forms`
- TODO 8 – Implement `onSubmit(form: NgForm)` to log the form value and reset the form

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  registration.component.ts
  registration.component.html
solution/
  app.module.ts
  app.component.ts
  registration.component.ts
  registration.component.html
```

## Expected Behaviour
1. The form shows inline validation messages after each field is touched and left empty (or invalid).
2. The Submit button is disabled while the form is invalid.
3. On valid submission, the form values are logged and the form resets.
