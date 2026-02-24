import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <nav style="padding:0.5rem 0.75rem; background:#eee;">
      <a routerLink="/">Home</a> |
      <a routerLink="/edit-profile">Edit Profile</a>
    </nav>
    <router-outlet></router-outlet>
  `,
})
export class AppComponent {}
