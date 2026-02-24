import { Component, Input, OnInit } from '@angular/core';
import { TeamMember } from '../team-data';

@Component({
  selector: 'app-team-member-card',
  templateUrl: './team-member-card.component.html'
})
export class TeamMemberCardComponent implements OnInit {
  // TODO Task 1: Declare @Input() properties for: name, role, skills, isAvailable, avatarUrl
  // Use the TeamMember interface fields as a guide.
  // Example:
  // @Input() name: string = '';

  // TODO Task 4: Add a 'selected' boolean property (default false)
  selected = false;

  ngOnInit(): void {
    // Lifecycle hook — runs once after the component is initialized
    console.log(`TeamMemberCard initialized for: ${/* TODO: your name property */ 'member'}`);
  }

  // TODO Task 3: Implement onViewProfile() — log "Viewing profile for: [name]"
  onViewProfile(): void {
    // TODO: Log a message and toggle selected
  }
}
