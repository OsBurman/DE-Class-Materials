import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
})
export class RegisterComponent implements OnInit {
  form!: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {}

  ngOnInit(): void {
    // TODO Task 5: Build the reactive form using FormBuilder
    // this.form = this.fb.group({
    //   username: ['', [Validators.required, Validators.minLength(3)]],
    //   email:    ['', [Validators.required, Validators.email]],
    //   password: ['', [Validators.required, Validators.minLength(8)]],
    //   confirm:  ['', Validators.required],
    // }, { validators: this.passwordMatchValidator });
  }

  // TODO Task 6: Custom cross-field validator
  // Returns { passwordMismatch: true } if password !== confirm, else null
  passwordMatchValidator(group: AbstractControl): { [key: string]: boolean } | null {
    const password = group.get('password')?.value;
    const confirm  = group.get('confirm')?.value;
    // TODO: compare and return error or null
    return null;
  }

  onSubmit(): void {
    if (this.form.valid) {
      // Navigate to login after "registration"
      this.router.navigate(['/login']);
    }
  }

  // Convenience getter for cleaner template access
  get f() { return this.form.controls; }
}
