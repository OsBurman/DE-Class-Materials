// =============================================================
// DAY 18b — Part 2, File 2: Lazy Loading
// =============================================================
// Topics: loadChildren, feature modules, RouterModule.forChild(),
// preloading strategies (PreloadAllModules, custom), canMatch guard
// =============================================================

import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules, Route } from '@angular/router';
import { PreloadingStrategy } from '@angular/router';
import { Observable, of } from 'rxjs';

// =============================================================
// SECTION 1 — What Is Lazy Loading?
// =============================================================
// By default, Angular bundles ALL components/modules into one JS file
// (main.js). This is called EAGER loading.
//
// Problem: Large apps have huge initial bundle → slow first load.
//
// Lazy Loading: Load feature module bundles ON DEMAND — only when
// the user navigates to that route for the first time.
//
// Angular automatically code-splits at the loadChildren boundary.
// Each lazy-loaded module becomes its own JS chunk (e.g., courses.js).
// =============================================================

// =============================================================
// SECTION 2 — Eager vs Lazy Loading: Side-by-Side
// =============================================================

// ❌ EAGER (default) — CoursesModule is bundled into main.js immediately:
// const routes: Routes = [
//   { path: 'courses', component: CoursesComponent },  // component must be imported
// ];

// ✅ LAZY — CoursesModule is loaded only when user visits /courses:
// const routes: Routes = [
//   {
//     path: 'courses',
//     loadChildren: () =>
//       import('./courses/courses.module').then(m => m.CoursesModule),
//   },
// ];
//
// Key difference:
// - No `component` property — the entire MODULE is loaded
// - `import()` is a standard JS dynamic import (returns a Promise)
// - `.then(m => m.CoursesModule)` — extract the NgModule class from the file
// =============================================================

// =============================================================
// SECTION 3 — Feature Module Structure
// =============================================================
// When a route uses loadChildren, it points to a "feature module".
// That module MUST define its own routes using RouterModule.forChild().
// =============================================================

// --- courses/courses-routing.module.ts ---
// (This would live inside the courses/ feature folder)

// import { NgModule } from '@angular/core';
// import { RouterModule, Routes } from '@angular/router';
// import { CourseListComponent } from './course-list/course-list.component';
// import { CourseDetailComponent } from './course-detail/course-detail.component';

const coursesRoutes: Routes = [
  // NOTE: path here is RELATIVE to the lazy-loaded parent path ('courses')
  // So '' matches '/courses' and ':id' matches '/courses/123'
  { path: '', component: 'CourseListComponent' as any },
  { path: ':id', component: 'CourseDetailComponent' as any },
  { path: ':id/enroll', component: 'CourseEnrollComponent' as any },
];

// @NgModule({
//   imports: [RouterModule.forChild(coursesRoutes)],  // <-- forChild, NOT forRoot!
//   exports: [RouterModule],
// })
// export class CoursesRoutingModule {}

// CRITICAL: forRoot() is called ONCE in AppRoutingModule.
// forChild() is called in each feature module.
// Calling forRoot() in a feature module causes duplicate router providers!

// =============================================================
// SECTION 4 — Standalone Components with Lazy Loading (Angular 14+)
// =============================================================
// Modern Angular uses standalone components instead of NgModules.
// Use loadComponent instead of loadChildren.
// =============================================================

const modernRoutes: Routes = [
  {
    path: 'courses',
    // loadComponent — lazy loads a single standalone component
    loadComponent: () =>
      import('./courses/course-list/course-list.component').then(
        m => m.CourseListComponent
      ),
  },
  {
    path: 'admin',
    // loadChildren with an array of routes — no NgModule needed!
    loadChildren: () =>
      import('./admin/admin.routes').then(m => m.ADMIN_ROUTES),
  },
];

// INSTRUCTOR NOTE: NgModule-based lazy loading is still common.
// Standalone approach is the Angular team's recommended direction.
// Teach both — trainees will encounter both in real codebases.

// =============================================================
// SECTION 5 — Full App Routing Module with Lazy Loading
// =============================================================

