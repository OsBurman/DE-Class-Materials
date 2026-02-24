import { Injectable } from '@angular/core';

// auth.service.ts — provided, do not modify
@Injectable({ providedIn: 'root' })
export class AuthService {
  private _isLoggedIn = false;
  private _username = '';

  isLoggedIn(): boolean {
    return this._isLoggedIn;
  }

  getUsername(): string {
    return this._username;
  }

  login(email: string, password: string): boolean {
    // Simplified: any non-empty email/password logs in
    if (email && password) {
      this._isLoggedIn = true;
      this._username = email.split('@')[0];
      return true;
    }
    return false;
  }

  logout(): void {
    this._isLoggedIn = false;
    this._username = '';
  }
}
