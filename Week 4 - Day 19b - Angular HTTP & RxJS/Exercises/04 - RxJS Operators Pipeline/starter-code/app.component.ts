import { Component, OnInit } from '@angular/core';
// TODO: import DataService from './data.service'

@Component({
  selector: 'app-root',
  template: `
    <h2>RxJS Operators Pipeline</h2>
    <ul>
      <li *ngFor="let u of users">{{ u.id }} — {{ u.displayName }}</li>
    </ul>
    <p *ngIf="albumTitle">First album for user 1: <strong>{{ albumTitle }}</strong></p>
  `,
})
export class AppComponent implements OnInit {
  users: any[] = [];
  albumTitle = '';

  // TODO: inject DataService

  ngOnInit(): void {
    // TODO: subscribe to this.dataService.getProcessedUsers(), set this.users
    // TODO: subscribe to this.dataService.getUserWithAlbum(1), set this.albumTitle
  }
}