const appRoutes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },

  // Eagerly loaded — small, always needed
  { path: 'home', component: 'HomeComponent' as any },
  { path: 'login', component: 'LoginComponent' as any },

  // Lazily loaded — only bundled/fetched when user navigates here
  {
    path: 'courses',
    loadChildren: () =>
      import('./courses/courses.module').then(m => m.CoursesModule),
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then(m => m.AdminModule),
    // canMatch: [authGuard]  <-- can combine with guards
  },
  {
    path: 'profile',
    loadChildren: () =>
      import('./profile/profile.module').then(m => m.ProfileModule),
  },

  { path: '**', component: 'NotFoundComponent' as any },
];

// =============================================================
// SECTION 6 — Preloading Strategies
// =============================================================
// Problem with lazy loading: first visit to /courses causes a network
// request — there's a slight delay while Angular fetches the chunk.
//
// Preloading: Load lazy modules IN THE BACKGROUND after the app starts.
// The initial bundle is still small, but lazy modules load silently.
// =============================================================

// Strategy 1: NoPreloading (default) — only load when navigated to
// RouterModule.forRoot(routes)  // default = NoPreloading

// Strategy 2: PreloadAllModules — preload ALL lazy modules immediately
// RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })

// The difference in practice:
// ┌─────────────────────┬──────────────────────────────────────────┐
// │ Strategy            │ Behavior                                 │
// ├─────────────────────┼──────────────────────────────────────────┤
// │ NoPreloading        │ Load chunk only when user navigates      │
// │ PreloadAllModules   │ Load all chunks in background after init │
// │ Custom Strategy     │ Load select chunks (e.g., by data flag)  │
// └─────────────────────┴──────────────────────────────────────────┘

// =============================================================
// SECTION 7 — Custom Preloading Strategy
// =============================================================
// You can selectively preload routes by adding a `data: { preload: true }`
// flag and writing a custom PreloadingStrategy.
// =============================================================

// Custom strategy: only preload routes that have data.preload === true
export class SelectivePreloadingStrategy implements PreloadingStrategy {
  preload(route: Route, load: () => Observable<any>): Observable<any> {
    // If route has { data: { preload: true } }, preload it
    if (route.data && route.data['preload']) {
      console.log(`Preloading: ${route.path}`);
      return load(); // trigger the module fetch
    }
    return of(null); // skip preloading for this route
  }
}

// Usage in AppRoutingModule:
// @NgModule({
//   imports: [
//     RouterModule.forRoot(routes, {
//       preloadingStrategy: SelectivePreloadingStrategy,
//     })
//   ],
//   providers: [SelectivePreloadingStrategy],
//   exports: [RouterModule],
// })
// export class AppRoutingModule {}

// Route with preload flag:
// {
//   path: 'courses',
//   loadChildren: () => import('./courses/courses.module').then(m => m.CoursesModule),
//   data: { preload: true },   // <-- will be preloaded by SelectivePreloadingStrategy
// }

// =============================================================
// SECTION 8 — canMatch Guard (Angular 15+ — replaces canLoad)
// =============================================================
// canLoad (deprecated v15) prevented loading the lazy module at all.
// canMatch is the replacement — works for both lazy and eager routes.
// =============================================================

// const routesWithCanMatch: Routes = [
//   {
//     path: 'admin',
//     loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
//     canMatch: [() => {
//       const authService = inject(AuthService);
//       return authService.isAuthenticated();
//       // false → Angular won't even attempt to load the admin bundle
//     }],
//   },
// ];

// =============================================================
// SECTION 9 — Inspecting Lazy Loading in DevTools
// =============================================================
// Open Chrome DevTools → Network tab → filter by "JS"
// Navigate to a lazy-loaded route and watch a new chunk file appear.
//
// Example: navigating to /courses triggers:
//   GET /courses-courses-module.js  (the lazy chunk)
//
// After PreloadAllModules: that same chunk appears shortly after page load,
// without any user interaction.
//
// Bundle analysis: run `ng build --stats-json` then use webpack-bundle-analyzer
// or `ng build` → check the output for named chunks.
// =============================================================
