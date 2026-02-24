// =============================================================
// DAY 18b — Part 2, File 1: Route Guards
// =============================================================
// Topics: CanActivate, CanDeactivate, functional guards (Angular 15+)
// Route config integration, guard return types (boolean | UrlTree)
// =============================================================

import { Injectable, inject } from '@angular/core';
import {
  CanActivate,
  CanActivateFn,
  CanDeactivate,
  CanDeactivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  UrlTree,
} from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './auth.service'; // hypothetical service

// =============================================================
// SECTION 1 — CanActivate: Class-Based Guard
// =============================================================
// Class-based guards are the traditional approach (Angular 2–14 style).
// They implement the CanActivate interface from @angular/router.
// Registers as a provider in the module or as standalone.
// =============================================================

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  // Inject dependencies via the constructor
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,    // current route snapshot
    state: RouterStateSnapshot        // full router state (has url property)
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    const isLoggedIn = this.authService.isAuthenticated();

    if (isLoggedIn) {
      return true; // Allow navigation
    }

    // Redirect to login, and pass the attempted URL as a query param
    // so we can redirect back after login
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    // NOTE: createUrlTree returns a UrlTree — Angular will redirect to it.
    // This is cleaner than returning false + calling router.navigate separately.
  }
}

// Route config with class-based guard:
// const routes: Routes = [
//   {
//     path: 'dashboard',
//     component: DashboardComponent,
//     canActivate: [AuthGuard],   // <-- pass the class itself
//   },
// ];

// =============================================================
// SECTION 2 — Role-Based Guard (extends AuthGuard pattern)
// =============================================================
// Sometimes a page requires a specific role (admin, instructor, etc.)
// We can pass data through the route and check it in the guard.
// =============================================================

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    // Read the required role from the route's static data property
    const requiredRole = route.data['role'] as string;
    const userRole = this.authService.getUserRole(); // 'admin' | 'student' | 'instructor'

    if (userRole === requiredRole) {
      return true;
    }

    // Redirect to a forbidden / unauthorized page
    return this.router.createUrlTree(['/forbidden']);
  }
}

// Route config example with RoleGuard:
// {
//   path: 'admin',
//   component: AdminDashboardComponent,
//   canActivate: [AuthGuard, RoleGuard],  // multiple guards — all must pass
//   data: { role: 'admin' },              // static data consumed by RoleGuard
// }

// =============================================================
// SECTION 3 — CanActivate: Functional Guard (Angular 15+)
// =============================================================
// Modern Angular (v15+) introduced functional guards.
// Instead of a class, you write a plain function typed as CanActivateFn.
// Benefits: no class boilerplate, easier to compose, works well with inject().
// =============================================================

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean | UrlTree => {
  // inject() works in functional guards — Angular's DI context is active
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  });
};

// Route config with functional guard:
// const routes: Routes = [
//   {
//     path: 'dashboard',
//     component: DashboardComponent,
//     canActivate: [authGuard],   // function reference, not a class
//   },
// ];

// INSTRUCTOR NOTE: Angular 16+ deprecates class-based guards in favor of
// functional guards. Teach both: older codebases still use class-based.

// =============================================================
// SECTION 4 — CanDeactivate: Unsaved Changes Guard
// =============================================================
// Prevents the user from navigating away from a component
// if there are unsaved changes (e.g., a form mid-edit).
// The component must implement a canDeactivate() method.
// =============================================================

// Step 1: Define an interface that "guarded" components must implement
export interface CanComponentDeactivate {
  canDeactivate(): boolean | Observable<boolean>;
}

// Step 2: Write the guard — it calls canDeactivate() on the component
@Injectable({ providedIn: 'root' })
export class UnsavedChangesGuard implements CanDeactivate<CanComponentDeactivate> {
  canDeactivate(
    component: CanComponentDeactivate,  // the component being navigated away from
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot     // where the user is trying to go
  ): boolean | Observable<boolean> {
    return component.canDeactivate();
    // If the component returns false → navigation is BLOCKED
    // If the component returns true  → navigation is ALLOWED
  }
}

// Step 3: A component that uses this guard
// export class CourseEditComponent implements CanComponentDeactivate {
//   private form = inject(FormBuilder).group({ title: [''] });
//   private savedValue = '';
//
//   hasUnsavedChanges(): boolean {
//     return this.form.get('title')?.value !== this.savedValue;
//   }
//
//   canDeactivate(): boolean {
//     if (this.hasUnsavedChanges()) {
//       return confirm('You have unsaved changes. Leave anyway?');
//     }
//     return true;
//   }
// }

// Route config with CanDeactivate:
// {
//   path: 'courses/:id/edit',
//   component: CourseEditComponent,
//   canDeactivate: [UnsavedChangesGuard],
// }

// =============================================================
// SECTION 5 — CanDeactivate Functional Guard (Angular 15+)
// =============================================================

export const unsavedChangesGuard: CanDeactivateFn<CanComponentDeactivate> = (
  component: CanComponentDeactivate
): boolean | Observable<boolean> => {
  return component.canDeactivate ? component.canDeactivate() : true;
};

// =============================================================
// SECTION 6 — Observable-Based Auth Guard (Async Check)
// =============================================================
// Real apps often check auth state from an Observable (e.g., Firebase Auth).
// CanActivate can return Observable<boolean | UrlTree>.
// =============================================================

@Injectable({ providedIn: 'root' })
export class AsyncAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.isAuthenticated$.pipe(
      map((isAuth: boolean) => {
        if (isAuth) return true;
        return this.router.createUrlTree(['/login']);
      })
      // Angular subscribes automatically — no manual subscribe() needed!
    );
  }
}

// =============================================================
// SECTION 7 — Guard Execution Order & Combining Guards
// =============================================================
// When multiple guards are listed, Angular runs them in ORDER.
// ALL must return true for the route to activate.
//
// canActivate: [AuthGuard, RoleGuard]
//   → First checks AuthGuard, if false → stops there (RoleGuard not called)
//   → If AuthGuard passes, then checks RoleGuard
//
// canActivateChild — guards all child routes of a parent
// canLoad         — prevents even loading the lazy module (deprecated in v15+)
// canMatch        — modern replacement for canLoad (Angular 15+)
// =============================================================

// Route config showing all guard types together:
// const routes: Routes = [
//   {
//     path: 'courses',
//     canActivate: [authGuard],           // guards this route
//     canActivateChild: [authGuard],      // guards all children
//     children: [
//       { path: '', component: CourseListComponent },
//       {
//         path: ':id/edit',
//         component: CourseEditComponent,
//         canDeactivate: [unsavedChangesGuard],
//       },
//     ],
//   },
// ];
