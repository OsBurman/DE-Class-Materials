import { Component, Input, OnInit } from '@angular/core';
import { TeamMember } from '../team-data';

@Component({
  selector: 'app-team-list',
  templateUrl: './team-list.component.html'
})
export class TeamListComponent implements OnInit {
  // TODO Task 5: Declare @Input() members: TeamMember[] = []

  // TODO Task 8: Implement ngOnInit — log "TeamList initialized with X members"
  ngOnInit(): void {
    // TODO
  }

  // TODO Task 6: trackBy function for *ngFor performance
  trackByMemberId(index: number, member: TeamMember): number {
    // TODO: return member.id
    return index;
  }
}
