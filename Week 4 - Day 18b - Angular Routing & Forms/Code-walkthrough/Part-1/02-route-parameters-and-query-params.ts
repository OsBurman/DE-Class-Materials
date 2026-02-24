// =============================================================================
// Day 18b — Angular Routing & Forms
// FILE: 02-route-parameters-and-query-params.ts
//
// Topics covered:
//   1. Route parameters (:id) — declared in the route config
//   2. ActivatedRoute service — injected into components
//   3. snapshot.paramMap vs observable paramMap (when to use each)
//   4. Reading route parameters with paramMap.get()
//   5. Query parameters (?sort=asc&page=2)
//   6. Reading query params with queryParamMap
//   7. Navigating with query params (Router.navigate + queryParams option)
//   8. Fragment (#section) navigation
//   9. Persisting query params across navigation (queryParamsHandling)
// =============================================================================

import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  ActivatedRoute,   // Service: holds info about the currently active route
  Router,
  ParamMap,         // Interface: provides paramMap / queryParamMap
} from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

// =============================================================================
// SECTION 1 — Route configuration with parameters
// =============================================================================

/**
 * In your Routes array you declare dynamic segments with a colon prefix:
 *
 *   { path: 'courses/:courseId', component: CourseDetailComponent }
 *   { path: 'courses/:courseId/lesson/:lessonId', component: LessonComponent }
 *
 * `:courseId` is a path parameter — it matches any value in that position.
 *   /courses/1      → courseId = '1'
 *   /courses/react  → courseId = 'react'
 *
 * IMPORTANT: params are ALWAYS strings — convert to number if needed.
 */

// Sample data (pretend this comes from a service)
const COURSES = [
  { id: 1, title: 'React Fundamentals',   category: 'frontend', level: 'beginner',     description: 'Learn React from the ground up.' },
  { id: 2, title: 'Redux & State',        category: 'frontend', level: 'intermediate', description: 'Manage global state with Redux.' },
  { id: 3, title: 'Spring Boot',          category: 'backend',  level: 'intermediate', description: 'Build REST APIs with Spring Boot.' },
  { id: 4, title: 'Docker & Kubernetes',  category: 'devops',   level: 'advanced',     description: 'Containerise and orchestrate apps.' },
  { id: 5, title: 'TypeScript Deep Dive', category: 'frontend', level: 'intermediate', description: 'Master TypeScript for large codebases.' },
];

// =============================================================================
// SECTION 2 — Reading route parameters: snapshot vs observable
// =============================================================================

/**
 * Two ways to read route params from ActivatedRoute:
 *
 * A) SNAPSHOT — a one-time read, good when the component is NOT reused
 *    for different param values (i.e. navigating away always creates a new instance).
 *
 *    const id = this.route.snapshot.paramMap.get('courseId');
 *
 * B) OBSERVABLE — subscribes to param changes; required when the SAME component
 *    instance is reused with different params (e.g. next/prev course buttons).
 *    Without the observable, navigating from /courses/1 to /courses/2 would
 *    NOT re-run ngOnInit — the component never re-renders.
 *
 *    this.route.paramMap.subscribe(params => {
 *      const id = params.get('courseId');
 *    });
 */

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="course-detail" *ngIf="course; else notFound">
      <h1>{{ course.title }}</h1>
      <p><strong>Category:</strong> {{ course.category }}</p>
      <p><strong>Level:</strong> {{ course.level }}</p>
      <p>{{ course.description }}</p>

      <!-- SECTION 8 — Fragment navigation (jump to a section in the same page) -->
      <nav>
        <a [routerLink]="[]" [fragment]="'overview'">Overview</a> |
        <a [routerLink]="[]" [fragment]="'curriculum'">Curriculum</a>
      </nav>

      <section id="overview"><h2>Overview</h2><p>Section content here.</p></section>
      <section id="curriculum"><h2>Curriculum</h2><p>Curriculum content here.</p></section>
    </div>

    <ng-template #notFound>
      <p class="error">Course not found (id: {{ courseId }})</p>
    </ng-template>
  `,
})
export class CourseDetailComponent implements OnInit, OnDestroy {
  course: typeof COURSES[0] | undefined;
  courseId: string | null = null;
  private paramSub!: Subscription;

  constructor(
    private route: ActivatedRoute,  // inject ActivatedRoute — NOT Router
    private router: Router,
  ) {}

  ngOnInit(): void {
    // ── OPTION A: Snapshot (simple, use when component is never reused) ──────
    // const idSnapshot = this.route.snapshot.paramMap.get('courseId');
    // this.loadCourse(idSnapshot);

    // ── OPTION B: Observable (use when navigating between items of same type) ─
    // The paramMap Observable emits a new value every time the route param changes.
    this.paramSub = this.route.paramMap.subscribe((params: ParamMap) => {
      this.courseId = params.get('courseId'); // always a string or null
      this.loadCourse(this.courseId);
    });
  }

  private loadCourse(id: string | null): void {
    if (!id) return;
    // Params are STRINGS — convert to number for array lookup
    this.course = COURSES.find(c => c.id === Number(id));
  }

  ngOnDestroy(): void {
    // ALWAYS unsubscribe to prevent memory leaks
    this.paramSub?.unsubscribe();
  }
}

// =============================================================================
// SECTION 3 — Reading query parameters with queryParamMap
// =============================================================================

/**
 * Query parameters appear after the '?' in the URL:
 *   /courses?category=frontend&level=intermediate&page=2
 *
 * They are accessed via:
 *   this.route.snapshot.queryParamMap.get('category')   — snapshot
 *   this.route.queryParamMap.subscribe(...)              — observable
 *
 * Or the convenience property:
 *   this.route.queryParams (Observable<Params>)          — plain object
 *
 * Watch out: queryParams are ALSO always strings.
 * page = Number(params.get('page') ?? '1') — always provide a default!
 */

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="course-list">
      <h1>Courses</h1>

      <!-- Filter controls — clicking these navigates with updated query params -->
      <div class="filters">
        <select (change)="applyFilter('category', $any($event.target).value)">
          <option value="">All Categories</option>
          <option value="frontend">Frontend</option>
          <option value="backend">Backend</option>
          <option value="devops">DevOps</option>
        </select>

        <select (change)="applyFilter('level', $any($event.target).value)">
          <option value="">All Levels</option>
          <option value="beginner">Beginner</option>
          <option value="intermediate">Intermediate</option>
          <option value="advanced">Advanced</option>
        </select>

        <button (click)="clearFilters()">Clear</button>
      </div>

      <!-- Current query params for demo -->
      <p class="debug">
        Filtering by: category={{ activeCategory || 'all' }},
        level={{ activeLevel || 'all' }},
        page={{ currentPage }}
      </p>

      <ul>
        <li *ngFor="let course of filteredCourses">
          <!-- Navigate to detail with course id as route param -->
          <a [routerLink]="['/courses', course.id]">{{ course.title }}</a>
          ({{ course.category }} · {{ course.level }})
        </li>
      </ul>
    </div>
  `,
})
export class CourseListComponent implements OnInit, OnDestroy {
  filteredCourses = COURSES;
  activeCategory  = '';
  activeLevel     = '';
  currentPage     = 1;

