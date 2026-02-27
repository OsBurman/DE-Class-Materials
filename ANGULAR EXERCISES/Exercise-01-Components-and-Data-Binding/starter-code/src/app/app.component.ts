import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProfileCardComponent } from './profile-card/profile-card.component';

// ─────────────────────────────────────────────
// Interface for our Profile data model
// ─────────────────────────────────────────────
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
  imports: [
    FormsModule,
    ProfileCardComponent,
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

  // TODO 1: Declare a `profile` property of type Profile with default values.
  //         Give it a name, a title like "Software Developer", a short bio,
  //         an avatarUrl (use https://i.pravatar.cc/150), a skills array with
  //         at least 2 skills, and set isAvailableForWork to true.
  profile: Profile = {
    // your code here
  };

  // TODO 2: Declare a `newSkill` string property (starts as an empty string).
  //         This will be bound to the "add skill" text input.
  newSkill = '';

  // TODO 3: Implement `addSkill()`.
  //         - Push `newSkill` into `profile.skills` (only if it's not empty)
  //         - Reset `newSkill` to '' after adding
  addSkill(): void {
    // your code here
  }

  // TODO 4: Implement `removeSkill(index: number)`.
  //         - Remove 1 element at `index` from `profile.skills`
  removeSkill(index: number): void {
    // your code here
  }

  // TODO 5: Implement `toggleAvailability()`.
  //         - Flip the value of `profile.isAvailableForWork`
  toggleAvailability(): void {
    // your code here
  }
}
