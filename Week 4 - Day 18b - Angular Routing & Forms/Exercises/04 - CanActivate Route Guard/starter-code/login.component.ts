import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div style="padding:1.5rem;">
      <h1>Login</h1>
      <p>You must log in to access the dashboard.</p>
      <button (click)="login()">Log in</button>
    </div>
  `,
})
export class LoginComponent {
  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login();
    this.router.navigate(['/dashboard']);
  }
}
