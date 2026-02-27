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

  // TODO 11: Declare an @Input() property named `profile` of type Profile.
  //          Provide a default value so the card still renders before data arrives.
  @Input() profile: Profile = {
    // your default values here
  };
}
