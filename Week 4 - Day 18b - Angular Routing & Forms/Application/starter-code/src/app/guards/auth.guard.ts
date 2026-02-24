import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// TODO Task 3: Implement AuthGuard
// Implements CanActivate interface
// Inject AuthService and Router
// canActivate(): boolean
//   - if authService.isLoggedIn() → return true
//   - else → navigate to '/login' with queryParams: { returnUrl: state.url }
//             → return false

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    // TODO: implement guard logic
    return true; // remove this line when implementing
  }
}
