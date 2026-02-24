import { Component } from '@angular/core';
// TODO 7 – Import NgForm from '@angular/forms'

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
})
export class RegistrationComponent {
  model = {
    username: '',
    email:    '',
    password: '',
  };

  submitted = false;

  // TODO 8 – Implement onSubmit(form: NgForm): void
  //   Log form.value to the console
  //   Set this.submitted = true
  //   Call form.reset() to clear the form
  onSubmit(form: any): void {
    // Your code here
  }
}
