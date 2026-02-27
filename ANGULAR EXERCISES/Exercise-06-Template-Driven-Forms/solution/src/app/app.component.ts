import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

interface ApplicationData {
  fullName: string;
  email: string;
  phone: string;
  yearsOfExperience: number | null;
  coverLetter: string;
  skills: string[];
  agreeToTerms: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  formData: ApplicationData = {
    fullName: '',
    email: '',
    phone: '',
    yearsOfExperience: null,
    coverLetter: '',
    skills: [],
    agreeToTerms: false,
  };

  submitted = false;

  readonly availableSkills = ['JavaScript', 'TypeScript', 'Angular', 'React', 'Node.js', 'Python', 'Java', 'SQL'];

  onSubmit(form: NgForm): void {
    if (form.invalid) return;
    console.log('Application submitted:', this.formData);
    this.submitted = true;
  }

  reset(form: NgForm): void {
    form.reset();
    this.formData = { fullName: '', email: '', phone: '', yearsOfExperience: null, coverLetter: '', skills: [], agreeToTerms: false };
    this.submitted = false;
  }

  toggleSkill(skill: string, checked: boolean): void {
    if (checked) {
      this.formData.skills.push(skill);
    } else {
      this.formData.skills = this.formData.skills.filter(s => s !== skill);
    }
  }
}
