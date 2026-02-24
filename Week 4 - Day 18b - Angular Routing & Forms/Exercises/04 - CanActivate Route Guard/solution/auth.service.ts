import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Simple in-memory flag — in a real app this would check a JWT or session cookie.
  isLoggedIn = false;

  login(): void  { this.isLoggedIn = true;  }
  logout(): void { this.isLoggedIn = false; }
}
