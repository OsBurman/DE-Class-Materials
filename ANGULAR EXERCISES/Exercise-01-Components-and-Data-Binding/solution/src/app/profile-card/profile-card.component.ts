import { Component, Input } from '@angular/core';
import { Profile } from '../app.component';

@Component({
  selector: 'app-profile-card',
  standalone: true,
  imports: [],
  templateUrl: './profile-card.component.html',
  styleUrls: ['./profile-card.component.css'],
})
export class ProfileCardComponent {
  @Input() profile: Profile = {
    name: '',
    title: '',
    bio: '',
    avatarUrl: 'https://i.pravatar.cc/150',
    skills: [],
    isAvailableForWork: false,
  };
}
