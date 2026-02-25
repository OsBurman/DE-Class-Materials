// Day 18b Part 2 — Route Guards, Lazy Loading, Template-Driven & Reactive Forms
// Run: npm install && npm start

import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { RouterModule, RouterLink, RouterOutlet, RouterLinkActive,
         CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

// ─────────────────────────────────────────────────────────────────────────────
// Auth Service (simulates login state for route guard demo)
// ─────────────────────────────────────────────────────────────────────────────
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _loggedIn = false;
  get isLoggedIn() { return this._loggedIn; }
  login()  { this._loggedIn = true; }
  logout() { this._loggedIn = false; }
}

// ─────────────────────────────────────────────────────────────────────────────
// Route Guard (CanActivateFn — functional guard, Angular 15+)
// ─────────────────────────────────────────────────────────────────────────────
export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn) return true;
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};

// ─────────────────────────────────────────────────────────────────────────────
// Page Components
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div style="max-width:400px;margin:4rem auto;padding:2rem;background:white;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.1);font-family:sans-serif">
      <h2 style="color:#dd0031;margin-bottom:1.5rem">🔐 Login Required</h2>
      <p style="color:#555;margin-bottom:1rem">You need to log in to access the protected page.</p>
      <button (click)="auth.login()" [routerLink]="[returnUrl]"
              style="background:#dd0031;color:white;border:none;padding:.5rem 1.5rem;border-radius:4px;cursor:pointer;width:100%;font-size:1rem">
        Simulate Login
      </button>
    </div>`
})
export class LoginPageComponent {
  auth = inject(AuthService);
  private route = inject(import('@angular/router').then(m => m.ActivatedRoute) as any);
  returnUrl = '/protected';
}

@Component({
  selector: 'app-protected-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="max-width:600px;margin:2rem auto;padding:2rem;background:#e8f5e9;border-radius:8px;font-family:sans-serif">
      <h2 style="color:#27ae60">✅ Protected Page</h2>
      <p style="color:#555;margin-top:.5rem">You are authenticated! The <code>authGuard</code> allowed navigation here.</p>
    </div>`
})
export class ProtectedPageComponent {}

// ─────────────────────────────────────────────────────────────────────────────
// Template-Driven Form
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-template-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2 style="color:#dd0031;margin-bottom:1rem">Template-Driven Form</h2>
      <p style="color:#555;font-size:.85rem;margin-bottom:1rem">Uses <code>FormsModule</code>, <code>[(ngModel)]</code>, and template reference variables for validation.</p>
      <form #f="ngForm" (ngSubmit)="onTemplateSubmit(f.value)" novalidate>
        <div style="margin-bottom:.8rem">
          <label style="display:block;margin-bottom:.3rem">Name</label>
          <input name="name" [(ngModel)]="templateModel.name" required minlength="2"
                 #nameCtrl="ngModel"
                 style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px;outline:none"
                 [style.border-color]="nameCtrl.invalid && nameCtrl.touched ? '#e74c3c' : '#ccc'" />
          <small style="color:#e74c3c" *ngIf="nameCtrl.invalid && nameCtrl.touched">
            <span *ngIf="nameCtrl.errors?.['required']">Name is required</span>
            <span *ngIf="nameCtrl.errors?.['minlength']">Min 2 characters</span>
          </small>
        </div>
        <div style="margin-bottom:.8rem">
          <label style="display:block;margin-bottom:.3rem">Email</label>
          <input name="email" [(ngModel)]="templateModel.email" required email
                 #emailCtrl="ngModel"
                 style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px"
                 [style.border-color]="emailCtrl.invalid && emailCtrl.touched ? '#e74c3c' : '#ccc'" />
          <small style="color:#e74c3c" *ngIf="emailCtrl.invalid && emailCtrl.touched">Valid email required</small>
        </div>
        <div style="margin-bottom:1rem">
          <label style="display:block;margin-bottom:.3rem">Role</label>
          <select name="role" [(ngModel)]="templateModel.role"
                  style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px">
            <option value="student">Student</option>
            <option value="instructor">Instructor</option>
          </select>
        </div>
        <button type="submit" [disabled]="f.invalid"
                style="background:#dd0031;color:white;border:none;padding:.5rem 1.5rem;border-radius:4px;cursor:pointer;width:100%">
          Submit (Template-Driven)
        </button>
      </form>
      <div *ngIf="templateResult" style="margin-top:.8rem;background:#e8f5e9;padding:.8rem;border-radius:6px;font-size:.85rem">
        ✅ Submitted: <code>{{ templateResult | json }}</code>
      </div>
    </div>`
})
export class TemplateFormComponent {
  templateModel = { name: '', email: '', role: 'student' };
  templateResult: any = null;
  onTemplateSubmit(value: any) { this.templateResult = value; }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reactive Form
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-reactive-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="card">
      <h2 style="color:#dd0031;margin-bottom:1rem">Reactive Form</h2>
      <p style="color:#555;font-size:.85rem;margin-bottom:1rem">Uses <code>ReactiveFormsModule</code>, <code>FormBuilder</code>, and programmatic validation. More control, more testable.</p>
      <form [formGroup]="form" (ngSubmit)="onReactiveSubmit()" novalidate>
        <div style="margin-bottom:.8rem">
          <label style="display:block;margin-bottom:.3rem">Username</label>
          <input formControlName="username" style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px"
                 [style.border-color]="isInvalid('username') ? '#e74c3c' : '#ccc'" />
          <small style="color:#e74c3c" *ngIf="isInvalid('username')">
            <span *ngIf="form.get('username')?.errors?.['required']">Required</span>
            <span *ngIf="form.get('username')?.errors?.['minlength']">Min 3 characters</span>
          </small>
        </div>
        <div style="margin-bottom:.8rem">
          <label style="display:block;margin-bottom:.3rem">Password</label>
          <input type="password" formControlName="password" style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px"
                 [style.border-color]="isInvalid('password') ? '#e74c3c' : '#ccc'" />
          <small style="color:#e74c3c" *ngIf="isInvalid('password')">Min 8 characters required</small>
        </div>
        <div style="margin-bottom:1rem">
          <label style="display:block;margin-bottom:.3rem">Confirm Password</label>
          <input type="password" formControlName="confirmPassword" style="width:100%;padding:.4rem;border:1px solid #ccc;border-radius:4px"
                 [style.border-color]="form.errors?.['mismatch'] && form.get('confirmPassword')?.touched ? '#e74c3c' : '#ccc'" />
          <small style="color:#e74c3c" *ngIf="form.errors?.['mismatch'] && form.get('confirmPassword')?.touched">Passwords do not match</small>
        </div>
        <button type="submit" [disabled]="form.invalid"
                style="background:#dd0031;color:white;border:none;padding:.5rem 1.5rem;border-radius:4px;cursor:pointer;width:100%">
          Submit (Reactive)
        </button>
      </form>
      <div *ngIf="reactiveResult" style="margin-top:.8rem;background:#e8f5e9;padding:.8rem;border-radius:6px;font-size:.85rem">
        ✅ Valid form submitted for: <strong>{{ reactiveResult }}</strong>
      </div>
    </div>`
})
export class ReactiveFormComponent implements OnInit {
  form!: FormGroup;
  reactiveResult: string | null = null;

