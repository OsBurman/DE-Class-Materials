# Exercise 07 — Reactive Forms

## 🎯 Learning Objectives
- Build forms with **`FormBuilder`**, **`FormGroup`**, and **`FormControl`**
- Apply **built-in validators**: `Validators.required`, `Validators.email`, `Validators.min`, etc.
- Write **custom validator functions**
- Create a **cross-field validator** (e.g., password must match confirm password)
- Use **`FormArray`** for dynamic lists of form controls
- Subscribe to **`valueChanges`** and **`statusChanges`**
- Understand why reactive forms are preferred for complex forms

---

## 📋 What You're Building
A **User Registration Wizard** with:
- Step 1 — Account: username, email, password, confirm password (cross-field validation)
- Step 2 — Profile: first name, last name, date of birth, bio
- Step 3 — Social Links: a dynamic **`FormArray`** where users can add/remove social media links
- Progress indicator showing which step you're on
- Summary screen showing all entered data

---

## 🏗️ Project Setup
```bash
ng new exercise-07-reactive-forms --standalone --routing=false --style=css
cd exercise-07-reactive-forms
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Inject `FormBuilder` using `inject(FormBuilder)`
- [ ] **TODO 2**: Create `accountForm` with: `username` (required, minLength 3), `email` (required, email), `password` (required, minLength 8), `confirmPassword` (required) — apply the `passwordsMatch` cross-field validator at the group level
- [ ] **TODO 3**: Create `profileForm` with: `firstName` (required), `lastName` (required), `dateOfBirth` (required), `bio` (maxLength 200)
- [ ] **TODO 4**: Create `socialForm` with a `links` FormArray — each link has `platform` and `url`
- [ ] **TODO 5**: Implement `passwordsMatch` validator function (returns `{ passwordsMismatch: true }` if they don't match)
- [ ] **TODO 6**: Implement `addLink()` — push a new FormGroup to the links FormArray
- [ ] **TODO 7**: Implement `removeLink(index)` — remove from FormArray at index
- [ ] **TODO 8**: Create `get links()` getter that returns the links FormArray
- [ ] **TODO 9**: Implement `nextStep()` / `prevStep()` step navigation
- [ ] **TODO 10**: Implement `onSubmit()` — only if all three forms are valid

### `app.component.html`
- [ ] **TODO 11**: Show progress indicator for current step
- [ ] **TODO 12**: Step 1 — bind `[formGroup]="accountForm"`, use `formControlName` on each input
- [ ] **TODO 13**: Show error messages using `accountForm.get('email')?.errors`
- [ ] **TODO 14**: Show cross-field error for password mismatch from the group-level error
- [ ] **TODO 15**: Step 3 — use `formArrayName="links"` and loop with `@for`

---

## 💡 Key Concepts Reminder

```typescript
// FormBuilder injection
private fb = inject(FormBuilder);

// Building a form
form = this.fb.group({
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(8)]],
}, { validators: this.passwordsMatch });

// Custom validator
passwordsMatch(group: AbstractControl) {
  const pw = group.get('password')?.value;
  const cpw = group.get('confirmPassword')?.value;
  return pw === cpw ? null : { passwordsMismatch: true };
}

// FormArray
links = this.fb.array([
  this.fb.group({ platform: [''], url: [''] })
]);

// Template
<input formControlName="email" />
<span *ngIf="form.get('email')?.errors?.['required']">Required</span>
```
