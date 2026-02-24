import { Component } from '@angular/core';
import { TeamMember, teamMembers } from './team-data';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  // TODO Task 9: Add a title property using interpolation in the template
  title = 'Dev Team Directory';

  // TODO Task 10: Add a filterText property for two-way binding with ngModel
  filterText = '';

  allMembers: TeamMember[] = teamMembers;

  // TODO Task 10: Compute filtered members based on filterText
  get filteredMembers(): TeamMember[] {
    // TODO: Return allMembers filtered where the member's name
    //       includes filterText (case-insensitive).
    //       If filterText is empty, return all members.
    return this.allMembers;
  }
}
