import { Component, OnInit } from '@angular/core';
import { DataService } from './data.service';

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
  users: { id: number; displayName: string }[] = [];
  albumTitle = '';

  constructor(private dataService: DataService) {}

  ngOnInit(): void {
    this.dataService.getProcessedUsers().subscribe(users => (this.users = users));
    this.dataService
      .getUserWithAlbum(1)
      .subscribe(result => (this.albumTitle = result.albumTitle));
  }
}
