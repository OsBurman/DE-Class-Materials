// =============================================================================
// Day 18b — Angular Routing & Forms
// FILE: 03-nested-routes.ts
//
// Topics covered:
//   1. Nested routes — `children` array inside a parent route
//   2. Parent component with its OWN <router-outlet>
//   3. Index / default child route (redirectTo or pathMatch: 'full')
//   4. Relative navigation inside nested routes
//   5. Accessing parent route params from a child component
//   6. Named router outlets (secondary outlets) — brief demo
// =============================================================================
//
// Scenario: A /dashboard area with a persistent sidebar and multiple sub-pages:
//   /dashboard            → redirects to /dashboard/overview
//   /dashboard/overview   → DashboardOverviewComponent
//   /dashboard/courses    → DashboardCoursesComponent
//   /dashboard/courses/:id → EnrolledCourseDetailComponent
//   /dashboard/settings   → DashboardSettingsComponent
// =============================================================================

import { Component, OnInit } from '@angular/core';
import {
  Routes,
  RouterModule,
  RouterLink,
  RouterLinkActive,
  ActivatedRoute,
  Router,
} from '@angular/router';
import { CommonModule } from '@angular/common';

// =============================================================================
// SECTION 1 — Child page components (stubs)
// =============================================================================

@Component({
  selector: 'app-dashboard-overview',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div>
      <h2>Dashboard Overview</h2>
      <p>Welcome back! You have 3 courses in progress.</p>
      <!-- Relative link: "courses" → /dashboard/courses -->
      <a [routerLink]="['courses']">View my courses →</a>
    </div>
  `,
})
export class DashboardOverviewComponent {}

const ENROLLED = [
  { id: 1, title: 'React Fundamentals',   progress: 80 },
  { id: 2, title: 'Redux & State',        progress: 45 },
  { id: 3, title: 'Spring Boot',          progress: 20 },
];

@Component({
  selector: 'app-dashboard-courses',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div>
      <h2>My Courses</h2>
      <ul>
        <li *ngFor="let c of courses">
          <!--
            Relative link to a child route from /dashboard/courses:
            "1" → /dashboard/courses/1
            No leading slash = relative.  Leading slash = absolute.
          -->
          <a [routerLink]="[c.id]">{{ c.title }}</a>
          — {{ c.progress }}% complete
        </li>
      </ul>
    </div>
  `,
})
export class DashboardCoursesComponent {
  courses = ENROLLED;
}

@Component({
  selector: 'app-dashboard-settings',
  standalone: true,
  template: `
    <div>
      <h2>Settings</h2>
      <p>Theme, notifications, and account preferences.</p>
    </div>
  `,
})
export class DashboardSettingsComponent {}

// =============================================================================
// SECTION 2 — Accessing PARENT route params from a child component
// =============================================================================

/**
 * When /dashboard/courses/:id is a CHILD route, the courseId param lives
 * on the PARENT route's ActivatedRoute snapshot.
 *
 * Two strategies:
 *   A) parent.snapshot.paramMap.get('id')
 *   B) Set `paramsInheritanceStrategy: 'always'` in RouterModule.forRoot config
 *      — then child can access parent params directly.
 */
