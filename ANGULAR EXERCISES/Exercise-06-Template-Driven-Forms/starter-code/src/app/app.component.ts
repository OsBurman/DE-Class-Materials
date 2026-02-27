import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

// TODO 1: Define ApplicationData interface with:
//   fullName: string
//   email: string
//   phone: string
//   yearsOfExperience: number
//   coverLetter: string
//   skills: string[]
//   agreeToTerms: boolean
interface ApplicationData {
  // your fields here
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  // TODO 2: Create a `formData` object of type ApplicationData with default values.
  formData: ApplicationData = {
    // your defaults here
  };

  // TODO 3: Declare a `submitted` boolean flag, default false.
  submitted = false;

  readonly availableSkills = ['JavaScript', 'TypeScript', 'Angular', 'React', 'Node.js', 'Python', 'Java', 'SQL'];

  // TODO 4: Implement onSubmit(form: NgForm).
  //   - Log the form value
  //   - Set submitted = true
  onSubmit(form: NgForm): void {
    // your code here
  }

  // TODO 5: Implement reset(form: NgForm).
  //   - Call form.reset() to clear Angular's form state
  //   - Reset formData back to default values
  reset(form: NgForm): void {
    // your code here
  }

  // Helper: toggle a skill in/out of the skills array
  toggleSkill(skill: string, checked: boolean): void {
    if (checked) {
      this.formData.skills.push(skill);
    } else {
      this.formData.skills = this.formData.skills.filter(s => s !== skill);
    }
  }
}
