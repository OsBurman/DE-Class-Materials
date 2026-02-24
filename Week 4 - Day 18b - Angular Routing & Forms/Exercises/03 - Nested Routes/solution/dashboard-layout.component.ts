import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-layout',
  template: `
    <div style="display:flex; min-height:100vh;">
      <!-- Sidebar — this element stays rendered for all /dashboard/* routes -->
      <nav style="width:160px; background:#2c3e50; color:#fff; padding:1rem;">
        <h3 style="margin:0 0 1rem;">Dashboard</h3>
        <ul style="list-style:none; padding:0; margin:0;">
          <!-- routerLink="/dashboard" matches the empty '' child (OverviewComponent) -->
          <!-- [routerLinkActiveOptions]="{exact:true}" prevents /dashboard from
               being "active" on /dashboard/analytics as well -->
          <li>
            <a routerLink="/dashboard"
               routerLinkActive="active"
               [routerLinkActiveOptions]="{ exact: true }">Overview</a>
          </li>
          <li>
            <a routerLink="/dashboard/analytics"
               routerLinkActive="active">Analytics</a>
          </li>
          <li>
            <a routerLink="/dashboard/settings"
               routerLinkActive="active">Settings</a>
          </li>
        </ul>
      </nav>

      <!-- Child outlet — the matched child component renders here -->
      <main style="flex:1; padding:1.5rem;">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    a { display:block; color:#ccc; text-decoration:none; padding:0.4rem 0; }
    a.active { color:#fff; font-weight:bold; }
  `],
})
export class DashboardLayoutComponent {}
