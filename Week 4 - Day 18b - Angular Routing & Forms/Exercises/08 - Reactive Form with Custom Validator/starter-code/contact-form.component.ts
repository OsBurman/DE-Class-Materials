import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
// TODO 2 – Import FormBuilder from '@angular/forms'

// TODO 4 – Write the custom validator function here (outside the class):
// function noSpacesValidator(control: AbstractControl): ValidationErrors | null {
//   return control.value?.includes(' ') ? { noSpaces: true } : null;
// }

@Component({
  selector: 'app-contact-form',
  templateUrl: './contact-form.component.html',
})
export class ContactFormComponent implements OnInit {
  form!: FormGroup;
  submitted = false;

  // TODO 2 – Inject FormBuilder: constructor(private fb: FormBuilder) {}
  constructor() {}

  ngOnInit(): void {
    // TODO 3 – Build the form group:
    // this.form = this.fb.group({
    //   username: ['', [Validators.required, Validators.minLength(3), noSpacesValidator]],
    //   email:    ['', [Validators.required, Validators.email]],
    //   message:  ['', [Validators.required, Validators.minLength(10)]],
    // });
  }

  // TODO 5 – Implement onSubmit():
  //   console.log(this.form.value)
  //   this.submitted = true
  //   this.form.reset()
  onSubmit(): void {}
}