  private querySub!: Subscription;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    // Subscribe to query param changes (react when URL bar changes)
    this.querySub = this.route.queryParamMap.subscribe(params => {
      this.activeCategory = params.get('category') || '';
      this.activeLevel    = params.get('level')    || '';
      this.currentPage    = Number(params.get('page') ?? '1');
      this.filterCourses();
    });
  }

  private filterCourses(): void {
    this.filteredCourses = COURSES.filter(c => {
      const catMatch   = !this.activeCategory || c.category === this.activeCategory;
      const levelMatch = !this.activeLevel    || c.level    === this.activeLevel;
      return catMatch && levelMatch;
    });
  }

  /**
   * SECTION 4 — Navigating with query params
   *
   * router.navigate with queryParams option appends ?key=value to the URL.
   * queryParamsHandling: 'merge' — KEEPS existing params, adds/updates the new one.
   * queryParamsHandling: 'preserve' — keeps ALL existing params unchanged.
   * default (undefined) — REPLACES all params with the new set.
   */
  applyFilter(key: string, value: string): void {
    this.router.navigate([], {       // [] = stay on the same route (no path change)
      relativeTo: this.route,         // relative to current activated route
      queryParams: { [key]: value || null },
      queryParamsHandling: 'merge',   // keep the OTHER filter params that are already set
    });
  }

  clearFilters(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {},               // empty object clears all params
    });
  }

  ngOnDestroy(): void {
    this.querySub?.unsubscribe();
  }
}

// =============================================================================
// SECTION 5 — Combining route params + query params in one component
// =============================================================================

/**
 * Example: /courses/3/lessons?tab=overview&autoplay=true
 *
 * Route params  → this.route.paramMap  (courseId, lessonId)
 * Query params  → this.route.queryParamMap (tab, autoplay)
 * Fragment      → this.route.fragment  (Observable<string | null>)
 */

@Component({
  selector: 'app-lesson',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div>
      <h2>Course {{ courseId }} — Lesson {{ lessonId }}</h2>
      <p>Active tab: {{ activeTab }}</p>
      <p>Autoplay: {{ autoplay }}</p>
      <p>Fragment: {{ fragment }}</p>
    </div>
  `,
})
export class LessonComponent implements OnInit {
  courseId  = '';
  lessonId  = '';
  activeTab = 'overview';
  autoplay  = false;
  fragment  = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Read route params (snapshot is fine here — navigating away recreates the component)
    const snap = this.route.snapshot;
    this.courseId = snap.paramMap.get('courseId') ?? '';
    this.lessonId = snap.paramMap.get('lessonId') ?? '';

    // Read query params from snapshot
    this.activeTab = snap.queryParamMap.get('tab')      ?? 'overview';
    this.autoplay  = snap.queryParamMap.get('autoplay') === 'true';

    // Read fragment (#section)
    this.fragment  = snap.fragment ?? '';
  }
}

// =============================================================================
// SECTION 6 — queryParamsHandling: 'merge' vs 'preserve' demo (comment block)
// =============================================================================

/**
 * URL: /courses?category=frontend&level=beginner&page=1
 *
 * If you call navigate with just { queryParams: { page: 2 } }:
 *
 *   queryParamsHandling: undefined (default)
 *     → /courses?page=2          (DROPS category and level!)
 *
 *   queryParamsHandling: 'merge'
 *     → /courses?category=frontend&level=beginner&page=2  (updates only page)
 *
 *   queryParamsHandling: 'preserve'
 *     → /courses?category=frontend&level=beginner&page=1  (ignores your new params)
 *
 * Use 'merge' when updating ONE filter and keeping the rest.
 * Use 'preserve' when navigating away and wanting to return to the same filters.
 */
