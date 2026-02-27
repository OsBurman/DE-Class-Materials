# Exercise 06 — Template-Driven Forms

## 🎯 Learning Objectives
- Build forms using **`ngModel`** and **`ngForm`**
- Apply Angular's **built-in validators** (`required`, `minlength`, `email`, `pattern`)
- Display **validation error messages** using template reference variables
- Check form state: `form.valid`, `form.dirty`, `form.touched`
- Handle **form submission** with `(ngSubmit)`
- **Reset** a form programmatically

---

## 📋 What You're Building
A **Job Application Form** with:
- Full name, email, phone
- Years of experience (number with min/max)
- Cover letter (textarea with min length)
- Skills checkboxes
- Terms & Conditions checkbox (required)
- Live validation feedback with red/green states
- Success confirmation screen after submit

---

## 🏗️ Project Setup
```bash
ng new exercise-06-template-forms --standalone --routing=false --style=css
cd exercise-06-template-forms
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Define `ApplicationData` interface with all form fields
- [ ] **TODO 2**: Create a `formData` property with default values
- [ ] **TODO 3**: Create `submitted = false` flag
- [ ] **TODO 4**: Implement `onSubmit(form)` — set `submitted = true`, log the data
- [ ] **TODO 5**: Implement `reset(form)` — reset the Angular form AND reset `formData`

### `app.component.html`
- [ ] **TODO 6**: Add `#appForm="ngForm"` on the `<form>` tag and bind `(ngSubmit)`
- [ ] **TODO 7**: Add `[(ngModel)]`, `name`, `required` to each input field
- [ ] **TODO 8**: Create a template reference variable like `#nameInput="ngModel"` for each field
- [ ] **TODO 9**: Show error messages using `*ngIf` (e.g., `nameInput.invalid && nameInput.touched`)
- [ ] **TODO 10**: Disable the Submit button when `appForm.invalid`
- [ ] **TODO 11**: Show a success screen when `submitted === true`

---

## 💡 Key Concepts Reminder

```html
<!-- Template-driven form setup -->
<form #myForm="ngForm" (ngSubmit)="onSubmit(myForm)">

  <!-- Binding a field with validation -->
  <input
    [(ngModel)]="data.name"
    name="name"
    required
    minlength="2"
    #nameRef="ngModel"
  />

  <!-- Showing errors -->
  @if (nameRef.invalid && nameRef.touched) {
    @if (nameRef.errors?.['required']) {
      <span class="error">Name is required</span>
    }
    @if (nameRef.errors?.['minlength']) {
      <span class="error">Must be at least 2 characters</span>
    }
  }

  <button [disabled]="myForm.invalid">Submit</button>
</form>
```

> 💡 **Form States**: `pristine` (untouched), `dirty` (changed), `touched` (focused+left), `valid`/`invalid`
