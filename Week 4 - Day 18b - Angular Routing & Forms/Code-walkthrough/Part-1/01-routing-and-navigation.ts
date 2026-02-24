// =============================================================================
// Day 18b — Angular Routing & Forms
// FILE: 01-routing-and-navigation.ts
//
// Topics covered:
//   1. RouterModule — importing and configuring the router
//   2. Route configuration array (Routes)
//   3. RouterLink and RouterLinkActive directives
//   4. routerLinkActiveOptions ({ exact: true })
//   5. Router service — programmatic navigation
//   6. router-outlet — where matched components render
//   7. Redirect routes
//   8. Wildcard (404) route
// =============================================================================
//
// NOTE: Angular routing is wired through the NgModule (or standalone)
// system. The structures below show how each piece fits together.
// All @angular/core and @angular/router imports would resolve in a real project.
//
// Scenario: a DevAcademy course catalog with Home, Courses, About, and Login.
// =============================================================================

import { NgModule, Component, OnInit } from '@angular/core';
import {
  RouterModule,         // The Angular router module
  Routes,               // Type for the route configuration array
  Router,               // Service for programmatic navigation
  RouterLink,           // Directive: [routerLink]="['/path']"
  RouterLinkActive,     // Directive: routerLinkActive="active-class"
} from '@angular/router';
import { CommonModule } from '@angular/common';

// =============================================================================
// SECTION 1 — Page components (simple stubs for routing demo)
// =============================================================================

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <h1>Welcome to DevAcademy 🎓</h1>
      <p>Browse our <a [routerLink]="['/courses']">course catalog</a>.</p>
    </div>
  `,
})
export class HomeComponent {}

@Component({
  selector: 'app-about',
  standalone: true,
  template: `
    <div class="page">
      <h1>About DevAcademy</h1>
      <p>We teach full-stack engineering from zero to production-ready.</p>
    </div>
  `,
})
export class AboutComponent {}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <h1>Login</h1>
      <p>Please sign in to access your dashboard.</p>
      <a [routerLink]="['/']">Cancel</a>
    </div>
  `,
})
export class LoginComponent {}

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <h1>404 — Page Not Found</h1>
      <p>The URL you requested doesn't exist.</p>
      <a [routerLink]="['/']">Go Home</a>
    </div>
  `,
})
export class NotFoundComponent {}

// =============================================================================
// SECTION 2 — RouterModule route configuration (the Routes array)
// =============================================================================

/**
 * The Routes array is the heart of Angular routing.
 * Each object maps a `path` to a `component`.
 *
 * Key rules:
 *   - Paths do NOT start with "/"
 *   - Routes are matched top to bottom — more specific first
 *   - The empty string '' matches the root URL "/"
 *   - '**' is the wildcard — matches ANYTHING not matched above
 *
 * Angular Router matches the FIRST route that fits, then stops.
 * ORDER MATTERS — put the wildcard LAST.
 */
export const APP_ROUTES: Routes = [
  // Redirect the root "" to "/home" (pathMatch: 'full' is required for empty paths)
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',   // <-- REQUIRED for empty path redirect
  },

  // Simple component routes
  { path: 'home',    component: HomeComponent },
  { path: 'about',   component: AboutComponent },
  { path: 'login',   component: LoginComponent },

  // Courses routes (defined in a separate file in a real app)
  // We add them here for completeness — detail in File 02 and 03
  { path: 'courses', loadChildren: () =>
      import('./courses-routing-placeholder').then(m => m.CoursesModule) },

  // Wildcard — catches all unmatched URLs (put LAST)
  { path: '**', component: NotFoundComponent },
];

// =============================================================================
// SECTION 3 — AppModule: registering RouterModule
// =============================================================================

/**
 * RouterModule.forRoot(routes) — call ONCE in the root module.
 *   - `forRoot` sets up the router service, the history API, and registers
 *     the route configuration.
 *
 * RouterModule.forChild(routes) — used in FEATURE modules (lazy loaded).
 *   - Does NOT re-create the router service — just adds more routes.
 *
 * Watch out: calling forRoot in a lazy-loaded feature module breaks routing.
 * Always use forRoot ONLY in the root AppModule.
 */
@NgModule({
  imports: [
    RouterModule.forRoot(APP_ROUTES, {
      // scrollPositionRestoration: 'enabled', // scroll to top on navigation
      // enableTracing: true, // debug: logs every route event to the console
    }),
  ],
  exports: [RouterModule], // export so app template can use router-outlet, routerLink, etc.
})
export class AppRoutingModule {}

// =============================================================================
// SECTION 4 — NavBar component: RouterLink and RouterLinkActive
// =============================================================================

/**
 * RouterLink — the Angular equivalent of React's <Link>.
 *   Syntax options:
 *     [routerLink]="['/home']"           — array syntax (recommended)
 *     [routerLink]="'/home'"             — string (simpler, no params)
 *     [routerLink]="['/courses', 5]"     — array with route param (→ /courses/5)
 *
 * RouterLinkActive — adds a CSS class when the link's route is active.
 *   routerLinkActive="active"            — adds class "active" when matched
 *   [routerLinkActiveOptions]="{ exact: true }" — exact match only (needed for '/')
 *
 * Watch out: without `exact: true`, the Home link "/" would ALWAYS appear
 * active because "/" is a prefix of every path.
 */
@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="main-nav">

      <!-- routerLinkActive adds "active" class when route matches -->
      <!-- [routerLinkActiveOptions]="{exact:true}" prevents '/' matching everything -->
      <a
        [routerLink]="['/home']"
        routerLinkActive="active"
        [routerLinkActiveOptions]="{ exact: true }"
      >Home</a>

      <a [routerLink]="['/courses']" routerLinkActive="active">Courses</a>
      <a [routerLink]="['/about']"   routerLinkActive="active">About</a>

      <!-- Programmatic navigation handled in NavComponent class (Section 5) -->
      <button (click)="goToLogin()">Login</button>
    </nav>
  `,
  styles: [`
    .main-nav { display: flex; gap: 1.5rem; padding: 1rem; background: #2d3748; }
    .main-nav a { color: #e2e8f0; text-decoration: none; }
    .main-nav a.active { color: #63b3ed; font-weight: bold; border-bottom: 2px solid #63b3ed; }
    button { background: #3182ce; color: white; border: none; padding: 0.4rem 1rem;
              border-radius: 4px; cursor: pointer; }
  `],
})
export class NavComponent {
  // SECTION 5 — Programmatic navigation with the Router service
  constructor(private router: Router) {}

