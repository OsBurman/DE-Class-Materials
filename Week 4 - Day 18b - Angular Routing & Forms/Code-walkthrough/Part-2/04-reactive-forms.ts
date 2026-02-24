// =============================================================
// DAY 18b — Part 2, File 4: Reactive Forms
// =============================================================
// Topics: ReactiveFormsModule, FormGroup, FormControl, FormBuilder,
// built-in Validators, custom validators, cross-field validators,
// FormArray, valueChanges, form submission, error display pattern
// =============================================================

// Requires: ReactiveFormsModule imported in your NgModule or standalone component
// import { ReactiveFormsModule } from '@angular/forms';

import {
  FormGroup,
  FormControl,
  FormBuilder,
  FormArray,
  Validators,
  AbstractControl,
  ValidationErrors,
  AsyncValidatorFn,
} from '@angular/forms';
import { Observable, of, timer } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

// =============================================================
// SECTION 1 — FormGroup and FormControl: Manual Setup
// =============================================================
// Reactive forms are defined in the COMPONENT CLASS — not the template.
// You construct a FormGroup with named FormControls.
// The template just BINDS to the model — it has no logic.
// =============================================================

// In a component class:
// registrationForm = new FormGroup({
//   name: new FormControl('', [Validators.required, Validators.minLength(3)]),
//   email: new FormControl('', [Validators.required, Validators.email]),
//   password: new FormControl('', [Validators.required, Validators.minLength(8)]),
// });
//
// FormControl(initialValue, syncValidators, asyncValidators)

// Corresponding HTML template:
const REACTIVE_FORM_TEMPLATE = `
<form [formGroup]="registrationForm" (ngSubmit)="onSubmit()">

  <div class="form-group">
    <label>Name</label>
    <input formControlName="name" type="text" />
    <!-- formControlName binds to the FormControl by its key in the FormGroup -->

    <div *ngIf="registrationForm.get('name')?.invalid &&
                registrationForm.get('name')?.touched">
      <span *ngIf="registrationForm.get('name')?.errors?.['required']">
        Name is required.
      </span>
      <span *ngIf="registrationForm.get('name')?.errors?.['minlength']">
        Name must be at least 3 characters.
      </span>
    </div>
  </div>

  <div class="form-group">
    <label>Email</label>
    <input formControlName="email" type="email" />
    <div *ngIf="registrationForm.get('email')?.invalid &&
                registrationForm.get('email')?.touched">
      <span *ngIf="registrationForm.get('email')?.errors?.['required']">Email required.</span>
      <span *ngIf="registrationForm.get('email')?.errors?.['email']">Invalid email.</span>
    </div>
  </div>

  <button type="submit" [disabled]="registrationForm.invalid">Register</button>
</form>
`;

// INSTRUCTOR NOTE: [formGroup] binds the entire FormGroup to the form element.
// formControlName (no square brackets) binds a specific control by its string key.
// Never mix formControlName with ngModel in the same form.

// =============================================================
// SECTION 2 — FormBuilder: The Concise Way
// =============================================================
// FormBuilder is a service that creates FormGroup/FormControl/FormArray
// with less boilerplate. The inject() approach is most modern.
// =============================================================

// import { inject } from '@angular/core';
// private fb = inject(FormBuilder);
//
// registrationForm = this.fb.group({
//   name: ['', [Validators.required, Validators.minLength(3)]],
//   email: ['', [Validators.required, Validators.email]],
//   password: ['', [Validators.required, Validators.minLength(8)]],
//   confirmPassword: ['', Validators.required],
//   role: ['student', Validators.required],
//   agreeToTerms: [false, Validators.requiredTrue],
// });
//
// fb.group({ key: [defaultValue, validators] })
// fb.control(defaultValue, validators)
// fb.array([initial controls])

// =============================================================
// SECTION 3 — All Built-In Validators
// =============================================================

// Validators.required         — value is not empty/null
// Validators.requiredTrue      — value must be true (checkbox)
// Validators.email             — RFC email format
// Validators.minLength(n)      — string length >= n
// Validators.maxLength(n)      — string length <= n
// Validators.min(n)            — number value >= n
// Validators.max(n)            — number value <= n
// Validators.pattern(regex)    — value matches the regex
// Validators.nullValidator     — always returns null (no-op, useful as placeholder)
// Validators.compose([...])    — combine validators into one
// Validators.composeAsync([...]) — combine async validators