@Component({
  selector: 'app-enrolled-course-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div *ngIf="course; else notFound">
      <h2>{{ course.title }}</h2>
      <div class="progress-bar-track">
        <div class="progress-bar-fill" [style.width.%]="course.progress"></div>
      </div>
      <p>{{ course.progress }}% complete</p>

      <!--
        '..' goes UP one level: /dashboard/courses/:id → /dashboard/courses
        Relative navigations work without knowing the full path.
      -->
      <a [routerLink]="['..']">← Back to My Courses</a>
    </div>

    <ng-template #notFound>
      <p>Course not found.</p>
      <a [routerLink]="['..']">← Back</a>
    </ng-template>
  `,
  styles: [`
    .progress-bar-track { height: 12px; background: #e2e8f0; border-radius: 6px; width: 300px; }
    .progress-bar-fill  { height: 100%; background: #3182ce; border-radius: 6px; transition: width .3s; }
  `],
})
export class EnrolledCourseDetailComponent implements OnInit {
  course: typeof ENROLLED[0] | undefined;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Child reads its OWN paramMap
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.course = ENROLLED.find(c => c.id === id);

    // If the parent route also had params (e.g. /users/:userId/courses/:id):
    //   const parentId = this.route.parent?.snapshot.paramMap.get('userId');
  }
}

// =============================================================================
// SECTION 3 — DashboardComponent: the PARENT with its own <router-outlet>
// =============================================================================

/**
 * Key concept: any component that is a PARENT in the route tree MUST
 * contain its own <router-outlet>.  That outlet is where the child route's
 * component renders.
 *
 * Two levels of outlet:
 *   AppComponent      → primary <router-outlet>  → DashboardComponent
 *   DashboardComponent → secondary <router-outlet> → child pages
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  template: `
    <div class="dashboard-layout">

      <!-- Persistent sidebar — stays visible across all child routes -->
      <aside class="sidebar">
        <h3>Dashboard</h3>
        <nav>
          <!--
            routerLink with RELATIVE paths (no leading slash):
            'overview' → /dashboard/overview
          -->
          <a [routerLink]="['overview']"  routerLinkActive="active">Overview</a>
          <a [routerLink]="['courses']"   routerLinkActive="active">My Courses</a>
          <a [routerLink]="['settings']"  routerLinkActive="active">Settings</a>
        </nav>
      </aside>

      <!-- Child route renders here (inside the dashboard's own outlet) -->
      <main class="content">
        <router-outlet></router-outlet>
      </main>

    </div>
  `,
  styles: [`
    .dashboard-layout { display: flex; min-height: 80vh; }
    .sidebar { width: 200px; background: #f7fafc; padding: 1.5rem 1rem; border-right: 1px solid #e2e8f0; }
    .sidebar nav a { display: block; padding: 0.5rem 0.75rem; border-radius: 4px; text-decoration: none; color: #2d3748; margin-bottom: 4px; }
    .sidebar nav a.active { background: #3182ce; color: white; }
    .content { flex: 1; padding: 1.5rem 2rem; }
  `],
})
export class DashboardComponent {}

// =============================================================================
// SECTION 4 — Nested route configuration with `children`
// =============================================================================

/**
 * The `children` array inside a route defines sub-routes.
 * Their paths are RELATIVE to the parent's path.
 *
 *   path: 'dashboard'        → /dashboard
 *     children:
 *       path: ''  redirectTo → /dashboard  → /dashboard/overview
 *       path: 'overview'     → /dashboard/overview
 *       path: 'courses'      → /dashboard/courses
 *       path: 'courses/:id'  → /dashboard/courses/1
 *       path: 'settings'     → /dashboard/settings
 *
 * Watch out:
 *   - The parent component MUST have <router-outlet> or children never appear.
 *   - Child paths must NOT start with '/'.
 */
export const DASHBOARD_ROUTES: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,   // parent — has its own <router-outlet>
    children: [
      {
        // Default child: redirect empty path to 'overview'
        path: '',
        redirectTo: 'overview',
        pathMatch: 'full',
      },
      {
        path: 'overview',
        component: DashboardOverviewComponent,
      },
      {
        path: 'courses',
        component: DashboardCoursesComponent,
      },
      {
        // Deeply nested: /dashboard/courses/:id
        // This component is a child of DashboardCourses in the URL tree,
        // but they all render inside DashboardComponent's outlet.
        path: 'courses/:id',
        component: EnrolledCourseDetailComponent,
      },
      {
        path: 'settings',
        component: DashboardSettingsComponent,
      },
    ],
  },
];

// =============================================================================
// SECTION 5 — Named (secondary) router outlets — brief overview
// =============================================================================

/**
 * Angular supports NAMED outlets — multiple router-outlets on the same page,
 * each rendering a different component simultaneously.
 *
 * Use case: a slide-out detail panel that appears alongside the main content.
 *
 * Template:
 *   <router-outlet></router-outlet>          ← primary outlet
 *   <router-outlet name="sidebar"></router-outlet> ← secondary outlet
 *
 * Route config:
 *   { path: 'courses/:id', component: CourseDetailComponent, outlet: 'sidebar' }
 *
 * Navigation:
 *   this.router.navigate([{ outlets: { primary: ['courses'], sidebar: ['courses', 5] } }]);
 *
 * RouterLink:
 *   [routerLink]="[{ outlets: { sidebar: ['courses', 5] } }]"
 *
 * Close a secondary outlet:
 *   this.router.navigate([{ outlets: { sidebar: null } }]);
 *
 * Named outlets are an advanced feature — useful for complex layouts
 * (e.g. a chat panel that persists across navigation).
 * For most applications, primary outlets + nested children are sufficient.
 */
