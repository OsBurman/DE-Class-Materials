import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-layout',
  template: `
    <div style="display:flex; min-height:100vh;">
      <!-- Sidebar navigation — always visible while on /dashboard/* -->
      <nav style="width:160px; background:#2c3e50; color:#fff; padding:1rem;">
        <h3 style="margin:0 0 1rem;">Dashboard</h3>
        <!-- TODO 4 – Replace plain <a> tags with routerLink and add routerLinkActive="active" -->
        <ul style="list-style:none; padding:0; margin:0;">
          <li><a href="/dashboard">Overview</a></li>
          <li><a href="/dashboard/analytics">Analytics</a></li>
          <li><a href="/dashboard/settings">Settings</a></li>
        </ul>
      </nav>

      <!-- Main content area — child components render here -->
      <main style="flex:1; padding:1.5rem;">
        <!-- TODO 5 – Add <router-outlet> so child routes are projected into this area -->
      </main>
    </div>
  `,
  styles: [`
    a { display:block; color:#ccc; text-decoration:none; padding:0.4rem 0; }
    a.active { color:#fff; font-weight:bold; }
  `],
})
export class DashboardLayoutComponent {}