// Multiple validators passed as an ARRAY:
// new FormControl('', [Validators.required, Validators.email, Validators.maxLength(100)])

// =============================================================
// SECTION 4 — Accessing Controls and Errors
// =============================================================

// Verbose (always works, null-safe with ?.):
// this.registrationForm.get('email')?.value
// this.registrationForm.get('email')?.errors
// this.registrationForm.get('email')?.errors?.['required']

// With a getter shortcut (best practice — avoids repeated .get() calls):
// get email(): AbstractControl { return this.registrationForm.get('email')!; }
// Then in template: email.errors?.['required']

// Nested path navigation:
// this.registrationForm.get('address.city')?.value
// this.registrationForm.get(['address', 'city'])?.value   // array syntax

// =============================================================
// SECTION 5 — Custom Sync Validator
// =============================================================
// A custom validator is just a FUNCTION:
// (control: AbstractControl) => ValidationErrors | null
//
// Returns null → VALID
// Returns an object → INVALID (the object becomes the errors value)
// =============================================================

// Validator: no whitespace allowed
export function noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value ?? '';
  const hasWhitespace = value.includes(' ');
  return hasWhitespace ? { noWhitespace: { actual: value } } : null;
}

// Validator: password strength (at least 1 uppercase, 1 lowercase, 1 digit)
export function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const value: string = control.value ?? '';
  const hasUpper = /[A-Z]/.test(value);
  const hasLower = /[a-z]/.test(value);
  const hasDigit = /[0-9]/.test(value);

  if (!hasUpper || !hasLower || !hasDigit) {
    return {
      passwordStrength: {
        hasUpper,
        hasLower,
        hasDigit,
        message: 'Must contain uppercase, lowercase, and a digit.',
      },
    };
  }
  return null;
}

// Usage:
// new FormControl('', [Validators.required, Validators.minLength(8), passwordStrengthValidator])

// Template error display:
// <span *ngIf="passwordControl.errors?.['passwordStrength']">
//   {{ passwordControl.errors?.['passwordStrength'].message }}
// </span>

// =============================================================
// SECTION 6 — Cross-Field Validator (Group-Level)
// =============================================================
// To compare two fields (e.g., password === confirmPassword),
// the validator is applied to the FORM GROUP, not an individual control.
// =============================================================

export function passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;

  // Only validate if both fields have values
  if (!password || !confirm) return null;

  return password === confirm ? null : { passwordMismatch: true };
}

// Usage — pass the validator as the SECOND argument to fb.group():
// this.fb.group(
//   {
//     password: ['', [Validators.required, Validators.minLength(8)]],
//     confirmPassword: ['', Validators.required],
//   },
//   { validators: passwordMatchValidator }   // <-- group-level validator
// )

// Template: read the error from the FORM level, not the field level
// <div *ngIf="registrationForm.errors?.['passwordMismatch'] &&
//             registrationForm.get('confirmPassword')?.touched">
//   Passwords do not match.
// </div>

// =============================================================
// SECTION 7 — Async Validator
// =============================================================
// Async validators return Observable<ValidationErrors | null>
// or Promise<ValidationErrors | null>.
// Used for: checking username availability via HTTP, email uniqueness, etc.
// =============================================================

// Simulate checking if an email already exists in the database
export function emailUniqueValidator(takenEmails: string[]): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    // Add a debounce — don't call API on every keystroke
    return timer(400).pipe(
      switchMap(() => {
        const isTaken = takenEmails.includes(control.value);
        return of(isTaken ? { emailTaken: true } : null);
      })
    );
  };
}

// Usage:
// email: ['', [Validators.required, Validators.email], [emailUniqueValidator(['a@a.com'])]]
//                sync validators ↑                      async validators ↑

// While async validation is pending: control.status === 'PENDING'
// Template: <span *ngIf="emailControl.pending">Checking availability...</span>
// Template: <span *ngIf="emailControl.errors?.['emailTaken']">Email is already taken.</span>

// =============================================================
// SECTION 8 — FormArray: Dynamic Form Fields
// =============================================================
// FormArray holds an ordered list of controls — useful when the user
// can add/remove items (skills, phone numbers, addresses, etc.)
// =============================================================

