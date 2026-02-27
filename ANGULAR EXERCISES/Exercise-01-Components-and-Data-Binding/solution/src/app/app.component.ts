import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProfileCardComponent } from './profile-card/profile-card.component';

export interface Profile {
  name: string;
  title: string;
  bio: string;
  avatarUrl: string;
  skills: string[];
  isAvailableForWork: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, ProfileCardComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  profile: Profile = {
    name: 'Jane Smith',
    title: 'Senior Software Developer',
    bio: 'Passionate about building great user experiences with Angular and TypeScript.',
    avatarUrl: 'https://i.pravatar.cc/150?img=47',
    skills: ['Angular', 'TypeScript', 'RxJS'],
    isAvailableForWork: true,
  };

  newSkill = '';

  addSkill(): void {
    const trimmed = this.newSkill.trim();
    if (trimmed) {
      this.profile.skills.push(trimmed);
      this.newSkill = '';
    }
  }

  removeSkill(index: number): void {
    this.profile.skills.splice(index, 1);
  }

  toggleAvailability(): void {
    this.profile.isAvailableForWork = !this.profile.isAvailableForWork;
  }
}
