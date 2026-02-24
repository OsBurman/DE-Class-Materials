import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';

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

  onSubmit(form: NgForm): void {
    console.log('Form submitted:', form.value);
    this.submitted = true;
    // reset() clears all values AND marks all controls as pristine/untouched
    form.reset();
  }
}
