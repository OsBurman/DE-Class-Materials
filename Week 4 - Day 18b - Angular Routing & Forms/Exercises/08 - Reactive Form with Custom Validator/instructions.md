# Exercise 08 – Reactive Form with Custom Validator

## Learning Objectives
- Import `ReactiveFormsModule` and build a form using `FormBuilder`
- Use `FormGroup` and `FormControl` with built-in `Validators`
- Write a **custom validator function** matching the `ValidatorFn` signature
- Display per-field errors in the template using `form.get('field')?.errors`

## Background
Reactive forms define the form model in the **component class**, not the template. This makes
the form logic easier to unit test and reason about. Validators are plain functions of the
shape `(control: AbstractControl) => ValidationErrors | null` — they return an error object
when the value is invalid, or `null` when it is valid.

## Exercise

Build a **Contact Us** form with the following fields and validators:

| Field | Built-in Validators | Custom Validator |
|---|---|---|
| Username | required, minlength(3) | `noSpacesValidator` — rejects usernames containing spaces |
| Email | required, email | — |
| Message | required, minlength(10) | — |

### Starter code TODOs

**`app.module.ts`**
- TODO 1 – Import `ReactiveFormsModule` from `@angular/forms`

**`contact-form.component.ts`**
- TODO 2 – Inject `FormBuilder` in the constructor
- TODO 3 – Create the `FormGroup` using `this.fb.group({...})` with the correct validators
- TODO 4 – Write the `noSpacesValidator` function (outside the class):
  ```ts
  function noSpacesValidator(control: AbstractControl): ValidationErrors | null {
    return control.value?.includes(' ') ? { noSpaces: true } : null;
  }
  ```
- TODO 5 – Implement `onSubmit()`: log form value and reset

**`contact-form.component.html`**
- TODO 6 – Bind `[formGroup]="form"` on the `<form>` tag and `(ngSubmit)="onSubmit()"`
- TODO 7 – Bind `formControlName` on each input
- TODO 8 – Display per-field error messages using `form.get('field')?.errors` and `touched`

## Files
```
starter-code/
  app.module.ts
  app.component.ts
  contact-form.component.ts
  contact-form.component.html
solution/
  app.module.ts
  app.component.ts
  contact-form.component.ts
  contact-form.component.html
```

## Expected Behaviour
1. On load, all fields are empty and the submit button is disabled.
2. Typing a username with a space shows "Username cannot contain spaces."
3. All built-in validator errors display after the field is touched.
4. On valid submission, form values are logged and the form resets.
