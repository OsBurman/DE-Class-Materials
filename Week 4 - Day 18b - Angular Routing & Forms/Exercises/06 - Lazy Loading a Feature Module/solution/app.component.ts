import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <nav style="padding:0.5rem 0.75rem; background:#eee; display:flex; gap:1rem;">
      <a routerLink="/home"    routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Home</a>
      <a routerLink="/reports" routerLinkActive="active">Reports</a>
    </nav>

    <div style="padding:1rem;" *ngIf="isHome">
      <h2>Home</h2>
      <p>Navigate to Reports to trigger lazy loading.</p>
    </div>

    <router-outlet></router-outlet>
  `,
})
export class AppComponent {
  get isHome(): boolean {
    return location.pathname === '/home' || location.pathname === '/';
  }
}
