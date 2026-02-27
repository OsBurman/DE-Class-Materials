import { Component, inject } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  // TODO 1: Inject FormBuilder using inject()
  private fb = inject(FormBuilder);

  currentStep = 1;
  submitted = false;

  // TODO 2: Create accountForm using this.fb.group()
  //   Fields: username (required, minLength 3), email (required, email),
  //           password (required, minLength 8), confirmPassword (required)
  //   Group-level validator: this.passwordsMatch (defined below)
  accountForm = this.fb.group({
    // your controls here
  });

  // TODO 3: Create profileForm
  //   Fields: firstName (required), lastName (required),
  //           dateOfBirth (required), bio (maxLength 200)
  profileForm = this.fb.group({
    // your controls here
  });

  // TODO 4: Create socialForm with a `links` FormArray.
  //   Start with one empty link group: { platform: '', url: '' }
  socialForm = this.fb.group({
    links: this.fb.array([
      // this.fb.group({ platform: [''], url: ['', Validators.pattern('https?://.+')] })
    ])
  });

  // TODO 5: Implement passwordsMatch(group: AbstractControl): ValidationErrors | null
  //   Return { passwordsMismatch: true } when password !== confirmPassword
  //   Return null when they match
  passwordsMatch(group: AbstractControl): ValidationErrors | null {
    // your code here
    return null;
  }

  // TODO 6: Implement addLink() — push this.fb.group({ platform: '', url: '' }) to links
  addLink(): void {
    // your code here
  }

  // TODO 7: Implement removeLink(index: number) — call this.links.removeAt(index)
  removeLink(index: number): void {
    // your code here
  }

  // TODO 8: Create a `links` getter that returns the FormArray
  get links(): FormArray {
    // return this.socialForm.get('links') as FormArray;
    return this.fb.array([]);
  }

  // TODO 9: Implement nextStep() — only advance if the current step's form is valid
  nextStep(): void {
    // your code here
  }

  prevStep(): void {
    if (this.currentStep > 1) this.currentStep--;
  }

  // TODO 10: Implement onSubmit() — check all forms valid, then set submitted = true
  onSubmit(): void {
    // your code here
  }

  // Helper: get all form data as one object
  get allData() {
    return {
      ...this.accountForm.value,
      ...this.profileForm.value,
      links: this.links.value,
    };
  }
}
