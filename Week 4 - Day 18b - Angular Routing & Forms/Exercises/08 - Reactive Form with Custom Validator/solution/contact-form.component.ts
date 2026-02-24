import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';

// ── Custom Validator ──────────────────────────────────────────────────────────
// A validator is a plain function: (AbstractControl) => ValidationErrors | null
// It returns null when the value is valid, or an error object when it is not.
// The key in the error object ('noSpaces') is what the template checks.
function noSpacesValidator(control: AbstractControl): ValidationErrors | null {
  return control.value && (control.value as string).includes(' ')
    ? { noSpaces: true }
    : null;
}
// ─────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-contact-form',
  templateUrl: './contact-form.component.html',
})
export class ContactFormComponent implements OnInit {
  form!: FormGroup;
  submitted = false;

  // FormBuilder is a shorthand service — fb.group({}) is equivalent to
  // new FormGroup({ field: new FormControl('', [...validators]) })
  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), noSpacesValidator]],
      email:    ['', [Validators.required, Validators.email]],
      message:  ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      console.log('Form submitted:', this.form.value);
      this.submitted = true;
      this.form.reset();
    }
  }
}
