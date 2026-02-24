// =============================================================
// DAY 18b — Part 2, File 3: Template-Driven Forms
// =============================================================
// Topics: FormsModule, ngModel (two-way binding), ngForm directive,
// template reference variables, built-in validators, validation
// display, ngSubmit, resetting forms
// =============================================================

// IMPORTANT: Template-driven forms are defined IN THE TEMPLATE (HTML),
// not in the component class. Angular reads the ngModel directives and
// builds a FormGroup model for you behind the scenes.
//
// Requires: FormsModule imported in your NgModule (or standalone component)
// import { FormsModule } from '@angular/forms';

// =============================================================
// SECTION 1 — Basic ngModel: Two-Way Binding
// =============================================================
// [(ngModel)] is "banana in a box" syntax — a combination of:
//   [ngModel]="value"       → property binding (sets the input value)
//   (ngModelChange)="..."   → event binding (updates when value changes)
//
// Template example:
// <input [(ngModel)]="student.name" name="name" />
//
// In the component class, `student.name` stays in sync with the input.
// =============================================================

// Component class for the template-driven form examples below:
// import { Component } from '@angular/core';
//
// @Component({
//   selector: 'app-registration',
//   templateUrl: './registration.component.html',
// })
// export class RegistrationComponent {
//   student = {
//     name: '',
//     email: '',
//     password: '',
//     confirmPassword: '',
//     role: 'student',
//     agreeToTerms: false,
//   };
//
//   onSubmit(form: NgForm): void {
//     if (form.valid) {
//       console.log('Form submitted:', form.value);
//       form.reset();
//     }
//   }
// }

// =============================================================
// SECTION 2 — ngForm and the Template Reference Variable
// =============================================================
// When FormsModule is imported, Angular automatically attaches the
// NgForm directive to every <form> element.
//
// #f="ngForm" captures the NgForm instance into a template variable.
// This gives access to: f.valid, f.invalid, f.dirty, f.touched, f.value
//
// Full template example:
// =============================================================

const REGISTRATION_TEMPLATE = `
<form #f="ngForm" (ngSubmit)="onSubmit(f)">

  <!-- Text Input -->
  <div class="form-group">
    <label for="name">Full Name</label>
    <input
      id="name"
      name="name"                    <!-- name attribute is REQUIRED for template-driven forms -->
      type="text"
      [(ngModel)]="student.name"     <!-- two-way binding to component property -->
      required                       <!-- built-in HTML5 validator — Angular recognizes it -->
      minlength="3"                  <!-- Angular validator directive -->
      #nameField="ngModel"           <!-- capture ngModel instance for this specific field -->
    />

    <!-- Validation error messages -->
    <!-- Show only when: field is invalid AND has been touched (user interacted) -->
    <div *ngIf="nameField.invalid && nameField.touched" class="error-messages">
      <span *ngIf="nameField.errors?.['required']">Name is required.</span>
      <span *ngIf="nameField.errors?.['minlength']">
        Name must be at least {{ nameField.errors?.['minlength'].requiredLength }} characters.
        You entered {{ nameField.errors?.['minlength'].actualLength }}.
      </span>
    </div>
  </div>

  <!-- Email Input -->
  <div class="form-group">
    <label for="email">Email</label>
    <input
      id="email"
      name="email"
      type="email"
      [(ngModel)]="student.email"
      required
      email                          <!-- Angular's email validator directive -->
      #emailField="ngModel"
    />
    <div *ngIf="emailField.invalid && emailField.touched">
      <span *ngIf="emailField.errors?.['required']">Email is required.</span>
      <span *ngIf="emailField.errors?.['email']">Please enter a valid email address.</span>
    </div>
  </div>

  <!-- Password Input -->
  <div class="form-group">
    <label for="password">Password</label>
    <input
      id="password"
      name="password"
      type="password"
      [(ngModel)]="student.password"
      required
      minlength="8"
      #passwordField="ngModel"
    />
    <div *ngIf="passwordField.invalid && passwordField.touched">
      <span *ngIf="passwordField.errors?.['required']">Password is required.</span>
      <span *ngIf="passwordField.errors?.['minlength']">Minimum 8 characters.</span>
    </div>
  </div>

  <!-- Select / Dropdown -->
  <div class="form-group">
    <label for="role">Role</label>
    <select id="role" name="role" [(ngModel)]="student.role" required>
      <option value="">-- Select a role --</option>
      <option value="student">Student</option>
      <option value="instructor">Instructor</option>
      <option value="admin">Admin</option>
    </select>
  </div>

  <!-- Checkbox -->
  <div class="form-group">
    <label>
      <input
        type="checkbox"
        name="agreeToTerms"
        [(ngModel)]="student.agreeToTerms"
        required
      />
      I agree to the Terms of Service
    </label>
  </div>

  <!-- Submit button — disabled when form is invalid -->
  <button type="submit" [disabled]="f.invalid">Register</button>

  <!-- Debug: display the form's current value and state -->
  <pre>Value: {{ f.value | json }}</pre>
  <pre>Valid: {{ f.valid }}</pre>
  <pre>Touched: {{ f.touched }}</pre>
  <pre>Dirty: {{ f.dirty }}</pre>

</form>
`;

