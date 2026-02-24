import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-dashboard',
  template: `
    <div style="padding:1.5rem;">
      <h1>Dashboard</h1>
      <p>Welcome! You are now logged in.</p>
      <button (click)="logout()">Log out</button>
    </div>
  `,
})
export class DashboardComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