  /**
   * router.navigate(['/login']) — navigates programmatically.
   *
   * Useful when navigation happens AFTER an action (form submit, button click,
   * API response).  Pass an array (same format as RouterLink).
   *
   * Options:
   *   queryParams: { returnUrl: '/dashboard' }  — append query string
   *   replaceUrl: true                          — replace current history entry
   *   relativeTo: this.route                    — navigate relative to current route
   */
  goToLogin(): void {
    this.router.navigate(['/login'], {
      queryParams: { returnUrl: '/dashboard' }, // adds ?returnUrl=%2Fdashboard
    });
  }

  /** Navigate to a course detail page (id comes from a selection event) */
  openCourse(courseId: number): void {
    this.router.navigate(['/courses', courseId]);
    // Equivalent RouterLink: [routerLink]="['/courses', courseId]"
  }

  /** Go back in browser history */
  goBack(): void {
    history.back(); // works, but Router has no built-in back(); use Location service
    // Or: this.location.back() if you inject Location from @angular/common
  }
}

// =============================================================================
// SECTION 6 — AppComponent: the root component with <router-outlet>
// =============================================================================

/**
 * <router-outlet> is the placeholder where the router renders the matched
 * component.  There is ONE primary outlet in the root template.
 *
 * Think of it as the Angular equivalent of React Router's <Outlet>.
 *
 * Watch out:
 *   - If you forget <router-outlet>, routing "works" (URL changes)
 *     but nothing renders on screen.
 *   - NavComponent sits OUTSIDE <router-outlet> so it's always visible.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, NavComponent],
  template: `
    <!-- NavBar rendered on EVERY page -->
    <app-nav></app-nav>

    <!-- Matched route component renders here -->
    <main>
      <router-outlet></router-outlet>
    </main>

    <footer>
      <p>© 2025 DevAcademy</p>
    </footer>
  `,
})
export class AppComponent {}