// =============================================================
// SECTION 3 — NgForm State Properties Explained
// =============================================================
// Every field and the form itself tracks these boolean states:
//
// ┌──────────────┬─────────────────────────────────────────────────────┐
// │ Property     │ Meaning                                             │
// ├──────────────┼─────────────────────────────────────────────────────┤
// │ valid        │ All validators pass                                 │
// │ invalid      │ One or more validators fail                         │
// │ pristine     │ Value has NOT been changed by the user              │
// │ dirty        │ Value HAS been changed by the user                  │
// │ untouched    │ Field has NOT been focused/blurred yet              │
// │ touched      │ Field HAS been focused and blurred at least once    │
// │ pending      │ Async validator in progress                         │
// └──────────────┴─────────────────────────────────────────────────────┘
//
// Angular also adds CSS classes matching these states to the <input>:
//   .ng-valid / .ng-invalid
//   .ng-pristine / .ng-dirty
//   .ng-untouched / .ng-touched
//
// You can style them directly:
//   input.ng-invalid.ng-touched { border-color: red; }
//   input.ng-valid.ng-dirty { border-color: green; }
// =============================================================

// =============================================================
// SECTION 4 — Available Built-in Validator Directives
// =============================================================
// These HTML attributes are recognized as Angular validators
// when FormsModule is imported:
//
// required                    → Validators.required
// minlength="3"               → Validators.minLength(3)
// maxlength="50"              → Validators.maxLength(50)
// email                       → Validators.email
// pattern="[A-Za-z]+"        → Validators.pattern(...)
// min="1"                     → Validators.min(1)  (number inputs)
// max="100"                   → Validators.max(100)
// =============================================================

// =============================================================
// SECTION 5 — Handling Form Submission
// =============================================================
// (ngSubmit) is Angular's form submit event.
// Always pass the NgForm reference so you can check validity.

// Template: <form #f="ngForm" (ngSubmit)="onSubmit(f)">
// Component:

// import { NgForm } from '@angular/forms';
//
// onSubmit(form: NgForm): void {
//   if (form.invalid) {
//     form.control.markAllAsTouched();  // show all errors at once on submit attempt
//     return;
//   }
//
//   const data = form.value;
//   // { name: 'Jane', email: 'jane@example.com', role: 'student', agreeToTerms: true }
//
//   this.registrationService.register(data).subscribe({
//     next: () => {
//       form.reset();                    // clears form AND resets all state flags
//       this.successMessage = 'Registered!';
//     },
//     error: err => console.error(err),
//   });
// }

// =============================================================
// SECTION 6 — ngModelGroup: Grouping Related Fields
// =============================================================
// ngModelGroup creates a sub-object in the form's value.
// Useful for address fields, name parts, etc.
// =============================================================

const GROUP_TEMPLATE = `
<form #f="ngForm" (ngSubmit)="onSubmit(f)">

  <!-- Fields grouped under "address" key in f.value -->
  <div ngModelGroup="address" #addressGroup="ngModelGroup">
    <input name="street" ngModel required placeholder="Street" />
    <input name="city" ngModel required placeholder="City" />
    <input name="zip" ngModel required pattern="\\d{5}" placeholder="ZIP" />

    <div *ngIf="addressGroup.invalid && addressGroup.touched">
      Please complete all address fields.
    </div>
  </div>

  <button type="submit">Submit</button>
</form>

<!-- f.value would be: { address: { street: '123 Main St', city: 'NY', zip: '10001' } } -->
`;

// =============================================================
// SECTION 7 — Resetting the Form
// =============================================================
// form.reset()         — clears all values AND resets pristine/untouched
// form.resetForm({})   — resets and optionally provides new initial values
//
// After reset, all .ng-touched classes are removed from inputs.
// =============================================================

// =============================================================
// SECTION 8 — Template-Driven vs Reactive Forms: When to Use Each
// =============================================================
// ┌─────────────────────┬──────────────────┬────────────────────┐
// │ Feature             │ Template-Driven  │ Reactive           │
// ├─────────────────────┼──────────────────┼────────────────────┤
// │ Logic lives in      │ HTML template    │ Component class    │
// │ Setup complexity    │ Low              │ Medium             │
// │ Dynamic fields      │ Harder           │ Easy (FormArray)   │
// │ Unit testability    │ Harder           │ Easy               │
// │ Custom validators   │ Directive-based  │ Plain functions    │
// │ Async validators    │ Supported        │ First-class        │
// │ Reactive transforms │ Limited          │ valueChanges pipe  │
// └─────────────────────┴──────────────────┴────────────────────┘
//
// Recommendation:
// - Simple forms (login, contact, 3-5 fields): template-driven is fine
// - Complex forms (wizards, dynamic fields, custom validation): reactive
// =============================================================

export {}; // prevent TypeScript "isolatedModules" error