// Component class example:
// private fb = inject(FormBuilder);
//
// profileForm = this.fb.group({
//   name: ['', Validators.required],
//   skills: this.fb.array([
//     this.fb.control('', Validators.required)  // start with one skill field
//   ]),
// });
//
// // Getter for convenient access
// get skills(): FormArray {
//   return this.profileForm.get('skills') as FormArray;
// }
//
// addSkill(): void {
//   this.skills.push(this.fb.control('', Validators.required));
// }
//
// removeSkill(index: number): void {
//   this.skills.removeAt(index);
// }

// Template for FormArray:
const FORM_ARRAY_TEMPLATE = `
<form [formGroup]="profileForm" (ngSubmit)="onSubmit()">

  <div class="form-group">
    <label>Name</label>
    <input formControlName="name" />
  </div>

  <!-- formArrayName directive binds to the FormArray by key -->
  <div formArrayName="skills">
    <h3>Skills</h3>

    <!-- Iterate over controls using the 'skills' getter -->
    <div *ngFor="let skill of skills.controls; let i = index">
      <!-- Each array item bound by its numeric index -->
      <input [formControlName]="i" placeholder="Skill {{ i + 1 }}" />
      <button type="button" (click)="removeSkill(i)">Remove</button>
    </div>

    <button type="button" (click)="addSkill()">+ Add Skill</button>
  </div>

  <button type="submit" [disabled]="profileForm.invalid">Save Profile</button>
</form>
`;

// FormArray methods:
// skills.push(control)        — add at end
// skills.insert(index, ctrl)  — insert at position
// skills.removeAt(index)      — remove at position
// skills.clear()              — remove all controls
// skills.at(index)            — get control at position
// skills.length               — number of controls
// skills.value                — array of all values: ['JavaScript', 'TypeScript']

// =============================================================
// SECTION 9 — valueChanges: Reactive Updates
// =============================================================
// FormControl and FormGroup expose an Observable for every change.
// This lets you react to input changes without event listeners.
// =============================================================

// In ngOnInit or constructor:
// this.registrationForm.get('email')?.valueChanges.subscribe(value => {
//   console.log('Email changed to:', value);
// });
//
// // Listen to the whole form:
// this.registrationForm.valueChanges.subscribe(formValue => {
//   console.log('Form changed:', formValue);
//   this.autosave(formValue);   // e.g., save draft on every keystroke
// });
//
// IMPORTANT: Unsubscribe in ngOnDestroy to prevent memory leaks!
// Better pattern: use takeUntilDestroyed() (Angular 16+) or async pipe.

// =============================================================
// SECTION 10 — Programmatic Control: setValue vs patchValue
// =============================================================
// Use these to update form values from code (e.g., after loading from API).

// setValue — must provide ALL controls (strict)
// this.registrationForm.setValue({
//   name: 'Jane Doe',
//   email: 'jane@example.com',
//   password: '',
//   confirmPassword: '',
//   role: 'instructor',
//   agreeToTerms: false,
// });

// patchValue — update only SOME controls (partial)
// this.registrationForm.patchValue({
//   name: 'Jane Doe',
//   email: 'jane@example.com',
//   // other fields unchanged
// });

// =============================================================
// SECTION 11 — Form Submission: Best Practices
// =============================================================

// onSubmit(): void {
//   if (this.registrationForm.invalid) {
//     // Mark all as touched so all error messages appear
//     this.registrationForm.markAllAsTouched();
//     return;
//   }
//
//   const data = this.registrationForm.getRawValue();
//   // getRawValue() includes DISABLED controls; .value does not
//
//   this.authService.register(data).subscribe({
//     next: () => {
//       this.registrationForm.reset();
//       this.router.navigate(['/dashboard']);
//     },
//     error: err => this.errorMessage = err.message,
//   });
// }

// =============================================================
// SECTION 12 — Disabling Controls
// =============================================================
// Disabled controls are NOT included in .value (use getRawValue())
// They don't participate in validation.
//
// control.disable()   — disable the control
// control.enable()    — re-enable it
// new FormControl({ value: 'fixed', disabled: true })  — start disabled

// Example: disable confirm-password until password is filled
// this.registrationForm.get('password')?.valueChanges.subscribe(val => {
//   const confirmCtrl = this.registrationForm.get('confirmPassword');
//   val ? confirmCtrl?.enable() : confirmCtrl?.disable();
// });

export {}; // prevent TypeScript "isolatedModules" error
