import { Component, inject } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule, JsonPipe],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  private fb = inject(FormBuilder);

  currentStep = 1;
  submitted = false;

  accountForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  }, { validators: this.passwordsMatch });

  profileForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    dateOfBirth: ['', Validators.required],
    bio: ['', Validators.maxLength(200)],
  });

  socialForm = this.fb.group({
    links: this.fb.array([
      this.fb.group({ platform: [''], url: ['', Validators.pattern('https?://.+')] })
    ])
  });

  passwordsMatch(group: AbstractControl): ValidationErrors | null {
    const pw = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { passwordsMismatch: true };
  }

  get links(): FormArray {
    return this.socialForm.get('links') as FormArray;
  }

  addLink(): void {
    this.links.push(this.fb.group({ platform: [''], url: ['', Validators.pattern('https?://.+')] }));
  }

  removeLink(index: number): void {
    this.links.removeAt(index);
  }

  nextStep(): void {
    if (this.currentStep === 1 && this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }
    if (this.currentStep === 2 && this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.currentStep++;
  }

  prevStep(): void {
    if (this.currentStep > 1) this.currentStep--;
  }

  onSubmit(): void {
    if (this.accountForm.invalid || this.profileForm.invalid) return;
    this.submitted = true;
  }

  get allData() {
    const { confirmPassword, ...account } = this.accountForm.value as any;
    return { ...account, ...this.profileForm.value, links: this.links.value };
  }
}