  private fb = inject(FormBuilder);

  ngOnInit() {
    this.form = this.fb.group({
      username:        ['', [Validators.required, Validators.minLength(3)]],
      password:        ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
    }, { validators: this.passwordMatch });
  }

  passwordMatch(group: AbstractControl) {
    const pw  = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { mismatch: true };
  }

  isInvalid(field: string) {
    const ctrl = this.form.get(field);
    return ctrl?.invalid && ctrl?.touched;
  }

  onReactiveSubmit() {
    if (this.form.valid) {
      this.reactiveResult = this.form.value.username;
    }
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root Component
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
            TemplateFormComponent, ReactiveFormComponent],
  styles: [`.card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }`],
  template: `
    <nav style="background:#dd0031;padding:.8rem 2rem;display:flex;gap:1.5rem;align-items:center;font-family:sans-serif">
      <span style="color:white;font-weight:bold">🅰️ Day 18b Part 2</span>
      <a routerLink="/"          style="color:white;text-decoration:none" routerLinkActive="active-link" [routerLinkActiveOptions]="{exact:true}">Forms</a>
      <a routerLink="/protected" style="color:white;text-decoration:none" routerLinkActive="active-link">Protected</a>
      <span style="margin-left:auto">
        <button (click)="toggleAuth()" style="background:white;color:#dd0031;border:none;padding:.3rem .8rem;border-radius:4px;cursor:pointer">
          {{ auth.isLoggedIn ? 'Logout' : 'Login' }}
        </button>
        <span style="color:rgba(255,255,255,.8);font-size:.85rem;margin-left:.5rem">
          {{ auth.isLoggedIn ? '✅ Logged in' : '🔒 Not logged in' }}
        </span>
      </span>
    </nav>
    <div style="max-width:900px;margin:0 auto;padding:2rem 1rem;font-family:sans-serif">
      <router-outlet></router-outlet>
    </div>
    <style>a.active-link { background: rgba(255,255,255,.2); border-radius: 4px; padding: .3rem .6rem; }</style>
  `
})
export class AppComponent {
  auth = inject(AuthService);
  router = inject(Router);

  toggleAuth() {
    if (this.auth.isLoggedIn) {
      this.auth.logout();
      this.router.navigate(['/']);
    } else {
      this.auth.login();
    }
  }
}
